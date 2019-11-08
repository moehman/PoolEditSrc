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
package objecttree;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 *
 * @author mohman
 */
public class ObjectTreeDragSourceListener implements DragSourceListener {
    
    private final boolean DEBUG = false;
    
    /** 
     * Creates a new instance of ObjectTreeDragListener 
     */
    public ObjectTreeDragSourceListener() {
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
    
    @Override
    public void dragEnter(DragSourceDragEvent e) {
        dmsg("dragEnter");
        //intersection of the users selected action, and the source and target actions
        setCursor(e.getDropAction(), e.getDragSourceContext());
    }
    
    @Override
    public void dragOver(DragSourceDragEvent e) {
        DragSourceContext c = e.getDragSourceContext();
        dmsg(   "dragOver:" +
                " source actions " + c.getSourceActions() +
                " user action " + e.getUserAction() +
                " drop actions " + e.getDropAction() +
                " target actions " + e.getTargetActions());
    }
    
    @Override
    public void dropActionChanged(DragSourceDragEvent e) {
        dmsg("dropActionChanged");
        setCursor(e.getDropAction(), e.getDragSourceContext());
    }
    
    /**
     * Sets cursor.
     * @param dropAction
     * @param context
     */
    private void setCursor(int dropAction, DragSourceContext context) {
        if (dropAction == DnDConstants.ACTION_COPY) {
            context.setCursor(DragSource.DefaultCopyDrop);
        } 
        else if (dropAction == DnDConstants.ACTION_MOVE) {
            context.setCursor(DragSource.DefaultMoveDrop);
        }
        else if (dropAction == DnDConstants.ACTION_LINK) {
            context.setCursor(DragSource.DefaultLinkDrop);
        }
        else {
            context.setCursor(DragSource.DefaultCopyNoDrop);
        }
    }
    
    @Override
    public void dragExit(DragSourceEvent e) {
        dmsg("dragExit");
        DragSourceContext context = e.getDragSourceContext();
    }
    
    @Override
    public void dragDropEnd(DragSourceDropEvent e) {
        if (e.getDropSuccess() == false) {
            dmsg("dragDropEnd (not ok)");
            return;
        }
        
        /*
         * the dropAction should be what the drop target specified
         * in acceptDrop
         */
        dmsg("dragDropEnd (ok)");
        
        // this is the action selected by the drop target
        if (e.getDropAction() != DnDConstants.ACTION_MOVE) {
            return;
        }
        
        Object source = e.getSource();
        if (!(source instanceof ObjectTree)) {
            return;
        }
        
        ObjectTree tree = (ObjectTree) source;
        
        //System.out.println("REMOVE SOMETHING FROM THE TREE!");
    }
}
