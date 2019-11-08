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
package attributetable;

import static pooledit.Definitions.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import color.ColorPalette;
import color.ColorIcon;
import font.BitmapFont;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import pooledit.Tools;
import pooledit.Utils;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class AttributeTable extends JTable {

    private final AttributeTablePopupMenu popup = new AttributeTablePopupMenu(this);
    
    private final TableCellEditor yesNoEditor;
    //private final TableCellEditor trueFalseEditor;
    private final TableCellEditor hideShowEditor;
    private final TableCellEditor enableDisableEditor;
    private final TableCellEditor languageEditor;
    private final TableCellEditor horizontalJustificationEditor;
    private final TableCellEditor nroDecimalsEditor;
    private final TableCellEditor formatEditor;
    private final TableCellEditor lineDirectionEditor;
    private final TableCellEditor lineSuppressionEditor;
    private final TableCellEditor ellipseTypeEditor;
    private final TableCellEditor polygonTypeEditor;
    private final TableCellEditor fontSizeEditor;
    private final TableCellEditor fontTypeEditor;
    private final TableCellEditor fontStyleEditor;
    private final TableCellEditor fillTypeEditor;
    private final TableCellEditor priorityEditor;
    private final TableCellEditor acousticSignalEditor;
    private final TableCellEditor colorEditor;
    private final TableCellEditor pictureGraphicOptionEditor;
    private final TableCellEditor meterOptionEditor;
    private final TableCellEditor linearBbarGraphOptionEditor;
    private final TableCellEditor archedBarGraphOptionEditor;
    private final TableCellEditor outputStringOptionEditor;
    private final TableCellEditor outputNumberOptionEditor;
    private final TableCellEditor booleanAnalogEditor;    
    private final TableCellEditor validationTypeEditor;
    private final TableCellEditor maskTypeEditor;
    
    private final FileCellEditor fileEditor;    
    private final FileCellRenderer fileRenderer;
    private final TableCellRenderer colorRenderer;
    
    /**
     * Private constructor, call getInstance() instead.
     * @param model
     */
    private AttributeTable(TableModel model) {
	super(model);
        yesNoEditor = createEditor("no", "yes");
        //trueFalseEditor = createEditor("false", "true");
        hideShowEditor = createEditor("hide", "show");
        enableDisableEditor = createEditor("enable", "disable");
        languageEditor = createEditor(Locale.getISOLanguages());
        horizontalJustificationEditor = createEditor("left", "middle", "right");
        nroDecimalsEditor = createEditor("0", "1", "2", "3", "4", "5", "6", "7");
        formatEditor = createEditor("fixed", "exponential");
        lineDirectionEditor = createEditor("toplefttobottomright", "bottomlefttotopright");
        lineSuppressionEditor = new CheckBoxListCellEditor("top", "right", "bottom", "left");
        ellipseTypeEditor = createEditor("closed", "open", "closedsegment", "closedsection");
        polygonTypeEditor = createEditor("convex", "nonconvex", "complex", "open");
        fontSizeEditor = createEditor(BitmapFont.getNames());
        fontTypeEditor = createEditor("latin1", "latin9");
        fontStyleEditor = new CheckBoxListCellEditor("bold", "crossed", "underlined", "italic", "inverted", "flashinginverted", "flashinghidden");
        fillTypeEditor = createEditor("nofill", "linecolour", "fillcolour", "pattern");
	priorityEditor = createEditor("high", "medium", "low");
	acousticSignalEditor = createEditor("high", "medium", "low", "none");
	colorEditor = createEditor(createColorListRenderer(), ColorPalette.getAllColorNames());
	colorRenderer = createColorRenderer();
        pictureGraphicOptionEditor = new CheckBoxListCellEditor("transparent", "flashing");
        meterOptionEditor = new CheckBoxListCellEditor("arc", "clockwise", "ticks", "border");
        linearBbarGraphOptionEditor = new CheckBoxListCellEditor("border", "targetline", "ticks", "nofill", "horizontal", "growpositive");
        archedBarGraphOptionEditor = new CheckBoxListCellEditor("border", "targetline", "nofill", "clockwise");
        outputStringOptionEditor = new CheckBoxListCellEditor("transparent", "autowrap");
        outputNumberOptionEditor = new CheckBoxListCellEditor("transparent", "leadingzeros", "blankzero");
        booleanAnalogEditor = createEditor("boolean", "analog");
        validationTypeEditor = createEditor("invalidcharacters", "validcharacters");
        maskTypeEditor = createEditor("datamask", "alarmmask");
        
        fileRenderer = FileCellRenderer.getInstance();
        fileEditor = FileCellEditor.getInstance();
    }
    static public AttributeTable getInstance(TableModel model) {
        AttributeTable at = new AttributeTable(model);
        at.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    at.popup.showPopup(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    at.popup.showPopup(e);
                }
            }
        });
        return at;
    }

    /*
     * Overrides the corresponding method in the super class. Stops editing
     * when the table is changed. 
     *
     * NOTE: THIS WILL CAUSE INFINITE RECURSION AS STOPCELLEDITING WILL
     * TRIGGER A NEW TABLE CHANGED EVENT!
     *
    public void tableChanged(TableModelEvent e) {
        TableCellEditor tableCellEditor = getCellEditor();
        if (tableCellEditor != null) {
            tableCellEditor.stopCellEditing();
        }
        super.tableChanged(e);
    }
    */

    private static TableCellRenderer createColorRenderer() {
	return new DefaultTableCellRenderer() {
            private final ColorIcon icon = ColorIcon.getInstance();
            @Override
            public Component getTableCellRendererComponent
                (JTable tbl, Object val, boolean isSel, 
                 boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent
                (tbl, val, isSel, hasFocus, row, col);

                String clr = (String) val;
                icon.setColor(ColorPalette.getColor8Bit(clr));
                setIcon(icon);
                setText(clr);
                return this;
            }
        };
    }

    /**
     * Creates a color list renderer.
     * @return
     */
    public static ListCellRenderer<String> createColorListRenderer() {
	return new ListCellRenderer<String>() {
            protected DefaultListCellRenderer defren = new DefaultListCellRenderer();
            private final ColorIcon icon = ColorIcon.getInstance();
            @Override
            public Component getListCellRendererComponent
                (JList list, String val, int idx,
                 boolean isSel, boolean hasFocus) {
                JLabel renderer = (JLabel) defren.getListCellRendererComponent
                    (list, val, idx, isSel, hasFocus);

                String clr = (String) val;
                icon.setColor(ColorPalette.getColor8Bit(clr));
                renderer.setIcon(icon);
                renderer.setText(clr);
                return renderer;
            }
        };
    }

    /**
     * Gets an appropriate cell renderer.
     * @param row
     * @param col
     * @return
     */
    @Override
    public TableCellRenderer getCellRenderer(int row, int col) {
        // should not happen?
        if (col != 1) {
            return super.getCellRenderer(row, col);
        }
	String attr = (String) getModel().getValueAt(row, 0);
	if (Utils.equals(attr, BACKGROUND_COLOUR, ARC_AND_TICK_COLOUR,
                BORDER_COLOUR, NEEDLE_COLOUR, TARGET_LINE_COLOUR, 
                COLOUR, TRANSPARENCY_COLOUR, FONT_COLOUR, // color is only for bar graphs!
                LINE_COLOUR, FILL_COLOUR)) {
	    return colorRenderer;
	}
        else if (Utils.equals(attr, FILE, FILE1, FILE4, FILE8)) {
            return fileRenderer;
        }
	else {
	    return super.getCellRenderer(row, col);
	}
    }
    
    /**
     * Gets an appropriate cell editor.
     * @param row
     * @param col
     * @return
     */
    @Override
    public TableCellEditor getCellEditor(int row, int col) {
        // should not happen?
        if (col != 1) {
            return super.getCellEditor(row, col);
        }
        AttributeTableModel model = (AttributeTableModel) getModel();        
	String attr = (String) model.getValueAt(row, 0);
        Element elem = model.getCurrentElement();
        Document doc = model.getDocument(); // should be the same as elem.getOwnerDocument() ?
        
        if (elem == null) {
            return null;
        }
        String type = elem.getNodeName();
	if (Utils.equals(attr, SELECTABLE, LATCHABLE, ENABLED, HIDDEN)) {
            return yesNoEditor;
        }
        else if (Utils.equals(attr, HIDE_SHOW)) { // for the hide_show_object command
            return hideShowEditor;
        }
        else if (Utils.equals(attr, ENABLE_DISABLE)) { // for the enable_disable_object command
            return enableDisableEditor;
        }
        else if (Utils.equals(attr, CODE)) { // "language" is different as it can be a combination e.g. "en+de+it"
            return languageEditor;
        }
        else if (Utils.equals(attr, HORIZONTAL_JUSTIFICATION)) {
            return horizontalJustificationEditor;
        }
        else if (Utils.equals(attr, NUMBER_OF_DECIMALS)) {
            return nroDecimalsEditor;
        }
        else if (Utils.equals(attr, FORMAT)) {
            if (Utils.equals(type, INPUTNUMBER, OUTPUTNUMBER)) {
                return formatEditor;    
            }
            else if (Utils.equals(type, PICTUREGRAPHIC, FIXEDBITMAP)) {
                 // FIXME: do something else...
                return super.getCellEditor(row, col);
            }
            else {
                return super.getCellEditor(row, col);
            }
        }
        else if (Utils.equals(attr, OPTIONS)) {
            if (Utils.equals(type, PICTUREGRAPHIC)) {
                return pictureGraphicOptionEditor;
            }
            else if (Utils.equals(type, METER)) {
                return meterOptionEditor;
            }
            else if (Utils.equals(type, LINEARBARGRAPH)) {
                return linearBbarGraphOptionEditor;
            }
            else if (Utils.equals(type, ARCHEDBARGRAPH)) {
                return archedBarGraphOptionEditor;
            }
            else if (Utils.equals(type, OUTPUTSTRING)) {
                return outputStringOptionEditor;
            }
            else if (Utils.equals(type, OUTPUTNUMBER, INPUTNUMBER)) {
                return outputNumberOptionEditor;
            }
            else {
                return super.getCellEditor(row, col);
            }
        }
        else if (Utils.equals(attr, LINE_DIRECTION)) {
            return lineDirectionEditor;
        }
        else if (Utils.equals(attr, LINE_SUPPRESSION)) {
            return lineSuppressionEditor;
        }
        else if (Utils.equals(attr, ELLIPSE_TYPE)) {
            return ellipseTypeEditor;
        }
        else if (Utils.equals(attr, POLYGON_TYPE)) {
            return polygonTypeEditor;
        }
        else if (Utils.equals(attr, FONT_SIZE)) {
            return fontSizeEditor;
        }
        else if (Utils.equals(attr, FONT_TYPE)) {
            return fontTypeEditor;
        }
        else if (Utils.equals(attr, FONT_STYLE)) {
            return fontStyleEditor;
        }
        else if (Utils.equals(attr, FILL_TYPE)) {
            return fillTypeEditor;
        }
        else if (Utils.equals(attr, PRIORITY)) {
	    return priorityEditor;
	}
	else if (Utils.equals(attr, ACOUSTIC_SIGNAL)) {
	    return acousticSignalEditor;
	}
	else if (Utils.equals(attr, BACKGROUND_COLOUR, ARC_AND_TICK_COLOUR, 
                BORDER_COLOUR, NEEDLE_COLOUR, TARGET_LINE_COLOUR,
                COLOUR, TRANSPARENCY_COLOUR, FONT_COLOUR,
                LINE_COLOUR, FILL_COLOUR)) {
	    return colorEditor;
	}
        else if (Utils.equals(attr, FUNCTION_TYPE)) {
            return booleanAnalogEditor;
        }
        else if (Utils.equals(attr, VALIDATION_TYPE)) {
            return validationTypeEditor;
        }
        else if (Utils.equals(attr, MASK_TYPE)) {
            return maskTypeEditor;
        }
        else if (Utils.equals(attr, ROLE)) {
            return createEditor(findPossibleRoles(model.getCurrentNode()));
        }
        // NOTE: font_attributes is a name of an attribute, fontattributes
        // is a name of an ISOBUS object
        else if(Utils.equals(attr, FONT_ATTRIBUTES, BLOCK_FONT) || 
                (Utils.equals(type, INPUTBOOLEAN) && Utils.equals(attr, FOREGROUND_COLOUR))) {
            return createEditor(findElements(doc, false, FONTATTRIBUTES));     
        }
        else if (Utils.equals(attr, VARIABLE_REFERENCE) || Utils.equals(attr, TARGET_VALUE_VARIABLE_REFERENCE)) {
            if (Utils.equals(type, OUTPUTSTRING, INPUTSTRING)) {
                return createEditor(findElements(doc, true, STRINGVARIABLE)); 
            }
            else if (Utils.equals(type, INPUTBOOLEAN, INPUTNUMBER, INPUTLIST, OUTPUTNUMBER, METER, LINEARBARGRAPH, ARCHEDBARGRAPH)) {
                return createEditor(findElements(doc, true, NUMBERVARIABLE));     
            }
            else {
                return super.getCellEditor(row, col);
            }
        }
        else if(Utils.equals(attr, ACTIVE_MASK)) {
            return createEditor(findElements(doc, true, DATAMASK, ALARMMASK));
        }
        else if(Utils.equals(attr, SOFT_KEY_MASK)) {
            return createEditor(findElements(doc, true, SOFTKEYMASK));
        }
        else if(Utils.equals(attr, LINE_ATTRIBUTES)) {
            return createEditor(findElements(doc, true, LINEATTRIBUTES));     
        }
        else if(Utils.equals(attr, FILL_ATTRIBUTES)) {
            return createEditor(findElements(doc, true, FILLATTRIBUTES));     
        }
        else if(Utils.equals(attr, FILL_PATTERN)) {
            return createEditor(findElements(doc, true, PICTUREGRAPHIC));     
        }
        else if(Utils.equals(attr, FILE, FILE1, FILE4, FILE8)){
            return fileEditor;
        }
        else if(Utils.equals(attr, "parents")){
            return createEditor(findLinkingElements(elem));
        }
	else {
	    return super.getCellEditor(row, col);
	}
    }
    
    /**
     * Finds all elements that are given type, and returns their names.
     * @param doc
     * @param empty
     * @param names
     * @return
     */
    private static String[] findElements(Document doc, boolean empty, String ... names){
        List<String> namelist = new ArrayList<>();
        if (empty) { 
            namelist.add(""); // empty string
        } 
        for (int j = 0, m = names.length; j < m; j++) {
            NodeList nodes = doc.getElementsByTagName(names[j]);
            for (int i = 0, n = nodes.getLength(); i < n; i++) {
                namelist.add(((Element) (nodes.item(i))).getAttribute(NAME));
            }
        }
        return namelist.toArray(new String[0]);
    }

    /**
     * Finds every element that has a include_object reference or a 
     * attribute-reference to current object. Returns an array of names 
     * of elements having a link to the specified element.
     * @param elem
     * @return
     */
    private static String[] findLinkingElements(Element elem) {
        List<Element> list = new ArrayList<>();
        Tools.findParentElements(elem, list);
        
        List<String> namelist = new ArrayList<>();
        namelist.add(""); // empty string
        for (Element e : list) {
            namelist.add(Tools.getPath(e));
        }
        return namelist.toArray(new String[0]);
    }
    /*
    private static Object[] findLinkingElements(Element elem) {
        String name = elem.getAttribute(NAME);
        List<String> namelist = new ArrayList<String>();
        namelist.add(""); // empty string

        // this object can also be embedded in another object (pseudo objects,
        // and real objects in non-linear documents)
        Node parent = elem.getParentNode();
        if (parent.getNodeType() == Element.ELEMENT_NODE &&
                equals(parent.getNodeName(), OBJECTS)) {
            
            namelist.add(((Element) parent).getAttribute(NAME));
        }

        // if the current element has no name, there is nothing more to do
        if (name.isEmpty()) {
            return namelist.toArray();
        }

        // iterate over all elements in the document
        Document doc = elem.getOwnerDocument();
        NodeList nodes = doc.getElementsByTagName("*");
        for (int i = 0, n = nodes.getLength(); i < n; i++) {
            Element element = (Element) nodes.item(i);
            
            // other objects can use include objects to make references
            if (element.getNodeName().equals(INCLUDE_OBJECT)) {
                if (element.getAttribute(NAME).equals(name)) {
                    parent = element.getParentNode();
                    if (parent.getNodeType() == Element.ELEMENT_NODE) {
                        
                        namelist.add(((Element) parent).getAttribute(NAME));
                    }
                }
            }
            // other objects can use some attributes to make references
            else if (Utils.equalsAttribute(name, element, ACTIVE_MASK, SOFT_KEY_MASK, 
                    FONT_ATTRIBUTES, BLOCK_FONT, LINE_ATTRIBUTES, 
                    FILL_ATTRIBUTES, FILL_PATTERN, VARIABLE_REFERENCE, 
                    TARGET_VALUE_VARIABLE_REFERENCE)) {
                
                namelist.add(element.getAttribute(NAME));
            }
        }
        return namelist.toArray();
    }
    */
    
    /**
     * This method creates a new DefaultCellEditor with JComboBox filled with
     * the specified names. This method is divided into two parts because it
     * is a really bad idea to say: foo(Object ... bar)
     * @param names
     * @return
     */
    /*
    static private TableCellEditor createEditor(String ... names) {
        return createEditor((Object[]) names);
    }
    */
    static private TableCellEditor createEditor(String ... names) {
        JComboBox<String> cb = new JComboBox<>();
        for (int i = 0, n = names.length; i < n; i++) {
            cb.addItem(names[i]);
        }
	return new DefaultCellEditor(cb);
    }
       
    /**
     * This method is divided into two parts because it
     * is a really bad idea to say: foo(Object ... bar)
     * @param renderer
     * @param names
     * @return
     */
    /*
    static private TableCellEditor createEditor(ListCellRenderer renderer, String ... names) {
        return createEditor(renderer, (Object[]) names);
    }
    */
    static private TableCellEditor createEditor(ListCellRenderer<String> renderer, String[] names) {
        JComboBox<String> cb = new JComboBox<>();
        for (int i = 0, n = names.length; i < n; i++) {
            cb.addItem(names[i]);
        }
        cb.setRenderer(renderer);
	return new DefaultCellEditor(cb);
    }
    
    /**
     * Checks, if at least one value equals name.
    static private boolean equals(String name, String ... values) {
        for (int i = 0, n = values.length; i < n; i++) {
            if (name.equals(values[i])) {
                return true;
            }
        }
        return false;
    }
    */
    
    /**
     * Check, if at least one attribute of the specified element has the given value.
    static private boolean equalsAttribute(String value, Element element, String ... attributes) {
        for (int i = 0, n = attributes.length; i < n; i++) {
            if (value.equals(element.getAttribute(attributes[i]))) {
                return true;
            }
        }
        return false;
    }   
    */
    
    /**
     * Finds possible roles for the specified node. If no roles are possible,
     * this method returns a single, empty string.
     * @param node
     * @return
     */
    private static String[] findPossibleRoles(XMLTreeNode node) {
        Node parentNode = null;
        if (node.link() != null) {
            parentNode = node.link().getParentNode();
        }
        else if (node.actual() != null) {
            parentNode = node.actual().getParentNode();
        }
        
        if (parentNode == null || 
                parentNode.getNodeType() != Node.ELEMENT_NODE) {
            return new String[] {""};
        }
        Element parent = (Element) parentNode;
        return findPossibleRoles(parent.getNodeName(), node.getType());
    }
    
    /**
     * Finds possible roles for the given element type in the context of
     * the specified parent type. If no roles are possible, this method 
     * returns a single, empty string.
     * @param parentType
     * @param elemType
     * @return
     */
    public static String[] findPossibleRoles(String parentType, String elemType) {
        if (Utils.equals(parentType, WORKINGSET) &&
                Utils.equals(elemType, DATAMASK, ALARMMASK)) {
            return new String[] {ACTIVE_MASK};
        }
        else if (Utils.equals(parentType, DATAMASK, ALARMMASK) &&
                Utils.equals(elemType, SOFTKEYMASK)) {
            return new String[] {SOFT_KEY_MASK};
        }
        else if (Utils.equals(parentType, INPUTSTRING, INPUTNUMBER, OUTPUTSTRING, OUTPUTNUMBER) &&
                Utils.equals(elemType, FONTATTRIBUTES)) {
            return new String[] {FONT_ATTRIBUTES};    
        }
        else if (Utils.equals(parentType, INPUTBOOLEAN) &&
                Utils.equals(elemType, FONTATTRIBUTES)) {
            return new String[] {FOREGROUND_COLOUR};
        }
        else if (Utils.equals(parentType, INPUTSTRING) &&
                Utils.equals(elemType, INPUTATTRIBUTES)) {
            return new String[] {INPUT_ATTRIBUTES};
        }
        else if (Utils.equals(parentType, INPUTNUMBER, INPUTLIST, OUTPUTNUMBER, METER) &&
                Utils.equals(elemType, NUMBERVARIABLE)) {
            return new String[] {VARIABLE_REFERENCE};
        }
        else if (Utils.equals(parentType, INPUTSTRING, OUTPUTSTRING) &&
                Utils.equals(elemType, STRINGVARIABLE)) {
            return new String[] {VARIABLE_REFERENCE};
        }
        else if (Utils.equals(parentType, LINEARBARGRAPH, ARCHEDBARGRAPH) &&
                Utils.equals(elemType, NUMBERVARIABLE)) {
            return new String[] {VARIABLE_REFERENCE, TARGET_VALUE_VARIABLE_REFERENCE};
        }
        else if (Utils.equals(parentType, LINE, RECTANGLE, ELLIPSE, POLYGON) &&
                Utils.equals(elemType, LINEATTRIBUTES)) {
            return new String[] {LINE_ATTRIBUTES};
        }
        else if (Utils.equals(parentType, RECTANGLE, ELLIPSE, POLYGON) &&
                Utils.equals(elemType, FILLATTRIBUTES)) {    
            return new String[] {FILL_ATTRIBUTES};
        }
        else if (Utils.equals(parentType, FILLATTRIBUTES) &&
                Utils.equals(elemType, PICTUREGRAPHIC)) {
        
            return new String[] {FILL_PATTERN};
        }
        // commands
        else if (Utils.equals(parentType, COMMAND_HIDE_SHOW_OBJECT) &&
                Utils.equals(elemType, CONTAINER)) {
            
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_ENABLE_DISABLE_OBJECT) &&
                Utils.equals(elemType, INPUTBOOLEAN, INPUTSTRING, INPUTNUMBER, INPUTLIST)) {
        
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_SELECT_INPUT_OBJECT) &&
                Utils.equals(elemType, INPUTBOOLEAN, INPUTSTRING, INPUTNUMBER, INPUTLIST)) {
        
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CONTROL_AUDIO_DEVICE, COMMAND_SET_AUDIO_VOLUME)) {
            
            return new String[] {""};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_CHILD_LOCATION, COMMAND_CHANGE_CHILD_POSITION)) {
            
            return new String[] {"parent_id", "child_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_SIZE)) { // FIXME: what objects can be included here?
            
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_BACKGROUND_COLOUR)) { // FIXME: what objects can be included here?
            
            return new String[] {"object_id"};            
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_NUMERIC_VALUE) &&
                Utils.equals(elemType, INPUTNUMBER, OUTPUTNUMBER, METER, 
                LINEARBARGRAPH, ARCHEDBARGRAPH, NUMBERVARIABLE)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_STRING_VALUE) &&
                Utils.equals(elemType, INPUTSTRING, OUTPUTSTRING, STRINGVARIABLE)) {
        
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_END_POINT) &&
                Utils.equals(elemType, LINE)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_FONT_ATTRIBUTES) &&
                Utils.equals(elemType, FONTATTRIBUTES)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_LINE_ATTRIBUTES) &&
                Utils.equals(elemType, LINEATTRIBUTES)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_FILL_ATTRIBUTES) &&
                Utils.equals(elemType, FILLATTRIBUTES)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_ACTIVE_MASK)) {
            
            if (Utils.equals(elemType, WORKINGSET)) {                
                return new String[] {"parent_id"};
            }
            else if (Utils.equals(elemType, DATAMASK, ALARMMASK)) {
                return new String[] {"child_id"};
            }
            else {
                return new String[] {""};
            }
        }    
        else if (Utils.equals(parentType, COMMAND_CHANGE_SOFT_KEY_MASK)) {
            
            if (Utils.equals(elemType, DATAMASK, ALARMMASK)) {                
                return new String[] {"parent_id"};
            }
            else if (Utils.equals(elemType, SOFTKEYMASK)) {
                return new String[] {"child_id"};
            }
            else {
                return new String[] {""};
            }
        }   
        else if (Utils.equals(parentType, COMMAND_CHANGE_ATTRIBUTE)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_PRIORITY) &&
                Utils.equals(elemType, ALARMMASK)) {
                
            return new String[] {"object_id"};
        }
        else if (Utils.equals(parentType, COMMAND_CHANGE_LIST_ITEM) &&
                Utils.equals(elemType, INPUTLIST)) {
                
            return new String[] {"object_id"};
        }        
        // macros
        else if (Utils.equals(elemType, MACRO)) {
            if (Utils.equals(parentType, WORKINGSET)) {
                return new String[] {ON_ACTIVATE, ON_DEACTIVATE,
                ON_CHANGE_ACTIVE_MASK, ON_CHANGE_BACKGROUND_COLOUR,
                ON_CHANGE_CHILD_LOCATION, ON_CHANGE_CHILD_POSITION};
            }
            else if (Utils.equals(parentType, DATAMASK)) {
                return new String[] {ON_SHOW, ON_HIDE, 
                ON_CHANGE_BACKGROUND_COLOUR, ON_CHANGE_CHILD_LOCATION, 
                ON_CHANGE_CHILD_POSITION, ON_CHANGE_SOFT_KEY_MASK, 
                ON_CHANGE_ATTRIBUTE};                
            }
            else if (Utils.equals(parentType, ALARMMASK)) {
                return new String[] {ON_SHOW, ON_HIDE, 
                ON_CHANGE_BACKGROUND_COLOUR, ON_CHANGE_CHILD_LOCATION, 
                ON_CHANGE_CHILD_POSITION, ON_CHANGE_PRIORITY,
                ON_CHANGE_SOFT_KEY_MASK, ON_CHANGE_ATTRIBUTE};                
            }
            else if (Utils.equals(parentType, CONTAINER)) {
                return new String[] {ON_SHOW, ON_HIDE, 
                ON_CHANGE_CHILD_LOCATION, ON_CHANGE_CHILD_POSITION, 
                ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, SOFTKEYMASK)) {
                return new String[] {ON_SHOW, ON_HIDE, 
                ON_CHANGE_BACKGROUND_COLOUR, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, KEY)) {
                return new String[] {ON_KEY_PRESS, ON_KEY_RELEASE, 
                ON_CHANGE_BACKGROUND_COLOUR, ON_CHANGE_CHILD_LOCATION,
                ON_CHANGE_CHILD_POSITION, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, BUTTON)) {
                return new String[] {ON_KEY_PRESS, ON_KEY_RELEASE,
                ON_CHANGE_BACKGROUND_COLOUR, ON_CHANGE_SIZE, 
                ON_CHANGE_CHILD_LOCATION, ON_CHANGE_CHILD_POSITION,
                ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, INPUTBOOLEAN, INPUTSTRING, 
                    INPUTNUMBER, INPUTLIST)) {
                return new String[] {ON_ENABLE, ON_DISABLE,
                ON_INPUT_FIELD_SELECTION, ON_INPUT_FIELD_DESELECTION,
                ON_ESC, ON_CHANGE_BACKGROUND_COLOUR, 
                ON_CHANGE_VALUE, // applies to both numeric and string values
                ON_ENTRY_OF_VALUE, ON_ENTRY_OF_NEW_VALUE,
                ON_CHANGE_ATTRIBUTE, ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, INPUTLIST)) {
                return new String[] {ON_ENABLE, ON_DISABLE,
                ON_INPUT_FIELD_SELECTION, ON_INPUT_FIELD_DESELECTION,
                ON_ESC, ON_CHANGE_VALUE, // applies to both numeric and string values
                ON_ENTRY_OF_VALUE, ON_ENTRY_OF_NEW_VALUE,
                ON_CHANGE_ATTRIBUTE, ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, OUTPUTSTRING, OUTPUTNUMBER)) {
                return new String[] {ON_CHANGE_BACKGROUND_COLOUR, 
                ON_CHANGE_VALUE, ON_CHANGE_ATTRIBUTE, ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, LINE)) {
                return new String[] {ON_CHANGE_END_POINT, ON_CHANGE_ATTRIBUTE, 
                ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, RECTANGLE, ELLIPSE, POLYGON)) {
                return new String[] {ON_CHANGE_SIZE, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, METER, LINEARBARGRAPH, ARCHEDBARGRAPH)) {
                return new String[] {ON_CHANGE_VALUE, ON_CHANGE_ATTRIBUTE, ON_CHANGE_SIZE};
            }
            else if (Utils.equals(parentType, PICTUREGRAPHIC)) {
                return new String[] {ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, NUMBERVARIABLE, STRINGVARIABLE)) {
                return new String[] {ON_CHANGE_VALUE};
            }
            else if (Utils.equals(parentType, FONTATTRIBUTES)) {
                return new String[] {ON_CHANGE_FONT_ATTRIBUTES, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, LINEATTRIBUTES)) {
                return new String[] {ON_CHANGE_LINE_ATTRIBUTES, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, FILLATTRIBUTES)) {
                return new String[] {ON_CHANGE_FILL_ATTRIBUTES, ON_CHANGE_ATTRIBUTE};
            }
            else if (Utils.equals(parentType, INPUTATTRIBUTES, OBJECTPOINTER)) {
                return new String[] {ON_CHANGE_VALUE};
            }
            else {
                return new String[] {""};
            }
        }
        else {
            return new String[] {""};
        }
    }
}
