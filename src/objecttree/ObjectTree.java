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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import org.w3c.dom.Element;
import java.awt.dnd.DragGestureRecognizer;
import javax.swing.tree.TreeSelectionModel;
import dragndrop.XMLTransferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import pooledit.Definitions;
import pooledit.Utils;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class ObjectTree extends JTree implements KeyListener {
    
    /** 
     * Creates a new instance of ObjectTree.
     * @param model
     */
    private ObjectTree(XMLTreeModel model) {
        super(model);
    }

    static public ObjectTree getInstance(XMLTreeModel model) {
        ObjectTree ot = new ObjectTree(model);
        
         // create renderer and editor
        DefaultTreeCellRenderer renderer = new ObjectTreeCellRenderer();
        DefaultTreeCellEditor editor = new ObjectTreeCellEditor(ot, renderer);
        
        // set renderer and editor
        ot.setEditable(true);
        //setToggleClickCount(10); // default is two
        ot.setCellRenderer(renderer);
        ot.setCellEditor(editor);
        ot.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                
        final DragSource dragSource = DragSource.getDefaultDragSource();
        final DragSourceListener dsListener = new ObjectTreeDragSourceListener();
        
        // component, action, listener
        DragGestureRecognizer recognizer = dragSource.createDefaultDragGestureRecognizer(
                ot,
                DnDConstants.ACTION_COPY |
                DnDConstants.ACTION_MOVE |
                DnDConstants.ACTION_LINK,
                new DragGestureListener() {
            
            @Override
            public void dragGestureRecognized(DragGestureEvent e) {
                if ((e.getDragAction() & 
                        (DnDConstants.ACTION_COPY | 
                        DnDConstants.ACTION_MOVE | 
                        DnDConstants.ACTION_LINK)) == 0) {
                    
                    return;
                }
                // category nodes cannot be dragged
                TreePath path = ot.getSelectionPath();
                if (path == null) {
                    return;
                }
                XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
                if (node.isType(OBJECTPOOL) ||
                        node.isType(CATEGORIES) ||
                        node.isType(SUBCATEGORIES)) {
                    return;
                }
                // accept only 1st mouse button
                InputEvent ie = e.getTriggerEvent();
                if (ie instanceof MouseEvent &&
                        ((MouseEvent) ie).getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                /*
                // make dragging somewhat less sensitive (google for "Bug ID 4244358"),
                // in Windows there are typically 2 to 4 events (a mouse pressed followed
                // by mouse one or more mouse dragged events) -> DOES NOT WORK TOO GREAT...
                final int EVENT_LIMIT = 2;
                int count = 0;
                for (Iterator i = e.iterator(); count < EVENT_LIMIT && i.hasNext(); i.next()) {
                    count++;
                }
                if (count < EVENT_LIMIT) {
                    return;
                }
                 */
                // drag is recognized!
                try {
                    e.startDrag(DragSource.DefaultCopyNoDrop,
                            new XMLTransferable(ot.getSelectionPaths()),
                            dsListener);
                } 
                catch (InvalidDnDOperationException idoe) {
                    idoe.printStackTrace();
                }
            }
        });
        
        final DropTargetListener dtListener = new ObjectTreeDropTargetListener();
        
        // component, ops, listener, accepting
        final DropTarget dropTarget = new DropTarget(
                ot,
                DnDConstants.ACTION_COPY |
                DnDConstants.ACTION_MOVE |
                DnDConstants.ACTION_LINK,
                dtListener,
                true);
        
        // set up popup menu
        ObjectTreePopup objectTreePopup = new ObjectTreePopup(ot);
        ot.addMouseListener(objectTreePopup);
        
        // set up keylistener
        ot.addKeyListener(ot);
        
        return ot;
    }
    
    /**
     * Sets the active path.
     * @param tp
     */
    public void setActivePath(TreePath tp) {
        if (Utils.equalObjects(super.getSelectionPath(), tp)) {
            return;
        }
        // System.out.println("ObjectTree: setActivePath: " + tp);
        super.setSelectionPath(tp);
        
        // use scroll pane if needed
        scrollRowToVisible(getLeadSelectionRow());
        // System.out.println(getClass().getName() + ": setActivePath(): " + tp);
    }
    
    /**
     * Reacts to key types - does nothing.
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    /**
     * Reacts to key presses.
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            //System.out.println("DELETE: ");
            
            TreePath path = super.getSelectionPath();
            
            if (path != null && path.getPathCount() > 1) {
                
                XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
                Element actual = node.actual();
                Element link = node.link();
                
                // not all nodes can be deleted
                if (node.isType(Definitions.getTypes()) ||
                        node.isType(LANGUAGE, POINT, FIXEDBITMAP, INCLUDE_OBJECT) ||
                        node.getType().startsWith(COMMAND)) {
                    
                    // actual object (or broken link)
                    if (link == null || node.isType(INCLUDE_OBJECT)) {
                        actual.getParentNode().removeChild(actual);
                    }
                    // working link (link != null)
                    else {
                        link.getParentNode().removeChild(link);
                    }
                }
            }
        }
    }
    
    /**
     * Reacts to key releases - does nothing.
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }
}
