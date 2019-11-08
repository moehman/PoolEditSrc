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
package objectview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class HelpGrid {
    
    private final Stroke DASH_STROKE = new BasicStroke(0.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
            new float[] {1.0f, 1.0f}, 0.0f);
    
    private final List<Line2D.Double> lines = new ArrayList<Line2D.Double>();
    private final AffineTransform xform = new AffineTransform();
    private Rectangle2D rect;
    
    double dx = 10;
    double dy = 10;
    double ox = 0;
    double oy = 0;
    
    /** 
     * Constructor.
     */
    public HelpGrid() {
    }
    
    public void reset() {
        rect = null;
    }
    
    public boolean isSet() {
        return rect != null;
    }
    
    public void makeLines(double width, double height) {
        
        lines.clear();
        
        // add vertical lines
        for (double x = ox; x < width; x += dx) {
            lines.add(new Line2D.Double(x, 0, x, height));
        }
        
        // add horizontal lines
        for (double y = oy; y < height; y += dy) {
            lines.add(new Line2D.Double(0, y, width, y));
        }
    }
    
    public void set(AffineTransform xfrm, Shape shape, XMLTreeNode source) {
        this.xform.setTransform(xfrm);
        rect = xform.createTransformedShape(shape).getBounds2D();
        
        Rectangle2D r = shape.getBounds2D();
        makeLines(r.getWidth(), r.getHeight());
    }
    
    public void adjust(AffineTransform xfrm) {
        if (isSet()) {
            xform.preConcatenate(xfrm);
            rect = xform.createTransformedShape(rect).getBounds2D();
        }
    }
    
    /**
     * Draws the help grid.
     * @param gfx
     * @param zoom
     */
    public void draw(Graphics2D gfx, double zoom) {
        Stroke olds = gfx.getStroke();
        gfx.setStroke(DASH_STROKE);
        gfx.setXORMode(Color.PINK);
        for (Line2D.Double line : lines) {
            gfx.draw(xform.createTransformedShape(line));
        }
        gfx.setPaintMode();
        gfx.setStroke(olds);
    }   
}
