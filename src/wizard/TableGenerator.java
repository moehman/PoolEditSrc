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
package wizard;

import static pooledit.Definitions.*;
import static wizard.WizardTools.*;
import font.BitmapFont;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pooledit.Definitions;
import treemodel.XMLTreeNode;


/**
 *
 * @author  jkalmari
 */
public class TableGenerator {
    
    static private final char SEPARATOR = '_';
    private static final String CELLNAME = "cell";
    private final TableWizard wiz;
    private final XMLTreeNode root;
    private final String name;
    
    /** 
     * Creates a new instance of TableGenerator 
     * @param tableWizard
     * @param root
     * @param name
     */
    public TableGenerator(TableWizard tableWizard, XMLTreeNode root, String name) {
        this.wiz = tableWizard;         
        this.root = root;
        this.name = name;
        tableWizard.setTableGenerator(this);
    }
    
    public XMLTreeNode getRoot() {
        return root;
    }
    
     /**
     *  This method is called when the table should be updated
     */
    public void update() {                
        // create container
        int width = wiz.getCellWidth() * wiz.getCellsHorizontal() + 1;
        width += wiz.hasHeadingColumn() ? wiz.getHeadingColumnWidth() : 0;
        int height = wiz.getCellHeight() * wiz.getCellsVertical() + 1;
        height += wiz.hasHeadingRow() ? wiz.getHeadingRowHeight() : 0;
        
        Element container = createContainer(root.actual(), name, width, height);                 
        
        if (root.isType(Definitions.getTypes())) {
            setAttributeIfMissing(container, POS_X, "0");
            setAttributeIfMissing(container, POS_Y, "0");        
        }
        
        if (wiz.hasHeadingColumn()) {
            Element rectangle = createRectangle(container, "colrectangle", 
                    wiz.getHeadingColumnWidth()+1, height, wiz.getLineAttribute(), 
                    wiz.getFillAttributeHeading(), true);
            setIncludeAttributes(rectangle, 0, 0);
        } 
        else {
            removeObject(container, "colrectangle"); 
        } 
        
        //create a box under the heading row        
        if (wiz.hasHeadingRow()) {
            Element rectangle = createRectangle(container, "rowrectangle", width, 
                    wiz.getHeadingRowHeight() + 1, wiz.getLineAttribute(), 
                    wiz.getFillAttributeHeading(), true);
            setIncludeAttributes(rectangle, 0, 0);
        } 
        else {
            removeObject(container, "rowrectangle"); 
        }  
        
        //create a box under the table
        removeObject(container, "rectangle"); 
        Element rectangle = createRectangle(container, "rectangle", width, height, 
                wiz.getLineAttribute(), wiz.getFillAttributeCell(), true);
        setIncludeAttributes(rectangle, 0, 0);
        
        //create horizontal lines
        int i;
        if (wiz.hasHeadingRow())
            for (i = 0; i < wiz.getCellsVertical(); i++){
                Element horizontalLine = createLine(container, "lineh"+i, width, 1, wiz.getLineAttribute());
                setIncludeAttributes(horizontalLine, 0, i*wiz.getCellHeight() + wiz.getHeadingRowHeight());            
            }
        else
            for (i = 0; i < (wiz.getCellsVertical()-1); i++){
                Element horizontalLine = createLine(container, "lineh"+i, width, 1, wiz.getLineAttribute());
                setIncludeAttributes(horizontalLine, 0, (i+1)*wiz.getCellHeight());            
            }
        removeExtraElements(container, "lineh", i);
        
        //create vertical lines  
        if (wiz.hasHeadingColumn()) {
            for (i = 0; i < wiz.getCellsHorizontal(); i++){
                Element verticalLine = createLine(container, "linev"+i, 1, height, wiz.getLineAttribute());
                setIncludeAttributes(verticalLine, i*wiz.getCellWidth() + wiz.getHeadingColumnWidth(), 0);            
            }
        }
        else {    
            for (i = 0; i < (wiz.getCellsHorizontal() - 1); i++){
                Element verticalLine = createLine(container, "linev"+i, 1, height, wiz.getLineAttribute());
                setIncludeAttributes(verticalLine, (i+1)*wiz.getCellWidth(), 0);            
            } 
        }
        removeExtraElements(container, "linev", i);
        
        //create text and number cells
        Element fontElement = root.getModel().getElementByName( wiz.getFontAttribute());        
        int fontHeight = (int) BitmapFont.nameToDimension(fontElement.getAttribute(FONT_SIZE)).getHeight();
        
        int maxRow = wiz.getCellsVertical() + (wiz.hasHeadingRow() ? 1 : 0);
        int maxCol = wiz.getCellsHorizontal() + (wiz.hasHeadingColumn() ? 1 : 0);
        
        //System.err.println("mRow: " + maxRow + ", mCol: " + maxCol);
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                String name = CELLNAME + row + SEPARATOR + col;
                
                int x = ((wiz.hasHeadingColumn() && col > 0) ? 
                    ((col - 1) * wiz.getCellWidth() + wiz.getHeadingColumnWidth()) : 
                    col * wiz.getCellWidth()) + 1;
                int y = ((wiz.hasHeadingRow() && row > 0) ? 
                    ((row - 1) * wiz.getCellHeight() + wiz.getHeadingRowHeight()) : 
                    row * wiz.getCellHeight()) + 1;
                
                int cellWidth = ((wiz.hasHeadingColumn() && col == 0) ? 
                    wiz.getHeadingColumnWidth() : 
                    wiz.getCellWidth()) - 1;
                int cellHeight = ((wiz.hasHeadingRow() && row == 0) ? 
                    wiz.getHeadingRowHeight() : 
                    wiz.getCellHeight()) - 1;
                
                if (wiz.getVerticalJustification().equals("Middle")) {
                    y += (cellHeight - fontHeight)/2;
                    cellHeight -= (cellHeight - fontHeight)/2;                    
                }
                if (wiz.getVerticalJustification().equals("Bottom")) {
                    y += (cellHeight - fontHeight);
                    cellHeight -= (cellHeight - fontHeight);
                }
                
                //create a text cell
                if ((row == 0 && wiz.hasHeadingRow()) || (col == 0 && wiz.hasHeadingColumn())) {
                    removeElement(container, OUTPUTNUMBER, name);
                    Element cell = createString(container, name, cellWidth, cellHeight, "A", 
                            wiz.getFontAttribute(), wiz.getHorizontalJustification().toLowerCase());
                    setIncludeAttributes(cell, x, y);
                }
                else {
                    removeElement(container, OUTPUTSTRING, name);
                    Element cell = createNumber(container, name, cellWidth, cellHeight, 0, null, 
                            wiz.getFontAttribute(), wiz.getHorizontalJustification().toLowerCase(), 
                            wiz.getOffset(), wiz.getScale(), wiz.getNumberOfDecimals());
                    setIncludeAttributes(cell, x, y);
                }
            }
        }   
        //remove old elements
        removeExtraElements2(container, CELLNAME, maxRow, maxCol);       
        
        //Tools.createRoles(container);
    }

    private static Element createLine(Element father, String name, 
            int width, int height, String lineAttribute) {
        
        Element line = createElement(LINE, name, father, false);        
        setAttribute(line, WIDTH, width);
        setAttribute(line, HEIGHT, height);
        setAttribute(line, LINE_DIRECTION, "toplefttobottomright");
        createIncludeRoleElement(lineAttribute, LINE_ATTRIBUTES, line);
        return line;        
    }   
  
    private static Element createNumber(Element father, String name, 
            int width, int height, int value, 
            String variableReference, String fontAttribute, String justification, 
            int offset, double scale, int decimals)  {
        
        Element number = createElement(OUTPUTNUMBER, name, father, false);
        setAttribute(number, WIDTH, width);
        setAttribute(number, HEIGHT, height);       
        setAttribute(number, VALUE, value);         
        setAttribute(number, HORIZONTAL_JUSTIFICATION, justification);        
        setAttribute(number, OPTIONS, "transparent");
        setAttribute(number, SCALE, scale);
        setAttribute(number, OFFSET, offset);  
        setAttribute(number, NUMBER_OF_DECIMALS, decimals);
        setAttribute(number, BACKGROUND_COLOUR, "white");
                
        createIncludeRoleElement(fontAttribute, FONT_ATTRIBUTES, number);
        createIncludeRoleElement(variableReference, VARIABLE_REFERENCE, number);
          
        return number;
    }   
          
    /**
     * Creates a new include object child element that points to an object 
     * called "name". Removes any other child elements that have the same role.
     * If name is null, no new element is created and the method returns null.
    private static Element createIncludeRoleElement(String name, String role, Element father) {
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
                    "C:\\pooledit\\schema\\iso11783.xsd");
            rv = (Element) father.getOwnerDocument().adoptNode(tmp.getDocumentElement()).getFirstChild();
            rv.setAttribute(NAME, name);
            rv.setAttribute(ROLE, role);
            father.appendChild(rv);       
        }
        return rv;
    }
     */
    
    private static Element includeObject(Element father, Element child, 
            int posX, int posY) {
        
        Element includeObject = createElement(INCLUDE_OBJECT, child.getAttribute(NAME), father, false);
        includeObject.setAttribute(POS_X, Integer.toString(posX));
        includeObject.setAttribute(POS_Y, Integer.toString(posY));
        return includeObject;
    }
    
    private static void removeElement(Element father, String type, String name){
        NodeList children = father.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals(type) && elem.getAttribute(NAME).equals(name)) {
                    father.removeChild(elem);
                }
            }
        }
    }            
    /*
    private static void removeExtraElements(Element father, String name, int start) {
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
     */
    private static void removeExtraElements2(Element father, String name, int rowStart, int colStart) {
        NodeList children = father.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                String fullname = elem.getAttribute(NAME);
                if (fullname.startsWith(name)) {
                    try {
                        int separatorIndex = fullname.indexOf(SEPARATOR, name.length());
                        String end1 = fullname.substring(name.length(), separatorIndex);
                        String end2 = fullname.substring(separatorIndex + 1);                        
                        int row = Integer.parseInt(end1);
                        int col = Integer.parseInt(end2);
                        if (row >= rowStart || col >= colStart) {
                            father.removeChild(elem);
                        }
                    }
                    catch (NumberFormatException e) {}
                }
            }
        }
    }

    private static void removeObject(Element father, String name) {
        NodeList children = father.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getAttribute(NAME).equals(name)) {
                    father.removeChild(elem);
                }
            }
        }
    }
}
