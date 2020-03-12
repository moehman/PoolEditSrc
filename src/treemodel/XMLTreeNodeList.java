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
package treemodel;

import static pooledit.Definitions.*;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import pooledit.Utils;

/**
 *
 * @author mohman
 */
class XMLTreeNodeList extends ArrayList<XMLTreeNode> {

    private final XMLTreeNode parent;
    
    /**
     * Constructor.
     * @param list
     */
    protected XMLTreeNodeList(XMLTreeNodeList list) {
        super(list);
        this.parent = list.parent;
    }
    
    /**
     * Constructor.
     * @param parent
     */
    protected XMLTreeNodeList(XMLTreeNode parent) {
        this.parent = parent;
    
        XMLTreeModel model = parent.getModel();
        String type = parent.getType();
	int index;
        
        // parent is root -> create category nodes
	if (Utils.indexEquals(type, OBJECTPOOL) >= 0) {
	    for (int i = 0, n = CATEGORIES.length; i < n; i++) {
                XMLTreeNode node = model.CATEGORY_NODES[i];
                add(node);
	    }
	}
        // parent is a category node -> create subcategory nodes
        else if ((index = Utils.indexEquals(type, CATEGORIES)) >= 0) {
	    for (int i = 0, n = SUBCATEGORYGROUPS[index].length; i < n; i++) {
                XMLTreeNode node = model.SUBCATEGORY_NODES[index][i];
                add(node);
	    }
	}
        // parent is a subcategory node -> create actual nodes stored in subcategories
        else if ((index = Utils.indexEquals(type, SUBCATEGORIES)) >= 0) {
            
            // iterate over all elements
            Element root = parent.getModel().getDocument().getDocumentElement();
            NodeList elements = root.getChildNodes();
            for (int i = 0, n = elements.getLength(); i < n; i++) {
                Node node = elements.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE &&
                        node.getNodeName().equals(OBJECTS[index].type)) {

                    add(parent.createChildNode((Element) node));
                }
            }   
	}
        // parent is a real node (actual or link)
        else {
            
            // add child elements            
            Element element = parent.actual();
            NodeList elements = element.getChildNodes();
            for (int i = 0, n = elements.getLength(); i < n; i++) {
                Node node = elements.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    add(parent.createChildNode((Element) node));
		}
            }
        }
    }
}
