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

import static pooledit.Definitions.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.tree.TreePath;
import pooledit.TreeEditPopup;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;

/**
 *
 * @author Autlab
 */
public class ObjectTreePopup extends TreeEditPopup implements MouseListener {
    
    private final ObjectTree objectTree;
    
    /**
     * Constructor.
     * @param objectTree
     */
    public ObjectTreePopup(ObjectTree objectTree){
        this.objectTree = objectTree;             
    }

    /**
     * Gets the current path.
     * @return
     */
    @Override
    public TreePath getCurrentPath() {
        return objectTree.getSelectionPath();
    }

    /**
     * Gets the xml tree model.
     * @return
     */
    @Override
    public XMLTreeModel getXMLTreeModel(){
        return (XMLTreeModel) objectTree.getModel();
    }
    
    /**
     * Reacts to mouse presses.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
        
        // this is not absolutely necessary, but it makes starting of
        // in-place editing much more predictable!
        if (e.getClickCount() == 2) {
            // get current path and start in-place editing 
            // (if the editor allows it for the selected node)
            TreePath path = objectTree.getClosestPathForLocation(e.getX(), e.getY());
            objectTree.startEditingAtPath(path);
        }        
    }

    /**
     * Reacts to mouse releases.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    /**
     * Shows popup menu when appropriate.
     * @param e
     */
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            // popup trigger can change selection path
            TreePath path = objectTree.getClosestPathForLocation(e.getX(), e.getY());
            objectTree.setSelectionPath(path);
            
            // the popup is shown only for real isobus objects (and some other pseudo objects)
            // FIXME: how about commands?
            XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
            if (node.isType(OBJECTS) || 
                    node.isType(LANGUAGE, POINT, FIXEDBITMAP, INCLUDE_OBJECT) ||
                    node.getType().startsWith(COMMAND)) {                
                super.showPopup(e);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { 
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
