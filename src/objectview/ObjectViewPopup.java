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

import javax.swing.tree.TreePath;
import pooledit.TreeEditPopup;
import treemodel.XMLTreeModel;


/**
 *
 * @author jkalmari
 */
public class ObjectViewPopup extends TreeEditPopup {
    
    private final ObjectView objectView;
    
    /**
     * Constructor.
     * @param objectView
     */
    public ObjectViewPopup(ObjectView objectView){
        this.objectView = objectView;
    }    

    /**
     * Gets the current path (which is in this case the active path from the 
     * object view).
     * @return
     */
    @Override
    public TreePath getCurrentPath() {
        return objectView.getActivePath();
    }
    
    /**
     * Gets the xml tree model.
     * @return
     */
    @Override
    public XMLTreeModel getXMLTreeModel() {
        return objectView.getModel();
    }   
}
