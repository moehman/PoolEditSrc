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

import static wizard.WizardTools.*;
import static pooledit.Definitions.*;
import font.BitmapFont;
import java.awt.Dimension;
import org.w3c.dom.Element;
import pooledit.Definitions;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class TrendGenerator {

    private final TrendWizard wiz;
    private final XMLTreeNode root;
    private final String name;
    
    /**
     * Creates a new instance of TrendGenerator
     * @param wiz
     * @param root
     * @param name
     */
    public TrendGenerator(TrendWizard wiz, XMLTreeNode root, String name) {
        this.wiz = wiz;         
        this.root = root;
        this.name = name;
        wiz.setTrendGenerator(this);        
    }

    public XMLTreeNode getRoot() {
        return root;
    }
    
     /**
     *  This method is called when the table should be updated
     */
    public void update() {
        int width = wiz.getContainerWidth();
        int height = wiz.getContainerHeight();
        Element container = createContainer(root.actual(), name, width, height);                 
        
        if (root.isType(Definitions.getTypes())) {
            setAttributeIfMissing(container, POS_X, "0");
            setAttributeIfMissing(container, POS_Y, "0");        
        }
    
        if (wiz.isHorizontal()) {
            updateHorizontal(container, width, height);
        }
        else {
            updateVertical(container, width, height);
        }
    }

    private void updateVertical(Element container, int width, int height) {
        boolean growPos = !wiz.isGrowsLeftDown();
        int nroNumbers = wiz.getNumbers();
        int max = wiz.getMaxValue();
        int min = wiz.getMinValue();
        String maxValue = Integer.toString(max);
        String minValue = Integer.toString(min);
                
        String fontName = wiz.getFontAttribute();
        Element font = root.getModel().getElementByName(fontName);
        Dimension fontdim = BitmapFont.nameToDimension(font.getAttribute(FONT_SIZE));
        int xPad = fontdim.width / 2 + 1;
        int yPad = fontdim.height / 2 + 1;
        int nroWidth = 0; 
        int nroHeight = 0;
        if (wiz.isDrawNumbers()) {
            nroWidth = fontdim.width * maxValue.length();
        }
        if (wiz.isDrawTitle()) {
            nroHeight = fontdim.height;
        }
        
        int nroBars = wiz.getNroBars();
        int barWidth = (width - nroWidth - 2 * xPad) / nroBars;        
        int barHeight = height - nroHeight - 2 * yPad;
       
        Element bg = createRectangle(container, "background", width, height, 
            wiz.getLineAttribute1(), wiz.getFillAttribute1(), false);
        setIncludeAttributes(bg, 0, 0);
        
        if (wiz.isDrawNumbers()) {
            if (nroNumbers == 1) {
                int nro = (min + max) / 2;
                int posY = barHeight / 2;
                Element label = createString(container, "label0", nroWidth, 
                            fontdim.height, Integer.toString(nro), fontName);
                setIncludeAttributes(label, 0, posY);
                removeExtraElements(container, "label", 1); 
            }
            else if (nroNumbers > 1) {
                int nroInc = (max - min) / (nroNumbers - 1);
                int posYInc = barHeight / (nroNumbers - 1);
                for (int i = 0, nro = min, posY = 1; i < nroNumbers; 
                    i++, nro += nroInc, posY += posYInc) {
                    Element label = createString(container, "label" + i, nroWidth, 
                            fontdim.height, Integer.toString(growPos ? max - nro : nro), fontName);
                    setIncludeAttributes(label, 2, posY);
                }
                removeExtraElements(container, "label", nroNumbers); 
            }
            else {
                removeExtraElements(container, "label", 0); 
            }
        }
        else {
            removeExtraElements(container, "label", 0); 
        }
        
        if (wiz.isDrawTitle()) {
            String title = wiz.getTitle();
            int titleWidth = fontdim.width * title.length();
            Element tle = createString(container, "title0", titleWidth, 
                    fontdim.height, title, fontName);
            setIncludeAttributes(tle, (width - titleWidth) / 2, height - nroHeight - 2);
        }
        else {
            removeExtraElements(container, "title", 0); 
        }
        
        Element panel = createRectangle(container, "panel", nroBars * barWidth + 2, barHeight + 2, 
            wiz.getLineAttribute2(), wiz.getFillAttribute2(), false);
        setIncludeAttributes(panel, nroWidth + xPad - 1, yPad - 1);
        
        String barColor = wiz.getBarColor();
        int nroTicks = wiz.getNroTicks();
        boolean drawTicks = wiz.isDrawTicks();
        boolean drawBorder = wiz.isDrawBorder();
        int valueInc = (max - min) / (nroBars - 1);
        for (int i = 0, posX = nroWidth + xPad, value = min; i < nroBars; i++, posX += barWidth, value += valueInc) {
            
            Element bar = createBarGraph(container, "bar" + i, barColor, barWidth, barHeight, 
                    max, min, value, nroTicks, drawTicks, growPos, drawBorder);
            setIncludeAttributes(bar, posX, yPad);
        }
        removeExtraElements(container, "bar", nroBars); 
    }
    
      
    private void updateHorizontal(Element container, int width, int height) {

    }

    private Element createBarGraph(Element father, String name, String color, 
            int width, int height, int maxValue, int minValue, int value,
            int nroTicks, boolean drawTicks, boolean growPos, boolean drawBorder) {
        
        Element bar = createElement(LINEARBARGRAPH, name, father, false);
        setAttribute(bar, COLOUR, color);
        setAttribute(bar, WIDTH, width);
        setAttribute(bar, HEIGHT, height);
        setAttribute(bar, MAX_VALUE, maxValue);
        setAttribute(bar, MIN_VALUE, minValue);
        setAttribute(bar, VALUE, value);
        setAttribute(bar, NUMBER_OF_TICKS, nroTicks);
        setOptions(bar, new String[] {"border", "ticks", "growpositive"}, 
                new boolean[] {drawBorder, drawTicks, growPos});
        
        return bar;
    }

    private static void setOptions(Element elem, String[] names, boolean[] values) {
        String oldOpt = elem.getAttribute(OPTIONS);
        String newOpt = oldOpt;
        
        for (int i = 0; i < names.length; i++) {
            // setting attribute
            if (values[i]) {
                if (!newOpt.contains(names[i])) {
                    newOpt = newOpt.concat("+" + names[i]);
                }
            }
            // resetting attribute
            else {
                newOpt = newOpt.replace(names[i], "");                    
            }
        }
        // trim attribute
        newOpt.replace("++", "+");
        if (newOpt.startsWith("+")) {
            newOpt = newOpt.substring(1);
        }
        if (newOpt.endsWith("+")) {
            newOpt = newOpt.substring(0, newOpt.length() - 1);
        }
        if (!newOpt.equals(oldOpt)) {
            elem.setAttribute(OPTIONS, newOpt);
        }
    }
}
