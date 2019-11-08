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
package multidom;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import org.w3c.dom.Document;

/**
 *
 * @author mohman
 */
public class MultiDOM {
    
    private final List<SingleDOM> documents;
    private SingleDOM activeDocument;
    private boolean newDocument = true;
    
    /** 
     * Constructor.
     */
    public MultiDOM() {
        this.documents = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param documents
     */
    public MultiDOM(List<SingleDOM> documents) {
        this.documents = documents;
    }
    
    /**
     * Gets the active path.
     * @return
     */
    public TreePath getActivePath() {
        return this.activeDocument == null ? null : this.activeDocument.getActivePath();
    }
    
    /**
     * Sets the active path.
     * @param path
     */
    public void setActivePath(TreePath path) {
        //this.activeDocument.setActivePath(path);
        //firePathChange();
        if (this.activeDocument.setActivePath(path) || newDocument) {
            this.newDocument  = false;
            firePathChange();
        }
    }
    
    /** 
     * Gets the active document.
     * @return
     */
    public SingleDOM getActiveDocument() {
        return this.activeDocument;
    }
    
    /**
     * Sets the active document.
     * @param doc
     */
    public void setActiveDocument(SingleDOM doc) {
        if (this.activeDocument == doc) {
            return;
        }
        this.activeDocument = doc;
        this.newDocument = true;
        fireDocChangeListeners();
    }
    
    /**
     * Sets the active document.
     * @param doc
     */
    public void setActiveDocument(Document doc) {
        for (SingleDOM d : documents) {
            if (d.actual().equals(doc)) {
                setActiveDocument(d);
                return;
            }
        }
        throw new RuntimeException();
    }
    
    /**
     * Gets all documents.
     * @return
     */
    public List<SingleDOM> getAllDocuments() {
        return this.documents;
    }
    
    /**
     * Creates a new document.
     * @return
     */
    public SingleDOM newDocument() {
        SingleDOM doc = new SingleDOM(this);
        documents.add(doc);
        return doc;
    }
    
    /**
     * Parses the active document.
     * @param text
     * @throws java.lang.Exception
     */
    public void parseActiveDocument(String text) throws Exception {
        activeDocument.parseDocument(text);
        fireDocChangeListeners();
    }
    
    /**
     * Loads a document.
     * @param name
     * @return
     * @throws java.lang.Exception
     */
    public SingleDOM loadDocument(String name) throws Exception {
        SingleDOM doc = newDocument();
        doc.setName(name);
        doc.loadDocument(); // can throw Exception!
        return doc;
    }
    
    /**
     * Saves the specified document.
     * @param doc
     * @throws java.lang.Exception
     */
    public void saveDocument(SingleDOM doc) throws Exception {
        check(doc);
        doc.saveDocument(); // can throw Exception!
    }
    
    /**
     * Saves the specified document with the given name.
     * @param doc
     * @param name
     * @throws java.lang.Exception
     */
    public void saveAsDocument(SingleDOM doc, String name) throws Exception {
        check(doc);
        doc.setName(name);
        doc.saveDocument(); // can throw Exception!
    }    
    
    /**
     * Saves all documents.
     * @throws java.lang.Exception
     */
    public void saveAllDocuments() throws Exception {
        String failure = "";
        for (SingleDOM d : documents) {
            try {
                saveDocument(d);
            }
            catch (Exception e) {
                failure += e.getMessage() + "\n";
            }
        }
        if (!failure.isEmpty()) {
            throw new Exception(failure);
        }
    }
    
    /**
     * Checks a document???
     * @param doc
     */
    private void check(SingleDOM doc) {
        if (!documents.contains(doc)) {
            throw new IllegalArgumentException("illegal document");
        }
    }
    
    private final Vector<ChangeListener> docChangeListeners = new Vector<>();
    
    /**
     * Adds a listener.
     * @param l
     */
    public void addDocumentChangeListener(ChangeListener l) {
        if (!docChangeListeners.contains(l)) {
            docChangeListeners.add(l);
        }
    }
    
    /**
     * Removes a listener.
     * @param l
     */
    public void removeDocumentChangeListener(ChangeListener l) {
        docChangeListeners.remove(l);
    }
    
    /**
     * Fires a state changed event.
     */
    private void fireDocChangeListeners() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : docChangeListeners) {
            l.stateChanged(e);
        }
    }
    
    private final Vector<ChangeListener> pathChangeListeners = new Vector<>();
    
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
     */
    public void removePathChangeListener(ChangeListener l) {
        pathChangeListeners.remove(l);
    }
    
    /**
     * Fires a state changed event.
     */
    public void firePathChange() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : pathChangeListeners) {
            l.stateChanged(e);
        }
    }
}
