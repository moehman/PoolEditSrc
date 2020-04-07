/*
 * Copyright (C) 2020 Automation technology laboratory,
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
package treemodel;

import javax.swing.tree.TreePath;
import org.w3c.dom.Element;

/**
 *
 * @author Alan Valmorbida
 */
public class XMLTreeAction {
    public int type;
    public Element elem;
    public TreePath parentPath;
    public Element nextSibling;
    public String name;
    public String newValue;
    public String oldValue;
    
    public static final int actionRemove = 0;
    public static final int actionInsert = 1;
    public static final int actionChange = 2;
    
    
    public XMLTreeAction(int type, Element elem, TreePath parentPath, Element nextSibling, String name, String newValue, String oldValue) {
        this.type = type;
        this.elem = elem;
        this.parentPath = parentPath;
        this.nextSibling = nextSibling;
        this.name = name;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }
}
