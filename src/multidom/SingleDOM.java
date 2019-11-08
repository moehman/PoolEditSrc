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
package multidom;

import java.io.File;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import org.w3c.dom.Document;
import pooledit.Tools;
import pooledit.Utils;
import treemodel.XMLTreeModel;


/**
 *
 * @author mohman
 */
public class SingleDOM {
    
    private  JFileChooser fc = new JFileChooser();
    private final XMLTreeModel treeModel = new XMLTreeModel();
        
    private final MultiDOM multidom;

    private Document doc;
    private TreePath activePath;    
    
    // Absolute paths are platform dependent!
    // private static String SCHEMA = "C:\\pooledit\\schema\\iso11783.xsd";
    
    // Relative paths are relative to the actual xml document?!?
    // public static final String SCHEMA = "schema" + File.separator + "iso11783.xsd";
    
    // Use schema stored in the jar file?
    public static String SCHEMA = SingleDOM.class.getResource("/schema/iso11783.xsd").toString();
    
    /** 
     * Creates a new instance of SingleDOM 
     * @param multidom
     */
    public SingleDOM(MultiDOM multidom) {
        this.multidom = multidom;
        
        // add FileNameExtensionFilter
        fc.setFileFilter(new FileNameExtensionFilter("XML-file", "xml") );
    }
    
    /**
     * Gets the dom document.
     * @return
     */
    public Document actual() {
        return this.doc;
    }
    
    /**
     * Gets the tree model.
     * @return
     */
    public XMLTreeModel getTreeModel() {
        return treeModel;
    }
    
    /**
     * Gets the file chooser.
     * @return
     */
    public JFileChooser getJFileChooser(){
        return fc;
    }
    
    /**
     * Sets the file chooser.
     * @param fc
     */
    public void setJFileChooser(JFileChooser fc) {
        this.fc = fc;
    }
    
    /**
     * Gets the active path.
     * @return
     */
    public TreePath getActivePath() {
        return this.activePath;
    }
    
    /** 
     * Sets the active path.
     * @param activePath
     * @return
     */
    public boolean setActivePath(TreePath activePath) {
        if (Utils.equalObjects(this.activePath, activePath)) {
            return false;
        }
        this.activePath = activePath;
        firePathChange();
        return true;
    }
    
    /**
     * Sets the name of this document.
     * @param name
     */
    public void setName(String name) {
       fc.setSelectedFile(new File(name));
       fireNameChange();
    }
    
    /**
     * Gets the name of this document.
     * @return
     */
    public String getName() {
        File file = fc.getSelectedFile();
        return file == null ? "New Document" : file.toURI().getPath();        
    }
       
    /**
     * Parses this document.
     * @param text
     * @throws java.lang.Exception
     */
    public void parseDocument(String text) throws Exception {
        doc = Tools.parseDocument(text, SCHEMA);
        treeModel.setDocument(doc);
    }
    
    /**
     * Loads a document.
     * @throws java.lang.Exception
     */
    public void loadDocument() throws Exception {
        doc = Tools.loadDocument(getName(), SCHEMA);
        
        // Map<String,Element> map = Tools.createNameMap(doc);
	// Tools.createMissingNames(doc, map);
	// Tools.removeNesting(doc);
        Tools.createRoles(doc.getDocumentElement());
        Tools.checkNaming(doc);
        treeModel.setDocument(doc);       
    }
    
    /**
     * Saves this document.
     * @throws java.lang.Exception
     */
    public void saveDocument() throws Exception {
        Tools.saveDocument(getName(), doc);
    }
        
    private final Vector<ChangeListener> pathChangeListeners = 
            new Vector<ChangeListener>();
    
    /**
     * Adds a listener.
     * @param l
     */
    public void addPathChangeListener(ChangeListener l) {
        if (!pathChangeListeners.contains(l)) {
            pathChangeListeners.add(l);
        }
    }
    
    /**
     * Removes a listener.
     * @param l
     * @return
     */
    public boolean removePathChangeListener(ChangeListener l) {
        return pathChangeListeners.remove(l);
    }
    
    /**
     * Fires a state changed event.
     */
    private void firePathChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : pathChangeListeners) {
            l.stateChanged(e);
        }
    }
    
    private final Vector<ChangeListener> nameChangeListeners =
            new Vector<ChangeListener>();
    
    /**
     * Adds a listener.
     * @param l
     */
    public void addNameChangeListener(ChangeListener l) {
        if (!nameChangeListeners.contains(l)) {
            nameChangeListeners.add(l);
        }
    }
    
    /**
     * Removes a listener.
     * @param l
     * @return
     */
    public boolean removeNameChangeListener(ChangeListener l) {
        return nameChangeListeners.remove(l);
    }
    
    /**
     * Fires a state changed event.
     */
    private void fireNameChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : nameChangeListeners) {
            l.stateChanged(e);
        }
    }
}
