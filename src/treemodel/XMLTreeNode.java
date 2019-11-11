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
package treemodel;

import java.awt.Graphics2D;
import static pooledit.Definitions.*;
import javax.swing.tree.TreePath;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.TexturePaint;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import color.ColorPalette;
import objectview.LineAttributes;
import font.BitmapFont;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.StringTokenizer;
import org.w3c.dom.Element;
import pooledit.FileTools;
import pooledit.PictureConverter;

/**
 * JTree sees XMLTreeNodes, override toString method to change the
 * textual presentation. NOTE: the textual presentation can also be
 * changed in the renderer; however, creating wrapper objects is a 
 * good idea (because we are showing a graph as a tree)
 *
 * @author mohman
 */
public class XMLTreeNode {
    
    private final XMLTreeModel model;
    private final XMLTreeNode parent;
    private final TreePath path; // of elements
        
    private final Element actual;
    private final Element link; // this is the link node, if any
    private final String type; // for category nodes for which node == null and link == null
    
    private XMLTreeNode(XMLTreeModel model, XMLTreeNode parent, TreePath path,
            Element actual, Element link, String type) {
        this.model = model;
        this.parent = parent;
        this.path = path;
        this.actual = actual;
        this.link = link;
        this.type = type;
    }
    
    /**
     * Creates a root node.
     * @param model
     * @param actual
     * @return 
     */
    protected static XMLTreeNode createRootNode(XMLTreeModel model, Element actual) {
        return new XMLTreeNode(model, null, new TreePath(actual), actual, null, null);
    }
       
    protected XMLTreeNode createChildNode(Element node) {
        Element act = findActualElement(node);
        Element lnk = null;
        // node was not an actual node? -> it must have been a link node
        if (act == null) {
            act = node;
        } 
        else if (act != node) {
            lnk = node;
        }
        return new XMLTreeNode(this.getModel(), this,
                this.getPath().pathByAddingChild(lnk != null ? lnk : act),
                act, lnk, null);
    }
    
    protected XMLTreeNode createTypeNode(String type) {
        return new XMLTreeNode(this.getModel(), this,
                this.getPath().pathByAddingChild(type),
                null, null, type);
    }
        
    private Element findActualElement(Element node) {
        if (node != null && node.getNodeName().equals(INCLUDE_OBJECT)) {
            String name = node.getAttribute(NAME);
            
            node = findActualElement(model.getNameMap().get(name));
        }
        return node;
    }
        
    public XMLTreeModel getModel() {
        return model;
    }
    
    public Element actual() {
        return actual;
    }  
    
    public Element link() {
        return link;       
    }

    public Element effective() {
        return link != null ? link : actual;
    }
    
    public boolean checkLink() {
        return (link == null) ? true : actual == model.getNameMap().get(link.getAttribute(NAME));
    }
    
    public TreePath getPath() {
        return path;
    }
    
    /**
     * This returns the type of a category node, otherwise returns null.
     * DO NOT use with other nodes!
     * @return
     */
    public String type() {
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof XMLTreeNode)) {
            return false;
        }
        XMLTreeNode other = (XMLTreeNode) o;
        return this.path.equals(other.path);
    }
    
    @Override
    public int hashCode() {
        return path.hashCode();
    }
    /**
     * Kludge
    static public boolean similar(XMLTreeNode n1, XMLTreeNode n2) {
        if (n1.equals(n2)) {
            return true;
        }
        boolean b0 = n1.link() != null || n1.type() != null;
        boolean b1 = (n1.link() == null) || n1.link().equals(n2.link());
        boolean b2 = (n1.type() == null) || n1.type().equals(n2.type());
        return (b0 && b1 && b2) || (n1.actual() != null && n1.link() == null && n1.actual().getAttribute(NAME).equals(n2.actual().getAttribute(NAME)));
    }
     */
    
    /**
     * This method checks whether the name of the node is one of the
     * parameters.
     * @param names
     * @return 
     */
    public boolean isType(String ... names) {
        String s = getType();
        for (int i = 0, n = names.length; i < n; i++) {
            if (s.equals(names[i])) {
                return true;
            }
        }
        return false;
    }
       
    /**
     * This method returns the name of the node, e.g."objectpool".
     * @return 
     */
    public String getType() {
        return actual == null ? type : actual.getNodeName();
    }
    /**
     * This method returns the value of the "name" attribute.
     * @return 
     */
    public String getName() {
        return actual != null ? actual.getAttribute(NAME) :
            link != null ? link.getAttribute(NAME) : "N/A";
    }
    /**
     * This method returns the value of the "role" attribute.
     * @return 
     */
    public String getRole() {
        return link != null ? link.getAttribute(ROLE) : 
            actual != null ? actual.getAttribute(ROLE) : "N/A";
    }
    /**
     * This method returns the value of the "pos_x" attribute corrected by
     * other attributes that affect it (such as block_col).
     * @return 
     */
    public Integer getX() {
        Element src = (link == null) ? actual : link;
        String pos_x = src.getAttribute(POS_X);
        if (pos_x.isEmpty()) {
            return null;
        }
        int x = parseInt(pos_x);
        
        String block_col = src.getAttribute(BLOCK_COL);
        String block_font = src.getAttribute(BLOCK_FONT);
        
        if (!block_col.isEmpty() && !block_font.isEmpty()) {
            
            Element font = model.getElementByName(block_font);            
            if (font != null) {
                Dimension dim = BitmapFont.nameToDimension(font.getAttribute(FONT_SIZE));
                x += parseInt(block_col) * dim.width;
            }
        }
        return x;
    }
    /**
     * This method returns the value of the "pos_y" attribute corrected by
     * other attributes that affect it (such as block_row).
     * @return 
     */
    public Integer getY() {
        Element src = (link == null) ? actual : link;
        String pos_y = src.getAttribute(POS_Y);
        if (pos_y.isEmpty()) {
            return null;
        }
        int y = parseInt(pos_y);
        
        String block_row = src.getAttribute(BLOCK_ROW);
        String block_font = src.getAttribute(BLOCK_FONT);
        
        if (!block_row.isEmpty() && !block_font.isEmpty()) {
            
            Element font = model.getElementByName(block_font);
            if (font != null) {
                Dimension dim = BitmapFont.nameToDimension(font.getAttribute(FONT_SIZE));
                y += parseInt(block_row) * dim.height;
            }
        }
        return y;
    }
    
    /**
     * This method changes the pos_x attribute
     * FIXME should this be corrected somehow
     * @param x
     */
    public void setX(int x) {   
        Element src = (link == null) ? actual : link;        
        src.setAttribute(POS_X, Integer.toString(x));        
    }
    
     /**
     * This method changes the pos_x attribute
     * FIXME should this be corrected somehow
     * @param y
     */
    public void setY(int y) {   
        Element src = (link == null) ? actual : link;        
        src.setAttribute(POS_Y, Integer.toString(y));        
    }
    
    /**
     * This method returns the value of the "number_of_decimals" attribute.
     * @return 
     */
    public Integer getNumberOfDecimals(){
        String d = actual.getAttribute(NUMBER_OF_DECIMALS);
        return d.isEmpty() ? 0 : parseInt(d);
    }
    /**
     * This method returns the value of the "width" attribute.
     * @return 
     */
    public Integer getWidth() {
        String w = actual.getAttribute(WIDTH);
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method changes the value of the "width" attribute.
     * @param width
     */
    public void setWidth(int width) {                
        actual.setAttribute(WIDTH, Integer.toString(width));        
    }
    /**
     * This method returns the value of the "height" attribute.
     * @return 
     */
    public Integer getHeight() {
        String h = actual.getAttribute(HEIGHT);
        return h.isEmpty() ? null : parseInt(h);
    }
     /**
     * This method changes the value of the "height" attribute.
     * @param height
     */
    public void setHeight(int height) {                
        actual.setAttribute(HEIGHT, Integer.toString(height));        
    }
    /**
     * This method returns the value of the "start_angle" attribute.
     * @return 
     */
    public Integer getStartAngle() {
        String w = actual.getAttribute(START_ANGLE);
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method changes the value of the "start_angle" attribute.
     * @param angle
     */
    public void setStartAngle(int angle) {        
        actual.setAttribute(START_ANGLE, Integer.toString(angle));
    }
    /**
     * This method returns the value of the "end_angle" attribute.
     * @return 
     */
    public Integer getEndAngle() {
        String h = actual.getAttribute(END_ANGLE);
        return h.isEmpty() ? null : parseInt(h);
    }
    /**
     * This method changes the value of the "end_angle" attribute.
     * @param angle
     */
    public void setEndAngle(int angle) {        
        actual.setAttribute(END_ANGLE, Integer.toString(angle));
    }
    /**
     * This method returns the value of the "line_direction" attribute.
     * @return 
     */
    public Boolean getLineDirection() {
        String d = actual.getAttribute(LINE_DIRECTION);
        if (d.isEmpty() || d.equals("0") || d.equals("toplefttobottomright")) { 
            return false; 
        } 
        else if (d.equals("1") || d.equals("bottomlefttotopright")) { 
            return true; 
        }
        else { 
            return null;
        }
    }
    /**
     * This method sets the value of the "line_direction" attribute.
     * @param lineDirection
     */
    public void setLineDirection(boolean lineDirection) {
        if (lineDirection && !getLineDirection())
            actual.setAttribute(LINE_DIRECTION, "bottomlefttotopright");
        if (!lineDirection && getLineDirection())
            actual.setAttribute(LINE_DIRECTION, "toplefttobottomright");       
    }
    
    /**
     * This method returns the value of the "actual_width" attribute.
     * FIXME: this is never used, actual witdh is read from the file?
     * @return
     */
    public Integer getActualWidth() {
        String w = actual.getAttribute("actual_width");
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method returns the value of the "actual_height" attribute.
     * FIXME: this is never used, actual height is read from the file?
     * @return
     */
    public Integer getActualHeight() {
        String h = actual.getAttribute("actual_height");
        return h.isEmpty() ? null : parseInt(h);
    }
    /**
     * This method returns the value of the "dimension" attribute of the 
     * objectpool root element.
     * @return 
     */
    public Integer getDimension() {
        String d = actual.getAttribute(DIMENSION);
        return d.isEmpty() ? null : parseInt(d);
    }
    /**
     * This method returns the value of the "sk_width" attribute of the 
     * objectpool root element.
     * @return 
     */
    public Integer getSKWidth() {
        String s = actual.getAttribute(SK_WIDTH);
        return s.isEmpty() ? null : parseInt(s);
    }
    /**
     * This method returns the value of the "sk_height" attribute of the 
     * objectpool root element.
     * @return 
     */
    public Integer getSKHeight() {
        String s = actual.getAttribute(SK_HEIGHT);
        return s.isEmpty() ? null : parseInt(s);
    }
    /**
     * This method returns the value of the "fix_bitmap_path" attribute of the 
     * objectpool root element.
     * @return 
     */
    public String getFixBitmapPath() {
        String f = actual.getAttribute(FIX_BITMAP_PATH);
        return f.isEmpty() ? null : f; // FIXME: use a more decent default value?
    }
    /**
     * This method returns the value of the "std_bitmap_path" attribute of the 
     * objectpool root element.
     * @return 
     */
    public String getStdBitmapPath() {
        String s = actual.getAttribute(STD_BITMAP_PATH);
        return s.isEmpty() ? null : s; // FIXME: use a more decent default value?
    }
    /**
     * This method determines what text is shown in the tree view.
     */
    @Override
    public String toString() {
        if (type != null) {
            return type;
        }
        String bs = getName();
        String tp = getType();
        
        if (tp == null) {
            // do nothing
        }
        else if (tp.equals(OBJECTPOOL)) {
            bs = tp;
        }
        else if (tp.equals(LANGUAGE)) {
            bs = tp + ": " + getCode();
        }
        else if (tp.equals(POINT)) {
            bs = tp;
        }
        else if (tp.equals(FIXEDBITMAP)) {
            bs = tp;
        }
        else if (tp.equals(OUTPUTSTRING) ||
                tp.equals(INPUTSTRING) ||
                tp.equals(STRINGVARIABLE)) {
            
            bs += ": \"" + getValue() + "\"";
        } 
        else if (tp.equals(NUMBERVARIABLE)) {
            bs += ": " + getValue();
        }
        else if (tp.startsWith(COMMAND)) {
            bs = tp.substring(tp.indexOf('_') + 1, tp.length());
        }
        Integer x = getX();
        Integer y = getY();
        if (x == null || y == null) {
            return bs;
        } 
        else {
            return "(" + x + ", " + y + ") " + bs;
        }
    }
    /**
     * This method returns the value of the "font_size" attribute.
     * @return 
     */
    public Dimension getFontSize() {
        String s = actual.getAttribute(FONT_SIZE);
        return s.isEmpty() ? BitmapFont.indexToDimension(0) : BitmapFont.nameToDimension(s);
    }
     
    /**
     * This method returns the value of the "font_size" attribute.
     * @return 
     */
    public String getFontSizeString() {
        String f = actual.getAttribute(FONT_SIZE);
        return f.isEmpty() ? BitmapFont.indexToName(0) : f;
    }
    
    public int getJustification(){
        if (isJustificationMiddle()) {
            return BitmapFont.JUSTIFICATION_MIDDLE;
        }
        else if (isJustificationRight()) {
            return BitmapFont.JUSTIFICATION_RIGHT;
        }
        else {
            return BitmapFont.JUSTIFICATION_LEFT;
        }
    }
    
    public boolean isJustificationMiddle() {
        String s = actual.getAttribute(HORIZONTAL_JUSTIFICATION);
        return (s.equals("middle") || s.equals("1"));
    }
    
    public boolean isJustificationRight() {
        String s = actual.getAttribute(HORIZONTAL_JUSTIFICATION);
        return (s.equals("right") || s.equals("2"));
    }
    
    /**
     * This method returns a integer representing the line supression of a rectangle     
     * @return 
     */
    public int getLineSuppression() {
        String s = actual.getAttribute(LINE_SUPPRESSION);
        if (Integer.getInteger(s) != null) {
            return Integer.parseInt(s);
        }
        int value = 0;
        if (s.contains("top")) value += 1;
        if (s.contains("right")) value += 2;
        if (s.contains("bottom")) value += 4;
        if (s.contains("left")) value += 8;
        
        return value;
    }
    
    /**
     * This method returns the value of the "fill_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getFillColor(int colorDepth) {
        String f = actual.getAttribute(FILL_COLOUR);
        return f.isEmpty() ? Color.GRAY : ColorPalette.getColor(f, colorDepth);
    }
    /**
     * This method returns the value of the "colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getColor(int colorDepth) {
        String c = actual.getAttribute(COLOUR);
        return c.isEmpty() ? Color.GRAY : ColorPalette.getColor(c, colorDepth);
    }
    /**
     * This method returns the value of the "font_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getFontColor(int colorDepth) {
        String c = actual.getAttribute(FONT_COLOUR);
        return c.isEmpty() ? Color.GRAY : ColorPalette.getColor(c, colorDepth);
    }
    /**
     * This method returns the value of the "background_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getBackgroundColor(int colorDepth) {
        String c = actual.getAttribute(BACKGROUND_COLOUR);
        return c.isEmpty() ? Color.GRAY : ColorPalette.getColor(c, colorDepth);
    }
    /**
     * This method returns the value of the "line_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getLineColor(int colorDepth) {
        String c = actual.getAttribute(LINE_COLOUR);
        return c.isEmpty() ? Color.GRAY : ColorPalette.getColor(c, colorDepth);
    }
    /**
     * This method returns the value of the "transparency_colour" attribute.
     * @param reduceImages
     * @param colorDepth
     * @return 
     */
    public Color getTransparencyColor(boolean reduceImages, int colorDepth) {
        String c = actual.getAttribute(TRANSPARENCY_COLOUR);
        return c.isEmpty() ? Color.GRAY : ColorPalette.getColor(c, reduceImages ?
                colorDepth : ColorPalette.COLOR_8BIT);
    }
    
    /**
     * This method returns the value of the line stroke parameter.
     * @return
     */
    public BasicStroke getLineStroke() {
        float w = Float.parseFloat(actual.getAttribute(LINE_WIDTH));
        String lineArt = actual.getAttribute(LINE_ART);
        
        // parse lineart
        float[] dash = new float[32];
        for (int i = 0; i < 16; i++) {
            if (lineArt.charAt(i) == '0') {
                dash[2 * i] = 0.0f;
                dash[2 * i + 1] = 1.0f * (float) w;
            } 
            else {
                dash[2 * i] = 1.0f * (float) w;
                dash[2 * i + 1] = 0.0f;
            }
        }
        // the default attributes are a solid line of width 1.0, CAP_SQUARE, 
        // JOIN_MITER, a miter limit of 10.0.
        //return new BasicStroke(w, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        
        //if CAP_SQUARE is used dashing might not be visible at all!!
        return new BasicStroke(w, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
    }

    /**
     * This method returns the value of the "fill_type" attribute.
     * @return 
     */
    public String getFillType() {
        String f = actual.getAttribute(FILL_TYPE);
        return f.isEmpty() ? "nofill" : f;
    }
    
    /**
     * Opens the image
     * Attributes are searched in this order: file8, file, file4, file1
     * @return 
     * @throws java.io.IOException
     */
    public BufferedImage getImageFile() throws IOException {
        XMLTreeNode root = (XMLTreeNode) model.getRoot();
        return getImageFile(root.getStdBitmapPath());
    }
        
    private BufferedImage getImageFile(String imagepath) throws IOException {
        BufferedImage image;
        if ((image = getImageFile(FILE8, imagepath)) != null ||
            (image = getImageFile(FILE, imagepath)) != null ||
            (image = getImageFile(FILE4, imagepath)) != null ||      
            (image = getImageFile(FILE1, imagepath)) != null) {            
            return image;
        }        
        return null;
    }
    
    private BufferedImage getImageFile(String fileAttr, String imagepath) throws IOException {
        String s = actual.getAttribute(fileAttr);
        File f;
        if (s.isEmpty()) {
            return null;
        }
        try {
            //all \ are converted to / on a unix system and vice versa
            String fullname = FileTools.joinPaths(imagepath, s);
            f = new File(fullname);
            return ImageIO.read(f);
        }
        catch (IOException ex) {
            BufferedImage img = new BufferedImage(64, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gfx = (Graphics2D) img.getGraphics();
            gfx.setColor(Color.RED);
            gfx.fillRect(0, 0, 64, 40);
            gfx.setColor(Color.YELLOW);
            gfx.drawRect(0, 0, 63, 39);
            gfx.drawString("MISSING", 5, 15);
            gfx.drawString("IMAGE", 5, 35);
            return img;
            //throw new IOException(ex.getMessage() + " " + f);
        }
    }
      
    /**
     * This method returns the value of the "ellipse_type" attribute.
     * @return 
     */
    public Integer getEllipseType() {
        String e = actual.getAttribute(ELLIPSE_TYPE);
        // the cast (integer) is needed, otherwise auto-boxing does not work
        switch (e) {
            case "open":
                return Arc2D.OPEN;
            case "closed":
                return null;
            case "closedsegment":
                return Arc2D.PIE;
            case "closedsection":
                return Arc2D.CHORD;
            default:
                return null; /* default */
        }
    }
    
    public boolean isHidden() {
        return isTrue(actual.getAttribute(HIDDEN));
    }
    
    public boolean isSelectable() {
        return isTrue(actual.getAttribute(SELECTABLE));
    }
    
    static private boolean isTrue(String value) {
        final String[] TRUE = new String[] {"yes", "true", "on", "show", "enable", "1"};
        final String[] FALSE = new String[] {"no", "false", "off", "hide", "disable", "0"};
        for (int i = 0, n = TRUE.length; i < n; i++) {
            if (TRUE[i].equals(value)) {
                return true;
            }
        }
        for (int i = 0, n = FALSE.length; i < n; i++) {
            if (FALSE[i].equals(value)) {
                return false;
            }
        }
        throw new RuntimeException("expecting a truth value (" + value + ")");
    }
    
    public boolean isPolygonTypeOpen() {
        return actual.getAttribute(POLYGON_TYPE).equals("open");
    }
    
    //Outputmeter and linear bar graph methods
    public boolean isOptionsArc() {
        return actual.getAttribute(OPTIONS).contains("arc");
    }
    
    public boolean isOptionsBorder() {
        return actual.getAttribute(OPTIONS).contains("border");
    }
    
    public boolean isOptionsTicks() {
        return actual.getAttribute(OPTIONS).contains("ticks");
    }
    /**
     * Returns whether of not no fill option is specified for Linear and 
     * arched bar graphs.
     * @return 
     */
    public boolean isOptionsNoFill() {
        return actual.getAttribute(OPTIONS).contains("nofill");
    }
    
    public boolean isOptionsTargetLine() {
        return actual.getAttribute(OPTIONS).contains("targetline");
    }
    
    public boolean isOptionsHorizontal() {
        return actual.getAttribute(OPTIONS).contains("horizontal");
    }
    
    public void changeOptionsHorizontal(boolean horizontal) {
        String options = actual.getAttribute(OPTIONS);
        
        if (horizontal && !options.contains("horizontal")) {
            options = options + "+horizontal";
            actual.setAttribute(OPTIONS, options);
        }
        
        if (!horizontal && options.contains("horizontal")) {
            options = options.replace("+horizontal", "");
            options = options.replace("horizontal", "");
            actual.setAttribute(OPTIONS, options);
        }
    }
    
    public boolean isOptionsGrowPositive() {
        return actual.getAttribute(OPTIONS).contains("growpositive");
    }
    
    public void changeOptionsGrowPositive(boolean growPositive) {
        String options = actual.getAttribute(OPTIONS);
        
        if (growPositive && !options.contains("growpositive")) {
            options = options + "+growpositive";
            actual.setAttribute(OPTIONS, options);
        }
        
        if (!growPositive && options.contains("growpositive")) {
            options = options.replace("+growpositive", "");
            options = options.replace("growpositive", "");
            actual.setAttribute(OPTIONS, options);
        }
    }
    
    public boolean isOptionsClockwise() {
        return actual.getAttribute(OPTIONS).contains("clockwise");
    }
    
    public void changeOptionsClockwise(boolean clockwise) {
        String options = actual.getAttribute(OPTIONS);
        
        if (clockwise && !options.contains("clockwise")) {
            options = options + "+clockwise";
            actual.setAttribute(OPTIONS, options);
        }
        
        if (!clockwise && options.contains("clockwise")) {
            options = options.replace("+clockwise", "");
            options = options.replace("clockwise", "");
            actual.setAttribute(OPTIONS, options);
        }
    }
    
    public boolean isOptionsTransparent(){
        return actual.getAttribute(OPTIONS).contains("transparent");
    }
    
    public boolean isOptionsFlashing() {
        return actual.getAttribute(OPTIONS).contains("flashing");
    }
    
    public boolean isFontStyleFlashingHidden() {
        return actual.getAttribute(FONT_STYLE).contains("flashinghidden");
    }
    
    public boolean isFontStyleFlashingInverted() {
        return actual.getAttribute(FONT_STYLE).contains("flashinginverted");
    }
    
    public boolean isFontStyleInverted() {
        String fontStyle = actual.getAttribute(FONT_STYLE);
        StringTokenizer st = new StringTokenizer(fontStyle, "+");
        while (st.hasMoreTokens()) {
            if (st.nextToken().equals("inverted")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method returns the value of the "autowrap" bit in the "code" attribute.
     * @return 
     */
    public boolean isOptionsAutoWrap() {
        return actual.getAttribute(OPTIONS).contains("autowrap");
    }
    
     /**
     * This method returns the value of the "format" attribute.
     * @return 
     */
    public boolean isExponentialFormat() {
        String f = actual.getAttribute(FORMAT);
        return f.equals("exponential") || f.equals("1");
    }
    
    /**
     * This method returns the value of the "leadingzeros" bit in the options
     * @return 
     */
    public boolean isOptionsDisplayLeadingZeros() {
        return actual.getAttribute(OPTIONS).contains("leadingzeros");
    }
    
    /**
     * This method returns the value of the "blankzero" bit in the options
     * @return 
     */
    public boolean isOptionsBlankZero() {
        return actual.getAttribute(OPTIONS).contains("blankzero");
    }
    
    /**
     * This method returns the value of the "needle_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getNeedleColor(int colorDepth) {
        String n = actual.getAttribute(NEEDLE_COLOUR);
        return n.isEmpty() ? Color.BLACK : ColorPalette.getColor(n, colorDepth);
    }
    /**
     * This method returns the value of the "border_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getBorderColor(int colorDepth) {
        String b = actual.getAttribute(BORDER_COLOUR);
        return b.isEmpty() ? Color.BLACK : ColorPalette.getColor(b, colorDepth);
    }
    /**
     * This method returns the value of the "target_line_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getTargetLineColor(int colorDepth) {
        String t = actual.getAttribute(TARGET_LINE_COLOUR);
        return t.isEmpty() ? Color.BLACK : ColorPalette.getColor(t, colorDepth);
    }
    /**
     * This method returns the value of the "arc_and_tick_colour" attribute.
     * @param colorDepth
     * @return 
     */
    public Color getArcAndTickColor(int colorDepth) {
        String a = actual.getAttribute(ARC_AND_TICK_COLOUR);
        return a.isEmpty() ? Color.BLACK : ColorPalette.getColor(a, colorDepth);
    }
    /**
     * This method returns the value of the "min_value" attribute.
     * @return 
     */
    public Integer getMinValue() {
        String w = actual.getAttribute(MIN_VALUE);
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method returns the value of the "max_value" attribute.
     * @return 
     */
    public Integer getMaxValue() {
        String w = actual.getAttribute(MAX_VALUE);
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method returns the value of the "number_of_ticks" attribute.
     * @return 
     */
    public Integer getTicks() {
        String w = actual.getAttribute(NUMBER_OF_TICKS);
        return w.isEmpty() ? null : parseInt(w);
    }
    /**
     * This method returns the value of the "bar_graph_width" attribute.
     * @return 
     */
    public int getBargraphWidth() {
        String w = actual.getAttribute(BAR_GRAPH_WIDTH);
        return w.isEmpty() ? 0 : parseInt(w);
    }
    /**
     * This method returns the value of the "scale" attribute.
     * @return 
     */
    public double getScale() {
        String w = actual.getAttribute(SCALE);
        return w.isEmpty() ? 1.0 : Double.parseDouble(w);
    }
    /**
     * This method returns the value of the "offset" attribute.
     * @return 
     */
    public int getOffset(){
        String w = actual.getAttribute(OFFSET);
        return w.isEmpty() ? 0 : parseInt(w);
    }
    /**
     * This method returns the value of the "value" attribute.
     * @return 
     */
    public String getValue() {
        return actual.getAttribute(VALUE);
    }    
    /**
     * This method returns the value of the "value" attribute parsed as 
     * integer. 
     * @return 
     */
    public Integer getValueInt() {
        String v = actual.getAttribute(VALUE);
        return v.isEmpty() ? null : parseInt(v);
    }
    
    /**
     * This method returns the value of the "target_value" attribute.
     * @return 
     */
    public Integer getTargetValueInt() {
        String v = actual.getAttribute(TARGET_VALUE);
        return v.isEmpty() ? null : parseInt(v);
    }
    /**
     * This method returns the value of the "code" attribute.
     * @return 
     */
    public String getCode() {
        return actual.getAttribute(CODE);
    }
    /**
     * This method returns the value of the "variable_reference" attribute.
     * @return 
     */
    public String getVariableReferenceName() {
        String n = actual.getAttribute(VARIABLE_REFERENCE);
        return n.isEmpty() ? null : n;
    }
    /**
     * This method returns the value of the "target_value_variable_reference" attribute.
     * @return 
     */
    public String getTargetValueVariableReferenceName() {
        String n = actual.getAttribute(TARGET_VALUE_VARIABLE_REFERENCE);
        return n.isEmpty() ? null : n;
    }
    /**
     * Returns a Paint-object which can be used for painting
     * @param lineColor
     * @param imagePath
     * @param errPaint
     * @param reduceImages
     * @param colorDepth
     * @return
     * @throws IOException
     */
    public Paint getFillPaint(Color lineColor, String imagePath, Paint errPaint,
            boolean reduceImages, int colorDepth) throws IOException {
        String fillType = this.getFillType();
        switch (fillType) {
            case "fillcolour":
                return this.getFillColor(colorDepth);
            case "linecolour":
                return lineColor;
            case "pattern":
                BufferedImage image = null;
                for (int j = 0, m = model.getChildCount( this ); j < m; j++) {
                    XMLTreeNode nd2 = (XMLTreeNode) model.getChild(this, j);
                    if (nd2.isType(PICTUREGRAPHIC)){
                        image = nd2.getImageFile();
                        Color transparencyColor = nd2.getTransparencyColor(reduceImages, colorDepth);
                        image = PictureConverter.applyTransparencyAndReduceColors(image,
                                transparencyColor, nd2.isOptionsTransparent(),
                                reduceImages, colorDepth);
                    }
                }
                return image == null ? errPaint :
                        new TexturePaint(image, new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
            default:
                return null;
        }
    }
    
    /**
     * Gets the value of the "value" attribute from the number variable child
     * @param role
     * @return 
     */
    public Integer getNumberVariableValue(String role) {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i);
            if (nd.isType(NUMBERVARIABLE) && nd.getRole().equals(role)) {
                return parseInt(nd.getValue());
            }
        }
        return null;
    }
    /**
     * Gets the value of the "value" attribute from the string variable child
     * @return 
     */
    public String getStringVariableValue() {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i);
            if (nd.isType(STRINGVARIABLE)) {                
                return nd.getValue();
            }
        }
        return null;
    }
    
    public XMLTreeNode getFontAttributes() {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(FONTATTRIBUTES)) {
                return nd;
            }	    	        
	}
        return null;
    }
        
    /**
     * Gets the value of the "font_colour" attribute from the font attributes child
     * (for input boolean field)
    public Color getFontAttributesColor(int colorDepth) {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(FONTATTRIBUTES)) {
                return nd.getFontColor(colorDepth);
            }	    	        
	}
        return null;
    }
    */
    
    /**
     * Gets the font.
     * @param colorDepth
     * @return
     */
    public BitmapFont getFont(int colorDepth) {
        return new BitmapFont(getFontSizeString(), getFontColor(colorDepth));
    }
    
    /**
     * Gets the font attributes from the font attributes child
     *
    public BitmapFont getFontAttributesFont(int colorDepth) {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(FONTATTRIBUTES)) {
                return new BitmapFont(nd.getFontSizeString(), nd.getFontColor(colorDepth));
            }	    	        
	}
        return null;
    }
     */
    /**
     * Gets the line attributes from the line attributes child
     * @param colorDepth
     * @return 
     */
    public LineAttributes getLineAttributes(int colorDepth) {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
	    if (nd.isType(LINEATTRIBUTES)) {
		return new LineAttributes(nd.getLineColor(colorDepth), nd.getLineStroke());
	    }	   
        }
        return null;
    }
    /**
     * Gets the fill attributes from the fill attributes child
     * @param lineColor
     * @param imagepath
     * @param errPaint
     * @param reduceImages
     * @param colorDepth
     * @return
     * @throws IOException
     */
    public Paint getFillAttributesPaint(Color lineColor, String imagepath,
            Paint errPaint, boolean reduceImages, int colorDepth) throws IOException {
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i);
	    if (nd.isType(FILLATTRIBUTES)) {
                return nd.getFillPaint(lineColor, imagepath, errPaint, reduceImages, colorDepth);
	    }
        }
        return null;
    }
    /**
     * Gets the polygon formed from the point children
     * @return 
     */
    public Polygon getPolygon() {
        Polygon p = new Polygon();
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(POINT)) {
		p.addPoint(nd.getX(), nd.getY());
            }
        }
        return p;
    }
    /**
     * Returns an array that has all points of polygon (x,y)-pairs
     * @return 
     */
    public int[] getPolygonPoints() {
        // calculate the number of points
        int pointsN = 0;
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(POINT)) {
		pointsN++;
            }
        }
        int[] points = new int[2*pointsN];
        int index = 0;
        for (int i = 0, n = model.getChildCount(this); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i); 
            if (nd.isType(POINT)) {
                points[2 * index] = nd.getX();                
                points[2 * index + 1] = nd.getY();
                index++;
            }
        }
        return points;
    }
    
    /**
     * Sets the wanted point of a polygon
     * @param index
     * @param x
     * @param y
     */
    public void setPolygonPoint(int index, int x, int y) {
        int index2 = 0;
        for (int i = 0,  n = model.getChildCount(this); i < n; i++) {
            XMLTreeNode nd = (XMLTreeNode) model.getChild(this, i);
            if (nd.isType(POINT)) {
                if (index2 == index) {
                    nd.setX(x);
                    nd.setY(y);
                }
                index2++;
            }
        }
    }
    
    /**
     * There is a bug in Integer.parseInt - it does not handle "positive"
     * strings e.g. "+123"
     * @param value
     * @return 
     */
    static public int parseInt(String value) {
        if (value.startsWith("+")) {
            value = value.substring(1);
        }
        return Integer.parseInt(value);
    }

    /**
     * Formats number (without leading zeros)
     * @return 
     */
    public String getFormatedNumber() {
        int number_of_decimals = getNumberOfDecimals();
        String format = "0";
        if (isExponentialFormat()) {
            // exponential format mantissa is allways 1-digit long (not zero)
            if (number_of_decimals > 0) {
                format = "0.";            
            }
            for (int i = 0; i < number_of_decimals; i++) {
                format += "0";
            }
            format += "E0";            
        }
        else {
            // fixed format            
            if (number_of_decimals > 0) {
                format = "0.";
            }
            for (int i = 0; i < number_of_decimals; i++) {
                format += "0";  
            }
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format((getEffectiveValue() + getOffset()) * getScale());
    }

    /**
     * Returns the value of the variable reference, if defined, otherwise
     * returns the embedded value. 
     * @return 
     */ 
    public Integer getEffectiveValue() {
        Integer value = getNumberVariableValue(VARIABLE_REFERENCE); //getVariableReferenceName());
        if (value == null) {
            value = getValueInt();
        }
        return value;
    }
    
    /**
     * Returns the value of the target variable reference, if defined, otherwise
     * returns the embedded target value. 
     * @return 
     */ 
    public Integer getEffectiveTargetValue() {
        Integer targetLineValue = getNumberVariableValue(TARGET_VALUE_VARIABLE_REFERENCE);
        if (targetLineValue == null) { // && isOptionsTargetLine()) {
            targetLineValue = getTargetValueInt();
        }
        return targetLineValue;
    }

    /**
     * Returns the size of this node.For example the size of a container
     * is obtained by calling getWidth() and getHeight() methods, but the
     * size of a meter is its width times width.The sizes of picture graphic
     * and object pointer objects are more difficult to calculate.
     * @param dim
     * @return 
     */
    public Dimension getNodeSize(Dimension dim) {
        if (isType(WORKINGSET, KEY, AUXILIARYFUNCTION, AUXILIARYINPUT)) {
            XMLTreeNode root = (XMLTreeNode) model.getRoot();
            dim.setSize(root.getSKWidth(), root.getSKHeight());
	}
	else if (isType(DATAMASK, ALARMMASK)) {
            XMLTreeNode root = (XMLTreeNode) model.getRoot();
            int d = root.getDimension();
            dim.setSize(d, d);
	}
	else if (isType(SOFTKEYMASK)) {
            XMLTreeNode root = (XMLTreeNode) model.getRoot();
            dim.setSize(root.getSKWidth(), model.getChildCount(actual) * root.getSKHeight());
	}
        else if (isType(CONTAINER, BUTTON, INPUTSTRING, INPUTNUMBER, INPUTLIST,
                OUTPUTSTRING, OUTPUTNUMBER, LINE, RECTANGLE, ELLIPSE, POLYGON,
                LINEARBARGRAPH, ARCHEDBARGRAPH)) {
	    dim.setSize(getWidth(), getHeight());
	}
        else if (isType(INPUTBOOLEAN, METER)) {
            int w = getWidth();
            dim.setSize(w, w);
        }
        else if (isType(PICTUREGRAPHIC)) {
            try {
                BufferedImage image = getImageFile(); // for size calculation only
                if (image != null) {
                    int w = getWidth();
                    int h = w * image.getHeight() / image.getWidth();
                    dim.setSize(w, h);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
                dim.setSize(64, 64); // questimate picture size?
            }
        }
        else if (isType(OBJECTPOINTER)) {
            // should be only one
            for (int i = 0, n = model.getChildCount(this); i < n; i++) {
		XMLTreeNode nd = (XMLTreeNode) model.getChild(actual, i); 
                nd.getNodeSize(dim);
            }
        }
        else if (isType(POINT)) {
            dim.setSize(1, 1);
        }
        else {
            dim.setSize(0, 0); // object has no size?
        }
        return dim;
    }   
}
