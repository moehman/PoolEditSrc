/*
 * Copyright (C) 2019 Automation technology laboratory,
 * Helsinki University of Technology
 *
 * Visit automation.tkk.fi for information about the automation
 * technology laboratory.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */
package pooledit;

import static pooledit.Definitions.*;
import attributetable.AttributeTable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author mohman
 */
public class Tools {
  
    private static final DOMImplementationRegistry REGISTRY;
    private static final DOMImplementationLS IMPL_LS;    
    static {
        System.setProperty(DOMImplementationRegistry.PROPERTY,
	       "org.apache.xerces.dom.DOMImplementationSourceImpl");
        DOMImplementationRegistry reg;
        try { 
            reg = DOMImplementationRegistry.newInstance();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
            reg = null;
        }
        REGISTRY = reg;
        IMPL_LS = (DOMImplementationLS) REGISTRY.getDOMImplementation("LS 3.0");
    }
       
    /**
     * Creates a (root level) name map of all real objects that have a name 
     * attribute.The key is the name value of attribute and value is the 
     * element.
     * @param doc
     * @return 
     */
    public static Map<String,Element> createNameMap(Document doc) {
	// create a new HashMap
	return updateNameMap(doc, new HashMap<>(), false); 
    }
    
    /**
     * See above.
     * @param doc
     * @param fullMap
     * @return
     */
    public static Map<String,Element> createNameMap(Document doc, boolean fullMap) {
	// create a new HashMap
	return updateNameMap(doc, new HashMap<>(), fullMap); 
    }
    
    /**
     * Updates (root level) name map. 
     * @param doc
     * @param nameMap
     * @return 
     */
    public static Map<String,Element> updateNameMap(Document doc, 
						    Map<String,Element> nameMap) {       
        return updateNameMap(doc, nameMap, false);
    }
    
    public static Map<String,Element> updateNameMap(Document doc,
            Map<String,Element> nameMap, boolean fullMap) {
        
	nameMap.clear();
	// get all elements in the order they appear in the document
        NodeList nodes = fullMap ? doc.getDocumentElement().getElementsByTagName("*") :
            doc.getDocumentElement().getChildNodes();
                
	// iterate over all elements
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // is interesting and has a name?
                if (Utils.equals(element.getTagName(), OBJECTS)) {

                    String name = element.getAttribute(NAME);
                    if (!name.isEmpty()) {

                        // use simple structured names for objects with language
                        // attribute
                        String language = element.getAttribute(LANGUAGE);
                        if (!language.isEmpty() && !language.equals("en")) {
                            name += "." + language;
                        }

                        // do not complain if building a full map
                        if (!fullMap && nameMap.containsKey(name)) {
                            System.err.println(Tools.class.getName() + " name \"" + 
                                    name + "\" is already defined!");
                        }
                        // add to map
                        nameMap.put(name, element);
                    }
                }
            }
        }
        return nameMap;
    }            
    
    /**
     * Creates a name-attribute (and name) to all objects that should
     * have it and don't.  The name map is updated.
     *
     * @attribute doc DOM-model of document
     * @attribue nameMap the HashMap
    public static void createMissingNames(Element element, 
					  Map<String, Element> nameMap) {
    
	// get all elements in the order they appear in the document
        NodeList elements = element.getElementsByTagName("*");
        
	// iterate over all elements
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element elem = (Element) elements.item(i);
            
	    // missing a name?
            if (equals(elem.getTagName(), OBJECTS) && 
		elem.getAttribute(NAME).isEmpty()) {
                
		// find free name, set it, and update name map
		String name = findFreeName(elem.getTagName(), nameMap);
                elem.setAttribute(NAME, name);
                nameMap.put(name, elem);
            }
        }
    }
     */
    
    /**
     * Must be executed after createRoles!
     * @param doc
     */
    public static void checkNaming(Document doc) {
    
        // full name map contains all the names in the document
        Map<String, Element> fullMap = createNameMap(doc, true);
        
        // root name map contains only the names defined at the root level
        Map<String, Element> rootMap = createNameMap(doc, false);
        
        // nodes that need fixing are put on this work list
        List<Element> worklist = new ArrayList<>();
        
        Element root = doc.getDocumentElement();
        
        // iterate over all references
        NodeList elements = root.getElementsByTagName(INCLUDE_OBJECT);
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element elem = (Element) elements.item(i);
            String name = elem.getAttribute(NAME);            
            if (fullMap.containsKey(name)) {                
                if (!rootMap.containsKey(name)) {
                    System.err.println(name + " (in " + ((Element) elem.getParentNode()).getAttribute(NAME) + 
                            ") refers to an element that is not on the root level!");
                    
                    Element referenced = fullMap.get(name);
                    rootMap.put(name, referenced);
                    worklist.add(referenced);
                }
            }
            else {
                System.err.println(name + " refers to an element that does not exist!");
            }
        }
        
        // work through the list, replace the problematic nodes with links 
        // and move actual nodes to the root level
        for (Element ref : worklist) {
            Element link = doc.createElement(INCLUDE_OBJECT);
            copyAttributes(ref, link, NAME, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
            removeAttributes(ref, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
            ref.getParentNode().replaceChild(link, ref);
            root.appendChild(ref);
        }
    }
    
    /**
     * Finds the next unused name, if there is number in the back it's
     * used
     *
     * @param name name to be examined
     * @param nameMap HashMap including all used names
     * @return next free name
     *
     * example: name="asdf2" and asdf2 and asdf3 are used
     * result asdf4
     */
    public static String findFreeName(String name, 
				      Map<String, Element> nameMap) {

        // separate name into basename and end digits
	int i = name.length() - 1;
	while (i >= 0 && Character.isDigit((name.charAt(i)))) i--;
	String basename = name.substring(0, i + 1);
	String digits = name.substring(i + 1);
	int n = digits.equals("") ? 0 : Integer.parseInt(digits);
        
        //go through names until free name is found
	String result;
        while (nameMap.containsKey(result = basename + n)) n++;
        return result;
    }

    /**
     * Checks if the document and nameMap are ok
     *
    public static boolean checkDocument(Document doc, 
					Map<String, Element> nameMap)  {
   
	// get all elements in the order they appear in the document
        NodeList elements = doc.getDocumentElement().getElementsByTagName("*");
        boolean ok = true;

	// iterate over all elements
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element element = (Element) elements.item(i);
            if (element.getNodeName().equals("include_object")) {
                if (!element.hasAttribute("name") || 
		    !nameMap.containsKey(element.getAttribute("name"))) {
                    System.out.println("Error, missing node: " + 
				       element.getAttribute("name"));
                    ok = false;
                }
	    }
	}
        return ok;
    }
    */

    /**
     * 
     * @param parent 
     */
    public static void removeEmptyTextNodes(Node parent) {
        //System.out.println(parent.getNodeName());
        NodeList nodeList = parent.getChildNodes();
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Node child = nodeList.item(i);
            int type = child.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(child); // recursion
            }
            else if (type == Node.TEXT_NODE) {
                String val = child.getNodeValue();
                if (val.trim().length() > 0) {
                    System.err.println("*** REMOVED NON-EMPTY TEXT NODE '" +
                            val + "' ***");
                }
                parent.removeChild(child);
            }
        }
    }
    
    /**
     * Parses document.
     * @param text
     * @param schema
     * @return
     */
    public static Document parseDocument(String text, String schema) {
        LSParser parser = 
            IMPL_LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
                                  "http://www.w3.org/2001/XMLSchema");
        
        DOMConfiguration config = parser.getDomConfig();
        config.setParameter("validate", Boolean.TRUE);                      
        config.setParameter("schema-type", "http://www.w3.org/2001/XMLSchema");
        config.setParameter("schema-location", schema);
        
        LSInput input = IMPL_LS.createLSInput();
        input.setStringData(text);
        Document doc = parser.parse(input);
        removeEmptyTextNodes(doc.getDocumentElement());
        return doc;
    }
    
    public static Document loadDocument(String name, String schema) {
        LSParser parser = 
            IMPL_LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
                                  "http://www.w3.org/2001/XMLSchema");
        
        DOMConfiguration config = parser.getDomConfig();
        config.setParameter("validate", Boolean.TRUE);                      
        config.setParameter("schema-type", "http://www.w3.org/2001/XMLSchema");
        config.setParameter("schema-location", schema);
            
        //config.setParameter("element-content-whitespace", Boolean.FALSE);
        
        //???? doesn't work
        //DOMErrorHandlerImpl errorHandler = new DOMErrorHandlerImpl();
        //config.setParameter("error-handler", errorHandler);

        //config.setParameter("validate", Boolean.TRUE);
        //config.setParameter("schema-type",
        //"http://www.w3.org/2001/XMLSchema");
        //config.setParameter("validate-if-schema", Boolean.TRUE);
        //config.setParameter("schema-location", "catalog.xsd");
        Document doc = parser.parseURI(name);
        removeEmptyTextNodes(doc.getDocumentElement());
        return doc;
    }
    
    public static void saveDocument(String name, Document doc) 
            throws FileNotFoundException, IOException
    {
        removeEmptyTextNodes(doc.getDocumentElement());
        
        LSSerializer dom3Writer = IMPL_LS.createLSSerializer();
        DOMConfiguration config = dom3Writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);

        OutputStream outputStream = new FileOutputStream(new File(name));
        LSOutput output = IMPL_LS.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(outputStream);
        dom3Writer.write(doc, output);
        outputStream.close(); // remember to close the output stream!
    }    
     
    /**
     * Exports the document using the XML format specified in ISOAgLib.
     * <ul>
     * <li> roles are converted back to attributes
     * <li> start and end angles are converted to "double degrees" (divided by two)
     * </ul>
     *
     * @param name
     * @param doc
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void exportToXML1(String name, Document doc) 
            throws FileNotFoundException, IOException {
        // the whole document is cloned (this copies all attributes too!)
        Document clone = (Document) doc.cloneNode(true);
        removeEmptyTextNodes(clone.getDocumentElement());
        
        removeRoles(clone.getDocumentElement());        
        divAngles(clone.getDocumentElement());
        
        // remove empty attributes? (at least file1 file4 file8... ?)
                
        LSSerializer dom3Writer = IMPL_LS.createLSSerializer();
        DOMConfiguration config = dom3Writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);

        OutputStream outputStream = new FileOutputStream(new File(name));
        LSOutput output = IMPL_LS.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(outputStream);
        dom3Writer.write(clone, output);
        outputStream.close(); // remember to close the output stream!
    }
    
    /**
     * Exports the document using a stand-alone XML format.
     * <ul>
     * <li> images are embedded into the XML using base64 encoding
     * <li> missing ids are generated for objects
     * <li> id attributes are added to include_objects
     * </ul>
     *
     * @param out
     * @param fileName
     * @param doc
     * @throws IOException
     * @throws PoolException
     */
    public static void exportToXML3(PrintStream out, String fileName, Document doc)
            throws IOException, PoolException {
        
        // the document is cloned (this copies all attributes too!)
        Document clone = (Document) doc.cloneNode(true);
        removeEmptyTextNodes(clone.getDocumentElement());
        
        // check the validity of document
        if (validateDocument(out, clone)) {
            out.println("Document valid");
        }
        else {
            out.println("Document invalid!");
        }
        
        divAngles(clone.getDocumentElement()); //This could be done?
        
        //createRoles(clone.getDocumentElement()); //this shouldn't be necessary !!!
        
        // find all picture elements
        NodeList nodes = clone.getElementsByTagName(PICTUREGRAPHIC); 
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            
            // get image and convert it to base64
            BufferedImage image = getImageFile(element, FILE, getStdBitmapPath(doc));
            
            // remove attributes that are no longer needed
            removeAttributes(element, FILE, FILE1, FILE4, FILE8, FORMAT, RLE);
            
            // remove all children
            NodeList childs = element.getChildNodes();
            for (int j = childs.getLength() - 1; j >= 0; j--) {
                element.removeChild(childs.item(j)); 
            }
            
            // create image data element
            Element dataElement = clone.createElement(IMAGE_DATA);
            
            // add image_width and image_height attributes
            dataElement.setAttribute(IMAGE_WIDTH, Integer.toString(image.getWidth()));
            dataElement.setAttribute(IMAGE_HEIGHT, Integer.toString(image.getHeight()));
            
            // set content
            dataElement.setTextContent(PictureConverter.convertToString(image));
            element.appendChild(dataElement);            
        }
        
        // set output string lengths
        NodeList outputstrings = clone.getElementsByTagName(OUTPUTSTRING); 
        for (int i = 0, n = outputstrings.getLength(); i < n; i++) {
            Element outputstring = (Element) outputstrings.item(i);
            String value = outputstring.getAttribute(VALUE);
            int valLen = value.length();
            String length = outputstring.getAttribute(LENGTH);
            
            // FIXME: what is this for?
            if (length.equals("")) {
                out.println(outputstring.getAttribute(NAME));
            }
            
            int len = 0;
            try {
                len = Integer.parseInt(length);
            }
            catch (NumberFormatException ex) { 
                // do nothing
            }
            if (valLen > len) {
                outputstring.setAttribute(LENGTH, Integer.toString(valLen));
            }
        }
        
        // set input string lengths
        NodeList inputstrings = clone.getElementsByTagName(INPUTSTRING); 
        for (int i = 0, n = inputstrings.getLength(); i < n; i++) {
            Element inputstring = (Element) inputstrings.item(i);
            String value = inputstring.getAttribute(VALUE);
            int valLen = value.length();
            String length = inputstring.getAttribute(LENGTH);
            int len = Integer.parseInt(length);
            if (valLen > len) {
                inputstring.setAttribute(LENGTH, Integer.toString(valLen));
            }
        }
                
        // create id to all objects
        nodes = clone.getElementsByTagName("*"); 
        TreeSet<Integer> usedIDs = new TreeSet<>();
        
        // find all ids and remove id from include_objects
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            String id = element.getAttribute(ID);
            if (!id.isEmpty()) {
                if (element.getNodeName().equals(INCLUDE_OBJECT)) {
                    element.removeAttribute(ID);
                    out.println("Removing ID " + id + " from " + element.getAttribute(NAME) +
                            " (include_object)");
                }                
                else {
                    Integer idn = Integer.valueOf(id);
                    if (idn == null || usedIDs.contains(idn)) {
                        element.removeAttribute(ID);
                        out.println("ID " + idn + " already in use. Removing id from " +
                                element.getAttribute(NAME) + " (" + element.getNodeName() + ")");   
                    }
                    else {
                        usedIDs.add(idn);
                        out.println("Reserving ID " + idn + " for " + element.getAttribute(NAME) + 
                                " (" + element.getNodeName() + ")"); 
                    }
                }
            }
        }
        
        // find all real elements with no id and them a unused id
        int nextMacroId = 0;
        int nextId = 256;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String id = element.getAttribute("id");
            // if a real isobus objects has no id, then create it one
            if (id.isEmpty() && Utils.equals(element.getNodeName(), OBJECTS)) {
                
                // macro ids are [0..255]
                if (element.getNodeName().equals(MACRO)) {
                    while (usedIDs.contains(nextMacroId)) nextMacroId++;
                    if (nextMacroId >= 256) {
                        throw new PoolException("Too many macros (>256)!");
                    }
                    element.setAttribute(ID, Integer.toString(nextMacroId));
                    usedIDs.add(nextMacroId);
                    out.println("Created ID " + nextMacroId + " for " + element.getAttribute(NAME) + 
                                " (" + element.getNodeName() + ")");
                }
                // other ids are [256..65535]
                else {
                    while (usedIDs.contains(nextId)) nextId++;
                    if (nextMacroId >= 65536) {
                        throw new PoolException("Too many objects!");
                    }
                    element.setAttribute(ID, Integer.toString(nextId));
                    usedIDs.add(nextId);
                    out.println("Created ID "+ nextId + " for " + element.getAttribute(NAME) + 
                                " (" + element.getNodeName() + ")");
                }
            }
        }
        
        // use name map to add right id to all include_objects
        Map<String, Element> nameMap = Tools.createNameMap(clone);
        nodes = clone.getElementsByTagName(INCLUDE_OBJECT);         
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            String name = element.getAttribute(NAME);
            if (nameMap.containsKey(name)) {
                element.setAttribute("id", nameMap.get(name).getAttribute("id"));
            } 
            else {
                out.println("ERROR: Can't find object: \"" + name + "\"");
            }            
        }
        
        // replace all block_font-attributes with block_font_size
        nodes = clone.getElementsByTagName("*");         
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            String blockFont = element.getAttribute(BLOCK_FONT);
            if (!blockFont.isEmpty()){
                if (nameMap.containsKey(blockFont)) {
                    Element font = nameMap.get(blockFont);
                    element.setAttribute(BLOCK_FONT_SIZE, font.getAttribute(FONT_SIZE));
                }
                element.removeAttribute(BLOCK_FONT);
            } 
            else {
                // if no block font then these are not needed
                element.removeAttribute(BLOCK_COL);
                element.removeAttribute(BLOCK_ROW);
                element.removeAttribute(BLOCK_FONT);
            }
        }
        
        nodes = clone.getElementsByTagName("*");
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            String name = element.getNodeName();
            if (Utils.equals(name, DATAMASK, ALARMMASK)) {
                markChildrenMask(out, element, nameMap);
            }
            // marking softkeymask is necessary as it can contain pointers as well as keys
            else if (Utils.equals(name, WORKINGSET, SOFTKEYMASK, KEY, AUXILIARYFUNCTION, AUXILIARYINPUT)) {
                markChildrenDesignator(out, element, nameMap);
            }
        }
        
        // FIXME: add an extra pass to find out unreferenced elements?
        
        /*
        // DIDN'T WORK TOO WELL, PROBLEMS WITH SPECIAL RELATIONS E.G. activemask, etc.
        // generate missing use attributes
        List<Element> list = new ArrayList<Element>();
        nodes = clone.getElementsByTagName("*");
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            String name = element.getNodeName();
            if (!equals(name, OBJECTS)) {
                continue;
            }
            list.clear();
            list.add(element); // if the element is e.g. working set its use should be designator
            findAncestorElements(element, list);
            boolean mask = containsElements(list, DATAMASK, ALARMMASK);
            boolean designator = containsElements(list, WORKINGSET, KEY, AUXILIARYFUNCTION, AUXILIARYINPUT);
 
            // special case: even if datamask is used in working set as "activemask"
            // it does not mean it is used inside a designator!
            if (equals(name, DATAMASK, ALARMMASK)) {
                designator = false;
            }
            // special case: even if softkeymask is used in datamask as "softkeymask"
            // it does not mean it is used inside a mask!
            if (equals(name, SOFTKEYMASK)) {
                mask = false;
            }
            if (mask && designator) {                
                if (equals(element.getAttribute(USE), "both")) {
                    System.out.println("WARNING: element \"" + element.getAttribute(NAME) + "\" (" + name +
                        ") use attribute is \"both\"!");
                }
                else {
                    System.err.println("ERROR: element \"" + element.getAttribute(NAME) + "\" (" + name + 
                        ") is used both in mask and designator!");
                }
                element.setAttribute(USE, "both");
            }
            else if (mask) {
                if (!equals(element.getAttribute(USE), "", "mask")) {
                    System.out.println("WARNING: element \"" + element.getAttribute(NAME) + "\" (" + name +
                        ") use attribute is overriden to \"mask\"!");
                }
                element.setAttribute(USE, "mask");
            }
            else if (designator) {
                if (!equals(element.getAttribute(USE), "", "designator")) {
                    System.out.println("WARNING: element \"" + element.getAttribute(NAME) + "\" (" + name +
                        ") use attribute is overriden to \"designator\"!");
                }
                element.setAttribute(USE, "designtor");
            }
            else {
                System.out.println("WARNING: element \"" + element.getAttribute(NAME) + "\" (" + name +
                        ") is not used in mask or designator (please specify \"mask\" or \"designator\" manually)!");
            }
        }
        */
        
        // write to file
        LSSerializer dom3Writer = IMPL_LS.createLSSerializer();
        DOMConfiguration config = dom3Writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);

        OutputStream outputStream = new FileOutputStream(new File(fileName));
        LSOutput output = IMPL_LS.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(outputStream);
        dom3Writer.write(clone, output);
        outputStream.close(); // remember to close the output stream!
    }
    
    private static void markChildrenMask(PrintStream out, Element element, Map<String, Element> nameMap) {
        String type = element.getNodeName();
        String name = element.getAttribute(NAME);
        String use = element.getAttribute(USE);
        if (Utils.equals(use, "designator", "both") &&
                !Utils.equals(type, FILLATTRIBUTES)) {
            out.println("ERROR: element \"" + name + "\" (" + type +
                    ") use attribute is now \"both\"!");
            element.setAttribute(USE, "both");
        }
        else {
            element.setAttribute(USE, "mask");
        }
        List<Element> children = getChildElementList(element);
        for (Element e : children) {
            // follow links
            if (Utils.equals(e.getNodeName(), INCLUDE_OBJECT)) {
                e = nameMap.get(e.getAttribute(NAME));
            }
            // skip if child is not a proper object or if
            // the element is data / alarmmask and the child is
            // softkeymask
            String childType = e.getNodeName();
            if (!Utils.equals(childType, OBJECTS) ||
                    (Utils.equals(type, DATAMASK, ALARMMASK) &&
                    Utils.equals(childType, SOFTKEYMASK))) {
                continue;
            }
            markChildrenMask(out, e, nameMap);
        }
    }
    
    private static void markChildrenDesignator(PrintStream out, Element element, Map<String, Element> nameMap) {
        String type = element.getNodeName();
        String name = element.getAttribute(NAME);
        String use = element.getAttribute(USE);
        if (Utils.equals(use, "mask", "both") &&
                !Utils.equals(type, FILLATTRIBUTES)) {
            out.println("ERROR: element \"" + name + "\" (" + type +
                    ") use attribute is now \"both\"!");
            element.setAttribute(USE, "both");
        }
        else {
            element.setAttribute(USE, "designator");
        }
        List<Element> children = getChildElementList(element);
        for (Element e : children) {
            // follow links
            if (Utils.equals(e.getNodeName(), INCLUDE_OBJECT)) {
                e = nameMap.get(e.getAttribute(NAME));
            }
            // skip if child is not a proper object or if
            // the element is workingset and the child is
            // data / alarmmask
            String childType = e.getNodeName();
            if (!Utils.equals(childType, OBJECTS) || 
                    (Utils.equals(type, WORKINGSET) &&
                    Utils.equals(childType, DATAMASK, ALARMMASK))) {
                continue;
            }
            markChildrenDesignator(out, e, nameMap);
        }
    }
    
    /**
     * Check the given list of elements for the specified element names.
     */
    private static boolean containsElements(List<Element> list, String ... names) {
        for (Element e : list) {
            if (Utils.equals(e.getNodeName(), names)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if all attributes, included objects are ok     
     * @param out
     * @param doc
     * @return 
     */
    public static boolean validateDocument(PrintStream out, Document doc) {
        ByteArrayOutputStream bff = new ByteArrayOutputStream();
        PrintStream tmp = new PrintStream(bff);
        boolean ok = true;
        Map<String, Element> nameMap = Tools.createNameMap( doc );
        
        // find all real isobus-elements
        NodeList elements = doc.getElementsByTagName("*"); 
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            Element element = (Element) elements.item(i);
            
            // validate element roles
            if (Utils.equals(element.getNodeName(), OBJECTS)) {
                if (!validateElementRoles(tmp, element, nameMap)) {
                    out.println(getPath(element) + " (" +  element.getNodeName() + ")");
                    out.print(bff);
                    ok = false;
                }
            }
        }
        // FIXME: check that the child objects are right?
        return ok;
    }
        
    public static String getPath(Element element) {
        String path = element.getAttribute(NAME);
        Node node = element;
        while ((node = node.getParentNode()) != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                path = ((Element) node).getAttribute(NAME) + "/" + path;
            }
            else {
                break;
            }
        }
        return path;
    }
    
    
    private static final String[] ELEMENTS_WITH_ROLES = 
            {DATAMASK, ALARMMASK, SOFTKEYMASK,
             NUMBERVARIABLE, STRINGVARIABLE, FONTATTRIBUTES, 
             LINEATTRIBUTES, FILLATTRIBUTES, INPUTATTRIBUTES};
    
    /**
     * Checks that the role attribute of the given element is appropriate
     * in the current context of the element. 
     *
     * If the element is a link, it is substituted with the actual object.
     * In principle, these kinds of check should be made using XML Schema, 
     * but linking complicates things.
     */
    private static boolean validateElementRoles(PrintStream out, 
            Element element, Map<String, Element> nameMap) {
        
        Element linkedElement = element;
        if (element.getNodeName().equals(INCLUDE_OBJECT)) {
            linkedElement = nameMap.get(element.getAttribute(NAME));
            if (linkedElement == null) {
                out.println("\tbroken link!");
                return false;
            }
        }
        
        String linkedElementName = linkedElement.getNodeName();
        Element parent = (Element) element.getParentNode();
                
        //check if elment is a attribute or variable element
                
        if (Utils.equals(linkedElementName, ELEMENTS_WITH_ROLES)) {
            
            String role = element.getAttribute(ROLE);  //role is in the link!!          
            
            // if the element is on the root level, it can't have a role            
            if (parent.getNodeName().equals(OBJECTPOOL)) {
                if (!Utils.equals(role, "", "none")) {
                    out.println("\tillegal role (" + role + ")!");
                    return false;
                }
            } 
            // if the element is under any other object, it must have the right role
            else { 
                if (Utils.equals(linkedElementName, DATAMASK, ALARMMASK)) {
                    if (!Utils.equals(role, ACTIVE_MASK)) {
                        out.println("\tillegal role! (" + role + ")!");
                        return false;
                    }
                }
                else if (Utils.equals(linkedElementName, SOFTKEYMASK)) {
                    if (!Utils.equals(role, SOFT_KEY_MASK)) {
                        out.println("\tillegal role! (" + role + ")!");
                        return false;
                    }
                }
                else if (linkedElementName.equals(NUMBERVARIABLE)) {
                    if (!Utils.equals(role, VARIABLE_REFERENCE, TARGET_VALUE_VARIABLE_REFERENCE)) {
                        out.println("\tillegal role! (" + role + ")!");
                        return false;
                    }
                }
                else if (linkedElementName.equals(STRINGVARIABLE)) {
                   if (!Utils.equals(role, VARIABLE_REFERENCE)) {
                        out.println("\tillegal role (" + role + ")!");
                        return false;
                    } 
                }
                else if (linkedElementName.equals(FONTATTRIBUTES)) {
                    if (!Utils.equals(role, FONT_ATTRIBUTES, FOREGROUND_COLOUR)) {
                        out.println("\tillegal role (" + role + ")!");
                        return false;
                    } 
                }
                else if (linkedElementName.equals(LINEATTRIBUTES)) {
                    if (!Utils.equals(role, LINE_ATTRIBUTES)) {
                        out.println("\tillegal role (" + role + ")!");
                        return false;
                    } 
                }
                else if (linkedElementName.equals(INPUTATTRIBUTES)) {
                    if (!Utils.equals(role, INPUT_ATTRIBUTES)) {
                        out.println("\tillegal role (" + role + ")!");
                        return false;
                    } 
                }                
            }
        }
        return true;
    }
    
    /**
     * Returns true, if some string s1 equals some of the strings in array ss
    private static boolean compareStrings(String s1, String[] ss) {
        for (int i = 0; i< ss.length; i++) {
            if (s1.equals(ss[i])) {
                return true;
            }
        }
        return false;
    }
     */
    
    private static String getFixBitmapPath(Document doc) {
        Element root = doc.getDocumentElement();
        return root.getAttribute(FIX_BITMAP_PATH);
    }
 
    private static String getStdBitmapPath(Document doc) {
        Element root = doc.getDocumentElement();
        return root.getAttribute(STD_BITMAP_PATH);
    }
    
    private static BufferedImage getImageFile(Element element, String attrib,
            String imagepath) throws IOException
    {
        String s = element.getAttribute(attrib);
        File f = null;
        if (s.isEmpty()) {
            return null;
        }
        try {
            String fullname = FileTools.joinPaths(imagepath, s);
            f = new File(fullname);
            return ImageIO.read(f);
        }
        catch (IOException ex) {
            throw new IOException(ex.getMessage() + " " + f);
        }
    }
    
    public static String writeToString(Node node) {
        if (node == null) { 
            return "null"; 
        }
        LSSerializer dom3Writer = IMPL_LS.createLSSerializer();
        DOMConfiguration config = dom3Writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);
        return dom3Writer.writeToString(node);
    }    
      
    public static String writeToStringNoDec(Node node) {
        if (node == null) { 
            return "null"; 
        }
        LSSerializer dom3Writer = IMPL_LS.createLSSerializer();
        DOMConfiguration config = dom3Writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);
        config.setParameter("xml-declaration", Boolean.FALSE);
        config.setParameter("discard-default-content", Boolean.FALSE);        
        return dom3Writer.writeToString(node);
    }        
    
    /*  OLD not recursion
    public static Element parseFragment(String text, Element otherElement, boolean asSibling) {
        LSParser parser = 
            IMPL_LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
                                  "http://www.w3.org/2001/XMLSchema");
                
        LSInput input = IMPL_LS.createLSInput();
        input.setStringData(text);
        Element child = parser.parse(input).getDocumentElement();    
        Document doc = otherElement.getOwnerDocument();
        Element adoptedChild = (Element) doc.adoptNode(child);

        //find father element
        Element father = otherElement;
        if (asSibling) {                  //FIXME What if otherElement is not a ISOBUS-object
            father = (Element) otherElement.getParentNode();
        }
        
        // father a real ISOBUS-object -> create a new link node and possibly a new actual node
        if (equals(father.getNodeName(), OBJECTS)) {  //yes
            System.out.println("ISOBUS OBJECT");
            // create new link-node with all link-attributes
            Element link = doc.createElement("include_object");
            //copyAttributes(adoptedChild, link, "name", "pos_x", "pos_y", "block_col", "block_row", "block_font");
            Tools.copyAndCreateAttributes(adoptedChild, link, new String[] {
                        "name", "pos_x", "pos_y", "block_col", "block_row", "block_font"},
                            new String[] {"", "0", "0", "0", "0", ""});
            
            // remove all link-attributes except name        
            removeAttributes(adoptedChild, "pos_x", "pos_y", "block_col", "block_row", "block_font");
            
            // add node to root if it doesn't exist
            Element root = (Element) doc.getFirstChild();
            System.out.println("Root: " + root);
            if (!elementExist(root, adoptedChild)) {
                System.out.println("Element does not exist");
                root.appendChild(adoptedChild);
            }
            
            // add link to given place
            if (asSibling) {
                father.insertBefore(link, otherElement);        
            }
            else {
                otherElement.appendChild(link);
            }
        }
        // father is not a real ISOBUS-object -> do not bother with the link, just remove all context info
        else {
            System.out.println("NOT A ISOBUS OBJECT");
            // remove all link-attributes except name        
            removeAttributes(adoptedChild, "pos_x", "pos_y", "block_col", "block_row", "block_font");
            // add node to given place
            if (asSibling) {
                father.insertBefore(adoptedChild, otherElement);        
            }
            else {
                otherElement.appendChild(adoptedChild);        
            }
        }        
        return adoptedChild;
    }
    */    

    /**
     * Parses a XML fragment which can contain objects
     * @param text other objects
     * @param doc
     * @return 
     */
    public static Element parseFragment(String text, Document doc) {
        LSParser parser = 
            IMPL_LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS,
                                  "http://www.w3.org/2001/XMLSchema");
                
        LSInput input = IMPL_LS.createLSInput();
        input.setStringData(text);
        Element child = parser.parse(input).getDocumentElement();   
        return (Element) doc.adoptNode(child);
    }
    
    public static boolean insertFragment(Element fragment, Element actual,
            Element link, boolean asSibling) {

        //System.out.println("insertFragment(fragment: " + fragment + ", actual: "
        //      + actual + ", link: " + link + ", asSibling: " + asSibling + ")");
        String trgName;
        if (asSibling) {
            Node parent;
            if (link != null) {
                trgName = link.getParentNode().getNodeName();
            }
            else {
                trgName = actual.getParentNode().getNodeName();
            }    
            /*
            else if (actual != null) {
                trgName = actual.getParentNode().getNodeName();
            }    
            else {                
                asSibling = false;
                actual = fragment.getOwnerDocument().getDocumentElement();
                trgName = actual.getNodeName();
            }
             */
        } 
        else {
            trgName = actual.getNodeName();
        }        
                 
        // if we have a working link, the name of the fragment is the name 
        // of the linked object, not "include object"
        String frgName = fragment.getNodeName();
        if (Utils.equals(frgName, INCLUDE_OBJECT)) {
            if (Utils.equals(trgName, OBJECTS)) {
                Map<String, Element> nameMap = Tools.createNameMap(fragment.getOwnerDocument());
                String name = fragment.getAttribute(NAME);
                if (!name.isEmpty()) {
                    Element elem = nameMap.get(name);
                    if (elem != null) {
                        frgName = elem.getNodeName();
                    }
                }
            }
            else {
                System.err.println("*** INSERTING A LINK TO THE ROOT IS NOT ALLOWED (see Tools.java) ***");
                return false;
            }
        }
        
        //System.out.println("insertFragment: trg: " + trgName + ", frg: " + frgName);
        
        //removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
        
        // objects with x-y-object references
        if (Utils.equals(trgName, WORKINGSET, DATAMASK, ALARMMASK, 
                CONTAINER, KEY, BUTTON, AUXILIARYFUNCTION, AUXILIARYINPUT) &&
                Utils.equals(frgName, CONTAINER, BUTTON, 
                INPUTBOOLEAN, INPUTSTRING, INPUTNUMBER, INPUTLIST, OUTPUTSTRING, 
                OUTPUTNUMBER, LINE, RECTANGLE, ELLIPSE, POLYGON, METER, 
                LINEARBARGRAPH, ARCHEDBARGRAPH, PICTUREGRAPHIC, OBJECTPOINTER)) {
            
            createMissingAttributes(fragment, 
                    new String[] {POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT},
                    new String[] {"0", "0", "0", "0", ""});
            
            removeAttributes(fragment, ROLE);
        }
        // objects with bare-object references
        else if ((Utils.equals(trgName, INPUTLIST) &&
                Utils.equals(frgName, CONTAINER, OUTPUTSTRING, OUTPUTNUMBER, 
                LINE, RECTANGLE, ELLIPSE, POLYGON, PICTUREGRAPHIC)) || 
                (Utils.equals(trgName, SOFTKEYMASK) &&
                Utils.equals(frgName, KEY, OBJECTPOINTER))) {
        
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE); 
        }
        // objects with active-mask-role references
        else if (Utils.equals(trgName, WORKINGSET) &&
                Utils.equals(frgName, DATAMASK, ALARMMASK)) {
            
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {ACTIVE_MASK});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
        }
        // objects with softkeymask-role references
        else if (Utils.equals(trgName, DATAMASK, ALARMMASK) &&
                Utils.equals(frgName, SOFTKEYMASK)) {
        
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {SOFT_KEY_MASK});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
        }
        // objects with foreground-colour-role references
        else if (Utils.equals(trgName, INPUTBOOLEAN) &&
                Utils.equals(frgName, FONTATTRIBUTES)) {
                
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {FOREGROUND_COLOUR});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
        }
        // objects with font-attributes-role references
        else if (Utils.equals(trgName, INPUTSTRING, INPUTNUMBER, OUTPUTSTRING, OUTPUTNUMBER) &&
                Utils.equals(frgName, FONTATTRIBUTES)) {
                
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {FONT_ATTRIBUTES});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
        }
        // objects with input-attributes-role references
        else if (Utils.equals(trgName, INPUTSTRING) &&
                Utils.equals(frgName, INPUTATTRIBUTES)) {
            
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {INPUT_ATTRIBUTES});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);   
        }
        // objects with variable-reference-role references (numbers)
        else if (Utils.equals(trgName, INPUTNUMBER, INPUTLIST, OUTPUTNUMBER, METER, LINEARBARGRAPH, ARCHEDBARGRAPH) &&
                Utils.equals(frgName, NUMBERVARIABLE)) {
            
            // could be also target value variable reference... (but the default is variable-reference,
            // there are no object types that have target value variable reference but no variable 
            // reference)
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {VARIABLE_REFERENCE}); 
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);         
        }
        // objects with variable-reference-role references (strings)
        else if (Utils.equals(trgName, INPUTSTRING, OUTPUTSTRING) &&
                Utils.equals(frgName, STRINGVARIABLE)) {
            
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {VARIABLE_REFERENCE});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // objects with line-attribute-role references
        else if (Utils.equals(trgName, LINE, RECTANGLE, ELLIPSE, POLYGON) &&
                Utils.equals(frgName, LINEATTRIBUTES)) {
        
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {LINE_ATTRIBUTES});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // objects with fill-attribute-role references
        else if (Utils.equals(trgName, RECTANGLE, ELLIPSE, POLYGON) &&
                Utils.equals(frgName, FILLATTRIBUTES)) {
        
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {FILL_ATTRIBUTES});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);         
        }
        // objects with value-role references (FIXME: could be more specific, e.g. not allow 
        // pointers to point to working sets, data masks, etc.)
        else if (Utils.equals(trgName, OBJECTPOINTER)) {
            
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {VALUE});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // objects with fill-pattern-role references
        else if (Utils.equals(trgName, FILLATTRIBUTES) &&
                Utils.equals(frgName, PICTUREGRAPHIC)) {
        
            createMissingAttributes(fragment, new String[] {ROLE}, new String[] {FILL_PATTERN});
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // polygon points (could allow block_font positioning, but who cares...)
        else if (Utils.equals(trgName, POLYGON) &&
                Utils.equals(frgName, POINT)) {
            
            createMissingAttributes(fragment, new String[] {POS_X, POS_Y}, new String[] {"0", "0"});
            removeAttributes(fragment, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);//, NAME); 
        }
        /*
        // for copying broken links(?!?)
        else if (Tools.equals(frgName, INCLUDE_OBJECT)) {
            
            // just in case!
            createMissingAttributes(fragment, 
                    new String[] {POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE},
                    new String[] {"0", "0", "0", "0", "", ""});
        }
         */
        // FIXME: could be much more specific: an object supports only certain macros
        else if (Utils.equals(frgName, MACRO)) {
        
            // defaults to the first possible role
            createMissingAttributes(fragment, new String[] {ROLE}, AttributeTable.findPossibleRoles(trgName, MACRO));
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // a macro is a list of commands
        else if (Utils.equals(trgName, MACRO) &&
                frgName.startsWith(COMMAND)) {
        
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT); 
        }
        // FIXME: could be much more specific: a command can have only certain targets
        else if (trgName.startsWith(COMMAND)) {
            createMissingAttributes(fragment, new String[] {ROLE}, AttributeTable.findPossibleRoles(trgName, frgName));
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
        }
        // root level objects have to be real isobus objects
        else if (Utils.equals(trgName, OBJECTPOOL) &&
                Utils.equals(frgName, OBJECTS)) {
            // && !Tools.equals(frgName, INCLUDE_OBJECT)) { // does not work as include_objects have already been replaced!
        
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE); 
        }
        // illegal insertion!
        else {
            System.err.println("*** INSERTING " + frgName + " TO " + trgName + " IS NOT ALLOWED (see Tools.java) ***");
            return false;
        }
        /*
        WORKINGSET, DATAMASK, ALARMMASK, CONTAINER, 
	SOFTKEYMASK, KEY, BUTTON, 
	INPUTBOOLEAN, INPUTSTRING, 
	INPUTNUMBER, INPUTLIST, 
	OUTPUTSTRING, OUTPUTNUMBER, 
	LINE, RECTANGLE, ELLIPSE, POLYGON,
	METER, LINEARBARGRAPH, ARCHEDBARGRAPH, PICTUREGRAPHIC,
	NUMBERVARIABLE, STRINGVARIABLE,
	FONTATTRIBUTES, LINEATTRIBUTES, FILLATTRIBUTES, INPUTATTRIBUTES,
	OBJECTPOINTER, MACRO, AUXILIARYFUNCTION, AUXILIARYINPUT
          */
        
        if (asSibling) {
            if (link != null) {
                link.getParentNode().insertBefore(fragment, link);
            }
            else {
                actual.getParentNode().insertBefore(fragment, actual);
            }
        } 
        // insert as child
        else {            
            actual.appendChild(fragment);
        }
        return true;
    }
    
    /**
     * Inserts a fragment at the specified position in the document.
    public static void insertFragment(Element fragment, Element otherElement, boolean asSibling) {
        
        // find father element
        Element father = otherElement;
        if (asSibling) {
            father = (Element) otherElement.getParentNode();
        }
        
        Document doc = otherElement.getOwnerDocument();
        // father a real ISOBUS-object -> create a new link node and possibly a new actual node
        if (equals(father.getNodeName(), OBJECTS)) {
            System.out.println("ISOBUS OBJECT");
            // create new link-node with all link-attributes
            Element link = doc.createElement(INCLUDE_OBJECT);
            //copyAttributes(adoptedChild, link, "name", "pos_x", "pos_y", "block_col", "block_row", "block_font");
            Tools.copyAndCreateAttributes(fragment, link, new String[] {
                        NAME, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT},
                            new String[] {"", "0", "0", "0", "0", ""});
            
            // remove all link-attributes except name        
            removeAttributes(fragment, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);
            
            // add node to root if it doesn't exist
            Element root = (Element) doc.getFirstChild();
            System.out.println("Root: " + root);
            if (!elementExist(root, fragment)) {
                System.out.println("Element does not exist");
                root.appendChild(fragment);
            }
            
            // add link to given place
            if (asSibling) {
                father.insertBefore(link, otherElement);        
            }
            else {
                otherElement.appendChild(link);
            }
        }
        // father is not a real ISOBUS-object -> do not bother with the link, just remove all context info
        else {
            System.out.println("NOT A ISOBUS OBJECT");
            // remove all link-attributes except name        
            removeAttributes(fragment, "pos_x", "pos_y", "block_col", "block_row", "block_font");
            // add node to given place
            if (asSibling) {
                father.insertBefore(fragment, otherElement);        
            }
            else {
                otherElement.appendChild(fragment);        
            }
        }        
        
        recursiveRemoveNesting(fragment, doc.getFirstChild());
    }
    */
    
    /**
     * Check for loops in the object pool.
     * @param fragment
     * @param actual
     * @param link
     * @param asSibling
     * @return
     * @throws pooledit.PoolException
     */
    public static boolean createsLoop(Element fragment, Element actual,
            Element link, boolean asSibling) throws PoolException {
        
        LinkedList<Element> worklist = new LinkedList<>();
        
        if (!fragment.getNodeName().equals(INCLUDE_OBJECT)) {
            throw new PoolException(fragment.getNodeName() + " is not include_object");
        }
        
        Map<String, Element> nameMap = createNameMap(actual.getOwnerDocument());
        String name = fragment.getAttribute(NAME);        
        Element target = nameMap.get(name);
        if (target == null) {
            throw new PoolException("\"" + name + "\" is not a valid name");
        }
        
        // Finding the parent node is quite complicated: is the include_object 
        // fragment is being inserted as a sibling to a link node, then the 
        // common parent is the parent of the link (not the actual object).
        Node parent;
        if (asSibling) {
            parent = (link != null) ? 
                link.getParentNode() :             
                actual.getParentNode();
        } 
        else {            
            parent = actual;
        }
        
        if (parent.getNodeType() == Element.ELEMENT_NODE &&
                Utils.equals(parent.getNodeName(), OBJECTS)) {
            
            worklist.addLast((Element) parent);
        }
        
        while (!worklist.isEmpty()) {
            Element element = worklist.removeFirst();
                       
            //System.out.println("CHECKING: " + element.getAttribute(NAME));
            // one of the parents is the target element -> loop
            if (element == target) {
                return true;
            }
            
            findParentElements(element, worklist);
        }
        //System.out.println("NO LOOPS FOUND!");
        return false;
    }
    
    /**
     * Finds all parents of the specified element and adds them to the given
     * list. An object can have one immediate parent (the enclosing element) 
     * and multiple non-immediate parents (elements that link to this object,
     * i.e. include objects).
     * @param elem
     * @param list
     */
    public static void findParentElements(Element elem, List<Element> list) {
        
        // look for real parent (the enclosing element)
        Node parent = elem.getParentNode();
        if (parent.getNodeType() == Element.ELEMENT_NODE &&
                Utils.equals(parent.getNodeName(), OBJECTS)) {
            
            list.add((Element) parent);
        }

        // look for objects that link to this element
        String name = elem.getAttribute(NAME);
        if (name.isEmpty()) {
            return;
        }

        // iterate over all elements in the document
        Document doc = elem.getOwnerDocument();
        NodeList nodes = doc.getElementsByTagName(INCLUDE_OBJECT);
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            // other objects can use include objects to make references
            if (element.getAttribute(NAME).equals(name)) {
                parent = element.getParentNode();
                if (parent.getNodeType() == Element.ELEMENT_NODE) {
                    
                    list.add((Element) parent);
                }
            }
        }
    }
    
    public static void findAncestorElements(Element element, List<Element> ancestors) {
        List<Element> worklist = new ArrayList<>();
        worklist.add(element);
        findAncestorElements(worklist, ancestors);
    }
    public static void findAncestorElements(List<Element> elements, List<Element> ancestors) {
        List<Element> worklist = new ArrayList<>();
        for (Element e : elements) {
            findParentElements(e, worklist);
            ancestors.addAll(worklist);
            findAncestorElements(worklist, ancestors);
        }
    }
        
    /**
     * Copies the specified attributes from the source element to the target 
     * element.
     * @param src
     * @param trg
     * @param attributes
     */
    static public void copyAttributes(Element src, Element trg, String ... attributes) {
         for (int i = 0, n = attributes.length; i < n; i++) { 
             if (src.hasAttribute(attributes[i])) {
                trg.setAttribute(attributes[i], src.getAttribute(attributes[i]));
             }
         }
    }  
    
    /**
     * Checks whether the target element can be replaced by a link to the 
     * source element. The source has to have every attribute the target has
     * except POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT and ROLE. The
     * values of the arguments must also match.
     *
     * @param trg
     * @param src
     * @return
     */
    static public boolean equalAttributes(Element trg, Element src) {
        NamedNodeMap attribs = trg.getAttributes();
        int nro = attribs.getLength();
        
        for (int i = 0; i < nro; i++) {
            Node attr = attribs.item(i);
            String name = attr.getNodeName();
            if (Utils.equals(name, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE)) {
                continue;
            }
            String trgval = attr.getNodeValue();
            String srcval = src.getAttribute(name);            
            if (!trgval.equals(srcval)) {
                System.err.println("attribute \"" + name + "\" has different value: " +
                        trgval + " <-> " + srcval);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks all children of the specified element. If at least one child has 
     * the same name-attribute than adoptedChild, returns true.
    private static boolean elementExist(Element element, Element adoptedChild) {
        String name = adoptedChild.getAttribute(NAME);
        NodeList childs = element.getChildNodes();
        for (int i = 0, n = childs.getLength(); i < n; i++) {
            Node node = childs.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getAttribute(NAME).equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
     */
    
    static private class Pair<A, B> {
        private final A parent;
        private final B child;
        Pair(A parent, B child) {
            this.parent = parent;
            this.child = child;
        }
        A getParent() { return (A) parent; }
        B getChild() { return (B) child; }
    }
    
    /**
     * XML2 -> XML1
     * 
     * Substitutes all include objects with role attribute with appropriate 
     * attributes in their parent elements. E.g.
     *
     * <inputnumber>
     *   <numbervariable name="speed" role="variable_reference"/>
     * </inputnumber>
     *
     * translates to
     *
     * <inputnumber variable_reference="speed"/>
     * ...
     * <numbervariable name="speed">
     *
     * @param root
     */
    public static void removeRoles(Element root) {
        // while iterating through the document, all the insertions of new 
        // elements are stored in a work list (inserting new elements while
        // iterating through the old ones, might yield unpredictable results)
        List<Pair<Element, Element>> worklist = new ArrayList<>();
        
        // iterate over all include objects
        NodeList elements = root.getElementsByTagName(INCLUDE_OBJECT); 
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String role = element.getAttribute(ROLE);
            if (!role.isEmpty()) {  //check that there is a role-attribute
                Element parent = (Element) element.getParentNode();
                String roleName = element.getAttribute(NAME);
                
                // add an attribute to the parent, e.g. font_attributes="font6x8"
                parent.setAttribute(role, roleName);
                
                // schedule the element for deletion
                worklist.add(new Pair<>(parent, element));
            }
        }
        for (Pair<Element, Element> p : worklist) {
            Element child = p.getChild();
            Element parent = p.getParent();
            
            // include objects are removed
            if (child.getNodeName().equals(INCLUDE_OBJECT)) {
                parent.removeChild(child);
            }
            // other objects are moved to the root
            else {
                child.removeAttribute(ROLE);
                root.appendChild(child);
            }
        }
    }
    
    /**
     * Divides all angle attributes by two (effectively changes the unit 
     * from 1 degree to 2 degrees). NOTE: it is very debatable whether any 
     * XML format should be using 2 degrees as angle unit. 
     */
    private static void divAngles(Element root) {
        String angleAttrs[] = {START_ANGLE, END_ANGLE};
        
        // iterate over all objects
        NodeList elements = root.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            
            // iterate over the specified attributes
            for (String attribute: angleAttrs) {
                if (!element.getAttribute(attribute).isEmpty()) {
                    int value = Integer.parseInt(element.getAttribute(attribute));
                    element.setAttribute(attribute, Integer.toString(value / 2));
                }
            }
        }
    }
    
    /**
     * XML1 -> XML2
     *
     * Substitutes all attributes that are actually object references with
     * include objects.This makes the resulting document easier to work with
     * as all references are include objects. Unlike attributes, the include
     * objects can also be replaced by actual objects.
     * @param root
     */
    public static void createRoles(Element root) {
        
        // while iterating through the document, all the insertions of new 
        // elements are stored in a work list (inserting new elements while
        // iterating through the old ones, might yield unpredictable results)
        List<Pair<Element, Element>> worklist = new ArrayList<>();
        
        // iterate over all elements and build work list
        NodeList elements = root.getElementsByTagName("*");      
        for (int i = 0, n = elements.getLength(); i < n; i++) {
            createRole((Element) elements.item(i), worklist);
        }

        // work through the list
        for (Pair<Element, Element> p : worklist) {
            p.getParent().insertBefore(p.getChild(), p.getParent().getFirstChild());
        }
    }  
    
    static void createRole(Element element, List<Pair<Element, Element>> worklist) {
        // attributes that are checked        
        final String attributes[] = {
            ACTIVE_MASK, SOFT_KEY_MASK, FONT_ATTRIBUTES, FOREGROUND_COLOUR, 
            INPUT_ATTRIBUTES, VARIABLE_REFERENCE, TARGET_VALUE_VARIABLE_REFERENCE,
            LINE_ATTRIBUTES, FILL_ATTRIBUTES, FILL_PATTERN
        };
        
        // iterate over all attributes
        Document doc = element.getOwnerDocument();
        for (int j = 0, m = attributes.length; j < m; j++) {
            String role = attributes[j];
            String roleName = element.getAttribute(role);
            
            // the attribute MUST BE REMOVED, or else the following
            // question must be answered:
            // - what happens if the attribute is changed and the current
            //   role object is embedded object (not just a link)?
            element.removeAttribute(role);
            
            if (!roleName.isEmpty()) {
                // creating a new role attribute
                Element includeObject = doc.createElement(INCLUDE_OBJECT);
                includeObject.setAttribute(ROLE, role);
                includeObject.setAttribute(NAME, roleName);
                worklist.add(new Pair<>(element, includeObject));
            }
        }
    
        // object pointer require special treatment
        if (element.getTagName().equals(OBJECTPOINTER) &&
                !element.getAttribute(VALUE).isEmpty()) { //element.hasAttribute(VALUE)) {
            
            String roleName = element.getAttribute(VALUE);
            element.removeAttribute(VALUE);
            Element includeObject = doc.createElement(INCLUDE_OBJECT);
            includeObject.setAttribute(ROLE, VALUE);
            includeObject.setAttribute(NAME, roleName);
            worklist.add(new Pair<>(element, includeObject));
        }        
    }     
    
    /**
     * Removes objects that are inside other objects (embedded elements) 
    public static void removeNesting(Document doc) {
        Tools.recursiveRemoveNesting(doc.getDocumentElement(), null);
    }
    
    private static void recursiveRemoveNesting(Element element, 
					       Node objectContainer) {

        if (element.getNodeName().equals(OBJECTPOOL)) {
            objectContainer = element;
	}

        Element child = Tools.getFirstChildElement(element);
        while (child != null) {
	    Element nextElement = Tools.getNextSiblingElement(child);

	    // valid tag name?
	    if (equals(child.getNodeName(), OBJECTS)) {
		Tools.recursiveRemoveNesting(child, objectContainer);
		if (element != objectContainer) {

		    // create include object element with proper
		    // attributes set
		    Element link = element.getOwnerDocument().createElement(INCLUDE_OBJECT);
                    
                    // this should be done automatically by createElement, but it is not?!?
		    Tools.copyAndCreateAttributes(child, link, new String[] {
                        NAME, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT},
                            new String[] {"", "0", "0", "0", "0", ""});

		    // replace the original element with the link
		    // element
		    element.replaceChild(link, child);

		    // remove now obsolete attributes
		    Tools.removeAttributes(child, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT);

		    // place the original at the end of the object
		    // container
		    objectContainer.appendChild(child);
		}
	    }
	    child = nextElement;      
	}
    }
     */
    
    /**
     * Gets the first child element.
     */
    private static Element getFirstChildElement(Element parent) {
        if (parent == null) {
            return null;
        }
	Node child = parent.getFirstChild();
	while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
	    child = child.getNextSibling();
	}
	return (Element) child;
    }

    /**
     * Gets the next sibling element, or null if there are none.
     * @param element
     * @return 
     */
    public static Element getNextSiblingElement(Element element) {
        if (element == null) {
            return null;
        }
	Node sibling = element.getNextSibling();
	while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
	    sibling = sibling.getNextSibling();
	}
	return (Element) sibling;
    }
    
    /**
     * Gets the previous sibling element, or null if there are none.
     * @param element
     * @return 
     */
    public static Element getPrevSiblingElement(Element element) {
        if (element == null) {
            return null;
        }
        Node sibling = element.getPreviousSibling();
        while (sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE) {
            sibling = sibling.getPreviousSibling();
        }
        return (Element) sibling;
    }
    
    /**
     * Gets a list of child elements for the specified parent
     * element.Node.getChildNodes() method returns all kinds of nodes. 
     * This method filters out only element nodes and returns them as a list.
     * @param parent
     * @return 
     */
    public static List<Element> getChildElementList(Element parent) {        
        NodeList children = parent.getChildNodes();
        int n = children.getLength();
        List<Element> elements = new ArrayList<>(n); // this list cannot have more than n elements
        for (int i = 0; i < n; i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) child);
            }
        }
        return elements;
    }
    
    /**
     * Removes the specified attributes.
     * @param node
     * @param names
     */
    public static void removeAttributes(Element node, String ... names) {
	for (int i = 0, n = names.length; i < n; i++) {
	    node.removeAttribute(names[i]);
	}	
    }
    
    /**
     * This method combines recursively element and it's linked or actual 
     * children in to a single composite object.
     * @param element
     * @param nameMap
     * @return 
     */
    public static Element createMergedElementRecursive(Element element, Map<String, Element> nameMap) {
        Document doc = element.getOwnerDocument();
        Element actual;
        Element link;
        
        // element is link object
        if (Utils.equals(element.getNodeName(), INCLUDE_OBJECT)) {
            // check for broken links
            actual = nameMap.get(element.getAttribute(NAME));
            link = element;
        }
        // element is actual object
        else {
            actual = element;
            link = null;
        }
        
        // create merged element and copy all attributes from link and actual node to it
        Element newelement = null;
        if (actual != null) {
            newelement = doc.createElement(actual.getNodeName());
            copyAllMissingAttributes(actual, newelement);
            if (link != null) {
                copyAllMissingAttributes(link, newelement);
            }
            // add included objects with recursion to merged element
            NodeList children = actual.getChildNodes();
            for (int i = 0, n = children.getLength(); i < n; i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element newchild = createMergedElementRecursive((Element) node, nameMap);
                    if (newchild != null) {
                        newelement.appendChild(newchild);
                    }
                }
            }
        }
        // broken link
        else if (link != null) {
            newelement = doc.createElement(link.getNodeName());
            copyAllMissingAttributes(link, newelement);
        }
        
        return newelement;
    }
    
    private static void copyAllMissingAttributes(Element from, Element to) {
        NamedNodeMap attrs = from.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; i++) {
	    Attr attr = (Attr) attrs.item(i);
            String name = attr.getName();
            String value = attr.getValue();
	    if (!to.hasAttribute(name)) {
		to.setAttribute(name, value);
	    }
	}
    }
    
    /**
     * Copies attributes from the source element to the target element.
     * If the source does not have the specified attribute, the specified
     * default value is used. The existing attributes in the target element
     * are overwritten.
     * @param src
     * @param trg
     * @param names
     * @param defaults
     */
    public static void copyAndCreateAttributes(Element src, Element trg, 
				       String[] names, String[] defaults) {
	for (int i = 0, n = names.length; i < n; i++) {
	    String name = names[i];
	    if (src.hasAttribute(name)) {
		trg.setAttribute(name, src.getAttribute(name));
	    }
            else {
                trg.setAttribute(name, defaults[i]);
            }
	}
    }
    
    /**
     * Creates the specified attributes with the specified default values.
     * The existing attributes are never overwritten.
     */
    private static void createMissingAttributes(Element to, String[] names,
            String[] defaults) {
        
        for (int i = 0, n = names.length; i < n; i++) {
            String name = names[i];
            if (!to.hasAttribute(name)) {
                to.setAttribute(name, defaults[i]);
            }
        }
    }
    
    /**
     * Tries to find the given element from a string
     * If it doesn't exist returns -1
     *
     * FIXME: this works only if names are unambiguous. Elements should be
     * searched by their paths. That would require the search function to
     * understand the structure of the xml file. 
     *
     * @param text
     * @param elementType
     * @param elementName
     * @return
     */
    public static int findElementFromString(String text, String elementType, String elementName) {
        int index = 0;
        while ((index = text.indexOf(elementType, index + 1)) != -1) {
            if (text.indexOf(elementName, index + 1) < text.indexOf(">", index + 1)) {
                return index - 1;
            }
        }
        return -1;
    }  

    public static int findElementEnd(String text, String elementType, int index) {
        int oldIndex = index;
        return ((index = text.indexOf(">", index)) != -1) ? index + 1 : oldIndex;
    }    
    
    /**
     * Searches for attribute-object that have an equalent object in the documents root 
     * and replaces it with a link (include_object)
     * Search begins from given element.
     * FIXME: can be removed?
    public static void optimize(Element parent) {
        String [] attributes = {FONTATTRIBUTES, LINEATTRIBUTES, FILLATTRIBUTES};
        Document doc = parent.getOwnerDocument();
        
        NodeList docElements = doc.getDocumentElement().getChildNodes( );
        
        //iterate all types of attribute-elements
        for (int i = 0; i < attributes.length; i++) {
            NodeList attributeElements = parent.getElementsByTagName(attributes[i]);            
            
            //iterate all child elements of the parent
            for (int j = 0; j < attributeElements.getLength(); j++)
                if (attributeElements.item(j).getNodeType() == Node.ELEMENT_NODE) {
                Element element1 = (Element) attributeElements.item(j);
                boolean equalIsFound = false;
                
                //iterate all elements in the root of the document
                for (int k = 0; k < docElements.getLength() && !equalIsFound; k++)
                    if (( docElements.item(k).getNodeType() == Node.ELEMENT_NODE ) &&
                        ( elementsAreEqual( element1, ( Element) docElements.item(k) ) )) {
                    
                        Element element2 = ( Element) docElements.item(k);
                    
                        //An equal attribute element is found, replace it with a link
                        equalIsFound = true;
                        String name = element1.getAttribute("name");
                        Element link = doc.createElement( "include_object" );
                        link.setAttribute("name", name);
                        Element realParent = (Element) element1.getParentNode();
                        
                        System.out.println("Found Equal attributes (parent: "+ realParent + ") : " + element1 + ", " + element2);
                        realParent.appendChild( link );
                        realParent.removeChild( element1 );
                        //realParent.replaceChild( element1, link);
                    }
                }
        }
    }
     */
    
    /**
     * Checks if two attribute-elments are equal. etc they are same type and have all the same attributes.
     * FIXME: can be removed?
    private static boolean elementsAreEqual( Element element1, Element element2) {
        //First check node name
        if ( !element1.getNodeName().equals( element2.getNodeName()) )
            return false;
        
        //compare all wanted attributes
        String attributes[] = {"name",                              //For All
            "font_colour", "font_size", "font_style", "font_type",  //font attributes
            "line_art", "line_colour", "line_width",                //line attributes
            "fill_colour", "fill_pattern", "fill_type"};            //fill attributes
        
        for (int i = 0; i < attributes.length; i++)
            if ( !element1.getAttribute( attributes[i] ).equals(element2.getAttribute( attributes[i] ) ))
                return false;     
        
        return true;
    }
     */
}
