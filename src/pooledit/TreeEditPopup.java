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

import color.ColorPalette;
import static pooledit.Definitions.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public abstract class TreeEditPopup {

    static private final int 
            FLIP_VERTICAL = 0,  
            FLIP_HORIZONTAL = 1,  
            ROTATE_90_CW = 2,  
            ROTATE_90_CCW = 3,  
            ROTATE_180 = 4;
    
    private final JPopupMenu popup;

    public abstract TreePath getCurrentPath();

    public abstract XMLTreeModel getXMLTreeModel();

    public TreeEditPopup() {
        popup = createPopupMenu();
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu pup = new JPopupMenu();

        JMenuItem uniqueItem = new JMenuItem("Make Unique (Deep)");
        uniqueItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();
                if (node.isType(OBJECTS)) {
                    Element actual = node.actual();
                    Element link = node.link();
                    Map<String, Element> nameMap = node.getModel().getNameMap();
                    Element merged = Tools.createMergedElementRecursive(actual, nameMap);

                    // setup unique name
                    String name = merged.getAttribute(NAME);
                    String newname = Tools.findFreeName(name, nameMap);
                    merged.setAttribute(NAME, newname);

                    // if node is a link node, then the link is replaced, otherwise the
                    // actual object is replaced
                    Element target = (link != null) ? link : actual;
                    Tools.copyAttributes(target, merged, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
                    target.getParentNode().replaceChild(merged, target);
                }
            }
        });
        /*        
        //FIXME this works only for links
        Document doc = node.getModel().getDocument();
        Element actual = node.actual();
        Element link = node.link();                
        if (link != null) {                   
        Element root = doc.getDocumentElement();
        String name = actual.getAttribute(NAME);
        // count all include_object-elements that link to element
        NodeList nodes = doc.getElementsByTagName(INCLUDE_OBJECT);
        int links = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
        if (((Element) nodes.item(i)).getAttribute(NAME).equals(name)) {
        links++;
        }
        }
        System.out.println(getClass().getName() + " Make Unique: links: " + links);
        if (links <= 1) {
        return; // already unique!
        }
        //clone the element, rename it and add to root     FIXME attributes won't copy                               
        Element clone = (Element) doc.importNode(actual, true);
        root.appendChild(clone);
        String newname = Tools.findFreeName(name, getXMLTreeModel().getNameMap());
        clone.setAttribute(NAME, newname);   
        link.setAttribute(NAME, newname);
        }
        // not a link
        else {
        //Check if there is a object with same name (not include_object)    FIXME not implemented yet
        // FIXME: how does this work? is this the same as "make duplicate", 
        // but only makes a duplicate, if there are referenes to this object?
        if (!actual.hasAttribute(NAME)) {
        JOptionPane.showMessageDialog(null, 
        "The selected object does not have a name!", 
        "Make Unique Error", JOptionPane.ERROR_MESSAGE);
        return;
        }
        String name = Tools.findFreeName( actual.getAttribute("name"), getXMLTreeModel().getNameMap());
        actual.setAttribute("name", name);
        }
        }                      
        });
         */
        pup.add(uniqueItem);

        JMenuItem duplicateItem = new JMenuItem("Make Duplicate (Deep)");
        duplicateItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();
                Element actual = node.actual();
                Element link = node.link();

                Element target = (link != null) ? link : actual;
                //clone the link and add to father    FIXME attributes won't copy (?)
                Element duplicate = (Element) target.cloneNode(true);
                //Tools.copyAllMissingAttributes(link, duplicate);

                // links can have identical names, but actual object should
                // prefer to have (globally) unique names
                if (link == null && node.isType(OBJECTS)) {
                    // setup unique name
                    String name = duplicate.getAttribute(NAME);
                    String newname = Tools.findFreeName(name, Tools.createNameMap(node.getModel().getDocument(), true));
                    // node.getModel().getNameMap());
                    duplicate.setAttribute(NAME, newname);
                }
                target.getParentNode().insertBefore(duplicate, target.getNextSibling());
            }
        });
        pup.add(duplicateItem);

        JMenuItem linkableItem = new JMenuItem("Make Linkable");
        linkableItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();

                if (node.link() != null) {
                    JOptionPane.showMessageDialog(null,
                            "Links cannot be made linkable!",
                            "Make Linkable Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!node.isType(OBJECTS)) {
                    JOptionPane.showMessageDialog(null,
                            "The selected object (" + node.getType() +
                            ") cannot be made linkable!",
                            "Make Linkable Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Element actual = node.actual();
                XMLTreeModel model = node.getModel();
                Map<String, Element> nameMap = model.getNameMap();
                Document doc = model.getDocument();
                Element root = doc.getDocumentElement();
                Node parent = actual.getParentNode();

                if (root == parent) {
                    JOptionPane.showMessageDialog(null,
                            "The selected object is already linkable!",
                            "Make Linkable Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // create link
                Element link = doc.createElement(INCLUDE_OBJECT);

                // setup unique names
                String name = actual.getAttribute(NAME);
                if (name.isEmpty()) {
                    name = actual.getNodeName();
                }
                if (nameMap.containsKey(name)) {
                    String newname = Tools.findFreeName(name, nameMap);
                    actual.setAttribute(NAME, newname);
                }
                Tools.copyAttributes(actual, link, NAME, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);

                // replace actual node with the link and put the actual
                // object in root
                Node mark = actual.getNextSibling();
                root.appendChild(actual);
                parent.insertBefore(link, mark);

                // remove extra attributes
                Tools.removeAttributes(actual, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
            }
        });
        pup.add(linkableItem);

        pup.addSeparator();

        JMenuItem normalizeItem = new JMenuItem("Normalize Object");
        normalizeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();

                // normalize width and height input and output strings and 
                // numbers
                if (node.isType(OUTPUTSTRING, INPUTSTRING, OUTPUTNUMBER, INPUTNUMBER)) {
                    String value;
                    if (node.isType(OUTPUTSTRING, INPUTSTRING)) {
                        value = node.getValue();
                    } else {
                        value = node.getFormatedNumber();
                    }
                    int length = value.length();
                    // we do not really care about the color palette (color reduction etc)
                    Dimension fontdim = node.getFontAttributes().getFont(ColorPalette.COLOR_8BIT).getDimension();
                    int width = fontdim.width * length;
                    int height = fontdim.height;
                    Element element = node.actual();
                    //element.setAttribute(LENGTH, Integer.toString(length)); // not for numbers!

                    // IDEA: depending on the horizontal justification, we could use delta width to
                    // properly align the object?

                    element.setAttribute(WIDTH, Integer.toString(width));
                    element.setAttribute(HEIGHT, Integer.toString(height));
                } // normalize width and height of an input list to match the
                // maximum dimensions of its children
                else if (node.isType(INPUTLIST)) {
                    int maxw = 0;
                    int maxh = 0;
                    Dimension size = new Dimension();
                    XMLTreeModel model = node.getModel();
                    for (int i = 0,  n = model.getChildCount(node); i < n; i++) {
                        XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
                        nd.getNodeSize(size);
                        maxw = Math.max(maxw, size.width);
                        maxh = Math.max(maxh, size.height);
                    }
                    Element element = node.actual();
                    element.setAttribute(WIDTH, Integer.toString(maxw));
                    element.setAttribute(HEIGHT, Integer.toString(maxh));
                } // normalize width and height of container, button and polygon objects
                else if (node.isType(CONTAINER, BUTTON, POLYGON)) {
                    int minx = 0x7FFF; // FIXME: magic number
                    int miny = 0x7FFF;
                    int maxx = 0;
                    int maxy = 0;
                    Dimension size = new Dimension();
                    XMLTreeModel model = node.getModel();
                    for (int i = 0,  n = model.getChildCount(node); i < n; i++) {
                        XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
                        nd.getNodeSize(size);
                        Integer x = nd.getX();
                        if (x != null) {
                            minx = Math.min(minx, x);
                            maxx = Math.max(maxx, x + size.width);
                        }
                        Integer y = nd.getY();
                        if (y != null) {
                            miny = Math.min(miny, y);
                            maxy = Math.max(maxy, y + size.height);
                        }
                    }
                    // leave room for button borders
                    if (node.isType(BUTTON)) {
                        maxx += 8;
                        maxy += 8;
                    }

                    // abjust position
                    Element pos = node.link() != null ? node.link() : node.actual();
                    if (pos != null) {
                        changeAttribute(pos, "pos_x", minx);
                        changeAttribute(pos, "pos_y", miny);
                    }

                    // adjust object
                    Element element = node.actual();
                    element.setAttribute(WIDTH, Integer.toString(maxx - minx));
                    element.setAttribute(HEIGHT, Integer.toString(maxy - miny));

                    // adjust children
                    for (int i = 0,  n = model.getChildCount(node); i < n; i++) {
                        XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
                        Element child = nd.link() != null ? nd.link() : nd.actual();
                        if (minx > 0 && child.hasAttribute(POS_X)) {
                            child.setAttribute(POS_X, Integer.toString(Integer.parseInt(child.getAttribute(POS_X)) - minx));
                        }
                        if (miny > 0 && child.hasAttribute(POS_Y)) {
                            child.setAttribute(POS_Y, Integer.toString(Integer.parseInt(child.getAttribute(POS_Y)) - miny));
                        }
                    }
                } else if (node.isType(PICTUREGRAPHIC)) {
                    try {
                        BufferedImage image = node.getImageFile(); // for size calculation only
                        int picW = node.getWidth();
                        int imageW = image.getWidth();
                        if (picW != image.getWidth()) {
                            Element actual = node.actual();
                            actual.setAttribute(WIDTH, Integer.toString(imageW));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        pup.add(normalizeItem);

        JMenuItem renameItem = new JMenuItem("Rename Object");
        renameItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode child = (XMLTreeNode) getCurrentPath().getLastPathComponent();
                Element actual = child.actual();                
                if (!actual.hasAttribute(NAME)) {
                    JOptionPane.showMessageDialog(null,
                            "The selected object does not have a name!",
                            "Rename Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String oldName = actual.getAttribute(NAME);
                String newName = (String) JOptionPane.showInputDialog(null /*parent*/,
                        "Please give a new name", "Rename Object",
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, oldName);
                        
                try {
                    renameObject(child, newName);
                } 
                catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(null,
                            "The specified name already exists!",
                            "Rename Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pup.add(renameItem);

        JMenuItem deleteItem = new JMenuItem("Delete Object");
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();
                Element actual = node.actual();
                Element link = node.link();

                // actual object (or broken link)
                if (link == null || node.isType(INCLUDE_OBJECT)) {
                    actual.getParentNode().removeChild(actual);
                } // working link (link != null)
                else {
                    link.getParentNode().removeChild(link);
                }
            }
        });
        pup.add(deleteItem);

        JMenuItem optimizeItem = new JMenuItem("Optimize Object");
        optimizeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                XMLTreeNode node = (XMLTreeNode) getCurrentPath().getLastPathComponent();
                Element actual = node.actual();
                Element link = node.link();

                //XMLTreeModel model = node.getModel();
                //Element candidate = model.getElementByName(node.getName());

                Map<String, Element> nameMap = Tools.createNameMap(actual.getOwnerDocument());
                Element candidate = nameMap.get(actual.getAttribute(NAME));
                optimize(actual, candidate, nameMap);

            //Tools.optimize( actual );
            }
        });
        pup.add(optimizeItem);

        pup.addSeparator();

        JMenuItem bringFrontItem = new JMenuItem("Bring to Front");
        bringFrontItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrontBack((XMLTreeNode) getCurrentPath().getLastPathComponent(), true, false);
            }
        });
        pup.add(bringFrontItem);

        JMenuItem bringForwardItem = new JMenuItem("Bring Forward");
        bringForwardItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrontBack((XMLTreeNode) getCurrentPath().getLastPathComponent(), true, true);
            }
        });
        pup.add(bringForwardItem);

        JMenuItem sendBackwardItem = new JMenuItem("Send Backward");
        sendBackwardItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrontBack((XMLTreeNode) getCurrentPath().getLastPathComponent(), false, true);
            }
        });
        pup.add(sendBackwardItem);

        JMenuItem sendBackItem = new JMenuItem("Send to Back");
        sendBackItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveFrontBack((XMLTreeNode) getCurrentPath().getLastPathComponent(), false, false);
            }
        });
        pup.add(sendBackItem);

        pup.addSeparator();

        JMenuItem mirrorVerticalItem = new JMenuItem("Flip Vertical ( | )");
        mirrorVerticalItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                geometricTransformation((XMLTreeNode) getCurrentPath().getLastPathComponent(), FLIP_VERTICAL);
            }
        });
        pup.add(mirrorVerticalItem);

        JMenuItem mirrorHorizontalItem = new JMenuItem("Flip Horizontal (-)");
        mirrorHorizontalItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                geometricTransformation((XMLTreeNode) getCurrentPath().getLastPathComponent(), FLIP_HORIZONTAL);
            }
        });
        pup.add(mirrorHorizontalItem);

        JMenuItem rotateClockwiseItem = new JMenuItem("Rotate 90\u00B0 CW");
        rotateClockwiseItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                geometricTransformation((XMLTreeNode) getCurrentPath().getLastPathComponent(), ROTATE_90_CW);
            }
        });
        pup.add(rotateClockwiseItem);

        JMenuItem rotateAntiClockwiseItem = new JMenuItem("Rotate 90\u00B0 CCW");
        rotateAntiClockwiseItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                geometricTransformation((XMLTreeNode) getCurrentPath().getLastPathComponent(), ROTATE_90_CCW);
            }
        });
        pup.add(rotateAntiClockwiseItem);

        JMenuItem rotateMoreItem = new JMenuItem("Rotate 180\u00B0");
        rotateMoreItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                geometricTransformation((XMLTreeNode) getCurrentPath().getLastPathComponent(), ROTATE_180);
            }
        });
        pup.add(rotateMoreItem);

        //pup.addSeparator();
                /*
        TreePath path = getCurrentPath();
        if (path != null && path.getPathCount() > 1) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();                               
        Element fatherElement = ((XMLTreeNode) path.getPathComponent(path.getPathCount()-2)).actual();
        // go trough all childs and find the child
        NodeList childElements = fatherElement.getChildNodes();
        for (int i = childElements.getLength() - 1; i >= 0; i--) {    // last first
        Node child = childElements.item(i);
        // assumes linearized document and unique link names 
        // which is not true!
        if (child.getNodeName().equals("include_object") && 
        ((Element)child).getAttribute("name").equals(node.getName())) {
        fatherElement.appendChild(child);
        }
        }         
        }
        }
        });
        pup.add(moveFrontItem);       
        JMenuItem moveBackItem = new JMenuItem("Move to Back");
        moveBackItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        System.out.println("Moving object to back");
        TreePath path = getCurrentPath();
        if (path != null && path.getPathCount() > 1) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();                               
        Element fatherElement = ((XMLTreeNode) path.getPathComponent(path.getPathCount()-2)).actual();
        NodeList childElements = fatherElement.getChildNodes();
        //go trough all childs and find the child
        for (int i = 0; i < childElements.getLength(); i++) {    // first first
        Node child = childElements.item(i);
        if (child.getNodeName().equals("include_object") && ((Element)child).getAttribute("name").equals(node.getName())) {
        fatherElement.insertBefore(child, childElements.item(0));  //insert first
        }
        }                   
        }
        }
        });
        pup.add(moveBackItem);
        pup.addSeparator();
         */
        /*
        JMenuItem meterWizard = new JMenuItem("Start Meter Wizard");
        meterWizard.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        // FIXME: it might be a good idea to have only one meter wizard 
        // in the program?
        // FIXME: it might also be better to put it in the menu 
        // instead?
        JFrame wizardFrame = new JFrame("Meter Wizard");
        MeterWizard wizard = new MeterWizard();
        TreePath path = getCurrentPath();
        // father element should not be category node!
        // Element fatherElement = ((XMLTreeNode) path.getPathComponent(path.getPathCount()-2)).actual();
        Element fatherElement = ((XMLTreeNode) path.getPathComponent(0)).actual(); // root
        MeterGenerator generator = new MeterGenerator(wizard, fatherElement.getDocument(), getXMLTreeModel());
        wizardFrame.getContentPane().add(wizard);
        wizardFrame.pack();
        wizardFrame.setVisible(true);
        }
        });
        pup.add(meterWizard);
         */

        return pup;
    }

    /**
     * Renames the specified object.
     * FIXME: maybe this should be in Tools.java?
     * @param child
     * @param newName
     */
    static public void renameObject(XMLTreeNode child, String newName) {
        
        // user has not entered a name
        if (newName == null) {
            return;
        }
        
        Element actual = child.actual();
        if (actual == null || !actual.hasAttribute(NAME)) {            
            throw new IllegalArgumentException("the specified node has no name");
        }
               
        // the new name is the same as the old name
        String oldName = actual.getAttribute(NAME);
        if  (newName.equals(oldName)) {
            return;
        }
        
        // the name already exists
        // if (getXMLTreeModel().getNameMap().containsKey(newName)) {
        if (child.getModel().getNameMap().containsKey(newName)) {
            throw new IllegalStateException("the name already exists");
        }

        System.out.println("Renaming, old name: " + oldName + " new name: " + newName);
        // System.err.println("RENAME NEEDS A LOT OF FIXING!");
        // NOTE: block_font must also be changed even though it is
        // not a "real" attribute
        //String attributes[] = {NAME, VARIABLE_REFERENCE,
        //FONT_ATTRIBUTES, LINE_ATTRIBUTES, FILL_ATTRIBUTES,
        //BLOCK_FONT};

        // FIXME: this code assumes that the names are unique, e.g.
        // if there are two actual objects with the same name and
        // the user wants to rename one of them, both names will change!

        Document doc = child.getModel().getDocument();
        Element link = child.link();
        
        // working link
        if (link != null) {

            // rename possible actual object in root
            // NOTE: this may temporarily break a lot of links...
            //System.out.println("CHANGING ROOT LEVEL OBJECT NAMES");
            NodeList nodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0,  n = nodes.getLength(); i < n; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;
                    if (elem.getAttribute(NAME).equals(oldName)) {
                        elem.setAttribute(NAME, newName);
                    }
                }
            }

            // rename possible links elsewhere in the document
            //System.out.println("CHANGING LINK NAMES");
            NodeList elements = doc.getElementsByTagName("*");
            for (int i = 0,  n = elements.getLength(); i < n; i++) {
                Element elem = (Element) elements.item(i);
                if (elem.getNodeName().equals(INCLUDE_OBJECT)) {
                    if (elem.getAttribute(NAME).equals(oldName)) {
                        elem.setAttribute(NAME, newName);
                    }
                }
            }
        } // actual object or broken link
        else if (actual != null) {
            
            // NOTE: this may temporarily break a lot of links...
            actual.setAttribute(NAME, newName);

            // if the object is at the root level, update all links to it
            if (actual.getParentNode().equals(doc.getDocumentElement())) {
                NodeList elements = doc.getElementsByTagName("*");
                for (int i = 0,  n = elements.getLength(); i < n; i++) {
                    Element elem = (Element) elements.item(i);
                    if (elem.getNodeName().equals(INCLUDE_OBJECT)) {
                        if (elem.getAttribute(NAME).equals(oldName)) {
                            elem.setAttribute(NAME, newName);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adapted from mouse controller.
     * @param element
     * @param attr
     * @param diff
     */
    static public void changeAttribute(Element element, String attr, int diff) {
        String val = element.getAttribute(attr);
        if (!val.isEmpty()) {
            int p = Integer.parseInt(val);
            if (diff != 0) {
                element.setAttribute(attr, Integer.toString(p + diff));
            }
        }
    }

    /**
     * Moves the selected node either as the last element (front) or the 
     * first element (back). 
     * @param node
     * @param front
     * @param one
     */
    static public void moveFrontBack(XMLTreeNode node, boolean front, boolean one) {
        Element elem = node.link() != null ? node.link() : node.actual();

        if (elem == null) {
            JOptionPane.showMessageDialog(null,
                    "The selected object is not in the document!",
                    "Move Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Node parentNode = elem.getParentNode();
        if (parentNode == null || parentNode.getNodeType() != Node.ELEMENT_NODE) {
            JOptionPane.showMessageDialog(null,
                    "The selected object's parent is not document element!",
                    "Move Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // one step
        if (one) {
            if (front) {
                Element next = Tools.getNextSiblingElement(elem);
                Element next2 = Tools.getNextSiblingElement(next);
                parentNode.insertBefore(elem, next2);
            } else {
                Element prev = Tools.getPrevSiblingElement(elem);
                if (prev != null) {
                    parentNode.insertBefore(elem, prev);
                }
            }
        } // all the way
        else {
            if (front) {
                parentNode.appendChild(elem);
            } else {
                parentNode.insertBefore(elem, parentNode.getFirstChild());
            }
        }
    }

    /**
     * NOTE: if node points to the same object as candidate, then this method
     * does nothing and returns true.
     * @param actual
     * @param candidate
     * @param nameMap
     * @return 
     */
    public static boolean optimize(Element actual, Element candidate,
            Map<String, Element> nameMap) {

        List<Element> alist = Tools.getChildElementList(actual);
        int n = alist.size();

        boolean isOptimized = true;
        for (int i = 0; i < n; i++) {
            Element nd = alist.get(i);
            Element md = nameMap.get(nd.getAttribute(NAME));
            if (!optimize(nd, md, nameMap)) {
                isOptimized = false;
            }
        }

        String aname = actual.getAttribute(NAME);
        if (candidate == null) {
            System.err.println("no candidate for: " + aname);
            return false;
        }

        String cname = actual.getAttribute(NAME);
        if (!Tools.equalAttributes(actual, candidate)) {
            System.err.println("attributes do not match: " + aname + " <-> " + cname);
            return false;
        }


        List<Element> clist = Tools.getChildElementList(candidate);
        int m = clist.size();
        if (n != m) {
            System.err.println("nro of children does not match: " +
                    aname + " (" + n + ") <-> " + cname + " (" + m + ")");
            return false;
        }

        if (!isOptimized) {
            System.err.println("children could not be optimized: " + aname + " <-> " + cname);
            return false;
        }

        if (actual == candidate) {
            System.err.println("elements are already optimized!");
            return true;
        }

        System.out.println("optimizing: " + aname + " <-> " + cname);

        Document doc = actual.getOwnerDocument();

        // create link and set its attributes
        Element link = doc.createElement(INCLUDE_OBJECT);
        Tools.copyAttributes(actual, link, NAME, POS_X, POS_Y, BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);

        // replace actual node with the link and put the actual
        // object in root
        Element parent = (Element) actual.getParentNode();
        parent.insertBefore(link, actual);
        parent.removeChild(actual);

        return isOptimized;
    }

    /**
     * Applies the specified geometric transformation to the given node (and
     * its children).
     * @param node the node to be transformed
     * @param xform the transformation
     */
    public static void geometricTransformation(XMLTreeNode node, int xform) {
        // create a set to prevent multiple transformations
        Set<Element> visitedElements = new HashSet<>();
        String type = node.getType();        
        int width;
        int height;
                    
        // do a recursive transformation
        if (Utils.equals(type, DATAMASK, ALARMMASK)) {
            XMLTreeNode root = (XMLTreeNode) node.getModel().getRoot();
            width = root.getDimension();
            height = width;
        } 
        else if (Utils.equals(type, KEY, AUXILIARYFUNCTION, AUXILIARYINPUT, WORKINGSET)) {
            XMLTreeNode root = (XMLTreeNode) node.getModel().getRoot();
            width = root.getSKWidth();
            height = root.getSKHeight();
        } 
        else if (node.getWidth() != null) {
            width = node.getWidth();
            height = (node.getHeight() != null) ? node.getHeight() : width;            
        }
        else {
            // the selected node cannot be transformed!
            return;
        }
        geometricTransformationRecursive(node, xform, width / 2, height / 2, visitedElements);
    }

    /**
     * This is the recursive part of the geometricTransformation method.
     * @param node
     * @param xform
     * @param midX
     * @param midY
     * @param visitedElements
     */
    private static void geometricTransformationRecursive(XMLTreeNode node,
            int xform, int midX, int midY, Set<Element> visitedElements) {

        Element actual = node.actual();
        if (actual == null) {
            return;
        }

        // add this element to the visited elements set so that it will not
        // be processed again later
        visitedElements.add(actual);

        //System.out.println("Doing transformation " + xform + " for " + node + ", mid: (" + midX + "," + midY + ")");
        String type = node.getType();

        // transform polygon points
        if (type.equals(POLYGON)) {

            int points[] = node.getPolygonPoints();
            for (int i = 0,  n = points.length / 2; i < n; i++) {

                // polygon points should not be rotated around the center point
                int midXNew = midX;
                int midYNew = midY;
                if (xform == ROTATE_90_CW) {
                    midXNew = midY;
                }
                if (xform == ROTATE_90_CCW) {
                    midYNew = midX;
                }
                node.setPolygonPoint(i, xformX(xform, midXNew, midYNew, points[2 * i], points[2 * i + 1]),
                        xformY(xform, midXNew, midYNew, points[2 * i], points[2 * i + 1]));
            }
        }

        // adjust start and end angle attributes
        if (Utils.equals(type, ELLIPSE, METER, ARCHEDBARGRAPH)) {
            int startAngle = node.getStartAngle();
            int endAngle = node.getEndAngle();
            switch (xform) {
                case FLIP_VERTICAL: {
                    int tmp = (startAngle < 180) ? 180 - startAngle : 540 - startAngle;
                    startAngle = (endAngle < 180) ? 180 - endAngle : 540 - endAngle;
                    endAngle = tmp;
                    break;
                }
                case FLIP_HORIZONTAL: {
                    int tmp = 360 - startAngle;
                    startAngle = 360 - endAngle;
                    endAngle = tmp;
                    break;
                }
                case ROTATE_90_CW: {
                    startAngle -= 90;
                    endAngle -= 90;
                    break;
                }
                case ROTATE_90_CCW: {
                    startAngle += 90;
                    endAngle += 90;
                    break;
                }
                case ROTATE_180: {
                    startAngle += 180;
                    endAngle += 180;
                    break;
                }
                default:
                    throw new RuntimeException("no such transform (" + xform + ")");
            }
            if (startAngle < 0) {
                startAngle += 360;
            }
            if (startAngle > 360) {
                startAngle -= 360;
            }
            if (endAngle < 0) {
                endAngle += 360;
            }
            if (endAngle > 360) {
                endAngle -= 360;
            }
            node.setStartAngle(startAngle);
            node.setEndAngle(endAngle);
        }
        
        // adjust meter attributes
        if (type.equals(METER)) {
            if (xform == FLIP_VERTICAL || xform == FLIP_HORIZONTAL) {
                node.changeOptionsClockwise(!node.isOptionsClockwise());
            }
        }        

        // adjust line attributes
        if (type.equals(LINE)) {
            if (xform == FLIP_VERTICAL || xform == FLIP_HORIZONTAL ||
                    xform == ROTATE_90_CW || xform == ROTATE_90_CCW) {
                node.setLineDirection(!node.getLineDirection());
            }
        }

        // adjust linear bar graph attributes
        if (type.equals(LINEARBARGRAPH)) {
            if ((xform == FLIP_VERTICAL && node.isOptionsHorizontal()) ||
                    (xform == FLIP_HORIZONTAL && !node.isOptionsHorizontal()) ||
                    (xform == ROTATE_90_CW && node.isOptionsHorizontal()) ||
                    (xform == ROTATE_90_CCW && !node.isOptionsHorizontal()) ||
                    (xform == ROTATE_180)) {
                node.changeOptionsGrowPositive(!node.isOptionsGrowPositive());
            }

            if (xform == ROTATE_90_CW || xform == ROTATE_90_CCW) {
                node.changeOptionsHorizontal(!node.isOptionsHorizontal());
            }
        }

        if (Utils.equals(type, CONTAINER, BUTTON, DATAMASK, ALARMMASK, KEY,
                AUXILIARYFUNCTION, AUXILIARYINPUT, WORKINGSET)) {
            // search through all childs and apply transformation for them
            XMLTreeModel model = node.getModel();
            for (int i = 0,  n = node.getModel().getChildCount(node); i < n; i++) {
                XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);

                // e.g. data masks and alarm masks do not have widths 
                if (nd.getWidth() == null) {
                    continue;
                }
                
                // don't transform the active mask in working set (it does not have a width!)
                //if (type.equals(WORKINGSET) && 
                //        (nd.getType().equals(DATAMASK) || nd.getType().equals(ALARMMASK))) {
                //    continue;
                //}

                // objects should not be rotated around the center point
                int midXNew = midX;
                int midYNew = midY;

                if (Utils.equals(type, CONTAINER, BUTTON)) {
                    if (xform == ROTATE_90_CW) {
                        midXNew = midY;
                    }
                    if (xform == ROTATE_90_CCW) {
                        midYNew = midX;
                    }
                }

                // if object is a button, the inside width and height are 8 pixels smaller than outside
                if (Utils.equals(type, BUTTON)) {
                    midXNew -= 4;
                    midYNew -= 4;
                }

                int width = nd.getWidth();
                int height = (nd.getHeight() != null) ? nd.getHeight() : width;
                            
                // magic...
                if (visitedElements.contains(nd.actual()) && 
                        (xform == ROTATE_90_CW || xform == ROTATE_90_CCW)) {
                    int temp = width;
                    width = height;
                    height = temp;
                }
                
                int newmidX = xformX(xform, midXNew, midYNew, nd.getX() + width / 2, nd.getY() + height / 2);
                int newmidY = xformY(xform, midXNew, midYNew, nd.getX() + width / 2, nd.getY() + height / 2);
                
                // apply the transformation only if the child has not yet been visited
                if (!visitedElements.contains(nd.actual())) {
                    geometricTransformationRecursive(nd, xform, width / 2, height / 2, visitedElements);
                }
                                
                int newHeight = (nd.getHeight() != null) ? nd.getHeight() : nd.getWidth();
                int newWidth = nd.getWidth();
                
                // transform position
                nd.setX(newmidX - newWidth / 2);
                nd.setY(newmidY - newHeight / 2);
            }
        }


        // swap the width and height attributes, if transformation is a 
        // rotation, object can be rotated and it has width and height
        if (((xform == ROTATE_90_CW) || (xform == ROTATE_90_CCW)) &&
                Utils.equals(type, CONTAINER, ELLIPSE, LINEARBARGRAPH, POLYGON, 
                RECTANGLE, LINE, BUTTON)) {
            int width = node.getWidth();
            int height = (node.getHeight() != null) ? node.getHeight() : width;
            node.setWidth(height);
            node.setHeight(width);
        }
    }

    private static int xformX(int xform, int midX, int midY, int x, int y) {
        switch (xform) {
            case FLIP_VERTICAL:
                return 2 * midX - x;
            case FLIP_HORIZONTAL:
                return x;
            case ROTATE_90_CW:
                return midX - (y - midY);
            case ROTATE_90_CCW:
                return midX + (y - midY);
            case ROTATE_180:
                return 2 * midX - x;
            default:
                throw new RuntimeException("no such transform (" + xform + ")");
        }
    /*
    if (transformation == 0)
    return 2*midX - x;
    if (transformation == 1)
    return x;
    if (transformation == 2)
    return midX - (y-midY);
    if (transformation == 3)
    return midX + (y-midY);
    if (transformation == 4)
    return 2*midX - x;
    else
    return 0;
     */
    }

    private static int xformY(int xform, int midX, int midY, int x, int y) {
        switch (xform) {
            case FLIP_VERTICAL:
                return y;
            case FLIP_HORIZONTAL:
                return 2 * midY - y;
            case ROTATE_90_CW:
                return midY + (x - midX);
            case ROTATE_90_CCW:
                return midY - (x - midX);
            case ROTATE_180:
                return 2 * midY - y;
            default:
                throw new RuntimeException("no such transform (" + xform + ")");
        }
    /*
    if (transformation == 0)
    return y;     
    if (transformation == 1)
    return 2*midY - y;
    if (transformation == 2)
    return midY + (x-midX);
    if (transformation == 3)
    return midY - (x-midX);
    if (transformation == 4)
    return 2*midY - y;
    else
    return 0;
     */
    }

    public void showPopup(final MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
