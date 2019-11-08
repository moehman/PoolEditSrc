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
package attributetable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import org.w3c.dom.Element;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class AttributeTablePopupMenu {
    
    private final JPopupMenu popup;
    private final AttributeTable table;
    
    /** 
     * Creates a new instance of AttributeTablePopupMenu 
     */
    public AttributeTablePopupMenu(AttributeTable table) {
        this.table = table;
        popup = createPopupMenu();
    }
    
    /**
     * Creates a popup menu.
     * @return
     */
    public JPopupMenu createPopupMenu() {
        JPopupMenu pup = new JPopupMenu();
        JMenuItem addAttribute = new JMenuItem("Add Attribute");
        addAttribute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField[] texts = new JTextField[] {new JTextField(), 
                    new JTextField()};
                int rv = showTextFieldDialog(null, new String[] {"Name", "Value"},
                        texts, "Add Attribute", JOptionPane.QUESTION_MESSAGE);
                if (rv != JOptionPane.OK_OPTION) {
                    return;
                }
                AttributeTableModel model = (AttributeTableModel) table.getModel();
                Element elem = model.getCurrentElement();
                elem.setAttribute(texts[0].getText(), texts[1].getText());                
            }
        });
        pup.add(addAttribute);
        
        JMenuItem removeAttribute = new JMenuItem("Remove Attribute");
        removeAttribute.addActionListener(new ActionListener() {
            /**
             * Removes the specified attribute from both actual and link nodes,
             * if any
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField[] texts = new JTextField[] {new JTextField()};
                int rv = showTextFieldDialog(null, new String[] {"Name"},
                        texts, "Remove Attribute", JOptionPane.QUESTION_MESSAGE);
                if (rv != JOptionPane.OK_OPTION) {
                    return;
                }
                AttributeTableModel model = (AttributeTableModel) table.getModel();
                XMLTreeNode node = model.getCurrentNode();
                if (node == null) {
                    return;
                }
                Element actual = node.actual();
                if (actual != null) {
                    actual.removeAttribute(texts[0].getText());                
                }
                Element link = node.link();
                if (link != null) {
                    link.removeAttribute(texts[0].getText());
                }
            }
        });
        pup.add(removeAttribute);
        return pup;
    }
    
    /**
     * Show a simple dialog assembled from labels and text fields.
     * @param parentComponent
     * @param labels
     * @param textFields
     * @param title
     * @param messageType
     * @return
     */
    public int showTextFieldDialog(Component parentComponent, String[] labels,
            JTextField[] textFields, String title, int messageType) {
        
        Object[] objects = new Object[labels.length + textFields.length];
        for (int i = 0, j = 0, k = 0; i < objects.length; ) {
            if (j < labels.length) {
                objects[i++] = labels[j++];
            }
            if (k < textFields.length) {
                objects[i++] = textFields[k++];
            }
        }
        return JOptionPane.showOptionDialog(parentComponent, objects, title,
                JOptionPane.OK_CANCEL_OPTION, messageType,
                null, null, null);
    }
    
    /**
     * Shows the popup menu.
     * @param e
     */
    public void showPopup(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
