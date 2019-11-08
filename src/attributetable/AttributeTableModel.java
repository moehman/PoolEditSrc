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

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import pooledit.Utils;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class AttributeTableModel extends AbstractTableModel implements EventListener {
    
    private Document doc;
    private TreePath path;
    private boolean newDoc = true;
    
    private XMLTreeNode node;
    private NamedNodeMap attribs;
    private NamedNodeMap linkAttribs;
    
    private final String[] COL_NAMES = {"Name", "Value"};
   
    /**
     * Sets the document.
     * @param doc
     */
    public void setDocument(Document doc) {
        if (this.doc == doc) {
            return;
        }
        stopCellEditing(); // this has to be done first!
        
        if (this.doc != null) {
            ((EventTarget) this.doc).removeEventListener("DOMAttrModified", this, false);
        }        
        ((EventTarget) doc).addEventListener("DOMAttrModified", this, false);
        
        this.doc = doc;
        this.newDoc = true;
        this.path = null;
        this.node = null;
        this.attribs = null;  
        this.linkAttribs = null;
        fireTableDataChanged();
    }
       
    /**
     * Sets the active path.
     * @param path
     */
    public void setActivePath(TreePath path) {           
        if (Utils.equalObjects(this.path, path) && !newDoc) {
            return;
        }
        this.path = path;
        this.newDoc = false;
        stopCellEditing(); // this has to be done first!     
        
        if (path == null) {
            this.attribs = null;
            this.linkAttribs = null;
        }
        else {
            this.node = (XMLTreeNode) path.getLastPathComponent();       
            this.attribs = node.actual() == null ? null : node.actual().getAttributes();
            this.linkAttribs = node.link() == null ? null : node.link().getAttributes();
        }
        fireTableDataChanged();       
    }
    
    /**
     * Iterates over table model listeners. If an instance of JTable is found
     * and it has an active cell editor, the editing process is interrupted.
     *
     * NOTE: THIS HAS TO BE TRIGGERED WHEN THE DOCUMENT OR THE ACTIVE PATH 
     * IS CHANGED - NOT WHEN THE MODEL IS CHANGED AS CALLING STOPCELLEDITING
     * WILL CHANGE THE MODEL (INFINITE RECURSION)
     */
    private void stopCellEditing() {
        // EventListenerList works really strangely, see the documentation!
        Object[] lsts = listenerList.getListenerList();
	// Process the listeners in reverse order
	for (int i = lsts.length - 2; i >= 0; i -= 2) {
	    if (lsts[i+1] instanceof JTable) {
                JTable table = (JTable) lsts[i+1];
                TableCellEditor tableCellEditor = table.getCellEditor();
                if (tableCellEditor != null) {
                    table.getCellEditor().stopCellEditing();
                }
	    }
	}
    }

    /**
     * Gets the document.
     * @return
     */
    public Document getDocument() {
        return doc;
    }
    
    /**
     * Gets the current element.
     * @return
     */
    public Element getCurrentElement() {
        return node != null ? node.actual() : null;
    }
    
    /**
     * Gets the current node.
     * @return
     */
    public XMLTreeNode getCurrentNode() {
        return node;
    } 
    
    //------------------------------------------//
    
    @Override
    public String getColumnName(int col) {
	return COL_NAMES[col];
    }
    @Override
    public int getColumnCount() {
        return 2;
    }
    @Override
    public int getRowCount() {
        int count = attribs == null ? 0 : attribs.getLength() + 1; // parents;
        count += linkAttribs == null ? 0 : linkAttribs.getLength();        
	return count;
    }
  
    @Override
    public Object getValueAt(int row, int col) {        
        if (row == (getRowCount() - 1)) {
            return col == 0 ? "parents" : "";
        }
        else if (row < attribs.getLength()) {
            Attr attr = (Attr) attribs.item(row);
            return col == 0 ? attr.getName() : attr.getValue();
        }
        else {
            Attr attr = (Attr) linkAttribs.item(row - attribs.getLength());
            return col == 0 ? attr.getName() : attr.getValue();
        }
    }
    
    @Override
    public void setValueAt(Object val, int row, int col) {
	if (col != 1) {
            return;
        }
        int nro = attribs.getLength();
        
        // last row contains the list of nodes that reference this node
        if (row == (getRowCount() - 1)) {    
            if (val != null && !((String) val).isEmpty()) {
                // FIXME!
                // if names are ambiguous, there is not enough information here
                // to select the right node!
                // final TreePath newpath = node.getModel().findPathByName((String) val);
                final TreePath newpath = node.getModel().findPathByPath((String) val);
                // System.out.println("Go to node: " + val + ", path: " + newpath);                
                fireTreeSelection(newpath);
            }
        }
        else if (row < nro) {
            Attr attr = (Attr) attribs.item(row);
            attr.setValue((String) val);
            fireTableCellUpdated(row, col);
        }
        else {
            Attr attr = (Attr) linkAttribs.item(row - nro);
            attr.setValue((String) val);
            fireTableCellUpdated(row, col);
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
	return col == 1;
    }

    //------------------------------------------------------------//
    
    private final Vector<TreeSelectionListener> listeners = new Vector<>();
    
    /**
     * Adds listener.
     * @param l
     */
    public void addTreeSelectionListener(TreeSelectionListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Removes listener.
     * @param l
     * @return true if listeners contained the specified listener
     */
    public boolean removeTreeSelectionListener(TreeSelectionListener l) {
        return listeners.remove(l);
    }
    
    /**
     * Fires a tree selection event.
     * @param newPath
     */
    public void fireTreeSelection(TreePath newPath) {
        TreeSelectionEvent e = new TreeSelectionEvent(this,
                          newPath,
                          newPath.equals(path),
                          path,
                          newPath);

        for (TreeSelectionListener l : listeners) {
            l.valueChanged(e);
        }
    }

    //------------------------------------------------------------//
    
    /**
     * Reacts to events from the dom model.
     * Note: This code does not execute in the GUI thread!!!
     * @param evt
     */
    @Override
    public void handleEvent(Event evt) {
        try {
            MutationEvent mev = (MutationEvent) evt;
            Element child = (Element) mev.getTarget();
            String type = mev.getType();
            if (type.equals("DOMAttrModified") && node != null) {
                
                // we are interested in both link and actual node changes
                if (node.link() == child || node.actual() == child) {
                    fireTableDataChanged();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
