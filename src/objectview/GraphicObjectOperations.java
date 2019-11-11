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
package objectview;

import color.ColorPalette;
import font.BitmapFont;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import pooledit.PictureConverter;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class GraphicObjectOperations implements ObjectOperations {
    
    static private final int IMAGE_SIZE = 3;
    static private final Paint PAINT = createPaint();
    static private Paint createPaint() {
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = (Graphics2D) image.getGraphics();
        gfx.setColor(Color.YELLOW);
        gfx.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        gfx.setColor(Color.RED);
        gfx.drawLine(0, IMAGE_SIZE - 1, IMAGE_SIZE - 1, 0);
        Rectangle anchor = new Rectangle(0, 0, IMAGE_SIZE, IMAGE_SIZE);
        return new TexturePaint(image, anchor);
    }
    private final Stroke dashStroke = new BasicStroke(0.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
            new float[] {2.0f, 2.0f}, 0.0f);
    
    private final Arc2D.Double arc = new Arc2D.Double();
    private final Line2D.Double line = new Line2D.Double();
    private final Rectangle2D.Double rect = new Rectangle2D.Double();
    private final Ellipse2D.Double ellipse = new Ellipse2D.Double();
    
    private int depth;
    private boolean drawBorders;
    
    private int colorDepth = ColorPalette.COLOR_8BIT;
    private boolean reduceImages;
    
    /* When flash is true, picture graphics objects are not drawn, 
     * if they have the flashing attribute set. */
    private boolean flash;
            
    /**
     * Sets draw borders.
     * @param drawBorders
     */
    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }
    
    /**
     * Gets draw borders.
     * @return
     */
    public boolean getDrawBorders() {
        return drawBorders;
    }
    
    /**
     * Sets color depth.
     * @param colorDepth
     */
    public void setColorDepth(int colorDepth) {
        if (colorDepth != ColorPalette.COLOR_1BIT &&
                colorDepth != ColorPalette.COLOR_4BIT  &&
                colorDepth != ColorPalette.COLOR_8BIT) {
            throw new IllegalArgumentException("color depth (" +
                    colorDepth + ") should be either 2, 16 or 256");
        }
        this.colorDepth = colorDepth;
    }
    
    /**
     * Gets color depth.
     * @return
     */
    public int getColorDepth() {
        return colorDepth;
    }
    
    /**
     * Sets reduce images flag.
     * @param reduceImages
     */
    public void setReduceImages(boolean reduceImages) {
        this.reduceImages = reduceImages;
    }
    
    /**
     * Gets reduce images flag.
     * @return
     */
    public boolean getReduceImages() {
        return reduceImages;
    }
    
    /**
     * Sets flash flag.
     * @param flash
     */
    public void setFlash(boolean flash) {
        this.flash = flash;
    }
    
    /**
     * Gets flash flag.
     * @return
     */
    public boolean getFlash() {
        return this.flash;
    }

    /**
     * Draws borders.
     * @param gfx
     * @param w
     * @param h
     */
    private void drawBorders(Graphics2D gfx, int w, int h) {
        if (drawBorders) {
            Stroke olds = gfx.getStroke();
            gfx.setStroke(dashStroke);
            
            // using XOR mode is bad idea because it is quite common that 
            // two components have exactly the same borders (i.e. a container 
            // and some other component in it) in which case the resulting 
            // XOR operations cancel out
            
            //gfx.setXORMode(Color.GREEN);
            gfx.setColor(Color.GREEN);
            rect.setRect(0, 0, w - 1, h - 1);
            gfx.draw(rect);
            gfx.setPaintMode();
            gfx.setStroke(olds);
        }
    }
    
    /** 
     * Constructor.
     */
    public GraphicObjectOperations() {
    }
    
    @Override
    public void opWorkingSet(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opDataMask(Graphics2D gfx, XMLTreeNode node, int w, int h)  {
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opAlarmMask(Graphics2D gfx, XMLTreeNode node, int w, int h)  {
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opContainer(Graphics2D gfx, XMLTreeNode node)  {
        int w = node.getWidth();
        int h = node.getHeight();
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opSoftKeyMask(Graphics2D gfx, XMLTreeNode node, int w, int h, int sk_height) {
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        // paint the extra key
        AffineTransform oldx = gfx.getTransform();
        gfx.translate(0, h - sk_height);
        gfx.setColor(Color.BLACK);
        gfx.fillPolygon(new int[] {w / 3, w / 3, 2 * w / 3},
                new int[] {sk_height / 3, 2 * sk_height / 3, sk_height / 2}, 3);
        
        drawBorders(gfx, w, sk_height);
        gfx.setTransform(oldx);
    }
    
    @Override
    public void opKey(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opButton(Graphics2D gfx, XMLTreeNode node) {
        int w = node.getWidth();
        int h = node.getHeight();
        
        Color bgcolor = node.getBackgroundColor(colorDepth);
        
        // fill back ground
        gfx.setColor(bgcolor);
        gfx.fillRect(0, 0, w, h);
        
        // draw border
        gfx.setColor(node.getBorderColor(colorDepth));
        rect.setRect(0, 0, w - 1, h - 1);
        gfx.draw(rect);
        
        //draw "shadow"
        gfx.fillPolygon(new int[] {0, w - 1, w - 1, w - 4, w - 4, 3},
                new int[] {h - 1, h - 1, 0, 3, h - 4, h - 4}, 6);
        
        // this could be just drawn but filling twice gives more consistent
        // look at different zoom levels...
        rect.setRect(3, 3, w - 7, h - 7);
        gfx.fill(rect);
        gfx.setColor(bgcolor);
        rect.setRect(4, 4, w - 8, h - 8);
        gfx.fill(rect);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opInputBoolean(Graphics2D gfx, XMLTreeNode node) {
        int w = node.getWidth();
        
        // fill background
        if (!node.isOptionsTransparent()) {
            gfx.setColor(node.getBackgroundColor(colorDepth));
            gfx.fillRect(0, 0, w, w);
        }
        
        // paint mark (V)
        if (node.getEffectiveValue() != 0) {
            gfx.setColor(node.getFontAttributes().getFontColor(colorDepth));
            gfx.fillPolygon(new int[] {w / 8, w / 10, w / 2, w - w / 10, w - w / 8, w / 2},
                    new int[] {w / 10, w / 8, w - w / 8, w / 8, w / 10, w / 2}, 6);
        }
        
        drawBorders(gfx, w, w);
    }
    
    @Override
    public void opInputString(Graphics2D gfx, XMLTreeNode node) throws IOException {
        int w = node.getWidth();
        int h = node.getHeight();
                        
        XMLTreeNode fas = node.getFontAttributes();
        BitmapFont font = fas.getFont(colorDepth);        
        Color backgroundColor = node.getBackgroundColor(colorDepth);
        
        // invert colors
        if (fas.isFontStyleInverted() ^ 
                (fas.isFontStyleFlashingInverted() && flash)) {
            Color tmp = font.getColor();
            font.setColor(backgroundColor);
            backgroundColor = tmp;
        }        
        
        // fill background
        if (!node.isOptionsTransparent()) {
            gfx.setColor(backgroundColor);
            gfx.fillRect(0, 0, w, h);
        }
        
        //read the value from stringvariable or the object
        String value = node.getStringVariableValue();
        if (value == null) {
            value = node.getValue();
        }
        
        // paint string
        if (!fas.isFontStyleFlashingHidden() || !flash) {
            font.paint(gfx, value, 0, 0, w, node.getJustification());
        }
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opInputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException {
        int w = node.getWidth();
        int h = node.getHeight();
        
        XMLTreeNode fas = node.getFontAttributes();
        BitmapFont font = fas.getFont(colorDepth);
        Color backgroundColor = node.getBackgroundColor(colorDepth);
        
        // invert colors
        if (fas.isFontStyleInverted() ^ 
                (fas.isFontStyleFlashingInverted() && flash)) {
            Color tmp = font.getColor();
            font.setColor(backgroundColor);
//          backgroundColor = tmp;
        }        
        
        // fill background
        if (!node.isOptionsTransparent()) {
            gfx.setColor(node.getBackgroundColor(colorDepth));
            gfx.fillRect(0, 0, w, h);
        }
        
        // format number
        String string = node.getFormatedNumber();
        
        // the standard says that this option has effect only if "the value of
        // the object is exactly zero", however it is unclear whether this refers
        // to the visible value or the internal value
        if (node.isOptionsBlankZero() &&
                (node.getEffectiveValue() + node.getOffset() == 0)) {
            //Double.parseDouble(string) == 0) {
            return;
        }
        
        //add leading zeros if necessary
        if (node.isOptionsDisplayLeadingZeros() && string.length() * font.getDimension().getWidth() < w) {
            int zeros = (int) ((w-string.length() * font.getDimension().getWidth())) / (int) (font.getDimension().getWidth());
            boolean negative = string.startsWith("-"); //val < 0.0);
            if (negative) {
                string = string.substring(1);
            }
            for (int i = 0; i < zeros; i++) {
                string = "0" + string;
            }
            if (negative) {
                string = "-" + string;
            }
        }
        
        if (!fas.isFontStyleFlashingHidden() || !flash) {
            font.paint(gfx, string, 0, 0, w, node.getJustification());
        }
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opInputList(Graphics2D gfx, XMLTreeNode node) {
        int w = node.getWidth();
        int h = node.getHeight();
        
        // this component does not have even a background!
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opOutputString(Graphics2D gfx, XMLTreeNode node) throws IOException {
        int w = node.getWidth();
        int h = node.getHeight();
        
        XMLTreeNode fas = node.getFontAttributes();
        BitmapFont font = fas.getFont(colorDepth);
        Color backgroundColor = node.getBackgroundColor(colorDepth); 
                
        // invert colors
        if (fas.isFontStyleInverted() ^ 
                (fas.isFontStyleFlashingInverted() && flash)) {
            Color tmp = font.getColor();
            font.setColor(backgroundColor);
            backgroundColor = tmp;
        }    
        
        // fill background
        if (!node.isOptionsTransparent()) {
            gfx.setColor(backgroundColor);
            gfx.fillRect(0, 0, w, h);
        }
        
        // read the value from stringvariable or the object
        String value = node.getStringVariableValue();
        if (value == null) {
            value = node.getValue();
        }
        
        // paint string
        if (!fas.isFontStyleFlashingHidden() || !flash) {
            if (node.isOptionsAutoWrap()) {
                font.paintWrap(gfx, value, 0, 0, w, node.getJustification());
            } else {
                font.paint(gfx, value, 0, 0, w, node.getJustification());
            }
        }
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opOutputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException {
        int w = node.getWidth();
        int h = node.getHeight();
        
        XMLTreeNode fas = node.getFontAttributes();
        BitmapFont font = fas.getFont(colorDepth);
        Color backgroundColor = node.getBackgroundColor(colorDepth); 
                
        // invert colors
        if (fas.isFontStyleInverted() ^ 
                (fas.isFontStyleFlashingInverted() && flash)) {
            Color tmp = font.getColor();
            font.setColor(backgroundColor);
//          backgroundColor = tmp;
        }    
        
        // fill background
        if (!node.isOptionsTransparent()) {
            gfx.setColor(node.getBackgroundColor(colorDepth));
            gfx.fillRect(0, 0, w, h);
        }
        
        // format number
        String string = node.getFormatedNumber();
        
        // the standard says that this option has effect only if "the value of
        // the object is exactly zero", however it is unclear whether this refers
        // to the visible value or the internal value
        if (node.isOptionsBlankZero() &&
                (node.getEffectiveValue() + node.getOffset() == 0)) {
            //Double.parseDouble(string) == 0) {
            return;
        }
        
        // add leading zeros if necessary
        if (string.length() * font.getDimension().getWidth() < w && node.isOptionsDisplayLeadingZeros()) {
            int zeros = (int)((w-string.length()*font.getDimension().getWidth())) / (int)(font.getDimension().getWidth());
            boolean negative = string.startsWith("-"); //(val < 0.0);
            if (negative) {
                string = string.substring(1);
            }
            for (int i = 0; i < zeros; i++) {
                string = "0" + string;
            }
            if (negative) {
                string = "-" + string;
            }
        }
        
        if (!fas.isFontStyleFlashingHidden() || !flash) {
            font.paint(gfx, string, 0, 0, w, node.getJustification());
        }
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opLine(Graphics2D gfx, XMLTreeNode node) {
        Stroke olds = gfx.getStroke();
        int w = node.getWidth();
        int h = node.getHeight();
        
        LineAttributes latt = node.getLineAttributes(colorDepth);
        latt.apply(gfx);
        
        float lw = ((BasicStroke) gfx.getStroke()).getLineWidth();
        float lw2 = lw / 2;
        
        // draw line
        /*
        if (!node.getLineDirection()) {
            line.setLine(lw2, lw2, w - 1 + lw2, h - 1 + lw2);
        } else {
            line.setLine(lw2, h - 1 + lw2, w - 1 + lw2, lw2);
        }*/
        if (!node.getLineDirection()) {
            line.setLine(lw2, lw2, w - lw + lw2, h - lw + lw2);
        } else {
            line.setLine(lw2, h - lw + lw2, w - lw + lw2, lw2);
        }
        
        
        gfx.draw(line);
        
        gfx.setStroke(olds);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opRectangle(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException {
        Stroke olds = gfx.getStroke();
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int h = node.getHeight();
        
        LineAttributes latt = node.getLineAttributes(colorDepth);
        Paint paint = node.getFillAttributesPaint(latt.getColor(), imagepath, PAINT, reduceImages, colorDepth);
        
        // fill rectangle
        if (paint != null) {
            rect.setRect(0, 0, w, h);
            gfx.setPaint(paint);
            gfx.fill(rect);
        }
        
        // line suppression support
        if (latt != null) {
            float lw = ((BasicStroke) latt.getStroke()).getLineWidth();
            float lw2 = lw / 2;
            latt.apply(gfx);
            if ((node.getLineSuppression() & 1) == 0) {
                line.setLine(0, lw2, w, lw2);
                gfx.draw(line);
            }
            if ((node.getLineSuppression() & 8) == 0) {
                line.setLine(lw2, 0, lw2, h);
                gfx.draw(line);
            }
            if ((node.getLineSuppression() & 4) == 0) {
                line.setLine(0, h - lw2, w , h - lw2);
                gfx.draw(line);
            }
            if ((node.getLineSuppression() & 2) == 0) {
                line.setLine(w - lw2, 0, w - lw2, h);
                gfx.draw(line);
            }
        }
        
        gfx.setPaint(oldp);
        gfx.setStroke(olds);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opEllipse(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException {
        Stroke olds = gfx.getStroke();
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int h = node.getHeight();
        
        int startAngle = node.getStartAngle();
        int endAngle = node.getEndAngle();
        if (startAngle > endAngle) {
            endAngle += 360;
        }
        Integer ellipseType = node.getEllipseType();
        
        LineAttributes latt = node.getLineAttributes(colorDepth);
        Paint paint = node.getFillAttributesPaint(latt.getColor(), imagepath, PAINT, reduceImages, colorDepth);
        
        float lw = ((BasicStroke) gfx.getStroke()).getLineWidth();
        float lw2 = lw / 2;
        
        // draw ellipse (null -> closed)
        if (ellipseType == null) { //startAngle == endAngle) {
            if (paint != null) {
                ellipse.setFrame(0, 0, w - 1, h - 1);
                gfx.setPaint(paint);
                gfx.fill(ellipse);
            }
            if (latt != null) {
                ellipse.setFrame(lw2, lw2, w - 1 - lw2, h - 1 - lw2);
                latt.apply(gfx);
                gfx.draw(ellipse);
            }
        }
        // draw arc
        else {
            double angleStart = calcAngle(w, h, startAngle);  //Angle*2?
            double angleLength = calcAngle(w, h, endAngle) - angleStart;
            
            // open ellipses are not fillable
            if (ellipseType != Arc2D.OPEN && paint != null) {
                gfx.setPaint(paint);
                arc.setArc(0, 0, w - 1, h - 1, angleStart, angleLength, ellipseType);
                gfx.fill(arc);
            }
            if (latt != null) {
                latt.apply(gfx);
                arc.setArc(lw2, lw2, w - 1 - lw2, h - 1 - lw2, angleStart, angleLength, ellipseType);
                gfx.draw(arc);
            }
        }
        
        gfx.setPaint(oldp);
        gfx.setStroke(olds);
        
        drawBorders(gfx, w, h);
    }
    
    // There is a problem with drawing thick lines if they don't fit inside the
    // clipping rectangle.
    @Override
    public void opPolygon(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException {
        Stroke olds = gfx.getStroke();
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int h = node.getHeight();
        
        LineAttributes latt = node.getLineAttributes(colorDepth);
        Paint paint = node.getFillAttributesPaint(latt.getColor(), imagepath, PAINT, reduceImages, colorDepth);
        Polygon p = node.getPolygon();
        
        float lw = ((BasicStroke) gfx.getStroke()).getLineWidth();
        
        // draw open polygon
        if (node.isPolygonTypeOpen()) {
            if (latt != null) {
                latt.apply(gfx);
                int points[] = node.getPolygonPoints();
                for (int i = 0, n = points.length - 2; i < n ; i += 2) {
                    gfx.drawLine(points[i], points[i + 1], points[i + 2], points[i + 3]);
                }
            }
        }
        // draw filled polgon
        else {
            if (paint != null) {
                gfx.setPaint(paint);
                gfx.fillPolygon(p);
            }
            if (latt != null) {
                latt.apply(gfx);
                gfx.drawPolygon(p);
            }
        }
        gfx.setPaint(oldp);
        gfx.setStroke(olds);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opMeter(Graphics2D gfx, XMLTreeNode node) {
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int w2 = w / 2;
        
        int startAngle = node.getStartAngle();   //*2?
        int endAngle = node.getEndAngle();       //*2?
        if (startAngle >= endAngle) {
            endAngle += 360;    //Important check
        }
        
        // draw border
        if (node.isOptionsBorder()) {
            Color borderColor = node.getBorderColor(colorDepth);
            gfx.setColor(borderColor);
            rect.setRect(0, 0, w - 1, w - 1);
            gfx.draw(rect);
        }
        
        // draw arc
        Color arcAndTickColor = node.getArcAndTickColor(colorDepth);
        if (node.isOptionsArc()) {
            gfx.setColor(arcAndTickColor);
            arc.setArc(0, 0, w - 1, w - 1, startAngle, endAngle - startAngle, Arc2D.OPEN);
            gfx.draw(arc);
        }
        
        // move origin to the center
        gfx.translate(w2, w2);
        
        // draw ticks
        if (node.isOptionsTicks()) {
            gfx.setColor(arcAndTickColor);
            int ticks = node.getTicks();
            if (ticks == 1) {
                double angle = Math.toRadians((endAngle + startAngle) / 2);
                double cos = Math.cos(-angle);
                double sin = Math.sin(-angle);
                line.setLine(4 * w / 10 * cos, 4 * w / 10 * sin,
                        (w - 1) / 2 * cos, (w - 1) / 2 * sin);
                gfx.draw(line);
            } else if (ticks > 1) {
                double angle = Math.toRadians(startAngle);
                double inc = Math.toRadians((endAngle - startAngle) / (ticks - 1.0));
                for (int i = 0; i < ticks; i++, angle += inc) {
                    double cos = Math.cos(-angle);
                    double sin = Math.sin(-angle);
                    line.setLine(4 * w / 10 * cos, 4 * w / 10 * sin,
                            (w - 1) / 2 * cos, (w - 1) / 2 * sin);
                    gfx.draw(line);
                }
            }
        }
        
        // draw needle
        Integer value = node.getEffectiveValue();
        int minValue = node.getMinValue();
        int maxValue = node.getMaxValue();
        double needleAngle = !node.isOptionsClockwise() ?
            Math.toRadians(interpolate(minValue, startAngle, maxValue, endAngle, value)) :
            Math.toRadians(interpolate(minValue, endAngle, maxValue, startAngle, value));
        
        gfx.setColor(node.getNeedleColor(colorDepth));
        line.setLine(0, 0, w2 * Math.cos(-needleAngle), w2 * Math.sin(-needleAngle));
        gfx.draw(line);
        
        // restore coordinate system
        gfx.translate(-w2, -w2);
        
        // restore paint
        gfx.setPaint(oldp);
        
        drawBorders(gfx, w, w);
    }
    
    @Override
    public void opLinearBarGraph(Graphics2D gfx, XMLTreeNode node) {
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int h = node.getHeight();
        
        int minValue = node.getMinValue();
        int maxValue = node.getMaxValue();
        
        Color color = node.getColor(colorDepth);
        Integer value = node.getEffectiveValue();
        
        // filled
        if (!node.isOptionsNoFill() ){
            gfx.setColor(color);
            // horizontal
            if (node.isOptionsHorizontal()) {
                if (node.isOptionsGrowPositive()) {
                    double x = interpolate(minValue, 0, maxValue, w - 1, value);
                    rect.setRect(0, 0, x, h - 1);
                    gfx.fill(rect);
                } else {
                    double x = interpolate(minValue, w - 1, maxValue, 0, value);
                    rect.setRect(x, 0, w - 1 - x, h - 1);
                    gfx.fill(rect);
                }
            }
            // vertical
            else {
                if (node.isOptionsGrowPositive()) {
                    double y = interpolate(minValue, h - 1, maxValue, 0, value);
                    rect.setRect(0, y, w - 1, h - 1 - y);
                    gfx.fill(rect);
                } else {
                    double y = interpolate(minValue, 0, maxValue, h - 1, value);
                    rect.setRect(0, 0, w - 1, y);
                    gfx.fill(rect);
                }
            }
        }
        // not filled
        else {
            gfx.setColor(color);
            // horizontal
            if (node.isOptionsHorizontal()) {
                double x = node.isOptionsGrowPositive() ?
                    interpolate(minValue, 0, maxValue, w - 1, value) :
                    interpolate(minValue, w - 1, maxValue, 0, value);
                
                line.setLine(x, 0, x, h - 1);
                gfx.draw(line);
            }
            // vertical
            else {
                double y = node.isOptionsGrowPositive() ?
                    interpolate(minValue, h - 1, maxValue, 0, value) :
                    interpolate(minValue, 0, maxValue, h - 1, value);
                
                line.setLine(0, y, w - 1, y);
                gfx.draw(line);
            }
        }
        
        // drawing ticks
        if (node.isOptionsTicks()) {
            gfx.setColor(color);
            int ticks = node.getTicks();
            if (ticks == 1) {
                if (node.isOptionsHorizontal()) {
                    double x = (w - 1.0) / 2.0;
                    line.setLine(x, 0, x, (h - 1) / 4);
                    gfx.draw(line);
                } else {
                    double y = (h - 1.0) / 2.0;
                    line.setLine(0, y, (w - 1) / 4, y);
                    gfx.draw(line);
                }
            }
            if (ticks > 1) {
                if (node.isOptionsHorizontal()) {
                    double x = 0;
                    double inc = (w - 1.0) / (ticks - 1.0);
                    for (int tick = 0; tick < ticks; tick++, x += inc) {
                        line.setLine(x, 0, x, (h - 1) / 4);
                        gfx.draw(line);
                    }
                } else {
                    double y = 0;
                    double inc = (h - 1.0) / (ticks - 1.0);
                    for (int tick = 0; tick < ticks; tick++, y += inc) {
                        line.setLine(0, y, (w - 1) / 4, y);
                        gfx.draw(line);
                    }
                }
            }
        }
        
        // border is drawn before target line, otherwise the target line
        // is not visible at the min and max positions
        if (node.isOptionsBorder()) {
            gfx.setColor(color);
            rect.setRect(0, 0, w - 1, h - 1);
            gfx.draw(rect);
        }
        
        // drawing targetline
        if (node.isOptionsTargetLine()) {
            gfx.setColor(node.getTargetLineColor(colorDepth));
            Integer targetLineValue = node.getEffectiveTargetValue();
            if (node.isOptionsHorizontal()) {
                double x = node.isOptionsGrowPositive() ?
                    interpolate(minValue, 0, maxValue, w - 1, targetLineValue) :
                    interpolate(minValue, w - 1, maxValue, 0, targetLineValue);
                
                line.setLine(x, 0, x, h - 1);
                gfx.draw(line);
            } else {
                double y = node.isOptionsGrowPositive() ?
                    interpolate(minValue, h - 1, maxValue, 0, targetLineValue) :
                    interpolate(minValue, 0, maxValue, h - 1, targetLineValue);
                
                line.setLine(0, y, w - 1, y);
                gfx.draw(line);
            }
        }
        
        gfx.setPaint(oldp);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opArchedBarGraph(Graphics2D gfx, XMLTreeNode node) {
        Paint oldp = gfx.getPaint();
        int w = node.getWidth();
        int h = node.getHeight();
        
        int minValue = node.getMinValue();
        int maxValue = node.getMaxValue();
        int startAngle = node.getStartAngle();
        int endAngle = node.getEndAngle();
        if (startAngle > endAngle) {
            endAngle += 360;
        }
        
        // bar width is intentionally converted to double here to force
        // some calculation to use double operations
        double barw = node.getBargraphWidth();
        
        // draw border
        gfx.setColor(node.getColor(colorDepth));
        if (node.isOptionsBorder()) {
            double angleStart = calcAngle(w, h, startAngle);
            double angleEnd = calcAngle(w, h, endAngle);
            arc.setArc(0, 0, w - 1, h - 1, angleStart, angleEnd - angleStart, Arc2D.OPEN);
            gfx.draw(arc);
            arc.setArc(barw, h * barw / w, w - 1 - 2 * barw, h * (w - 1 - 2 * barw) / w, angleStart, angleEnd - angleStart, Arc2D.OPEN);
            gfx.draw(arc);
            gfx.draw(setupLine(line, startAngle, w, h, barw));
            gfx.draw(setupLine(line, endAngle, w, h, barw));
        }
        
        // draw value either as a filled region or just a simple line
        Integer value = node.getEffectiveValue();
        // no fill option is not specified -> fill
        if (!node.isOptionsNoFill()){
            double angleS;
            double angleE;
            if (!node.isOptionsClockwise()) {
                angleS = calcAngle(w, h, startAngle);
                angleE = calcAngle(w, h, interpolate(minValue, startAngle, maxValue, endAngle, value));
            } else {
                angleS = calcAngle(w, h, interpolate(minValue, endAngle, maxValue, startAngle, value));
                angleE = calcAngle(w, h, endAngle);
            }
            
            arc.setArc(0, 0, w - 1, h - 1, angleS, angleE - angleS, Arc2D.PIE);
            Area arc1 = new Area(arc);
            arc.setArc(barw, barw / w * h, w - 1 - 2 * barw, (w - 1 - 2 * barw) / w * h, 0, 360, Arc2D.CHORD);
            Area arc2 = new Area(arc);
            arc1.subtract(arc2);
            gfx.fill(arc1);
        }
        // no fill option is specified -> no fill
        else {
            double angle = !node.isOptionsClockwise() ?
                interpolate(minValue, startAngle, maxValue, endAngle, value) :
                interpolate(minValue, endAngle, maxValue, startAngle, value);
            
            gfx.draw(setupLine(line, angle, w, h, barw));
        }
        
        // draw target line
        if (node.isOptionsTargetLine()) {
            gfx.setColor(node.getTargetLineColor(colorDepth));
            Integer targetLineValue = node.getEffectiveTargetValue();
            double angle = !node.isOptionsClockwise() ?
                interpolate(minValue, startAngle, maxValue, endAngle, targetLineValue) :
                interpolate(minValue, endAngle, maxValue, startAngle, targetLineValue);
            
            gfx.draw(setupLine(line, angle, w, h, barw));
        }
        
        gfx.setPaint(oldp);
        
        drawBorders(gfx, w, h);
    }
    
    /**
     * A convenience method for setting up lines for arched bar graph.
     * @param line
     * @param angle
     * @param w
     * @param h
     * @param barw
     * @return
     */
    static private Line2D.Double setupLine(Line2D.Double line, double angle, int w, int h, double barw) {
        double ang = Math.toRadians(calcAngle(w, h, angle));
        double cos = Math.cos(-ang);
        double sin = Math.sin(-ang);
        line.setLine((cos + 1) * (w - 1) / 2.0,
                (sin + 1) * (h - 1) / 2.0,
                cos * (((w - 1) - 2 * barw) / 2.0) + (w - 1) / 2.0,
                sin * ((w - 2 * barw) * (h - 1) / (2.0 * (w - 1))) + (h - 1) / 2.0);
        return line;
    }
    
    @Override
    public void opPictureGraphic(Graphics2D gfx, XMLTreeNode node, BufferedImage image) {
        int w = node.getWidth();
        int h = w * image.getHeight() / image.getWidth();
        Color transparencyColor = node.getTransparencyColor(reduceImages, colorDepth);
        
        image = PictureConverter.applyTransparencyAndReduceColors(image, 
                transparencyColor, node.isOptionsTransparent(), 
                reduceImages, colorDepth);
        
        if (!node.isOptionsFlashing() || !flash) {                      
            gfx.drawRenderedImage(image, new AffineTransform((float) w / (float) image.getWidth(),
                    0f, 0f, (float) h / (float) image.getHeight(), 0f, 0f));
        }
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opObjectPointer(Graphics2D gfx, XMLTreeNode node) {
        // not much to do here
    }
    
    @Override
    public void opAuxiliaryFunction(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        // fill back ground
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    @Override
    public void opAuxiliaryInput(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        // fill back ground
        gfx.setColor(node.getBackgroundColor(colorDepth));
        gfx.fillRect(0, 0, w, h);
        
        drawBorders(gfx, w, h);
    }
    
    /**
     * Sets depth.
     * @param depth
     */
    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    /**
     * Increments depth by one.
     */
    @Override
    public void incDepth() {
        depth++;
    }
    
    /**
     * Decrements depth by one.
     */
    @Override
    public void decDepth() {
        depth--;
    }
    
    /** 
     * Gets depth.
     * @return
     */
    @Override
    public int getDepth() {
        return depth;
    }
    
    /**
     * Linear interpolation between points p0 and p1. Returns y for the
     * specified x. The output is limited to the interval [y0, y1].
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x
     * @return
     */
    static public double interpolate(double x0, double y0, double x1, double y1, double x) {
        if (x <= x0) {
            return y0;
        } else if (x >= x1) {
            return y1;
        } else {
            double k = (y1 - y0) / (x1 - x0);
            return k * (x - x0) + y0;
        }
    }
    
        /*
    static public double calcAng2(double a, double b, double ang) {
        int cycle = 0;
        int quadrant;
        while (ang < 0) {
            ang += 360;
            cycle++;
        }
        while (ang >= 360) {
            ang -= 360;
            cycle--;
        }
        if (ang < 90) {
            quadrant = 0;
        }
        else if (ang < 180) {
            quadrant = 1;
            ang = 180 - ang;
        }
        else if (ang < 270) {
            quadrant = 2;
            ang -= 180;
        }
        else {
            quadrant = 3;
            ang = 360 - ang;
        }
        ang = Math.toRadians(ang);
        double t1 = Math.tan(ang);
        double t2 = a * a * t1 * t1 + b * b;
        double t3 = Math.sqrt(t2);
        double t4 = Math.toDegrees(Math.acos(b / t3));
        switch (quadrant) {
        case 0:
            return t4 - 360 * cycle;
        case 1:
            return 180 - t4 - 360 * cycle;
        case 2:
            return t4 + 180 - 360 * cycle;
        case 3:
            return 360 - t4 - 360 * cycle;
        default:
            throw new RuntimeException();
        }
    }
         */
    
    /**
     * The parametric representation for an ellipse is:
     * (cos(t) / a)^2 + (sin(t) / b)^2 = 1, t = [0, 360]
     * Although the parameter t is in degrees, it is not the angle one might
     * expect. This method takes in the parameters of the ellipse (a and b)
     * and the true angle and returns the "parametric angle".
     * @param a
     * @param b
     * @param ang
     * @return
     */
    static public double calcAngle(double a, double b, double ang) {
        int cycle = 0;
        boolean mirror = false;
        while (ang < -90) {
            ang += 360;
            cycle++;
        }
        while (ang >= 270) {
            ang -= 360;
            cycle--;
        }
        if (ang >= 90) {
            mirror = true;
            ang -= 180;
        }
        double t1 = a / b * Math.tan(Math.toRadians(ang));
        double t2 = Math.toDegrees(Math.atan(t1)) - 360 * cycle;
        return mirror ? t2 + 180 : t2;
    }
}
