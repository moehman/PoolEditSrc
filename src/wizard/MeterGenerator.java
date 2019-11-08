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
import treemodel.XMLTreeNode;

/**
 *
 * @author  jkalmari
 */
public class MeterGenerator {
    
    private final MeterWizard wiz;    
    private final XMLTreeNode root;
    private final String name;
    
    //A list of elements that are created in process
    //private List<Element> elements = new ArrayList<Element>();    
    
    /** 
     * Creates a new instance of MeterGenerator 
     * father is the father-elment where the meter should be added
     * @param meterWizard
     * @param root
     * @param name
     */    
    public MeterGenerator(MeterWizard meterWizard, XMLTreeNode root, String name) {
        this.wiz = meterWizard;         
        this.root = root;
        this.name = name;
        meterWizard.setMeterGenerator(this);        
    }
    
    public XMLTreeNode getRoot() {
        return root;
    }
    
    /**
     *  This method is called when the meter should be updated
     */
    public void update() {        
        // create container
        Element container = createContainer(root.actual(), name, 
                wiz.getTotalWidth(), wiz.getTotalHeight());
        
        if (root.isType(OBJECTS)) {
            setAttributeIfMissing(container, POS_X, "0");
            setAttributeIfMissing(container, POS_Y, "0");        
        }
        
        // create background circle
        int startAng = wiz.getStartAngle();
        int endAng = wiz.getEndAngle();
        Element ellipse;        
        ellipse = createEllipse(container, "background", wiz.getBackgroundWidth(), wiz.getBackgroundWidth(),
                        startAng, endAng,
                        wiz.getFillAttribute(), wiz.getLineAttribute(), !wiz.isCuttedCircle());        
        setIncludeAttributes(ellipse, (wiz.getTotalWidth() - wiz.getBackgroundWidth()) / 2, 
                (wiz.getTotalHeight() - wiz.getBackgroundWidth()) / 2);
        
        //create meter        
        int minVal = (int) (wiz.getMinValue() / wiz.getScale()) - wiz.getOffset();
        int maxVal = (int) (wiz.getMaxValue() / wiz.getScale()) - wiz.getOffset();
        Element meter = createMeter(container, "metercomp", wiz.getMeterWidth(),  
                startAng, endAng, minVal, maxVal,
                wiz.getValue(), wiz.getTicks(), wiz.isClockwise(),
                wiz.getNeedleColor(), wiz.getArcAndTickColor(), wiz.getNumberReference());
        setIncludeAttributes(meter, (wiz.getTotalWidth() - wiz.getMeterWidth()) / 2,
                (wiz.getTotalHeight() - wiz.getMeterWidth()) / 2);
        
        // create numbers
        if(startAng > endAng) startAng -= 360;
        if( wiz.isClockwise() ) {
            int temp = startAng;
            startAng = endAng;
            endAng = temp;
        }
        Element fontElement = root.getModel().getElementByName(wiz.getFontAttribute());
        int fontWidth = (int) BitmapFont.nameToDimension(fontElement.getAttribute(FONT_SIZE)).getWidth();
        int fontHeight = (int) BitmapFont.nameToDimension(fontElement.getAttribute(FONT_SIZE)).getHeight();
        int i;
        for (i = 0; i < wiz.getNumbers() && wiz.getNumbers() > 1; i++) {
            int value = wiz.getMinValue() + i*(wiz.getMaxValue()-wiz.getMinValue()) / (wiz.getNumbers()-1);            
            String valueString = Integer.toString(value);
            int stringWidth = (fontWidth * valueString.length());
            
            Element number = createString(container, "label" + i, fontWidth * valueString.length(), 
                    fontHeight, valueString, wiz.getFontAttribute());
            
            double ang = Math.toRadians(startAng + i * (endAng - startAng) / (wiz.getNumbers() - 1));
            double rad = wiz.getMeterWidth() / 2 + wiz.getNumberDistance();
            int x = wiz.getTotalWidth() / 2 + (int) (Math.cos(-ang) * rad) - stringWidth / 2;
            int y = wiz.getTotalHeight() / 2 + (int) (Math.sin(-ang) * rad) - fontHeight / 2;
            setIncludeAttributes(number, x, y);
        }
        
        // labelX, X >= i -> poistetaan
        removeExtraElements(container, "label", i);
        
        // create a numberfield
        int extraSpace = wiz.getOffset() < 0 ? 1 : 0;
        int nroWidth = fontWidth * (Integer.toString(wiz.getMaxValue()).length() + extraSpace);
        Element numberfield = createNumber(container, "number", nroWidth, fontHeight, wiz.getValue(), 
                wiz.getNumberReference(), wiz.getFontAttribute(), wiz.getOffset(), wiz.getScale());
        setIncludeAttributes(numberfield, (wiz.getTotalWidth() - nroWidth/*meterWizard.getMeterWidth()*/) / 2, 
                wiz.getTotalHeight() / 2 + wiz.getMeterWidth() / 4);
        
        // create heading
        String text = wiz.getHeading();
        Element heading = createString(container, "title", fontWidth*text.length(), fontHeight, text, wiz.getFontAttribute());
        setIncludeAttributes(heading, (wiz.getTotalWidth() - fontWidth * text.length()) / 2, 
                wiz.getTotalHeight() / 2 - wiz.getMeterWidth() / 4);
        
        // moves the attribute-objects to include objects with roles
        // System.out.println("finalizing...");
        // Tools.createRoles(container);
    }
    
    /**
     * Creates a new element to document and adds it as a child
     * if name is null a new name is created
     * if father is null, element will be added to root
    private static Element createElement(String type, String name, Element father) {
  
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
                "C:\\pooledit\\schema\\iso11783.xsd");
        Element elem = (Element) father.getOwnerDocument().adoptNode(tmp.getDocumentElement()).getFirstChild();
        elem.setAttribute(NAME, name);
        father.appendChild(elem);        
        return elem;        
    }
     */
    
    /*
    private static Element createContainer(Element father, String name, int width, int height)  {
        Element container = createElement(CONTAINER, name, father);        
        setAttribute(container, WIDTH, width);
        setAttribute(container, HEIGHT, height);
        return container;
    } 
     */  
    private static Element createEllipse(Element father, String name, int width, int height, 
            int startAngle, int endAngle, String fillAttribute, String lineAttribute, boolean closed)  {
        
        Element ellipse = createElement(ELLIPSE, name, father, false);
        setAttribute(ellipse, WIDTH, width);
        setAttribute(ellipse, HEIGHT, height);
        setAttribute(ellipse, START_ANGLE, startAngle);
        setAttribute(ellipse, END_ANGLE, endAngle);
        setAttribute(ellipse, ELLIPSE_TYPE, closed ? "closed" : "closedsection");
        //setAttribute(ellipse, FILL_ATTRIBUTES, fillAttribute); 
        //setAttribute(ellipse, LINE_ATTRIBUTES, lineAttribute);
        
        createIncludeRoleElement(fillAttribute, FILL_ATTRIBUTES, ellipse);
        createIncludeRoleElement(lineAttribute, LINE_ATTRIBUTES, ellipse);
        return ellipse;
    }
    
    private static Element createMeter(Element father, String name, int width, int startAngle, 
            int endAngle, int minValue, int maxValue, int value, int ticks, boolean clockwise, 
            String needleColor, String arcAndTickColor, String variableReference) {
        
        Element meter = createElement(METER, name, father, false);
        setAttribute(meter, WIDTH, width);
        //setAttribute(meter, HEIGHT, height); // METER DOES NOT HAVE SEPARATE HEIGHT FIELD!
        setAttribute(meter, START_ANGLE, startAngle);
        setAttribute(meter, END_ANGLE, endAngle);
        setAttribute(meter, MIN_VALUE, minValue);
        setAttribute(meter, MAX_VALUE, maxValue);
        setAttribute(meter, VALUE, value);
        setAttribute(meter, NUMBER_OF_TICKS, ticks);
        setAttribute(meter, OPTIONS,"ticks+arc" + (clockwise ? "+clockwise" : ""));
        setAttribute(meter, NEEDLE_COLOUR, needleColor);
        setAttribute(meter, ARC_AND_TICK_COLOUR, arcAndTickColor);
        //setAttribute(meter, VARIABLE_REFERENCE, variableReference);
        
        createIncludeRoleElement(variableReference, VARIABLE_REFERENCE, meter);
        return meter;
    }

    private static Element createNumber(Element father, String name, int width, int height, int value, 
            String variableReference, String fontAttribute, int offset, double scale)  {
        
        Element number = createElement(OUTPUTNUMBER, name, father, false);
        setAttribute(number, WIDTH, width);
        setAttribute(number, HEIGHT, height);       
        setAttribute(number, VALUE, value);         
        setAttribute(number, HORIZONTAL_JUSTIFICATION, "middle");        
        setAttribute(number, OPTIONS, "transparent");
        setAttribute(number, SCALE, scale);
        setAttribute(number, OFFSET, offset);
        //setAttribute(number, NUMBER_OF_DECIMALS, "0");
        setAttribute(number, BACKGROUND_COLOUR, "white");
        
        //setAttribute(number, FONT_ATTRIBUTES, fontAttribute);
        //setAttribute(number, VARIABLE_REFERENCE, variableReference);
        
        createIncludeRoleElement(fontAttribute, FONT_ATTRIBUTES, number);
        createIncludeRoleElement(variableReference, VARIABLE_REFERENCE, number);
        return number;
    }
}
