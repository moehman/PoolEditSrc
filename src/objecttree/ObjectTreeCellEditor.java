/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package objecttree;

import static pooledit.Definitions.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.w3c.dom.Element;
import pooledit.TreeEditPopup;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
class ObjectTreeCellEditor extends DefaultTreeCellEditor {

    final Container container = new Container();
    private XMLTreeNode node;
        
    /**
     * Constructor.
     * @param tree
     * @param renderer
     */
    public ObjectTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
        container.setLayout(new BorderLayout()); 
    }

    /**
     * Gets cell editor value.
     * @return
     */
    @Override
    public Object getCellEditorValue() {
        String value = (String) super.getCellEditorValue();
        try {
            TreeEditPopup.renameObject(node, value);
        }
        catch (IllegalArgumentException ex) {
        }
        catch (IllegalStateException ex) {
        }
        return node;
    }
    
    /**
     * Gets tree cell editor component.
     * @param tree
     * @param value
     * @param sel
     * @param expanded
     * @param leaf
     * @param row
     * @return
     */
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row) {
    
        node = (XMLTreeNode) value;
                        
        // get the name to be edited
        Element actual = node.actual();
        String name = actual.getAttribute(NAME);
        
        // get the editor (which does not print the icon for some reason...)
        Component editor = super.getTreeCellEditorComponent(tree, name, sel, expanded, leaf, row);

        // get the icon and put everything in the container
	Icon icon = node.link() != null ? 
            ObjectTreeCellRenderer.getIcon(ObjectTreeCellRenderer.LINK_ICONS, node.getType()) :
            ObjectTreeCellRenderer.getIcon(ObjectTreeCellRenderer.ICONS, node.getType());
        container.removeAll();
        container.add(new JLabel(icon), BorderLayout.WEST);
        container.add(editor, BorderLayout.EAST);
                
        return container;
    }
    
    /**
     * Checks, whether a cell is editable or not. Only elements with names
     * should be editable.
     * @param event
     * @return
     */
    @Override
    public boolean isCellEditable(EventObject event) {
        // find out the path to the selected node
        TreePath p;
        if (event == null) {
            p = tree.getSelectionPath();
        }
        else {
            MouseEvent e = (MouseEvent) event;
            p = tree.getClosestPathForLocation(e.getX(), e.getY());            
        }
        // check if the node should be editable (i.e. it has a name)
        XMLTreeNode n = (XMLTreeNode) p.getLastPathComponent();
        Element actual = n.actual();
        if (actual == null || !actual.hasAttribute(NAME)) {
            return false;
        }
        else {
            return super.isCellEditable(event);
        }
    }
}
