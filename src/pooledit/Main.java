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
package pooledit;

import static pooledit.Definitions.*;
import java.io.OutputStream;
import java.io.PrintStream;
import color.ColorPalette;
import javax.swing.text.PlainDocument;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import attributetable.AttributeTable;
import attributetable.AttributeTableModel;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.TreePath;
import multidom.MultiDOM;
import multidom.SingleDOM;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.mouse.DockingWindowActionMouseButtonListener;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.ClassicDockingTheme;
import net.infonode.docking.theme.DefaultDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.GradientDockingTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.theme.SlimFlatDockingTheme;
import net.infonode.docking.theme.SoftBlueIceDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.ViewMap;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.util.Direction;
import objecttree.ObjectTree;
import objectview.ObjectView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import poolimporter.PoolImportDialog;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;
import wizard.LineTrendGenerator;
import wizard.LineTrendWizard;
import wizard.MeterGenerator;
import wizard.MeterWizard;
import wizard.TableGenerator;
import wizard.TableWizard;
import wizard.TrendGenerator;
import wizard.TrendWizard;

/**
 *
 * @author mohman
 */
public class Main {
       
    private final MultiDOM multidom = new MultiDOM();
    
    private final AttributeTableModel tablemodel = new AttributeTableModel();
    private final DefaultStyledDocument plaindoc = new DefaultStyledDocument();
    private final JTextArea docArea = new JTextArea(plaindoc);
    private final JScrollPane docScroll = new JScrollPane(docArea);
        
    private final PlainDocument msgDoc = new PlainDocument();
    private final JTextArea msgArea = new JTextArea(msgDoc);
    private final JScrollPane msgScroll = new JScrollPane(msgArea);
    private final PrintStream msgOutput = new PrintStream(new OutputStream() {
        @Override
        public void write(byte[] b) {
            msgArea.append(new String(b));
        }
        @Override
        public void write(byte[] b, int off, int len) {
            msgArea.append(new String(b, off, len));
        }
        @Override
        public void write(int b) {
            msgArea.append(new String(new byte[] {(byte) b}));
        }
    });
    
    private final ObjectView objectview = ObjectView.getInstance();
    private final AttributeTable attributetable = AttributeTable.getInstance(tablemodel);
    
    private final View ovView = new View("Object View", Icons.VIEW_ICON, new JScrollPane(objectview));
    private final View atView = new View("Attribute Table", Icons.VIEW_ICON, new JScrollPane(attributetable));
    private final View xmlView = new View("XML Code", Icons.VIEW_ICON, docScroll);
    private final View msgView = new View("Messages", Icons.VIEW_ICON, msgScroll);
    private SingleDOM libdoc;
    
    /** An array of the static views */
    private final View[] views = new View[] {ovView, atView, xmlView, msgView};
    
    /** Contains all the static views */
    private final ViewMap viewMap = new ViewMap(views);
    
    // The mixed view map makes it easy to mix static and dynamic
    // views inside the same root window
    private final MixedViewHandler handler = new MixedViewHandler(viewMap, new ViewSerializer() {
        @Override
        public void writeView(View view, ObjectOutputStream out) throws IOException {
            out.writeInt(((DynamicView) view).getId());
        }
        
        @Override
        public View readView(ObjectInputStream in) throws IOException {
            return getDynamicView(in.readInt());
        }
    });
    /** Import dialog */
    private JFrame importFrame;
    
    /** The one and only root window */
    private final RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, handler, true);
    
    /** The view menu items */
    private final JMenuItem[] viewItems = new JMenuItem[views.length];
    
    /** Contains the dynamic views that has been added to the root window */
    private final HashMap<Integer,View> dynamicViews = new HashMap<>();
    
    /** The currently applied docking windows theme */
    private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();
        
    /**
     * In this properties object the modified property values for close
     * buttons etc. are stored. This object is cleared when the theme is
     * changed.
     */
    private final RootWindowProperties properties = new RootWindowProperties();
    
    /** Where the layouts are stored */
    private final byte[][] layouts = new byte[3][];
    
    /** The application frame */
    private final JFrame frame = new JFrame("PoolEdit");
    
    /** Filenames */
    private static final String TESTPOOL = "test.xml";
    private static final String LIBRARY = "library.xml";
    private static final String TEMPLATE = "template.xml";
    
    /**
     * Constructor 
     */
    public Main() {
        createRootWindow();
        setDefaultLayout();
        showFrame();
        objectview.setFlashing(true);
        
        // redirecting outputid
        System.setOut(msgOutput);
        System.setErr(msgOutput);
    }    
        
    /**
     * Creates a view component containing the specified text.
     * @param text the text
     * @return the view component
     */
    private static JComponent createViewComponent(String text) {
        return new JScrollPane(new JTextArea());
    }
    
    /**
     * Returns a dynamic view with specified id, reusing an existing
     * view if possible.
     * @param id the dynamic view id
     * @return the dynamic view
     */
    private View getDynamicView(int id) {
        View view = dynamicViews.get(Integer.valueOf(id));
        if (view == null) {
            view = new DynamicView("Dynamic View " + id, Icons.VIEW_ICON, createViewComponent(""), id);
        }        
        return view;
    }
    
    /**
     * Returns the next available dynamic view id.
     * @return the next available dynamic view id
     */
    private int getDynamicViewId() {
        int id = 0;
        while (dynamicViews.containsKey(id)) {
            id++;
        }
        return id;
    }
    
    /**
     * Creates a new object tree view.
     * @param sdom
     * @return
     */
    private DynamicView createNewObjectTreeView(final SingleDOM sdom) {        
        final XMLTreeModel model = sdom.getTreeModel(); 
        final ObjectTree objecttree = ObjectTree.getInstance(model);
        final DynamicView view = new DynamicView(sdom.getJFileChooser().getSelectedFile().getName(), 
                Icons.VIEW_ICON, new JScrollPane(objecttree), getDynamicViewId());
        
        // a listener is register to the newly created object tree that will 
        // notify the multidom whenever a new path is selected
        objecttree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                multidom.setActiveDocument(model.getDocument());
                multidom.setActivePath(e.getPath());
            }
        });
        
        // a listener is registered to the newly created single dom that will
        // notify the object tree whenever the path is changed (in that sdom)
        sdom.addPathChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                objecttree.setActivePath(multidom.getActivePath());
            }
        });
           
        sdom.addNameChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                view.getViewProperties().setTitle(sdom.getJFileChooser().getSelectedFile().getName());
            }
        });
        
        objecttree.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                multidom.setActiveDocument(model.getDocument());
                multidom.firePathChange();
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        
        return view;
    }
    
    /**
     * Creates the root window and the views.
     */
    private void createRootWindow() {
        // this is called when active document is changed
        multidom.addDocumentChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                SingleDOM doc = multidom.getActiveDocument();
                objectview.setModel(doc.getTreeModel());
                tablemodel.setDocument(doc.actual());
                int len = plaindoc.getLength();                
                try {                                                        
                    String text = Tools.writeToString(doc.actual());
                    plaindoc.replace(0, len, text, null);                     
                    /*
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setForeground(attrs, Color.RED);
                    StyleConstants.setFontFamily(attrs, "Serif"); 
                    plaindoc.setCharacterAttributes(0,40, attrs, false); */                 
                   
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        // this is called when active path is changed
        multidom.addPathChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TreePath path = multidom.getActivePath();                
                objectview.setActivePath(path);
                tablemodel.setActivePath(path);
                if (path == null) {
                    return;
                }
                try {                              
                    String text = plaindoc.getText(0, plaindoc.getLength());
                    Element element = ((XMLTreeNode)path.getLastPathComponent()).actual();
                                       
                    // find the place of the element in the text
                    if (element != null) {
                        int selectionStart = Tools.findElementFromString(text, element.getNodeName(), element.getAttribute(NAME));                  
                        if (selectionStart != -1) {                            
                            int selectionEnd = Tools.findElementEnd(text, element.getNodeName(), selectionStart);                                              
                            //Calculate the location of the viewport and set the caret position
                            int line = docArea.getLineOfOffset(selectionStart);
                            int lines = docArea.getLineCount();
                            int totalHeight = docArea.getHeight();
                            int y = (line * totalHeight) / lines;                    
                            docScroll.getViewport().setViewPosition(new Point(0, y));
                            //docArea.setCaretPosition(selectionStart);
                            //docArea.moveCaretPosition(selectionEnd);
                            docArea.select(selectionStart, selectionEnd);
                        }
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }                 
            }
        });
                
        // this listener is called when user selects object from the object 
        // view
        objectview.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                multidom.setActivePath(e.getPath());
            }
        });
        
        // this listener is called when the user selects reference links in 
        // the attribute table
        tablemodel.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                multidom.setActivePath(e.getPath());
            }
        });
        
        docArea.setDragEnabled(true);
        
        List<JButton> ovList = (List<JButton>) ovView.getCustomTabComponents();
        ovList.add(createButton(Icons.ZOOM_PLUS_ICON, "Zoom in", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double z = objectview.getZoom() + 0.5;
                if (z > 10) { z = 10; }
                objectview.setZoom(z);
            }
        }));
        
        ovList.add(createButton(Icons.ZOOM_MINUS_ICON, "Zoom out", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double z = objectview.getZoom() - 0.5;
                if (z < 0.5) { z = 0.5; }
                objectview.setZoom(z);
            }
        }));
        
        ovList.add(createButton(Icons.DRAW_BORDERS_ICON, "Show borders", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setDrawBorders(!objectview.getDrawBorders());
            }
        }));
        
        ovList.add(createButton(Icons.DRAW_GRID_ICON, "Show grid", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setDrawGrid(!objectview.getDrawGrid());
            }
        }));
        
        ovList.add(createButton(Icons.IMAGE_ZOOM_ICON, "Image zoom", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setImageZoom(!objectview.getImageZoom());
            }
        }));
        
        List<JButton> xmlList = (List<JButton>) xmlView.getCustomTabComponents();
        xmlList.add(createButton(Icons.XML_PARSE_ICON, "Parse XML", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = plaindoc.getText(0, plaindoc.getLength());
                    multidom.parseActiveDocument(text);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }                
            }
        }));
        
        xmlList.add(createButton(Icons.XML_GENERATE_ICON, "Generate XML", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SingleDOM sdom = multidom.getActiveDocument();
                if (sdom == null) {
                    return;
                }
                Document doc = sdom.actual();
                int len = plaindoc.getLength();
                try {
                    plaindoc.replace(0, len, Tools.writeToString(doc), null);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }));
        
        List<JButton> msgList = (List<JButton>) msgView.getCustomTabComponents();
        msgList.add(createButton(Icons.CLEAR_ICON, "Clear messages", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    msgDoc.remove(0, msgDoc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }));
        
        // Set gradient theme. The theme properties object is the super
        // object of our properties object, which means our property value
        // settings will override the theme values
        properties.addSuperObject(currentTheme.getRootWindowProperties());
        
        // Our properties object is the super object of the root window
        // properties object, so all property values of the theme and in
        // our property object will be used by the root window
        rootWindow.getRootWindowProperties().addSuperObject(properties);
        
        // Enable the bottom window bar
        rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
        
        // Add a listener which shows dialogs when a window is closing or
        // closed.
        rootWindow.addListener(new DockingWindowAdapter() {
            @Override
            public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
                updateViews(addedWindow, true);
            }
            
            @Override
            public void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow) {
                updateViews(removedWindow, false);
            }
            
            @Override
            public void windowClosing(DockingWindow window) throws OperationAbortedException {
                if (JOptionPane.showConfirmDialog(frame, "Really close window '" + window + "'?") != JOptionPane.YES_OPTION) {
                    throw new OperationAbortedException("Window close was aborted!");
                }
            }
        });
        
        // Add a mouse button listener that closes a window when it's
        // clicked with the middle mouse button.
        rootWindow.addTabMouseButtonListener(DockingWindowActionMouseButtonListener.MIDDLE_BUTTON_CLOSE_LISTENER);
    }
    
    /**
     * Creates a button with the specified icon and action listener.
     * @param icon
     * @param description
     * @param listener
     * @return
     */
    private static JButton createButton(Icon icon, String description, ActionListener listener) {
        JButton button = new JButton(icon);
        button.setOpaque(false);
        button.setBorder(null);
        button.setFocusable(false);
        button.setToolTipText(description);
        button.addActionListener(listener);
        return button;
    }
    
    /**
     * Update view menu items and dynamic view map.
     * @param window the window in which to search for views
     * @param added  if true the window was added
     */
    private void updateViews(DockingWindow window, boolean added) {
        if (window instanceof View) {
            if (window instanceof DynamicView) {
                if (added) {
                    dynamicViews.put(Integer.valueOf(((DynamicView) window).getId()), (View) window);
                } else {
                    dynamicViews.remove(Integer.valueOf(((DynamicView) window).getId()));
                }
            } else {
                for (int i = 0; i < views.length; i++) {
                    if (views[i] == window && viewItems[i] != null) {
                        viewItems[i].setEnabled(!added);
                    }
                }
            }
        } else {
            for (int i = 0; i < window.getChildWindowCount(); i++) {
                updateViews(window.getChildWindow(i), added);
            }
        }
    }
    
    /**
     * Sets the default window layout.
     */
    private void setDefaultLayout() {
        
        //TabWindow tabWindow = new TabWindow(views);
        try {
            View pool = createNewObjectTreeView(multidom.loadDocument(TESTPOOL)); // FIXME: should not be hard coded!
            libdoc = multidom.loadDocument(LIBRARY);
            View lib = createNewObjectTreeView(libdoc); // FIXME: should not be hard coded!
            rootWindow.setWindow(
                new SplitWindow(true, 0.6f,
                new SplitWindow(false, 0.7f, new TabWindow(ovView),
                    new TabWindow(new View[] {xmlView, msgView})),
                new SplitWindow(false, 0.5f,
                    new SplitWindow(true, 0.5f, new TabWindow(pool),
                        new TabWindow(lib)), new TabWindow(atView))));
        } catch (Exception e) { 
            e.printStackTrace(); 
            rootWindow.setWindow(
                new SplitWindow(true, 0.6f,
                new SplitWindow(false, 0.7f, new TabWindow(ovView),
                    new TabWindow(new View[] {xmlView, msgView})),
                new TabWindow(atView)));
        }
        /*
        WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);
        
        while (windowBar.getChildWindowCount() > 0)
            windowBar.getChildWindow(0).close();
        
        windowBar.addTab(views[3]);
        */
    }
    
    /**
     * Initializes the frame and shows it.
     */
    private void showFrame() {
        frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
        frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
        frame.setJMenuBar(createMenuBar());
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /**
     * Creates the frame tool bar.
     * @return the frame tool bar
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        
        TransferHandler hnd = new TransferHandler("XML");
        MouseListener listener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                JComponent comp = (JComponent)evt.getSource();
                TransferHandler th = comp.getTransferHandler();
                th.exportAsDrag(comp, evt, TransferHandler.COPY);
            }
        };
        for (int i = 0, n = Definitions.OBJECTS.length; i < n; i++) {            
            JLabel lbl = new DragLabel(Definitions.OBJECTS[i], libdoc);
            lbl.setTransferHandler(hnd);
            lbl.addMouseListener(listener);
            Border bdr = BorderFactory.createRaisedBevelBorder();            
            lbl.setBorder(bdr);
            toolBar.add(lbl);
            toolBar.addSeparator(new Dimension(2, 1));
        }
        
        /*
        // This will show a button with a document icon, which will create an
        // empty text window when dragged. The text window is useful for debugging
        // as it supports drag and drop - xml objects can be dragged to it for visual
        // inspection.
        JLabel lbl = new JLabel(ObjectTreeCellRenderer.getIcon(Definitions.OBJECTPOOL));
        Border bdr = BorderFactory.createRaisedBevelBorder();            
        lbl.setBorder(bdr);
        toolBar.add(lbl);
        new DockingWindowDragSource(lbl, new DockingWindowDraggerProvider() {
            public DockingWindowDragger getDragger(MouseEvent mouseEvent) {
                return getDynamicView(getDynamicViewId()).startDrag(rootWindow);
            }
        });
         */
        return toolBar;
    }
    
    /**
     * Creates the frame menu bar.
     * @return the menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menu = new JMenuBar();
        menu.add(createFileMenu());
        menu.add(createWizardMenu());
        //menu.add(createLayoutMenu()); // can lose open documents?
        //menu.add(createFocusViewMenu()); // not needed?
        menu.add(createThemesMenu());
        menu.add(createPropertiesMenu());
        menu.add(createWindowBarsMenu());
        menu.add(createViewMenu());
        menu.add(createColorMenu());
        menu.add(createHelpMenu());
        return menu;
    }
    
    /**
     * Creates the color menu.
     * @return
     */
    private JMenu createColorMenu() {
        JMenu menu = new JMenu("Colors");
        
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("1-Bit Colors");        
        group.add(rbMenuItem);
        menu.add(rbMenuItem).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setColorDepth(ColorPalette.COLOR_1BIT);
                objectview.repaint();
            }
        });
        
        rbMenuItem = new JRadioButtonMenuItem("4-Bit Colors");        
        group.add(rbMenuItem);
        menu.add(rbMenuItem).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setColorDepth(ColorPalette.COLOR_4BIT);
                objectview.repaint();
            }
        });
        
        rbMenuItem = new JRadioButtonMenuItem("8-Bit Colors");
        rbMenuItem.setSelected(true);        
        group.add(rbMenuItem);
        menu.add(rbMenuItem).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                objectview.setColorDepth(ColorPalette.COLOR_8BIT);
                objectview.repaint();
            }
        });   
        
        JCheckBoxMenuItem reducePicColors = new JCheckBoxMenuItem("Reduce Picture Colors");        
        menu.add(reducePicColors).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                objectview.setReduceImages(!objectview.getReduceImages());
                objectview.repaint();
            }
        });
        
        JCheckBoxMenuItem disableFlashing = new JCheckBoxMenuItem("Disable Flashing");        
        menu.add(disableFlashing).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                objectview.setFlashing(!objectview.getFlashing());
                objectview.repaint();
            }
        });
        
        return menu;
    }
    
    /**
     * Creates the wizard menu.
     * @return
     */
    private JMenu createWizardMenu() {
        JMenu menu = new JMenu("Wizard");
        
        menu.add("Meter Wizard").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = getSafeStartNodeForWizard();
                if (node != null) {
                    JFrame wizardFrame = new JFrame("Meter Wizard");
                    MeterWizard wizard = new MeterWizard();
                    String name = Tools.findFreeName("meter_wiz", 
                            Tools.createNameMap(node.getModel().getDocument(), true));
                        // node.getModel().getNameMap());
                    MeterGenerator generator = new MeterGenerator(wizard, node, name);
                    wizardFrame.getContentPane().add(wizard);
                    wizardFrame.pack();
                    wizardFrame.setLocationRelativeTo(frame);
                    wizardFrame.setVisible(true);
                }
            }
        });
        
        menu.add("Table Wizard").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                XMLTreeNode node = getSafeStartNodeForWizard();
                if (node != null) {
                    JFrame wizardFrame = new JFrame("Table Wizard");
                    TableWizard wizard = new TableWizard();
                    String name = Tools.findFreeName("table_wiz", 
                            Tools.createNameMap(node.getModel().getDocument(), true));
                        // node.getModel().getNameMap());
                    TableGenerator generator = new TableGenerator(wizard, node, name);
                    wizardFrame.getContentPane().add(wizard);
                    wizardFrame.pack();
                    wizardFrame.setLocationRelativeTo(frame);
                    wizardFrame.setVisible(true);
                }
            }
        });
        
        menu.add("Trend Wizard").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = getSafeStartNodeForWizard();
                if (node != null) {
                    JFrame wizardFrame = new JFrame("Trend Wizard");
                    TrendWizard wizard = new TrendWizard();
                    String name = Tools.findFreeName("trend_wiz", 
                            Tools.createNameMap(node.getModel().getDocument(), true));
                        // node.getModel().getNameMap());
                    TrendGenerator generator = new TrendGenerator(wizard, node, name);
                    wizardFrame.getContentPane().add(wizard);
                    wizardFrame.pack();
                    wizardFrame.setLocationRelativeTo(frame);
                    wizardFrame.setVisible(true);
                }
            }
        });
        
        menu.add("Line Trend Wizard").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XMLTreeNode node = getSafeStartNodeForWizard();
                if (node != null) {
                    JFrame wizardFrame = new JFrame("Line Trend Wizard");
                    LineTrendWizard wizard = new LineTrendWizard();
                    String name = Tools.findFreeName("line_trend_wiz", 
                            Tools.createNameMap(node.getModel().getDocument(), true));
                        // node.getModel().getNameMap());
                    LineTrendGenerator generator = new LineTrendGenerator(wizard, node, name);
                    wizardFrame.getContentPane().add(wizard);
                    wizardFrame.pack();
                    wizardFrame.setLocationRelativeTo(frame);
                    wizardFrame.setVisible(true);
                }
            }
        });
        return menu;
    }
    
    /**
     * A convenience method for getting a safe starting node for wizards.
     * @return
     */
    private XMLTreeNode getSafeStartNodeForWizard() {
        TreePath path = multidom.getActivePath();
        if (path == null) {
            JOptionPane.showMessageDialog(frame,
                    "No path is currently selected!",
                    "Wizard Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
        if (!node.isType(OBJECTS)) {
            node = (XMLTreeNode) node.getModel().getRoot(); // -> OBJECTPOOL
        }
        
        if (!node.isType(OBJECTPOOL, DATAMASK, ALARMMASK, CONTAINER)) {
            JOptionPane.showMessageDialog(frame,
                    "Path should be pointing to a objectpool, datamask, alarmmask or container object!",
                    "Wizard Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return node;
    }
     
    /**
     * Creates the file menu.
     * @return
     */
    private JMenu createFileMenu() {
        JMenu layoutMenu = new JMenu("File");
        
        layoutMenu.add("New").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fc = FileTools.getNewFileChooser();
                    fc.setSelectedFile(new File(TEMPLATE));
                    SingleDOM singleDom = multidom.loadDocument(fc.getSelectedFile().toURI().getPath());
                    singleDom.setJFileChooser(fc);
                    View view = createNewObjectTreeView(singleDom);
                    multidom.setActiveDocument(singleDom);     //is this necessary?
                    TabWindow twin = new TabWindow(view);
                    rootWindow.setWindow(new SplitWindow(true, 0.8f, rootWindow.getWindow(), twin));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        Action openAction = new AbstractAction("Open...") {
            public void actionPerformed(ActionEvent e) {
                // Creating a new file chooser (not using the current active)
                JFileChooser fc = FileTools.getNewFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("XML-file", "xml"));
                
                String cmd = e.getActionCommand();
                if (cmd.startsWith("PATH:")) {
                    /* special case: we already know the file */
                    fc.setSelectedFile(new File(cmd.substring(5)));
                }
                else {
                    /* normal case: ask for the file */
                    int rv = fc.showOpenDialog(frame);
                    if (rv != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                }
                File file = fc.getSelectedFile();
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(frame,
                    "Document cound not be loaded.",
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    SingleDOM singleDom = multidom.loadDocument(file.toURI().getPath());
                    singleDom.setJFileChooser(fc);
                    
                    // create view and open it
                    View view = createNewObjectTreeView( singleDom );                    
                    final TabWindow twin = new TabWindow(view);
                    rootWindow.setWindow(new SplitWindow(true, 0.8f, rootWindow.getWindow(), twin));
                    
                    // set active
                    multidom.setActiveDocument(singleDom);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        /*
        layoutMenu.add("Open...").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        */
        layoutMenu.add(openAction);
        
        layoutMenu.add("Import...").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* lazy initialization */
                if (importFrame == null) {
                    importFrame = new JFrame("Import dialog");
                    importFrame.getContentPane().add(new PoolImportDialog(openAction));
                }
                importFrame.pack();
                importFrame.setLocationRelativeTo(frame);
                importFrame.setVisible(true);
            }
        }); 
        
        layoutMenu.addSeparator();
        
        layoutMenu.add("Validate").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                SingleDOM doc = multidom.getActiveDocument();
                if (doc == null) {
                    JOptionPane.showMessageDialog(frame, 
                            "No active document selected!", 
                            "Validate Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    if (Tools.validateDocument(msgOutput, doc.actual())) {
                        System.out.println("No errors detected");
                    }
                    else {
                        System.out.println("Pool has errors");
                    }
                } 
                catch (Exception ex) {
                    ex.printStackTrace();
                }                    
            }
        });
        
        layoutMenu.addSeparator();
        
        layoutMenu.add("Save").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SingleDOM doc = multidom.getActiveDocument();
                if (doc == null) {
                    JOptionPane.showMessageDialog(frame, 
                            "No active document selected!", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    multidom.saveDocument(doc);                    
                } 
                catch (Exception ex) {
                    ex.printStackTrace();
                }                    
            }
        });
        
        // FIXME: if user adds no extension, .xml should be added to file
        layoutMenu.add("Save as...").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                SingleDOM doc = multidom.getActiveDocument();
                if (doc == null) {
                    JOptionPane.showMessageDialog(frame, 
                            "No active document to save!", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JFileChooser fc = doc.getJFileChooser();
                int rv = fc.showSaveDialog(frame);
                if (rv != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File file = fc.getSelectedFile();
                if (file.isDirectory()) { // this really should not happen?
                    JOptionPane.showMessageDialog(frame, 
                            "Cannot save document as a directory!", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // if file already exist prompt user
                if (file.exists() && JOptionPane.showConfirmDialog(frame,
                        "Do you want to overwrite?", "Save...",
                        JOptionPane.YES_NO_OPTION) != 0) {
                    return;
                }
                
                try {
                    multidom.saveAsDocument(doc, file.getAbsolutePath());                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        layoutMenu.add("Export to ISOAgLib XML...").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                SingleDOM doc = multidom.getActiveDocument();
                if (doc == null) {
                    JOptionPane.showMessageDialog(frame, 
                            "No active document to export!", 
                            "Export Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String testName = doc.getName().replace(".xml", "_export.xml");
                JFileChooser fc = FileTools.getNewFileChooser();
                fc.setSelectedFile(new File(testName));
                
                // show dialog
                int rv = fc.showSaveDialog(frame);
                if (rv != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File file = fc.getSelectedFile();
                if (file.isDirectory()) { // this really should not happen?
                    JOptionPane.showMessageDialog(frame, 
                            "Cannot save document as a directory!", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // if file already exist prompt user
                if (file.exists() && JOptionPane.showConfirmDialog(frame,
                        "Do you want to overwrite?", "Save...", JOptionPane.YES_NO_OPTION) != 0) {
                    return;
                }
                try {
                    Tools.exportToXML1(file.getAbsolutePath(), doc.actual());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        layoutMenu.add("Export to Embedded XML...").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                SingleDOM doc = multidom.getActiveDocument();
                if (doc == null) {
                    JOptionPane.showMessageDialog(frame, 
                            "No active document to export!", 
                            "Export Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String testName = doc.getName().replace(".xml", "_export.xml");
                JFileChooser fc = FileTools.getNewFileChooser();
                fc.setSelectedFile(new File(testName));
                
                // show dialog
                int rv = fc.showSaveDialog(frame);                
                if (rv != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                
                File file = fc.getSelectedFile();
                if (file.isDirectory()) { // this really should not happen?
                    JOptionPane.showMessageDialog(frame, 
                            "Cannot save document as a directory!", 
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // if file already exist prompt user
                if (file.exists() && JOptionPane.showConfirmDialog(frame,
                        "Do you want to overwrite?", "Save...", JOptionPane.YES_NO_OPTION) != 0) {
                    return;
                }
                try {
                    Tools.exportToXML3(msgOutput, file.getAbsolutePath(), doc.actual());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            ex.getMessage(),
                            "Export Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }            
        });
        
        layoutMenu.addSeparator();
        
        layoutMenu.add("Exit").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(frame, 
                        "Do you really want to exit?",
                        "Exit", JOptionPane.YES_NO_OPTION) == 0) {
                    
                    // NOTE: what is the difference between System.exit(0)
                    // and this?
                    Runtime.getRuntime().exit(0);
                }
            }
        });
        
        /*
        layoutMenu.add("Test").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    View view = createNewObjectTreeView(multidom.loadDocument(TEMPLATE));
                    final TabWindow twin = new TabWindow(view);        
                    rootWindow.setWindow(new SplitWindow(true, 0.8f, rootWindow.getWindow(), twin));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        */
        
        return layoutMenu;
    }
    
    /**
     * Creates the menu where layout can be saved and loaded.
     * @return the layout menu
     */
    private JMenu createLayoutMenu() {
        JMenu layoutMenu = new JMenu("Layout");
        
        layoutMenu.add("Default Layout").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultLayout();
            }
        });
        
        layoutMenu.addSeparator();
        
        for (int i = 0; i < layouts.length; i++) {
            final int j = i;
            
            layoutMenu.add("Save Layout " + i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Save the layout in a byte array
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(bos);
                        rootWindow.write(out, false);
                        out.close();
                        layouts[j] = bos.toByteArray();
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                }
            });
        }
        
        layoutMenu.addSeparator();
        
        for (int i = 0; i < layouts.length; i++) {
            final int j = i;
            
            layoutMenu.add("Load Layout " + j).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (layouts[j] != null) {
                                try {
                                    // Load the layout from a byte array
                                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(layouts[j]));
                                    rootWindow.read(in, true);
                                    in.close();
                                } catch (IOException e1) {
                                    throw new RuntimeException(e1);
                                }
                            }
                        }
                    });
                }
            });
        }
        return layoutMenu;
    }
    
    /**
     * Creates the menu where views can be shown and focused.
     * @return the focus view menu
     */
    private JMenu createFocusViewMenu() {
        JMenu viewsMenu = new JMenu("Focus View");
        
        for (int i = 0; i < views.length; i++) {
            final View view = views[i];
            viewsMenu.add("Focus " + view.getTitle()).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Ensure the view is shown in the root window
                            DockingUtil.addWindow(view, rootWindow);
                            
                            // Transfer focus to the view
                            view.restoreFocus();
                        }
                    });
                }
            });
        }
        return viewsMenu;
    }
    
    /**
     * Creates the menu where the theme can be changed.
     * @return the theme menu
     */
    private JMenu createThemesMenu() {
        JMenu themesMenu = new JMenu("Themes");
        
        DockingWindowsTheme[] themes = {new DefaultDockingTheme(),
        new BlueHighlightDockingTheme(),
        new SlimFlatDockingTheme(),
        new GradientDockingTheme(),
        new ShapedGradientDockingTheme(),
        new SoftBlueIceDockingTheme(),
        new ClassicDockingTheme()};
        
        ButtonGroup group = new ButtonGroup();
        
        for (int i = 0; i < themes.length; i++) {
            final DockingWindowsTheme theme = themes[i];
            
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(theme.getName());
            item.setSelected(i == 4);
            group.add(item);
            
            themesMenu.add(item).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Clear the modified properties values
                    properties.getMap().clear(true);
                    
                    setTheme(theme);
                }
            });
        }
        return themesMenu;
    }
    
    /**
     * Creates the menu where different property values can be modified.
     * @return the properties menu
     */
    private JMenu createPropertiesMenu() {
        JMenu buttonsMenu = new JMenu("Properties");
        
        final JCheckBoxMenuItem hideClose = new JCheckBoxMenuItem("Hide Close Buttons");
        final JCheckBoxMenuItem freezeLayout = new JCheckBoxMenuItem("Freeze Layout");
        
        buttonsMenu.add(hideClose).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hideClose.isSelected()) {
                    properties.getDockingWindowProperties().setCloseEnabled(false);
                }
                else if (!freezeLayout.isSelected()) {
                    properties.getDockingWindowProperties().setCloseEnabled(true);
                }
            }
        });
        buttonsMenu.add(freezeLayout).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (freezeLayout.isSelected()) {

                    // Disable window operations
                    properties.getDockingWindowProperties().setDragEnabled(false);
                    properties.getDockingWindowProperties().setCloseEnabled(false);
                    properties.getDockingWindowProperties().setMinimizeEnabled(false);
                    properties.getDockingWindowProperties().setRestoreEnabled(false);
                    properties.getDockingWindowProperties().setMaximizeEnabled(false);

                    // Enable tab reordering inside tabbed panel
                    properties.getTabWindowProperties().getTabbedPanelProperties().setTabReorderEnabled(true);
                } 
                else {
                    // Enable window operations
                    properties.getDockingWindowProperties().setDragEnabled(true);
                    if (!hideClose.isSelected()) {
                        properties.getDockingWindowProperties().setCloseEnabled(true);
                    }
                    properties.getDockingWindowProperties().setMinimizeEnabled(true);
                    properties.getDockingWindowProperties().setRestoreEnabled(true);
                    properties.getDockingWindowProperties().setMaximizeEnabled(true);

                    // Disable tab reordering inside tabbed panel
                    properties.getTabWindowProperties().getTabbedPanelProperties().setTabReorderEnabled(false);
                }
            }
        });
        return buttonsMenu;
    }
    
    /**
     * Creates the menu where individual window bars can be enabled and disabled.
     * @return the window bar menu
     */
    private JMenu createWindowBarsMenu() {
        JMenu barsMenu = new JMenu("Window Bars");
        
        for (int i = 0; i < 4; i++) {
            final Direction d = Direction.getDirections()[i];
            JCheckBoxMenuItem item = new JCheckBoxMenuItem("Toggle " + d);
            item.setSelected(d == Direction.DOWN);
            barsMenu.add(item).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Enable/disable the window bar
                    rootWindow.getWindowBar(d).setEnabled(!rootWindow.getWindowBar(d).isEnabled());
                }
            });
        }
        return barsMenu;
    }
    
    /**
     * Creates the menu where not shown views can be shown.
     * @return the view menu
     */
    private JMenu createViewMenu() {
        JMenu menu = new JMenu("Views");        
        for (int i = 0; i < views.length; i++) {
            final View view = views[i];
            viewItems[i] = new JMenuItem(view.getTitle());
            viewItems[i].setEnabled(views[i].getRootWindow() == null);
            menu.add(viewItems[i]).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (view.getRootWindow() != null)
                        view.restoreFocus();
                    else {
                        DockingUtil.addWindow(view, rootWindow);
                    }
                }
            });
        }
        return menu;
    }
    
    /**
     * Sets the docking windows theme.
     * @param theme the docking windows theme
     */
    private void setTheme(DockingWindowsTheme theme) {
        properties.replaceSuperObject(currentTheme.getRootWindowProperties(),
                theme.getRootWindowProperties());
        currentTheme = theme;
    }
    
    /**
     * Main method.
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // Avoid locate related grief?
        Locale.setDefault(Locale.ROOT);
    
        // Set InfoNode Look and Feel
        UIManager.setLookAndFeel(new InfoNodeLookAndFeel());
        
        // Docking windows should be run in the Swing thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

    /**
     * Creates the help menu.
     * @return
     */
    private JMenu createHelpMenu() {
        final JMenu menu = new JMenu("Help");
        menu.add("About").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        new String[] {
                    "PoolEdit",
                    "Version 1.5",
                    " ",
                    "Copyright (C) 2007-2019 Automation technology laboratory,",
                    "Helsinki University of Technology",
                    " ",
                    "Visit automation.tkk.fi for information about the automation",
                    "technology laboratory.",
                    " ",
                    "This program is free software; you can redistribute it and/or",
                    "modify it under the terms of the GNU General Public License",
                    "as published by the Free Software Foundation; either version",
                    "3 of the License, or (at your option) any later version.",
                    " ",
                    "Authors:",
                    "Matti Öhman (matti.ohman@aalto.fi)",
                    "Jouko Kalmari"},
                        "About PoolEdit", JOptionPane.INFORMATION_MESSAGE, Icons.POOLEDIT_LOGO);
            }
        });
        return menu;
    }
}
