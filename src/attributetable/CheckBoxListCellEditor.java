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
package attributetable;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import pooledit.Utils;

/**
 *
 * @author mohman
 */
public class CheckBoxListCellEditor extends DefaultCellEditor {
    
    static private final Icon CHECK = Utils.createImageIcon("/images/check.png");
    static private final Icon NOCHECK = Utils.createImageIcon("/images/nocheck.png");
    
    static private final Icon EMPTY = new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) { }
        @Override
        public int getIconWidth() { return CHECK.getIconWidth(); }
        @Override
        public int getIconHeight() { return CHECK.getIconHeight(); }
    };

    static class CheckBoxListCellRenderer implements ListCellRenderer<String> {
        protected DefaultListCellRenderer defren = new DefaultListCellRenderer();
        private final String selectedItems;
        CheckBoxListCellRenderer(String selectedItems) {
            this.selectedItems = selectedItems;
        }
        @Override
        public Component getListCellRendererComponent(JList list, String val, 
                int idx,  boolean isSel, boolean hasFocus) {
            JLabel renderer = (JLabel) defren.getListCellRendererComponent
                (list, val, idx, isSel, hasFocus);
            
            String clr = (String) val;
            renderer.setIcon(clr.isEmpty() ? EMPTY : Utils.optionsContain(selectedItems, clr) ? CHECK : NOCHECK);            
            renderer.setText(clr);
            return renderer;
        }
    }
       
    static private JComboBox<String> createJComboBox(String ... allItems) {
        JComboBox<String> cb = new JComboBox<>();
        cb.addItem(""); // "no change item"
        for (int i = 0, n = allItems.length; i < n; i++) {
            cb.addItem(allItems[i]);
        }
        return cb;
    }
    
    private final String[] allItems;
    private String selectedItems;
    
    /**
     * Constructor.
     * @param allItems
     */
    public CheckBoxListCellEditor(String ... allItems) {
        super(createJComboBox(allItems));
        this.allItems = allItems;
    }

    /**
     * Gets a tree cell editor component.
     * @param tree
     * @param value
     * @param isSelected
     * @param expanded
     * @param leaf
     * @param row
     * @return
     */
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row) {
	//this.selectedItems = tree.convertValueToText(value, isSelected,
	//				    expanded, leaf, row, false);
        this.selectedItems = (String) value;
        JComboBox<String> cb = (JComboBox<String>) this.getComponent();
        cb.setRenderer(new CheckBoxListCellRenderer(selectedItems));
	return super.getTreeCellEditorComponent(tree, "" /*value*/, isSelected, expanded, leaf, row);
    }

    /**
     * Gets a table cell editor component.
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param col
     * @return
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int col) {
        this.selectedItems = (String) value;
        JComboBox<String> cb = (JComboBox<String>) this.getComponent();
        cb.setRenderer(new CheckBoxListCellRenderer(selectedItems));
	return super.getTableCellEditorComponent(table, "" /*value*/, isSelected, row, col);
    }
     
    /**
     * Gets cell editor value.
     * @return
     */
    @Override
    public Object getCellEditorValue() {
        String rv = (String) super.getCellEditorValue();
        // selecting the empty item has no effect to the selected items
        if (rv.isEmpty()) {
            rv = selectedItems;
        }
        // selecting already selected item removes it from the list
        else if (Utils.optionsContain(selectedItems, rv)) {
            rv = Utils.optionsRemove(selectedItems, rv);
        }
        // selecting non-selected item adds it to the list
        else {
            rv = selectedItems + "+" + rv;
        }
        // regenerate options string from the "all items" list,
        // this way the order of the items stays the same every time
        StringBuilder out = new StringBuilder();
        for (int i = 0, n = allItems.length; i < n; i++) {
            if (Utils.optionsContain(rv, allItems[i])) {
                if (out.length() > 0) {
                    out.append('+');
                }
                out.append(allItems[i]);
            }
        }
        return out.toString();
    }
}
