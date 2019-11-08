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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import treemodel.XMLTreeNode;
import pooledit.Utils;

/**
 *
 * @author mohman
 */
public class ObjectTreeCellRenderer extends DefaultTreeCellRenderer {

    static Icon[] ICONS = new Icon[TREE_ELEMENTS.length];
    static Icon[] LINK_ICONS = new Icon[TREE_ELEMENTS.length];
    
    static class LinkIcon implements Icon {
        private final Icon icon;        
        public LinkIcon(Icon icon) { this.icon = icon; }        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            icon.paintIcon(c, g, x, y);
            int w = getIconWidth();
            int w2 = w / 2;
            int w8 = w / 8;
            int h = getIconHeight();
            int h2 = h / 2;
            int h8 = h / 8;
            g.setColor(Color.BLUE);
            g.fillRect(w - w2, h - h2, w2, h2);
            g.setColor(Color.WHITE);
            g.drawLine(w - 3 * w8, h - 3 * h8, w - 3 * w8, h - h8);
            g.drawLine(w - 3 * w8, h - h8, w - w8, h - h8);
        }        
        @Override
        public int getIconWidth() { return icon.getIconWidth(); }
        @Override
        public int getIconHeight() { return icon.getIconHeight(); }    
    }
    
    static {
        for (int i = 0, n = TREE_ELEMENTS.length; i < n; i++) {
            ICONS[i] = Utils.createImageIcon(TREE_ELEMENT_PATH + 
                    TREE_ELEMENT_FILENAMES[i]);
        }
        for (int i = 0, n = TREE_ELEMENTS.length; i < n; i++) {
            LINK_ICONS[i] = new LinkIcon(ICONS[i]);
        }
    }
    
    /**
     * Gets icon.
     * @param type
     * @return
     */
    static public Icon getIcon(String type) {
        return getIcon(ICONS, type);
    }
    
    /**
     * Gets icon.
     * @param icons
     * @param type
     * @return
     */
    static protected Icon getIcon(Icon[] icons, String type) {
        if (type == null) {
            return null;
        }
	for (int i = 0, n = TREE_ELEMENTS.length; i < n; i++) {
	    if (type.equals(TREE_ELEMENTS[i])) {
		return icons[i];
	    }
	}
	return type.startsWith(COMMAND) ? icons[TREE_ELEMENTS.length - 1] : null;
    }

    /**
     * Gets a tree cell renderer component.
     * @param tree
     * @param value
     * @param sel
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, 
            boolean sel, boolean expanded, boolean leaf, int row, 
            boolean hasFocus) {

	super.getTreeCellRendererComponent
	    (tree, value, sel, expanded, leaf, row, hasFocus);

        XMLTreeNode node = ((XMLTreeNode) value); 	
	Icon icon = node.link() != null ? 
            getIcon(LINK_ICONS, node.getType()) :
            getIcon(ICONS, node.getType());
        
	if (icon != null) {
	    setIcon(icon);
	}
	return this;
    }           
}
