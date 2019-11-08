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
package dragndrop;

import static pooledit.Definitions.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.tree.TreePath;
import pooledit.Tools;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class XMLTransferable implements Transferable {
    
    private TreePath[] paths;
            
    /** Creates a new instance of XMLTransferrable */
    public XMLTransferable(TreePath[] paths) {
        this.paths = paths;
    }

    public TreePath[] getPaths() {
        return paths;
    }
    
    /**
     * Returns an array of DataFlavor objects indicating the flavors the data 
     * can be provided in.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.stringFlavor};
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.stringFlavor.equals(flavor);
    }

    /**
     * Returns an object which represents the data to be transferred.  The class 
     * of the object returned is defined by the representation class of the flavor.
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        
        if (DataFlavor.stringFlavor.equals(flavor)) {
            String s = "";
            
            // FIXME: do not send separate objects, combine them?
            for (int i = 0, n = paths.length; i < n; i++) {
                XMLTreeNode node = (XMLTreeNode) paths[i].getLastPathComponent();
                
                // broken link
                if (node.isType(INCLUDE_OBJECT)) {
                    s = Tools.writeToStringNoDec(node.actual());
                }
                else {
                    //s = Tools.writeToStringNoDec(Tools.createMergedElementRecursive(node.actual(), node.getModel().getNameMap()));
                    
                    // this is very intresting: 
                    // the createMergedElementRecursive method is very good at handling links
                    // - if the first object is a link, the method must also be given a link, 
                    // if a consistent behaviour is expected - it usually is :)
                    s = Tools.writeToStringNoDec(Tools.createMergedElementRecursive(
                            node.link() != null ? node.link() : node.actual(), 
                            node.getModel().getNameMap()));
                }
            }
            return s;
        }
        throw new UnsupportedFlavorException(flavor);        
    }
}
