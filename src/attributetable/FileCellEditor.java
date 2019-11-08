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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author mohman
 */
public class FileCellEditor extends DefaultCellEditor implements 
        TableCellEditor, ActionListener {
    
    static private final String PATH = "images\\";
    private final JPanel panel = new JPanel();
    private final JButton button = new JButton("...");
    private final JFileChooser filechooser = new JFileChooser();  
    
    private File file;
    private String value; // super class has probably value as well, but it is not writable?
    
    /** 
     * Creates a new instance of FileCellEditor 
     */
    public FileCellEditor() {
        super(new JTextField());
        //panel.setBackground(Color.WHITE);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addActionListener(this);
            
        panel.setLayout(new BorderLayout());
        panel.add(super.getComponent(), BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        
        filechooser.setCurrentDirectory(new File(PATH));
    }

    /**
     * Shows the open file dialog when the button is pressed.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = filechooser.getSelectedFile();
        }
        stopCellEditing();
    }
    
    /**
     * Gets a table cell editor component.
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param column
     * @return
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
            boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        this.value = (String) value;
        filechooser.setSelectedFile(new File(this.value));
        return panel;
    }

    /**
     * Gets cell editor value.
     * @return
     */
    @Override
    public Object getCellEditorValue() {
        if (file != null) {
            value = file.getName();
            file = null;
        }
        else {
            value = (String) super.getCellEditorValue();
        }
        return value;
    }
}
