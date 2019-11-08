/*
 * Copyright (C) 2007 Automation technology laboratory,
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
package wizard;

import static pooledit.Definitions.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import multidom.SingleDOM;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pooledit.Tools;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class WizardTools {
    
    /** 
     * Creates a new instance of WizardTools 
     */
    public WizardTools() {
    }
    
    public static void checkSpinnerLimits(JSpinner spinner, int min, int max) {
        SpinnerModel model = spinner.getModel();
        Integer value = (Integer) model.getValue();
        if (value < min) {
            model.setValue(min);
        }
        if (value > max) {
            model.setValue(max);
        }
    }
    
    public static Element createContainer(Element father, String name, int width, int height)  {
        Element container = createElement(CONTAINER, name, father, false);        
        setAttribute(container, WIDTH, width);
        setAttribute(container, HEIGHT, height);
        return container;        
    }   
    
    public static Element createString(Element father, String name, int width, int height, 
            String value, String fontAttribute)  {        
        
        Element str = createElement(OUTPUTSTRING, name, father, false);
        setAttribute(str, WIDTH, width);
        setAttribute(str, HEIGHT, height);
        setAttribute(str, VALUE, value);         
        setAttribute(str, HORIZONTAL_JUSTIFICATION, "middle");        
        setAttribute(str, OPTIONS, "transparent");
        setAttribute(str, BACKGROUND_COLOUR, "white");
        
        createIncludeRoleElement(fontAttribute, FONT_ATTRIBUTES, str);
        return str;
    }
        
    public static Element createString(Element father, String name, int width, int height, 
            String value, String fontAttribute, String justification)  {        
        
        Element str = createElement(OUTPUTSTRING, name, father, false);
        setAttribute(str, WIDTH, width);
        setAttribute(str, HEIGHT, height);
        setAttributeIfEmpty(str, VALUE, value);         
        setAttribute(str, HORIZONTAL_JUSTIFICATION, justification);        
        setAttribute(str, OPTIONS, "transparent");
        setAttribute(str, BACKGROUND_COLOUR, "white");
        
        createIncludeRoleElement(fontAttribute, FONT_ATTRIBUTES, str);
        return str;
    }
    
    public static Element createRectangle(Element father, String name, int width, int height, 
            String lineAttribute, String fillAttribute, boolean first)  {
        
        Element rectangle = createElement(RECTANGLE, name, father, first);        
        setAttribute(rectangle, WIDTH, width);
        setAttribute(rectangle, HEIGHT, height);
        createIncludeRoleElement(lineAttribute, LINE_ATTRIBUTES, rectangle);
        createIncludeRoleElement(fillAttribute, FILL_ATTRIBUTES, rectangle);
        return rectangle;        
    }   
    
    /**
     * Creates a new element to document and adds it as a child
     * if name is null a new name is created
     * if father is null, element will be added to root
     * if first is true the element is created as first child
     */
    public static Element createElement(String type, String name, Element father, boolean first) {
  
        // iterate ovet father's direct children to see if it already has
        // the specified child
        NodeList children = father.getChildNodes();
        for (int i = 0, n = children.getLength(); i < n; i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName() == type &&
                    elem.getAttribute(NAME).equals(name)) {
                    return elem;
                }            
            }
        }
        
        // if new element has to be created, we do it this way to the default 
        // attributes "free" from the schema
        Document tmp = Tools.parseDocument("<objectpool><" + type + "/></objectpool>", 
                SingleDOM.SCHEMA);
        Element elem = (Element) father.getOwnerDocument().adoptNode(tmp.getDocumentElement()).getFirstChild();
        elem.setAttribute(NAME, name);
        if (first) {
            father.insertBefore(elem, father.getFirstChild());
        }
        else {
            father.appendChild(elem);
        }
        return elem;        
    }    
    
    /**
     * Creates a new include object child element that points to an object 
     * called "name". Removes any other child elements that have the same role.
     * If name is null, no new element is created and the method returns null.
     */
    public static Element createIncludeRoleElement(String name, String role, Element father) {
        Element rv = null;
        List<Element> worklist = new ArrayList<Element>();
        NodeList children = father.getChildNodes();
        for (int i = 0, n = children.getLength(); i < n; i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getAttribute(ROLE).equals(role)) {
                    if (elem.getAttribute(NAME).equals(name)) {
                        rv = elem;
                    }
                    else {
                        worklist.add(elem);
                    }
                }
            }
        }
        for (Element elem : worklist) {
            father.removeChild(elem);
        }
        
        if (rv == null && name != null) {
            // if new element has to be created, we do it this way to the default 
            // attributes "free" from the schema
            Document tmp = Tools.parseDocument("<objectpool><include_object/></objectpool>", 
                    SingleDOM.SCHEMA);
            rv = (Element) father.getOwnerDocument().adoptNode(tmp.getDocumentElement()).getFirstChild();
            rv.setAttribute(NAME, name);
            rv.setAttribute(ROLE, role);
            father.appendChild(rv);       
        }
        return rv;
    }

    public static void removeExtraElements(Element father, String name, int start) {
        NodeList children = father.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String fullname = elem.getAttribute(NAME);
                if (fullname.startsWith(name)) {
                    try {
                        String end = fullname.substring(name.length());
                        int nro = Integer.parseInt(end);
                        if (nro >= start) {
                            father.removeChild(elem);
                        }
                    }
                    catch (NumberFormatException e) {}
                }
            }
        }
    }
    
    /**
     * Sets the element's attribute only if it has been changed.
     */
    public static void setAttribute(Element elem, String name, Object value) {
        String val = value == null ? null : value.toString();
        if (!elem.getAttribute(name).equals(val)) {
            elem.setAttribute(name, val);            
        }
    }
    
    /**
     * Sets the element's attribute only if it does not exist.
     */
    public static void setAttributeIfMissing(Element elem, String name, Object value) {
        if (!elem.hasAttribute(name)) {
            setAttribute(elem, name, value);
        }
    }
    
    public static void setAttributeIfEmpty(Element elem, String name, Object value) {
        if (elem.getAttribute(name).isEmpty()) {
            setAttribute(elem, name, value);
        }
    }
    
    public static void setIncludeAttributes(Element element, int posX, int posY){
        setAttribute(element, POS_X, Integer.toString(posX));
        setAttribute(element, POS_Y, Integer.toString(posY));
    } 
    
    /**
     * Finds all elements of the given type *at the root level* and returns 
     * their names.
     */    
    public static List<String> findElementNames(Element root, String tagname) {
        List<String> names = new ArrayList<String>();
        NodeList children = root.getChildNodes();
        for (int i = 0, n = children.getLength(); i < n; i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {                
                Element elem = (Element) node;
                if (elem.getNodeName().equals(tagname)) {
                    names.add(elem.getAttribute(NAME));
                }
            }
        }
        return names;
    }
    
    public static Object[] findElements(Element root, String tagname) {
        List<String> names = findElementNames(root, tagname);
        return names.toArray();
    }
    
    /**
     * The same as findElements but includes a null element at the beginning 
     * of the array.
     */
    public static Object[] findElementsWithEmpty(Element root, String tagname) {
        List<String> names = findElementNames(root, tagname);
        names.add(0, null);
        return names.toArray();
    }
}
