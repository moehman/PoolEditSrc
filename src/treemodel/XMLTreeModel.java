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
package treemodel;

import static pooledit.Definitions.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pooledit.Tools;

/**
 * XML tree model, maps DOM to TreeModel interface
 *
 * @author mohman
 */
public class XMLTreeModel implements TreeModel, EventListener {
    
    /** Category nodes for this model */
    protected final XMLTreeNode[] CATEGORY_NODES = new XMLTreeNode[CATEGORIES.length];
    /** Subcategory nodes for this model */
    protected final XMLTreeNode[][] SUBCATEGORY_NODES = new XMLTreeNode[SUBCATEGORYGROUPS.length][];
    
    private Document doc;
    private XMLTreeNode root;
    private Map<String, Element> nameMap;
    
    private Map<XMLTreeNode, XMLTreeNodeList> childMap;
    
    /**
     * Default constructor.
     */
    public XMLTreeModel() {
        this.childMap = new HashMap();
    }
    
    /**
     * 
     * @param doc
     * @param nameMap
     */
    public XMLTreeModel(Document doc, Map<String, Element> nameMap) {
        this.doc = doc;
        this.nameMap = nameMap;
        this.childMap = new HashMap();
        this.root = createRootAndCategoryNodes();
    }
    
    /**
     * Replaces the old document with a new one. Event listener are removed
     * from the old document and added to the new one.
     * @param doc
     */
    public void setDocument(Document doc) {
        if (this.doc == doc) {
            return;
        }
        if (this.doc != null) {
            ((EventTarget) this.doc).removeEventListener("DOMNodeRemoved", this, false);
            ((EventTarget) this.doc).removeEventListener("DOMNodeInserted", this, false);
            ((EventTarget) this.doc).removeEventListener("DOMAttrModified", this, false);
        }
        ((EventTarget) doc).addEventListener("DOMNodeRemoved", this, false);
        ((EventTarget) doc).addEventListener("DOMNodeInserted", this, false);
        ((EventTarget) doc).addEventListener("DOMAttrModified", this, false);
        
        this.doc = doc;
        this.childMap.clear();
        this.nameMap = Tools.createNameMap(doc);
        
        this.root = createRootAndCategoryNodes();
        fireTreeStructureChanged(new TreePath(root));
    }
    
    private XMLTreeNode createRootAndCategoryNodes() {
        XMLTreeNode rt = XMLTreeNode.createRootNode(this, doc.getDocumentElement());
        
        // create category nodes for this model
        for (int i = 0, n = CATEGORIES.length; i < n; i++) {
            CATEGORY_NODES[i] = rt.createTypeNode(CATEGORIES[i]);
        }
        // create subcategory nodes for this model
        for (int j = 0, m = SUBCATEGORYGROUPS.length; j < m; j++) {
            int n = SUBCATEGORYGROUPS[j].length;
            SUBCATEGORY_NODES[j] = new XMLTreeNode[n];
            for (int i = 0; i < n; i++) {
                SUBCATEGORY_NODES[j][i] = rt.createTypeNode(SUBCATEGORYGROUPS[j][i]);
            }
        }
        return rt;
    }
    
    /**
     * Gets the document.
     * @return
     */
    public Document getDocument() {
        return doc;
    }
    
    /**
     * Gets the name map.
     * @return
     */
    public Map<String, Element> getNameMap() {
        return nameMap;
    }
    
    /**
     * Gets the element that has the specified name.
     * @param name
     * @return
     */
    public Element getElementByName(String name) {
        return nameMap.get(name);
    }
    
    /**
     * Gets a cached xml tree node list if one exists for this parent,
     * otherwise creates a new one and returns it.
     * @param parent
     * @return
     */
    private XMLTreeNodeList getXMLTreeNodeList(Object parent) {
        XMLTreeNodeList c = childMap.get(parent);
        if (c == null) {
            c = new XMLTreeNodeList(((XMLTreeNode) parent));
            childMap.put((XMLTreeNode) parent, c);
        }
        return c;
    }
    
    //------------------------------------------------------------//
    
    /**
     * Gets the child of parent at index index in the parent's
     * child array.
     * @param parent
     * @param index
     * @return
     */
    @Override
    public Object getChild(Object parent, int index) {
        XMLTreeNodeList c = getXMLTreeNodeList(parent);
        return c.get(index);
    }
    
    /**
     * Gets the number of children of parent.
     * @param parent
     * @return
     */
    @Override
    public int getChildCount(Object parent) {
        XMLTreeNodeList c = getXMLTreeNodeList(parent);
        return c.size();
    }
    
    /**
     * Gets the index of child in parent.
     * @param parent
     * @param child
     * @return
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        XMLTreeNodeList c = getXMLTreeNodeList(parent);
        return c.indexOf(child);
    }
    
    /**
     * Gets the root of the tree.
     */
    @Override
    public Object getRoot() {
        return root;
    }
    
    /**
     * Returns true if node is a leaf.
     * @param node
     * @return
     */
    @Override
    public boolean isLeaf(Object node) {
        XMLTreeNodeList c = getXMLTreeNodeList(node);
        return c.isEmpty();
    }
    
    /**
     * Messaged when the user has altered the value for the item
     * identified by path to newValue.
     * @param path
     * @param newValue
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        
    }
    
    //------------------------------------------------------------//
    
    private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
    
    /**
     * Adds a listener for the TreeModelEvent posted after the tree
     * changes.
     * @param l
     */
    @Override
    public void addTreeModelListener(TreeModelListener l) {
        if (!listeners.contains(l)) {
            //System.out.println("ADDING TREE MODEL LISTENER: " + l);
            listeners.add(l);
        }
    }
    
    /**
     * Removes a listener previously added with addTreeModelListener.
     * @param l
     */
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // System.out.println("REMOVING TREE MODEL LISTENER: " + l);
        listeners.remove(l);
    }
    
    //------------------------------------------------------------//
    
    /**
     * Invoked after a node (or a set of siblings) has changed in some
     * way.
     * @param path
     * @param childIndices
     * @param children
     */
    private void fireTreeNodesChanged(TreePath path,
            int[] childIndices,
            Object[] children) {
        
        TreeModelEvent e = new TreeModelEvent(this, path, childIndices,
                children);
        //System.out.println(" ---------- changed ---------- " + e);
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(e);
        }
    }
    
    /**
     * Invoked after nodes have been inserted into the tree.
     * @param path
     * @param childIndices
     * @param children
     */
    private void fireTreeNodesInserted(TreePath path,
            int[] childIndices,
            Object[] children) {
        
        TreeModelEvent e = new TreeModelEvent(this, path, childIndices,
                children);
        //System.out.println(" ---------- inserted ---------- " + e);
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(e);
        }
    }
    
    /**
     * Invoked after nodes have been removed from the tree.
     * @param path
     * @param childIndices
     * @param children
     */
    private void fireTreeNodesRemoved(TreePath path,
            int[] childIndices,
            Object[] children) {
        
        TreeModelEvent e = new TreeModelEvent(this, path, childIndices,
                children);
        //System.out.println(" ---------- removed ---------- " + e);
        
        // the listeners vector might change as a result of calling 
        // treeNodesRemoved
        Vector<TreeModelListener> temp = new Vector<TreeModelListener>(listeners);
        for (TreeModelListener l : temp) {
            l.treeNodesRemoved(e);
        }
    }
    
    /**
     * Invoked after the tree has drastically changed structure from a
     * given node down.
     * @param path
     */
    private void fireTreeStructureChanged(TreePath path) {
        TreeModelEvent e = new TreeModelEvent(this, path);
        //System.out.println(" ---------- structure ---------- " + e);
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(e);
        }
    }
    
    //------------------------------------------------------------//
    
    /*
    public Map<XMLTreeNode, XMLTreeNodeList> getMapCopy() {
        // we really need a deep copy!
        Map<XMLTreeNode, XMLTreeNodeList> newMap = new HashMap<XMLTreeNode, XMLTreeNodeList>();
        for (XMLTreeNode key : childMap.keySet()) {
            XMLTreeNodeList list = childMap.get(key);
            newMap.put(key, new XMLTreeNodeList(list));
        }
        return newMap;
    }
     */
    static public void showList(String msg, List l) {
        System.out.print(msg + ":");
        for (int i = 0, n = l.size(); i < n; i++) {
            System.out.print(" " + l.get(i));
        }
        System.out.println();
    }
    
    static public void compLists(List l1, List l2) {
        for (int i = 0, n = l1.size(); i < n; i++) {
            if (l1.get(i) == l2.get(i)) {
                System.out.print(" " + l1.get(i));
            } else {
                System.out.print(" ####" + l1.get(i) + "#### ");
            }
        }
        System.out.println();
    }
    
    public void fixModel() {
        Tools.updateNameMap(doc, nameMap);
        XMLTreeModel newModel = new XMLTreeModel(doc, nameMap);
        fixModel(newModel, new TreePath(root));
    }
    
    public void fixModel(XMLTreeModel newModel, TreePath parentPath) {
        XMLTreeNode parent = (XMLTreeNode) parentPath.getLastPathComponent();
        
        List<XMLTreeNode> oldChildren = getXMLTreeNodeList(parent);
        List<XMLTreeNode> newChildren = newModel.getXMLTreeNodeList(parent);
        
        /*
        System.out.println("FIXING: " + parent);
        showList("old: ", oldChildren);
        showList("new: ", newChildren);
         */
        
        List<XMLTreeNode> toBeRemoved = new ArrayList<XMLTreeNode>();
        toBeRemoved.addAll(oldChildren);
        toBeRemoved.removeAll(newChildren);
        
        List<XMLTreeNode> toBeInserted = new ArrayList<XMLTreeNode>();
        toBeInserted.addAll(newChildren);
        toBeInserted.removeAll(oldChildren);
        
        List<XMLTreeNode> toBeRetained = new ArrayList<XMLTreeNode>();
        toBeRetained.addAll(newChildren);
        toBeRetained.retainAll(oldChildren);
        //toBeRetained.addAll(oldChildren);
        //toBeRetained.retainAll(newChildren);
        
        // find out what nodes must be removed and then inserted
        // to get all nodes in the proper order
        int size = toBeRetained.size();
        BitSet bits = new BitSet(size * size);
        for (int i = size - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (!haveSameOrder(toBeRetained.get(i), toBeRetained.get(j),
                        oldChildren, newChildren)) {
                    
                    // the relation is symmetric
                    bits.set(i + size * j);
                    bits.set(size * i + j);
                }
            }
        }
        
        do {
            int maxVal = 0;
            int maxIndex = 0;
            for (int i = 0; i < size; i++) {
                int start = size * i;
                int end = start + size;
                BitSet row = bits.get(start, end);
                int val = row.cardinality();
                if (val > maxVal) {
                    maxVal = val;
                    maxIndex = i;
                }
            }
            if (maxVal > 0) {
                // clear bit row
                int start = size * maxIndex;
                int end = start + size;
                bits.clear(start, end);
                // clear bit col
                for (int i = 0; i < size; i++) {
                    bits.clear(maxIndex + size * i);
                }
                XMLTreeNode wrongOrder = toBeRetained.remove(maxIndex);
                // System.out.println("REORDERING: " + wrongOrder);
                toBeRemoved.add(wrongOrder);
                toBeInserted.add(wrongOrder);
            }
        }
        while (bits.cardinality() > 0);
        
        /*
        // they may look the same (for the tree model), but they are not...
        if (toBeRetained.size() > 0) {
            for (int i = 0, n = toBeRetained.size(); i < n; i++) {
                XMLTreeNode object = toBeRetained.get(i);
                oldChildren.set(oldChildren.indexOf(object), object);
            }
        }
         */
        
        // remove nodes
        if (toBeRemoved.size() > 0) {
            int[] removedIndices = new int[toBeRemoved.size()];
            for (int i = 0, n = removedIndices.length; i < n; i++) {
                removedIndices[i] = oldChildren.indexOf(toBeRemoved.get(i));
            }
            oldChildren.removeAll(toBeRemoved);
            fireTreeNodesRemoved(parentPath, removedIndices, toBeRemoved.toArray());
        }
        
        // insert nodes
        if (toBeInserted.size() > 0) {
            
            int[] insertedIndices = new int[toBeInserted.size()];
            Map<Integer, XMLTreeNode> insertMap = new HashMap<Integer, XMLTreeNode>();
            for (int i = 0, n = insertedIndices.length; i < n; i++) {
                XMLTreeNode object = toBeInserted.get(i);
                int index = newChildren.indexOf(object);
                insertedIndices[i] = index;
                insertMap.put(index, object);
            }
            
            // indices must be sorted for this operation!
            Arrays.sort(insertedIndices);
            for (int i = 0, n = insertedIndices.length; i < n; i++) {
                // order counts here -> cannot say just addAll(...)
                int index = insertedIndices[i];
                oldChildren.add(index, insertMap.get(index));
            }
            fireTreeNodesInserted(parentPath, insertedIndices, toBeInserted.toArray());
        }
        
        /*
        System.out.print("LIST ARE EQUAL: " +
                oldChildren.equals(newChildren));
        compLists(oldChildren, newChildren);
         */
        
        // continue with children
        for (int i = 0, n = oldChildren.size(); i < n; i++) {
            Object child = oldChildren.get(i);
            fixModel(newModel, parentPath.pathByAddingChild(child));
        }
    }
    
    /**
     * Returns true if the elements a and b are in the same order
     * in the lists p and q.
     */
    private static boolean haveSameOrder(Object a, Object b,
            List<?> p, List<?> q) {
        int v1 = p.indexOf(a) - p.indexOf(b);
        int v2 = q.indexOf(a) - q.indexOf(b);
        return (v1 < 0 && v2 < 0) || (v1 > 0 && v2 > 0);
    }
    
    //------------------------------------------------------------//
    
    /**
     * This code does not execute in the GUI thread!!!
     */
    @Override
    public void handleEvent(Event evt) {
        final MutationEvent mev = (MutationEvent) evt;
        final Element target = (Element) mev.getTarget();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String type = mev.getType();
                if (type.equals("DOMNodeRemoved")) {
                    //System.out.println("REMOVING: " + target.getAttribute(NAME));
                    changeAttribute(target, NAME, "", target.getAttribute(NAME));
                } else if (type.equals("DOMNodeInserted")) {
                    //System.out.println("INSERTING: " + target.getAttribute(NAME));
                    changeAttribute(target, NAME, target.getAttribute(NAME), "");
                }
                if (type.equals("DOMAttrModified")) {
                    String name = mev.getAttrName();
                    String newValue = mev.getNewValue();
                    String oldValue = mev.getPrevValue(); // can be null
                    changeAttribute(target, name, newValue, oldValue);
                    if (!name.equals(NAME)) {
                        return;
                    }
                }
                
                fixModel();
            }
        });
    }
    
    /**
     * Walks through the tree and changes the attributes of every instance of
     * the element.
     */
    private void changeAttribute(Element elem, String name, String newValue, String oldValue) {
        changeAttribute(elem, name, newValue, oldValue, new TreePath(root));
    }
    private void changeAttribute(Element elem, String name, String newValue, String oldValue, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        // XMLTreeNodeList c = childMap.get(node);
        XMLTreeNodeList c = this.getXMLTreeNodeList(node);
        if (c == null) {
            return;
        }
        for (int i = c.size() - 1; i >= 0; i--) {
            XMLTreeNode nd = c.get(i);
            Element actual = nd.actual();
            Element link = nd.link();
            // it makes no difference whether the node is link or actual object
            if (link == elem || actual == elem) {
                // XMLTreeNodes are immutable, so to change links we have
                // to remove the node and create a new one (it is done
                // automatically in fixModel())
                if (name.equals(NAME)) {
                    c.remove(nd);
                    fireTreeNodesRemoved(path, new int[] {i}, new Object[] {nd});
                } else {
                    // this node has somehow changed (i.e. name)
                    fireTreeNodesChanged(path, new int[] {i}, new Object[] {nd});
                }
            }
            // name attribute needs special attention
            else if (name.equals(NAME)) {
                // a working link that needs an update
                if (link != null) {
                    // someone has removed name attribute
                    if (newValue == null) {
                        // nothing to do here
                    }
                    // this link (possibly) now points to this object (whose name has changed)
                    else if (!newValue.isEmpty() && link.getAttribute(NAME).equals(newValue)) {
                        c.remove(nd);
                        fireTreeNodesRemoved(path, new int[] {i}, new Object[] {nd});
                    }
                    // this link now (possibly) no longer points to this object (whose name has changed)
                    else if (oldValue != null && !oldValue.isEmpty() && link.getAttribute(NAME).equals(oldValue)) {
                        c.remove(nd);
                        fireTreeNodesRemoved(path, new int[] {i}, new Object[] {nd});
                    }
                }
                // a broken link that needs fixing
                else if (actual != null && nd.isType(INCLUDE_OBJECT) &&
                        !newValue.isEmpty() && actual.getAttribute(NAME).equals(newValue)) {
                    c.remove(nd);
                    fireTreeNodesRemoved(path, new int[] {i}, new Object[] {nd});
                }
            }
        }
        for (XMLTreeNode n : c) {
            changeAttribute(elem, name, newValue, oldValue, path.pathByAddingChild(n));
        }
    }
    
    /*
    public void handleEvent(Event evt) {
        try {
            // FIXME: this is probably done too often, but this operation is
            // relatively cheap anyways...
            Tools.updateNameMap(doc, nameMap);
     
            // NOTE: this code seems to be "brain damaged" i.e. there are
            // separate cases for links and objects, it may have something to
            // do with the fact that attribute name can mean two things:
            // 1) it can be a name of an object or 2) it can be a reference
            // to an object.
            MutationEvent mev = (MutationEvent) evt;
            Element child = (Element) mev.getTarget();
            String type = mev.getType();
            String name = child.getNodeName();
     
            if (type.equals("DOMNodeRemoved")) {
                Element parent = (Element) mev.getRelatedNode();
                if (name.equals(INCLUDE_OBJECT) && nameMap.containsKey(name)) {
                    System.out.println("REMOVE LINK (" + parent + ", " + child + ")");
                    removeLink(parent, child);
                }
                else {
                    System.out.println("REMOVE OBJECT (" + parent + ", " + child + ")");
                    removeLinks(child);
                    removeObject(child);
                }
     
                // this is quite radical action, but removing objects (with
                // names) can break existing links anywhere in the document
                regenerateAll(new TreePath(root));
            }
            else if (type.equals("DOMNodeInserted")) {
                Element parent = (Element) mev.getRelatedNode();
                if (name.equals(INCLUDE_OBJECT) && nameMap.containsKey(name)) {
                    System.out.println("INSERT LINK (" + parent + ", " + child + ")");
                    insertLink(parent);
                }
                else {
                    System.out.println("INSERT OBJECT (" + parent + ", " + child + ")");
                    insertLink(parent);
                }
            }
            else if (type.equals("DOMAttrModified")) {
                System.out.println("CHANGE ATTRIBUTE (" + child + ": " + mev.getAttrName() + " -> " + mev.getNewValue() + ")");
                // if the name attribute of a link object is changed we have to
                // make sure that the link object is not using the old mapping
                // (actual) anymore
                if (mev.getAttrName().equals(NAME)) {
                    System.out.println("### " + getClass().getName() + " name changed from " + mev.getPrevValue() +
                            " to " + mev.getNewValue());
                    //updateName(child);
     
                    // this is quite radical action, but changing names can both
                    // create new links and break existing ones...
                    regenerateAll(new TreePath(root));
                    // regenerateChildNodes(new TreePath(root));
                }
     
                changeAttributes(child);
            }
            else {
                System.out.println("SOMETHING ELSE: " + type);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     */
    
    /**
     * Walks through the tree and removes all (links to) child elements
     * from the specified parent element.
    private void removeLink(Element parent, Element child) {
        removeLink(parent, child, new TreePath(root));
    }
    private void removeLink(Element parent, Element child, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return;
        }
        if (node.actual() == parent) {
            for (int i = c.size() - 1; i >= 0; i--) {
                XMLTreeNode n = c.get(i);
                if (n.link() == child) {
                    c.remove(i);
                    fireTreeNodesRemoved(path, new int[] {i}, new Object[] {n});
                }
            }
        }
        for (XMLTreeNode n : c) {
            removeLink(parent, child, path.pathByAddingChild(n));
        }
    }
    */
            
    /**
     * Regenerates all child nodes of every instance of the parent.
    private void insertLink(Element parent) {
        insertLink(parent, new TreePath(root));
    }
    private void insertLink(Element parent, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return;
        }
        if (node.actual() == parent) {
            // redo child list
            regenerateChildNodes(path);
        }
        for (XMLTreeNode n : c) {
            insertLink(parent, path.pathByAddingChild(n));
        }
    }
    */
            
    /**
     * Updates element name.
    private void updateName(Element element) {
        updateName(element, new TreePath(root));
    }
    private boolean updateName(Element element, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return false;
        }
        if (node.actual() == element || node.link() == element) {
            // redo parents child list
            regenerateChildNodes(path.getParentPath());
            return true;
        }
        boolean loop = true;
        while (loop) {
            loop = false;
            for (XMLTreeNode n : c) {
                if (updateName(element, path.pathByAddingChild(n))) {
                    loop = true;
                    break;
                }
            }
        }
        return false;
    }
    */
            
    /**
     * Walks through the tree and removes all (links to) child elements
     * regardless of the parent element
    private void removeLinks(Element child) {
        removeLinks(child, new TreePath(root));
    }
    private void removeLinks(Element child, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return;
        }
        for (int i = c.size() - 1; i >= 0; i--) {
            XMLTreeNode n = c.get(i);
            if (n.link() == child) {
                c.remove(i);
                fireTreeNodesRemoved(path, new int[] {i}, new Object[] {n});
            }
        }
        for (XMLTreeNode n : c) {
            removeLinks(child, path.pathByAddingChild(n));
        }
    }
    */
    
    /**
     * Walks through the tree and removes the (actual) child element.
     * The child element should be unique, however the whole tree is always
     * checked.
    private void removeObject(Element child) {
        removeObject(child, new TreePath(root));
    }
    private void removeObject(Element child, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return;
        }
        for (int i = c.size() - 1; i >= 0; i--) {
            XMLTreeNode n = c.get(i);
            // do not remove links!
            if (n.link() == null && n.actual() == child) {
                c.remove(i);
                fireTreeNodesRemoved(path, new int[] {i}, new Object[] {n});
            }
        }
        for (XMLTreeNode n : c) {
            removeObject(child, path.pathByAddingChild(n));
        }
    }
    */
            
    /**
     * Walks through the tree and changes the attributes of every instance of
     *  the child element.
    private void changeAttributes(Element child) {
        changeAttributes(child, new TreePath(root));
    }
    private void changeAttributes(Element child, TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // update model lazily
        // XMLTreeNodeList c = childMap.get(node);
        XMLTreeNodeList c = this.getXMLTreeNodeList(node);
        if (c == null) {
            return;
        }
        for (int i = 0, n = c.size(); i < n; i++) {
            XMLTreeNode nd = c.get(i);
            // it makes no difference whether the node is link or actual object
            if (nd.link() == child || nd.actual() == child) {
                // this node has somehow changed (i.e. name)
                fireTreeNodesChanged(path, new int[] {i}, new Object[] {nd});
                // the attribute nodes may also have been changed
                regenerateChildNodes(path.pathByAddingChild(nd));
            }
        }
        for (XMLTreeNode n : c) {
            changeAttributes(child, path.pathByAddingChild(n));
        }
    }
    */
    
    /*   
    private void regenerateAll(TreePath path) {
        regenerateChildNodes(path);
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        XMLTreeNodeList c = childMap.get(node);
        if (c == null) {
            return;
        }
        for (XMLTreeNode n : c) {
            regenerateAll(path.pathByAddingChild(n));
        }
    }
    */
    
    /**
     * This method replaces the XMLTreeNodeList in the childMap for the last
     * path element. It then removes the old nodes from the tree and adds the
     * new nodes. This is better than just firing tree structure changed
     * event because it will collapse that part of the JTree.
    private void regenerateChildNodes(final TreePath path) {
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        
        // be as lazy as possible
        XMLTreeNodeList list = childMap.get(node);
        if (list == null) {
            return;
        }
        
        // some special cases
        if (node.isType(OBJECTPOOL) || node.isType(CATEGORIES)) {
            for (int i = 0, n = getChildCount(node); i < n; i++) {
                regenerateChildNodes(path.pathByAddingChild(getChild(node, i)));
            }
            return;
        }
        
        // remove old nodes from the tree
        int[] indices = new int[list.size()];
        for (int i = 0, n = indices.length; i < n; i++) {
            indices[i] = i;
        }
        fireTreeNodesRemoved(path, indices, list.toArray());
        
        // insert new nodes to the tree
        indices = new int[list.size()];
        for (int i = 0, n = indices.length; i < n; i++) {
            indices[i] = i;
        }
        fireTreeNodesInserted(path, indices, list.toArray());
    }
    */
        /*
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        XMLTreeNodeList newlist = new XMLTreeNodeList(node);
        XMLTreeNodeList oldlist = childMap.put(node, newlist);
         
        // remove old nodes from the tree
        if (oldlist != null) {
            int[] oldindices = new int[oldlist.size()];
            for (int i = 0, n = oldlist.size(); i < n; i++) {
                oldindices[i] = i;
            }
            fireTreeNodesRemoved(path.getPath(), oldindices, oldlist.toArray());
        }
         
        // insert new nodes to the tree
        if (newlist != null) {
            int[] newindices = new int[newlist.size()];
            for (int i = 0, n = newlist.size(); i < n; i++) {
                newindices[i] = i;
            }
            fireTreeNodesInserted(path.getPath(), newindices, newlist.toArray());
        }
         */
    
    
    /*
    static public int[] removeAll(List where, List what) {
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0, n = what.size(); i < n; i++) {
            int index = where.indexOf(what.get(i));
            if (index >= 0) {
                indices.add(index);
            }
        }
        int[] rv = new int[indices.size()];
        for (int i = 0, n = indices.size(); i < n; i++) {
            rv[i] = indices.get(i);
        }
        return rv;
    }
     
    static public void showList(String name, List list) {
        System.out.print(name + ":");
        for (Object o : list) {
            System.out.print(" " + o);
        }
        System.out.println();
    }
     
    static public int[] getAllIndices(List list) {
        int[] indices = new int[list.size()];
        for (int i = 0, n = indices.length; i < n; i++) {
            indices[i] = i;
        }
        return indices;
    }
     */
    
    /**
     * Tries to convert string path into TreePath of XMLTreeNodes. 
     */
    public TreePath findPathByPath(String pathStr) {
        return findPathByPath(pathStr, new TreePath(root));
    }
    /**
     * This code is certainly not nice. The extra complexity is caused by 
     * non-real (category) nodes in the xml node tree. 
     */
    private TreePath findPathByPath(String pathStr, TreePath path) {
        
        // ending condition for the recursion
        if (pathStr == null || pathStr.equals("")) {
            return path;
        }
        
        // paths are of the form "/container/button/label", in which case 
        // beginning should be "container" and ending "/button/label", the 
        // special case is "/label", in which case beginning should be
        // "label" and ending "" or null. 
        int index = pathStr.indexOf('/', 1);
        String beginning = index < 0 ? pathStr.substring(1) : pathStr.substring(1, index);
        String ending = index < 0 ? null : pathStr.substring(index);
        
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        XMLTreeNodeList c = getXMLTreeNodeList(node);
        for (XMLTreeNode n : c) {
            if (n.isType(OBJECTS)) {
                // real object has a name and it must match
                if (n.getName().equals(beginning)) {                    
                    return findPathByPath(ending, path.pathByAddingChild(n));
                }
            }
            else {
                // non-real objects do not have a name (and are not in the path)
                // - we must check them all
                TreePath p = findPathByPath(pathStr, path.pathByAddingChild(n));
                if (p != null) {
                    return p;
                }
            }
        }
        return null;
    }
}
