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

import java.awt.Container;
import javax.swing.JViewport;
import static pooledit.Definitions.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class MouseController implements MouseListener, MouseMotionListener, MouseWheelListener {
    
    private final double MAX_ZOOM_FACTOR = 10;
    private final double MIN_ZOOM_FACTOR = 0.5;
    
    /** Link to the parent view */
    private ObjectView view;
    
    /** The original rectangle (before editing starts) */ 
    private Rectangle oldrect;
    
    /** The original point (location of polygon corner) */
    private Point oldpoint;
    
    /** Deltas during dragging and final release are calculated against this
     * point */
    private Point point;
   
    /** Popup window that is opened*/
    private ObjectViewPopup objectViewPopup;
        
    /** Keeps child object stationary, when the parent is expanded left / up */
    private boolean smartMoves = true;
    
    /** Creates a new instance of MouseController */
    public MouseController(ObjectView view, ObjectViewPopup objectViewPopup) {
        this.view = view;
        this.objectViewPopup = objectViewPopup;
    }

    @Override
    public void mouseDragged(MouseEvent e) {    
        if (point == null) {
            return;
        }
        SelectionRectangle selrect = view.getSelectionRectangle();
        int x = e.getX();
        int y = e.getY();
        if (oldpoint != null) {
            selrect.moveSelectedPolyCorner(x - point.x, y - point.y); 
        }
        if (oldrect != null) {
            selrect.moveSelectedCorner(x - point.x, y - point.y);
        }
        point = e.getPoint();
        view.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        SelectionRectangle selrect = view.getSelectionRectangle();
        if (e.isPopupTrigger()) {
            objectViewPopup.showPopup(e);
            selrect.deselectCorners();
            return;
        }
        //this makes it possible for objectview to listen the keys
        view.requestFocusInWindow();
        
        int x = e.getX();
        int y = e.getY();        
        
        if (selrect.isSet() && selrect.selectPolyCornerAt(x, y)) {
            point = e.getPoint();
            oldpoint = e.getPoint();
            view.repaint();
            return;
        }
        oldpoint = null;
        
        if (selrect.isSet() && selrect.selectCornerAt(x, y)) {
            point = e.getPoint();
            oldrect = selrect.getRectangle();
            view.repaint();
            return;
        }        
        oldrect = null;
        point = null;
        
        TreePath path = view.getPathToNodeAt(x, y);
        if (path == null) {
            return;
        }
        view.fireTreeSelection(path);
        //System.out.println("x: " + e.getX() + ", y: " + e.getY() + ", path: " + path);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        SelectionRectangle selrect = view.getSelectionRectangle();
        if (e.isPopupTrigger()) {
            objectViewPopup.showPopup(e);
            selrect.deselectCorners();
            return;
        }
        if (point == null) {
            return;
        }
        
        int x = e.getX();
        int y = e.getY();
        double z = view.getZoom();
        
        // polygon corner
        if (oldpoint != null) { 
            selrect.moveSelectedPolyCorner(x - point.x, y - point.y);
            Element element = selrect.getSelectedPolyCorner().actual();
            if (element != null) {
                changeAttribute(element, POS_X, x - oldpoint.x, z);
                changeAttribute(element, POS_Y, y - oldpoint.y, z);
            }              
        }
        // normal corner
        if (oldrect != null) {
            selrect.moveSelectedCorner(x - point.x, y - point.y);            
            Rectangle newrect = selrect.getRectangle();            
            XMLTreeNode node = (XMLTreeNode) selrect.getSource();

            int dx = newrect.x - oldrect.x;
            int dy = newrect.y - oldrect.y;            
            Element pos = node.link() != null ? node.link() : node.actual();
            if (pos != null) {
                changeAttribute(pos, POS_X, dx, z);
                changeAttribute(pos, POS_Y, dy, z);
            }        
            
            int dw = newrect.width - oldrect.width;
            int dh = newrect.height - oldrect.height;
            Element size = node.actual();
            if (size != null) {
                changeAttribute(size, WIDTH, dw, z);
                changeAttribute(size, HEIGHT, dh, z);
            }
            
            if (smartMoves) {
                // if object is expanded left, move children right to
                // keep them stationary
                if (dx == -dw) {
                    NodeList elements = size.getChildNodes();
                    for (int i = 0,  n = elements.getLength(); i < n; i++) {
                        Node child = elements.item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            changeAttribute((Element) child, POS_X, dw, z);
                        }
                    }
                }

                // if object is expanded up, move children down to
                // keep them stationary
                if (dy == -dh) {
                    NodeList elements = size.getChildNodes();
                    for (int i = 0,  n = elements.getLength(); i < n; i++) {
                        Node child = elements.item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            changeAttribute((Element) child, POS_Y, dh, z);
                        }
                    }
                }
            }
        }
        selrect.deselectCorners();
        view.repaint();    
        
        // just in case...
        point = null;
        oldrect = null;
        oldpoint = null;
    }

    /**
     * Changes the specified position or size attribute of the given element.
     * The attribute is changed by difference of the new and old pixel 
     * coordinates. Zoom factor is used to transform pixel coordinates to
     * actual design coordinates.
     * 
     * If the specified attribute does not exist, this method does nothing.
     * 
     * @param element
     * @param attr
     * @param diff
     * @param zoom
     */
    private void changeAttribute(Element element, String attr, int diff, double zoom) {
        String val = element.getAttribute(attr);
        if (!val.isEmpty()) {
            int p = Integer.parseInt(val);
            int d = (int) (diff / zoom);
            if (d != 0) {
                element.setAttribute(attr, Integer.toString(p + d));
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Implements zooming with the mouse wheel.
     * TODO: use the pointer position to affect the zooming area?
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int r = e.getWheelRotation();
        double z = view.getZoom();
        double z_ = z +  0.5 * r;
        z_ = Math.min(z_, MAX_ZOOM_FACTOR);
        z_ = Math.max(z_, MIN_ZOOM_FACTOR);
        
        // adjust the viewport (if any) so that the point under the mouse 
        // pointer stays stationary (in design coordinates)
        Container parent = view.getParent();
        if (parent instanceof JViewport) {
            double k = (z_ / z) - 1;
            int xm = e.getX();
            int ym = e.getY();
            JViewport viewport = (JViewport) parent;
            Point p = viewport.getViewPosition();
            double x = k * xm + p.x;
            double y = k * ym + p.y;
            viewport.setViewPosition(new Point((int) (x + 0.5), (int) (y + 0.5)));
        }
        
        view.setZoom(z_);
    }
}
