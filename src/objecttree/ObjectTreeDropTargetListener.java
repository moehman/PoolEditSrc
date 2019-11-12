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
package objecttree;

import static pooledit.Definitions.*;
import org.w3c.dom.Document;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.w3c.dom.Element;
import pooledit.PoolException;
import pooledit.Tools;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class ObjectTreeDropTargetListener implements DropTargetListener {
 
    private final boolean DEBUG = false;
    private final Rectangle rect2D = new Rectangle();
    private final Insets autoscrollInsets = new Insets(20, 20, 20, 20);
        
    private Rectangle lastRowBounds;
        
    // private ObjectTransferHandler handler;        
    private Point mostRecentLocation;
    private int insertAreaHeight = 8;
    private int acceptableActions = DnDConstants.ACTION_COPY | 
            DnDConstants.ACTION_MOVE | DnDConstants.ACTION_LINK; 
    private final DataFlavor stringFlavor;
    
    /** 
     * Creates a new instance of ObjectTreeDropListener 
     */
    public ObjectTreeDropTargetListener() {
        String stringType = DataFlavor.javaJVMLocalObjectMimeType +
                   ";class=java.lang.String";
        try {
            stringFlavor = new DataFlavor(stringType);
        } 
        catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Prints debug messages.
     * @param msg
     */
    private void dmsg(Object msg) {
        if (DEBUG) {
            System.out.println("[" + getClass().getName() + "]: " + msg);
        }
    }
    
     /* ----------------- drag image painting start ------------------ */

    /**
     * Paint the dragged node
    private final void paintImage(JTree tree, Point pt, TreePath[] paths) {
  	BufferedImage image = getDragImage(tree, paths);
  	if (image != null) {
	    tree.paintImmediately(rect2D.getBounds());
	    rect2D.setRect((int) pt.getX() - 15, (int) pt.getY() - 15,
			   image.getWidth(), image.getHeight());
	    tree.getGraphics().drawImage(image, (int) pt.getX() - 15,
					 (int) pt.getY() - 15, tree);
  	}
    }
  
    public BufferedImage getDragImage(JTree tree, TreePath[] paths) {
	BufferedImage image = null;
	try {
            TreePath dragPath = paths[0];
	    if (dragPath != null) {

		// FIXME: only the first node is drawn while dragging

		Rectangle pathBounds = tree.getPathBounds(dragPath);
		TreeCellRenderer r = tree.getCellRenderer();
		XMLTreeModel m = (XMLTreeModel)tree.getModel();
		boolean nIsLeaf = m.isLeaf(dragPath.getLastPathComponent());
		JComponent lbl = (JComponent)r.getTreeCellRendererComponent(tree, dragPath.getLastPathComponent(), false, 
									    tree.isExpanded(dragPath), nIsLeaf, 0, false);
		lbl.setBounds(pathBounds);
		image = new BufferedImage(lbl.getWidth(), lbl.getHeight(), 
					  java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D graphics = image.createGraphics();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		lbl.setOpaque(false);
		lbl.paint(graphics);
		graphics.dispose();
	    }
	}
	catch (RuntimeException re) {
	    // nothing to be done?
	}
	return image;
    }
    */
    
    /**
     * Clear drawings
     * @param tree
     */
    private void clearImage(JTree tree) {
	tree.paintImmediately(rect2D.getBounds());
    }

    /* ----------------- drag image painting end ------------------ */

    /* ----------------- autoscroll implementation start ---------- */

    /**
     * Gets autoscroll insets.
     * @return
     */
    private Insets getAutoscrollInsets() {
	return autoscrollInsets;
    }

    /**
     * Scroll visible tree parts when user drags outside an 'inner
     * part' of the visible region.
     * @param tree
     * @param cursorLocation
     */
    private void autoscroll(JTree tree, Point cursorLocation) {
	Insets insets = getAutoscrollInsets();
	Rectangle outer = tree.getVisibleRect();
	Rectangle inner = new Rectangle
	    (outer.x + insets.left, outer.y + insets.top,
	     outer.width - (insets.left+insets.right), outer.height - (insets.top+insets.bottom));
	if (!inner.contains(cursorLocation))  {
	    Rectangle scrollRect = new Rectangle
		(cursorLocation.x - insets.left, cursorLocation.y - insets.top,
		 insets.left + insets.right, insets.top + insets.bottom);
	    tree.scrollRectToVisible(scrollRect);
	}
    }

    /* ----------------- autoscroll implementation end ---------- */

    /* ----------------- insertion mark painting start ---------- */

    /**
     * Manage display of a drag mark either highlighting a node or drawing an
     * insertion mark.
     * @param tree
     * @param location
     */
    public void updateDragMark(JTree tree, Point location) {
  	mostRecentLocation = location;
	int row = tree.getRowForPath(tree.getClosestPathForLocation(location.x, location.y));
	TreePath path = tree.getPathForRow(row);
	if (path != null) {
	    Rectangle rowBounds = tree.getPathBounds(path);
	    
	    // find out if we have to mark a tree node or if we have
	    // to draw an insertion marker
	    int rby = rowBounds.y;
	    int topBottomDist = insertAreaHeight / 2;
	    // x = top, y = bottom of insert area
	    Point topBottom = new Point(rby - topBottomDist, 
					rby + topBottomDist);

	    if (topBottom.x <= location.y && topBottom.y >= location.y) {
		// we are inside an insertArea
		paintInsertMarker(tree, location);
	    }
	    else {
		// we are inside a node
		markNode(tree, location);
	    }
	}
    }
  
    /**
     * Get the most recent mouse location, i.e. the drop location when
     * called upon drop.
     * @return the mouse location recorded most recently during a drag
     * operation
     */
    public Point getMostRecentDragLocation() {
  	return mostRecentLocation;
    }
  
    /**
     * Mark the node that is closest to the current mouse location.
     * @param tree
     * @param location
     */
    private void markNode(JTree tree, Point location) {
	TreePath path = tree.getClosestPathForLocation(location.x, location.y);
	if (path != null) {
	    if (lastRowBounds != null) {
		Graphics2D g = (Graphics2D) tree.getGraphics();
		g.setColor(Color.WHITE);
		g.drawLine(lastRowBounds.x, lastRowBounds.y, 
			   lastRowBounds.x + lastRowBounds.width, lastRowBounds.y);
	    }
	    tree.setSelectionPath(path);
            
            // this will cause tree to expand as the object is dragged over 
            // it, very annoying! (there should be a small delay at least)
	    //tree.expandPath(path); 
	}
    }

    /**
     * Paint an insert marker between the nodes closest to the current
     * mouse location.
     * @param tree
     * @param location
     */
    private void paintInsertMarker(JTree tree, Point location) {
	Graphics2D g = (Graphics2D) tree.getGraphics();
	tree.clearSelection();
	int row = tree.getRowForPath(tree.getClosestPathForLocation(location.x, location.y));
	TreePath path = tree.getPathForRow(row);
	if (path != null) {
	    Rectangle rowBounds = tree.getPathBounds(path);
	    if (lastRowBounds != null) {
		g.setColor(Color.WHITE);
		g.drawLine(lastRowBounds.x, lastRowBounds.y, 
			   lastRowBounds.x + lastRowBounds.width, lastRowBounds.y);
	    }
	    if (rowBounds != null) {
		g.setColor(Color.BLACK);
		g.drawLine(rowBounds.x, rowBounds.y, rowBounds.x + rowBounds.width, rowBounds.y);
	    }
	    lastRowBounds = rowBounds;
	}
    }

    /* ----------------- insertion mark painting end ------------------ */
    
    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (isDragOk(e) == false) {
            dmsg("dragEnter (not ok)");
            e.rejectDrag();
            return;
        }
        dmsg("dragEnter (ok)");
        e.acceptDrag(e.getDropAction());
    }
    
    @Override
    public void dragOver(DropTargetDragEvent e) {
	JTree tree = (JTree) e.getDropTargetContext().getComponent();
	Point loc = e.getLocation();
	updateDragMark(tree, loc);
	
        //XMLTransferable trans = (XMLTransferable) e.getTransferable();
        //paintImage(tree, loc, trans.getPaths());
        
	autoscroll(tree, loc);
        
        if (isDragOk(e) == false) {
            dmsg("dragOver (not ok)");
            e.rejectDrag();
            return;
        }
        dmsg("dragOver (ok)");
        e.acceptDrag(e.getDropAction());
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent e) {
        if (isDragOk(e) == false) {
            dmsg("dropActionChanged (not ok)");
            e.rejectDrag();
            return;
        }
        dmsg("dropActionChanged (ok)");
        e.acceptDrag(e.getDropAction());
    }
    
    @Override
    public void dragExit(DropTargetEvent e) {
        dmsg("dragExit");
        clearImage((JTree) e.getDropTargetContext().getComponent());
    }
    
    @Override
    public void drop(DropTargetDropEvent e) {
        dmsg("drop");
        clearImage((JTree) e.getDropTargetContext().getComponent());
                
        DataFlavor chosen = chooseDropFlavor(e);
        if (chosen == null) {
            System.err.println("No flavor match found");
            e.rejectDrop();
            return;
        }
        dmsg("Chosen data flavor is " + chosen.getMimeType());
        
        // the actual operation
        int da = e.getDropAction();
        // the actions that the source has specified with DragGestureRecognizer
        int sa = e.getSourceActions();
        dmsg("drop: sourceActions: " + sa + "\tdropAction: " + da);
        
        if ((da & acceptableActions) == 0 ) {            
            dmsg("No action match found");
            e.rejectDrop();
            return;
        }
        
        Object data;
        try {
            /*
             * the source listener receives this action in dragDropEnd.
             * if the action is DnDConstants.ACTION_COPY_OR_MOVE then
             * the source receives MOVE!
             */
            e.acceptDrop(da);           
            data = e.getTransferable().getTransferData(chosen);
            if (data == null) {
                throw new NullPointerException();
            }
        } 
        catch (Throwable t) {
            System.err.println("Couldn't get transfer data: " + t.getMessage());
            t.printStackTrace();
            e.dropComplete(false);
            return;
        }
        dmsg("got: (" + data.getClass().getName() + ")");
        
        String result;
        if (data instanceof String ) {
            result = (String) data;
        }
        else if (data instanceof StringReader) {
            StringReader rdr = (StringReader) data;
            StringBuilder bff = new StringBuilder();
            try {
                int in;
                while ((in = rdr.read()) >= 0) {
                    if (in != 0) {
                        bff.append((char) in);
                    }
                }                
                result = bff.toString();
            } 
            catch (IOException ioe) {
                /*
                  bug #4094987
                  sun.io.MalformedInputException: Missing byte-order mark
                  e.g. if dragging from MS Word 97 still a bug in 1.2 final
                 */
                dmsg("cannot read " + ioe);
                e.dropComplete(false);
                return;
            }
        }
        // incompatible data
        else {
            dmsg("incompatible data: (" + data.getClass() + ")");
            e.dropComplete(false);
            return;
        }
        
        Component target = e.getDropTargetContext().getComponent();
        if (!(target instanceof JTree)) {
            e.dropComplete(false);
            return;
        }
        
        JTree tree = (JTree) target;
        TreePath currentPath = tree.getSelectionPath();
        boolean asSibling = (currentPath == null);
        
        // if a node is selected, add child nodes to it
        XMLTreeNode otherNode;
        if (asSibling) {
            // find sibling
            Point location = e.getLocation();
            TreePath closestPath = tree.getClosestPathForLocation(location.x, location.y);
            otherNode = (XMLTreeNode) closestPath.getLastPathComponent();
        } 
        else {
            // if parent is a category node, stuff is added to the root
            otherNode = (XMLTreeNode) currentPath.getLastPathComponent();
            /*
            if (otherNode.isType(CATEGORIES) || otherNode.isType(SUBCATEGORIES)) {
                otherNode = (XMLTreeNode) otherNode.getModel().getRoot();
            }
             */
        }
        
        // if parent is not a real object (or a command), user root instead
        if (!otherNode.isType(OBJECTS) &&
            !otherNode.isType(POINT) &&
            !otherNode.getType().startsWith(COMMAND))
        {
            //System.out.println("SUBSTITUTING: " + otherNode);
            otherNode = (XMLTreeNode) otherNode.getModel().getRoot();
            asSibling = false;
        }
        
        Document doc = otherNode.getModel().getDocument();
        
        // parse result
        Element fragment = Tools.parseFragment(result, doc);
        if (fragment == null) {
            e.dropComplete(false);
            return;
        }
        
        // implement the appropriate action (COPY, MOVE, LINK)
        if (e.getDropAction() == DnDConstants.ACTION_COPY ||
                e.getDropAction() == DnDConstants.ACTION_MOVE) {
            
            // if copy has a name, it needs to be unique
            String name = fragment.getAttribute(NAME);
            if (!name.isEmpty()) {
                String newname = Tools.findFreeName(name, Tools.createNameMap(doc, true));
                fragment.setAttribute(NAME, newname);  
            }
            
            e.dropComplete(Tools.insertFragment(fragment, otherNode.actual(), otherNode.link(), asSibling));    
        }
        /*
        else if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
        
            // moved object should retain its name, if there is no name 
            // conflict
            String name = fragment.getAttribute(NAME);
            if (Tools.createNameMap(doc).containsKey(name)) {
                String newname = Tools.findFreeName(name, Tools.createNameMap(doc, true));
                fragment.setAttribute(NAME, newname); 
            }
                        
            Tools.insertFragment(fragment, elem, asSibling);
            e.dropComplete(true);    
        }
         */
        else if (e.getDropAction() == DnDConstants.ACTION_LINK) {
            
            Element link = fragment.getOwnerDocument().createElement(INCLUDE_OBJECT);
            Tools.copyAttributes(fragment, link, NAME, POS_X, POS_Y, 
                    BLOCK_COL, BLOCK_ROW, BLOCK_FONT, ROLE);
            
            try {
                if (Tools.createsLoop(link, otherNode.actual(), otherNode.link(), asSibling)) {
                    System.out.println("OPERATION ABORTED AS IT WOULD CREATE A LOOP!");
                    e.dropComplete(false);
                }
                else {
                    e.dropComplete(Tools.insertFragment(link, otherNode.actual(), otherNode.link(), asSibling));
                }
            }
            catch (PoolException ex) {
                e.dropComplete(false);
            }
        }
    }
    
    /**
     * Checks to see if the flavor drag flavor is acceptable.
     * @param e the DropTargetDragEvent object
     * @return whether the flavor is acceptable
     */
    private boolean isDragFlavorSupported(DropTargetDragEvent e) {
        boolean ok = false;
        if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            ok = true;
        }
        /* Deprecated as of 1.3.
        else if (e.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {                    
            ok = true;
        }
        */
        else if (e.isDataFlavorSupported(stringFlavor)) { // shouldn't this be identical to stringFlavor (but it isn't)?
            ok = true;
        }
        else {
            dmsg("Supported flavors are:");
            DataFlavor[] list = e.getCurrentDataFlavors();
            for (int i = 0, n = list.length; i < n; i++) {
                dmsg(list[i]);
            }
            dmsg("Ref");
            dmsg(stringFlavor);
        }
        return ok;
    }
    
    /**
     * Checks the flavors and operations.
     * @param e the DropTargetDropEvent object
     * @return the chosen DataFlavor or null if none match
     */
    private DataFlavor chooseDropFlavor(DropTargetDropEvent e) {
        DataFlavor chosen = null;
        if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            chosen = DataFlavor.stringFlavor;
        }
        /* Deprecated as of 1.3.
        else if (e.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
            chosen = DataFlavor.plainTextFlavor;
        }
        */
        else if (e.isDataFlavorSupported(stringFlavor)) {
            chosen = stringFlavor;
        }
        return chosen;
    }
    
    /**
     * Checks the flavors and operations.
     * @param e the event object
     * @return whether the flavor and operation is ok
     */
    private boolean isDragOk(DropTargetDragEvent e) {
        if (isDragFlavorSupported(e) == false) {
            dmsg("isDragOk: (not ok)");
            return false;
        }
        
        // the actions specified when the source
        // created the DragGestureRecognizer
        //      int sa = e.getSourceActions();
        
        // the docs on DropTargetDragEvent rejectDrag says that
        // the dropAction should be examined
        int da = e.getDropAction();
        dmsg("dt drop action " + da + " my acceptable actions " + acceptableActions);
        
        // we're saying that these actions are necessary
        if ((da & acceptableActions) == 0) {
            return false;
        }
        dmsg("isDragOk: (ok)");
        return true;
    }
}
