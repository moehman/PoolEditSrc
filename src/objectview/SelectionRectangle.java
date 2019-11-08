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

import static pooledit.Definitions.*;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class SelectionRectangle {
    
    static private final int IMAGE_SIZE = 4;
    static private final int BORDER_WIDTH = 4;    
    static private final int CORNER_SIZE = 6;
    static private final int POLY_SIZE = 8;
    
    /** Corners and midpoints */
    static private final int TOP_LEFT = 0;
    static private final int TOP_CENTER = 1;
    static private final int TOP_RIGHT = 2;
    static private final int MIDDLE_LEFT = 3;
    static private final int MIDDLE_CENTER = 4;
    static private final int MIDDLE_RIGHT = 5;
    static private final int BOTTOM_LEFT = 6;
    static private final int BOTTOM_CENTER = 7;
    static private final int BOTTOM_RIGHT = 8;
    static private final int NRO_CORNERS = 9;
    
    static private final Paint PAINT = createPaint();
    static private Paint createPaint() {
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, 
						BufferedImage.TYPE_INT_ARGB);
	for (int i = 0; i < IMAGE_SIZE; i++) {
	    image.setRGB(i, i, 0xFF000000); 
	} 
	Rectangle anchor = new Rectangle(0, 0, IMAGE_SIZE, IMAGE_SIZE);
	return new TexturePaint(image, anchor);
    }
    
    static private Rectangle[] createCorners() {
        Rectangle[] corners = new Rectangle[NRO_CORNERS];
        for (int i = 0, n = corners.length; i < n; i++) {
            corners[i] = new Rectangle();
        }
        return corners;
    }
      
    private final AffineTransform xform = new AffineTransform();
    private final Rectangle[] corners;
    private Rectangle rect;
    private int corner = -1;
    
    /** Temporary point used the transform polygon geometry */
    private final Point pnt = new Point();
    private final Point polystartpoint = new Point();
    private final Polygon polygon = new Polygon();
    private final List<XMLTreeNode> polynodes = new ArrayList<>();
    private final List<Ellipse2D.Double> polycorners = new ArrayList<>();
    private int polycorner = -1;
    
    private XMLTreeNode source;
    
    /** 
     * Constructor.
     */
    public SelectionRectangle() {
        corners = createCorners();
    }
   
    /**
     * Gets the rectangle.
     * @return
     */
    public Rectangle getRectangle() {
        return new Rectangle(rect);
    }
        
    /**
     * Gets the source object (xml tree node).
     * @return
     */
    public Object getSource() {
        return source;
    }
    
    /**
     * Checks, whether editing is in progress.
     * @return
     */
    public boolean editInProgress() {
        return corner >= 0 || polycorner >= 0;
    }
    
    /**
     * Resets the selection rectangle, if editing is NOT in progress.
     */
    public void reset() {
        if (!editInProgress()) {
            rect = null;
            polynodes.clear();
            polycorners.clear();
        }
    }
    
    /**
     * Shape is in relative coordinates (to the current graphics object),
     * so it has to be converted to absolute coordinates.
     * @param xfrm
     * @param shape
     * @param source
     */
    public void set(AffineTransform xfrm, Shape shape, XMLTreeNode source) {
        if (!editInProgress()) {
            xform.setTransform(xfrm);
            rect = xfrm.createTransformedShape(shape).getBounds();
            this.source = source;
            if (source.isType(POLYGON)) {
                initPolyCorners(source);
            }
        }
    }
    
    /**
     * Adjusts the selection rectangle by the given transformation.
     * @param xfrm
     */
    public void adjust(AffineTransform xfrm) {
        if (!editInProgress() && isSet()) {
            xform.preConcatenate(xfrm);
            rect = xfrm.createTransformedShape(rect).getBounds();
            if (source.isType(POLYGON)) {
                initPolyCorners(source);
            }
        }
    }
        
    /**
     * Initializes the polygon corner circles.
     * @param node
     */
    public void initPolyCorners(XMLTreeNode node) {
        polynodes.clear();
        polycorners.clear();
        XMLTreeModel model = node.getModel();        
        for (int i = 0, n = model.getChildCount(node); i < n; i++) {
            XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 
            if (nd.isType(POINT)) {
                pnt.setLocation(nd.getX(), nd.getY());
                xform.transform(pnt, pnt);
                polynodes.add(nd);
                polycorners.add(new Ellipse2D.Double(pnt.x - (POLY_SIZE / 2), 
                        pnt.y - (POLY_SIZE / 2), POLY_SIZE, POLY_SIZE));
            }
        }
    }
    
    public boolean isSet() {
        return rect != null;
    }
    
    /**
     * Call deselectCorners and then reset to get the same effect.
     */
    public void unSet() {
        rect = null;
    }
    
    /**
     * Selects polygon corner at the given coordinates. Returns false is no
     * corner was selected.
     * @param x
     * @param y
     * @return
     */
    public boolean selectPolyCornerAt(int x, int y) {
        for (Ellipse2D e : polycorners) {
            if (e.contains(x, y)) {
                polystartpoint.setLocation(e.getX(), e.getY());
                polycorner = polycorners.indexOf(e);
                return true;
            }
        }
        polycorner = -1;
        return false;
    }
    
    /**
     * Selects the corner at the given coordinates. Returns false if no corner
     * was selected.
     * @param x
     * @param y
     * @return
     */
    public boolean selectCornerAt(int x, int y) {
        Rectangle[] c = getCorners();
        for (int i = c.length - 1; i >= 0; i--) {
            if (c[i].contains(x, y)) {
                corner = i;
                return true;
            }
        }
        corner = -1;
        return false;
    }
    
    /**
     * Gets the selected polygon corner.
     * @return
     */
    public XMLTreeNode getSelectedPolyCorner() {
        return polynodes.get(polycorner);
    }
    
    /**
     * Moves the selected polygon corner.
     * @param dx
     * @param dy
     */
    public void moveSelectedPolyCorner(int dx, int dy) {
        Ellipse2D.Double e = polycorners.get(polycorner);
        e.x += dx;
        e.y += dy;
    }
    
    /**
     * Moves selected corner.
     * @param dx
     * @param dy
     */
    public void moveSelectedCorner(int dx, int dy) {
        switch (corner) {
            case TOP_LEFT:
                rect.x += dx;
                rect.y += dy;
                rect.width -= dx;
                rect.height -= dy;
                break;
            case TOP_CENTER:
                rect.y += dy;
                rect.height -= dy;
                break;
            case TOP_RIGHT:
                rect.y += dy;
                rect.width += dx;
                rect.height -= dy;
                break;
            case MIDDLE_LEFT:
                rect.x += dx;
                rect.width -= dx;
                break;
            case MIDDLE_CENTER:
                rect.x += dx;
                rect.y += dy;
                break;
            case MIDDLE_RIGHT:
                rect.width += dx;
                break;
            case BOTTOM_LEFT:
                rect.x += dx;
                rect.width -= dx;
                rect.height += dy;
                break;
            case BOTTOM_CENTER:
                rect.height += dy;
                break;
            case BOTTOM_RIGHT:
                rect.width += dx;
                rect.height += dy;
                break;
        }
    }
    
    public void deselectCorners() {
        corner = -1;
        polycorner = -1;
    }
        
    /**
     * Gets the corner rectangles.
     * @return
     */
    public Rectangle[] getCorners() {
        int xl = rect.x - CORNER_SIZE;
        int xc = rect.x + (rect.width - CORNER_SIZE) / 2;
        int xr = rect.x + rect.width;
        int yt = rect.y - CORNER_SIZE;
        int ym = rect.y + (rect.height - CORNER_SIZE) / 2;
        int yb = rect.y + rect.height;
        
        corners[TOP_LEFT].setRect(xl, yt, CORNER_SIZE, CORNER_SIZE);
        corners[TOP_CENTER].setRect(xc, yt, CORNER_SIZE, CORNER_SIZE);
        corners[TOP_RIGHT].setRect(xr, yt, CORNER_SIZE, CORNER_SIZE);
        corners[MIDDLE_LEFT].setRect(xl, ym, CORNER_SIZE, CORNER_SIZE);
        corners[MIDDLE_CENTER].setRect(xc, ym, CORNER_SIZE, CORNER_SIZE);
        corners[MIDDLE_RIGHT].setRect(xr, ym, CORNER_SIZE, CORNER_SIZE);
        corners[BOTTOM_LEFT].setRect(xl, yb, CORNER_SIZE, CORNER_SIZE);
        corners[BOTTOM_CENTER].setRect(xc, yb, CORNER_SIZE, CORNER_SIZE);
        corners[BOTTOM_RIGHT].setRect(xr, yb, CORNER_SIZE, CORNER_SIZE);
        return corners;
    } 
   
    /**
     * Draws this selection rectangle.
     * @param gfx
     * @param zoom
     */
    public void draw(Graphics2D gfx, double zoom) {
        int x = rect.x - BORDER_WIDTH;
        int y = rect.y - BORDER_WIDTH;
        int w = rect.width + 2 * BORDER_WIDTH;
        int h = rect.height + 2 * BORDER_WIDTH;

        gfx.setPaint(PAINT); 
	gfx.setXORMode(Color.WHITE);
	gfx.fillRect(x, y, w, BORDER_WIDTH); // top	
	gfx.fillRect(x, rect.y + rect.height, w, BORDER_WIDTH); // bottom
	gfx.fillRect(x, rect.y, BORDER_WIDTH, rect.height); // left
	gfx.fillRect(rect.x + rect.width, rect.y, BORDER_WIDTH, rect.height); // left        
        if (source.isType(POLYGON)) {
            drawPolygon(gfx, zoom);
        }
        gfx.setPaintMode();
        
        drawCorners(gfx);        
        if (source.isType(POLYGON)) {
            drawPolyCorners(gfx);            
        }
    }
    
    /**
     * Draws the polygon corner circles.
     * @param gfx
     */
    private void drawPolyCorners(Graphics2D gfx) {
        for (Ellipse2D e : polycorners) {
            gfx.setColor(polycorners.indexOf(e) == polycorner ? Color.RED : Color.WHITE);
            gfx.fill(e);
            gfx.setColor(Color.BLACK);
            gfx.draw(e);
        }
    }
    
    /**
     * Draws the polygon.
     * @param gfx
     * @param zoom
     */
    public void drawPolygon(Graphics2D gfx, double zoom) {
        polygon.reset();
        for (int i = 0, n = polycorners.size(); i < n; i++) {
            Ellipse2D.Double e = polycorners.get(i);
            int x = (int) e.x;
            int y = (int) e.y;
            if (i == polycorner) {
                x = polystartpoint.x + (int) ((int) ((e.x - polystartpoint.x) / zoom) * zoom); 
                y = polystartpoint.y + (int) ((int) ((e.y - polystartpoint.y) / zoom) * zoom);
            }
            polygon.addPoint(x + (POLY_SIZE / 2), y + (POLY_SIZE / 2));
        }
        gfx.draw(polygon);
    }
     
    /**
     * Draws the corner rectangles.
     * @param gfx
     */
    private void drawCorners(Graphics2D gfx) {
        Rectangle[] c = getCorners();
        for (int i = 0, n = c.length; i < n; i++) {
            gfx.setColor(corner == i ? Color.RED : Color.WHITE);
            gfx.fill(c[i]);
            gfx.setColor(Color.BLACK);
            gfx.draw(c[i]);
        }
    }
}
