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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author mohman
 */
public class FileCellRenderer extends DefaultTableCellRenderer {
    
    private final JPanel panel = new JPanel();
    /** This button is just for show, it does not have any listeners */
    private final JButton button = new JButton("...");
            
    /** 
     * Private constructor, use getInstance() instead.
     */
    private FileCellRenderer() {
        button.setMargin(new Insets(0, 0, 0, 0));         
        panel.setLayout(new BorderLayout());
        panel.add(button, BorderLayout.EAST);
    }
    
    /**
     * Creates a FileCellEditor.
     * @return
     */
    static public FileCellRenderer getInstance() {
        FileCellRenderer cr = new FileCellRenderer();
        cr.panel.add(cr);
        return cr;
    }
    
    /**
     * DefaultTableCellRenderer extends JLabel and normally the returned 
     * component from this method is "this". Both JPanel and JLabel extend
     * JComponent, and so they both share foreground and background attributes.
     * @param tbl
     * @param val
     * @param isSel
     * @param hasFocus
     * @param row
     * @param col
     * @return
     */
    @Override
    public Component getTableCellRendererComponent(JTable tbl, Object val, 
            boolean isSel, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(tbl, val, isSel, hasFocus, row, col);
        panel.setForeground(getForeground());
        panel.setBackground(getBackground());        
        return panel;
    }
}
