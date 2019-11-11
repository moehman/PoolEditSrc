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
package objectview;

import static pooledit.Definitions.*;
import org.w3c.dom.Element;
import java.io.PrintStream;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.event.TreeModelListener;
import pooledit.Utils;
import treemodel.XMLTreeModel;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class ObjectView extends JComponent implements TreeModelListener, 
        Scrollable, KeyListener {

    private final boolean DEBUG = false;
        
    /** 
     * Padding around the graphical presentation of the selected path, 
     * THIS MUST BE GREATER THAN ZERO! 
     */
    static private final int EXTRA_SPACE = 8; 
    static private final int DEPTH_LIMIT = 20;
    
    //static private final AffineTransform IDENTITY = new AffineTransform();
    static private final GraphicObjectOperations GFXOP = new GraphicObjectOperations();
    
    static private final int IMAGE_FULL = 16;
    static private final int IMAGE_HALF = IMAGE_FULL / 2;
    static private final Paint BACKGROUND = createPaint();
    static private Paint createPaint() {
        BufferedImage image = new BufferedImage(IMAGE_FULL, IMAGE_FULL, 
						BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = image.createGraphics();
        gfx.setColor(Color.WHITE);
        gfx.fillRect(0, 0, IMAGE_FULL, IMAGE_FULL);
        gfx.setColor(Color.LIGHT_GRAY);
        gfx.fillRect(0, 0, IMAGE_HALF, IMAGE_HALF);
        gfx.fillRect(IMAGE_HALF, IMAGE_HALF, IMAGE_HALF, IMAGE_HALF);
        gfx.dispose();
	Rectangle anchor = new Rectangle(0, 0, IMAGE_FULL, IMAGE_FULL);
	return new TexturePaint(image, anchor);
    }
        
    private final HelpGrid grid = new HelpGrid();
    private final SelectionRectangle selrect = new SelectionRectangle();
    private final MouseController mc = new MouseController(this, new ObjectViewPopup(this));
    
    private PrintStream out;
    private XMLTreeModel model;
    private TreePath path;
    private boolean newModel = true;
    
    private double zoom = 1.0;
    
    // root node properties are cached here for convenient access
    private int dimension;
    private int sk_width;
    private int sk_height;
    private String fix_bitmap_path;
    private String std_bitmap_path;
    
    /** Image for double buffering */
    private Image bff;
    
    private boolean drawGrid = false;
    private boolean imageZoom = false;    
    
    private Timer flashTimer;
    
    class Lim1D {
	int min, max, val;
	void move(int v) {
	    update(val += v);	    
	}
	void set(int s) {
	    update(val + s);
	}
	private void update(int p) {
	    if (p < min) { min = p; }
	    else if (p > max) { max = p; }
	}
    }

    class Lim2D {
	Lim1D lx = new Lim1D();
	Lim1D ly = new Lim1D();
	void move(int x, int y) {
	    lx.move(x); ly.move(y);
	}
	void set(int w, int h) {
	    lx.set(w); ly.set(h);
	}
        boolean setAndTest(int w, int h, Point p) {
            lx.set(w); ly.set(h);
            return (p == null) ? false : (0 <= p.x && p.x < w && 0 <= p.y && p.y < h);
        }
	int getMaxX() {
	    return lx.max;
	}
	int getMaxY() {
	    return ly.max;
	}
    }
    
    /**
     * Private constructor, use getInstance() instead.
     */
    private ObjectView() {
    }

    /**
     * Creates an ObjectView.
     * @return
     */
    static public ObjectView getInstance() {
        return getInstance(null, System.out);
    }
    
    /**
     * Creates an ObjectView.
     * @param out
     * @return
     */
    static public ObjectView getInstance(PrintStream out) {
        return getInstance(null, out);
    }
    /**
     * Creates an ObjectView.
     * @param model
     * @param out
     * @return
     */
    static public ObjectView getInstance(XMLTreeModel model, PrintStream out) {
        ObjectView ov = new ObjectView();
        // double buffering is implemented in own code
        ov.setDoubleBuffered(false);
        
        // set key listener
        ov.setFocusable(true);
        ov.addKeyListener(ov);
        
        // set mouse listeners
        ov.addMouseListener(ov.mc);
        ov.addMouseMotionListener(ov.mc);
        ov.addMouseWheelListener(ov.mc);   
                
        // set model
        ov.setMessageStream(out);
        ov.setModel(model);
        return ov;
    }
    
    /**
     * Prints debug messages to System.out.
     * @param obj
     */
    private void dmsg(Object obj) {
	if (DEBUG) {
	    System.out.println(obj);
	}
    }

    /**
     * Sets a new model to this view. This method will have no effect if 
     * the new model is the same as the current model.
     * @param model
     */
    public void setModel(XMLTreeModel model) {
        if (this.model == model) {
            return;
        }
        // set model
	if (this.model != null) {
	    this.model.removeTreeModelListener(this);            
	}
	this.model = model;
        this.newModel = true;
	this.model.addTreeModelListener(this);
	repaint();
    }
    
    /**
     * Sets the stream where warning and error messages are printed.
     * @param out
     */
    public void setMessageStream(PrintStream out) {
        this.out = out;
    }
    
    /**
     * Returns the current model.
     * @return
     */
    public XMLTreeModel getModel() {
        return model;
    }    
    
    /**
     * Sets a new active path. This method will have no effect if the new
     * path is the same as the current path.
     * @param path
     */
    public void setActivePath(TreePath path) {
        if (Utils.equalObjects(this.path, path) && !newModel) {
            return;
        }
        this.path = path;
        this.newModel = false;
        // System.out.println(getClass().getName() + ": setActivePath(): " + path);
        // try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
        repaint();
    }
    
    /**
     * Gets the active path.
     * @return
     */
    public TreePath getActivePath() {
        return path;
    }
    
    /**
     * Sets the zoom factor.
     * @param zoom
     */
    public void setZoom(double zoom) {
	this.zoom = zoom;
	repaint();
    }

    /**
     * Gets the zoom factor.
     * @return
     */
    public double getZoom() {
        return this.zoom;
    }
    
    /**
     * Sets the draw borders flag.
     * @param drawBorders
     */
    public void setDrawBorders(boolean drawBorders) {
        GFXOP.setDrawBorders(drawBorders);
        repaint();
    }
    
    /**
     * Gets the draw borders flag.
     * @return
     */
    public boolean getDrawBorders() {
        return GFXOP.getDrawBorders();
    }
    
    /**
     * Sets the color depth.
     * @param colorDepth
     */
    public void setColorDepth(int colorDepth) {
       GFXOP.setColorDepth(colorDepth);
    }
    
    /**
     * Gets the color depth.
     * @return
     */
    public int getColorDepth() {
        return GFXOP.getColorDepth();
    }
    
    /**
     * Sets the reduce images flag.
     * @param reduceImages
     */
    public void setReduceImages(boolean reduceImages) {
        GFXOP.setReduceImages(reduceImages);
        repaint();
    }
    
    /**
     * Gets the reduce images flag.
     * @return
     */
    public boolean getReduceImages() {
        return GFXOP.getReduceImages();
    }

    /**
     * Sets the flashing feature on and off. When flashing is
     * enabled a new timer (thread) is created. When flasing is
     * disabled this timer is stopped. Flashing effect is created
     * by periodically toggling the flash attribute in the graphics
     * object.
     * @param flashing
     */
    public void setFlashing(boolean flashing) {        
        if (flashing) {
            flashTimer = new Timer("flashTimer");
            flashTimer.scheduleAtFixedRate(new TimerTask() {
                boolean flash;
                @Override
                public void run() {                    
                    GFXOP.setFlash(flash);                        
                    flash = !flash;
                    repaint();
                }
            }, 500, 500);
        }
        else {            
            flashTimer.cancel();
            flashTimer = null;
            GFXOP.setFlash(false);
        }
        repaint();
    }
     
    /**
     * Gets the state of the flashing feature.
     * @return
     */
    public boolean getFlashing() {
        return flashTimer != null;
    }

    /**
     * Sets the draw grid flag.
     * @param drawGrid
     */
    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        repaint();
    }
    
    /**
     * Gets the draw grid flag.
     * @return
     */
    public boolean getDrawGrid() {
        return this.drawGrid;
    }
    
    /**
     * Sets the image zoom flag.
     * @param imageZoom
     */
    public void setImageZoom(boolean imageZoom) {
        this.imageZoom = imageZoom;
        repaint();
    }
    
    /**
     * Gets the image zoom flag.
     * @return
     */
    public boolean getImageZoom() {
        return this.imageZoom;
    }
    
    /**
     * Gets the selection rectangle.
     * @return
     */
    public SelectionRectangle getSelectionRectangle() {
        return this.selrect;
    }
    
    /**
     * Gets the path to the node at the specified coordinates.
     * @param x
     * @param y
     * @return
     */
    public TreePath getPathToNodeAt(int x, int y) {
        XMLTreeNode start = getStartNode();
	if (start == null) {
	    return null;
	}
        Graphics2D gfx = (Graphics2D) this.getGraphics();
        gfx.scale(zoom, zoom);
        gfx.translate(EXTRA_SPACE / 2, EXTRA_SPACE / 2);
        PointObjectOperations op = new PointObjectOperations(x, y);
        processNode(gfx, start, op);
        TreePath pointedPath = op.getPointedPath();
        if (pointedPath == null) {
            return null;
        }
        Object[] m = mergeArrays(path.getPath(), op.getPointedPath().getPath());
        return new TreePath(m);
    }
       
    /**
     * Merges two arrays.
     * @param start
     * @param end
     * @return
     */
    static private Object[] mergeArrays(Object[] start, Object[] end) {
        for (int i = 0, n = start.length; i < n; i++) {
            if (start[i].equals(end[0])) {
                Object[] merge = new Object[i + end.length];
                System.arraycopy(start, 0, merge, 0, i);
                System.arraycopy(end, 0, merge, i, end.length);
                return merge;
            }
        }
        return null;
    }
    
    /**
     * Regenerates path. Will cause firing of a tree selection event. 
     */
    private void regeneratePath() {
        if (path == null) {
            return;
        }
        Object[] oldpath = path.getPath();        
        TreePath newpath = new TreePath(model.getRoot());      
        boolean loop = true;
        for (int j = 1, m = oldpath.length; j < m; j++) {
            loop = false;
            XMLTreeNode node = (XMLTreeNode) newpath.getLastPathComponent();
            for (int i = 0, n = model.getChildCount(node); i < n; i++) {
                XMLTreeNode newnode = (XMLTreeNode) model.getChild(node, i); 
                XMLTreeNode oldnode = (XMLTreeNode) oldpath[j];
                if (newnode == oldnode) {
                    newpath = newpath.pathByAddingChild(newnode);
                    loop = true;
                    break;
                }
            }
        }
        this.fireTreeSelection(newpath);
    }
    
    /**
     * Gets the node where the rendering should start.
     * @return
     */
    public XMLTreeNode getStartNode() {
        // nothing to select
        if (path == null || model == null || model.getRoot() == null) {
            return null;
        }        
        // no proper object selected
        Object[] p = path.getPath();         
        XMLTreeNode node = (XMLTreeNode) p[p.length - 1];
        if (!node.isType(OBJECTS) && !node.isType(POINT) && !node.isType(FIXEDBITMAP)) {
            return null;
        }
        // find last drawable node
        for (int i = p.length - 2; i >= 0; i--) {
            node = (XMLTreeNode) p[i];
            if (!node.isType(OBJECTS) && !node.isType(POINT)) {
                GFXOP.setDepth(i + 1);
                return (XMLTreeNode) p[i + 1];
            }
        }
        GFXOP.setDepth(0);
        return (XMLTreeNode) p[0];
    }
    
    /**
     * Paints this component.
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
	dmsg("paintComponent");               
        Graphics2D gfx = (Graphics2D) g;

	// FIXME: this is test code to get something meaningful to
	// draw, real code should use current selected path to find
	// out the proper starting point
	XMLTreeNode start = getStartNode();
	if (start == null) {
	    return;
	}

        XMLTreeNode root = (XMLTreeNode) model.getRoot();
        dimension = root.getDimension();
        sk_width = root.getSKWidth();
        sk_height = root.getSKHeight();
                        
        fix_bitmap_path = root.getFixBitmapPath();
        std_bitmap_path = root.getStdBitmapPath();
        
	// FIXME: is there a better place for this code?  Size cannot
	// be calculated in setzoom because the result depends on the
	// model and active node
	Lim2D lim = new Lim2D();
	calcSize(lim, start);
        
        // nasty things happen if the preferred size is set to zero
        int w = lim.getMaxX() + EXTRA_SPACE;
        int h = lim.getMaxY() + EXTRA_SPACE;
        
        // this should not be this complicated, but it is...
        if (zoom > 1 || !imageZoom) {
            w *= zoom;
            h *= zoom;
        }
        
	Dimension prefSize = getPreferredSize();
	if (prefSize.width != w || prefSize.height != h) {
	    setPreferredSize(new Dimension(w, h));
            bff = createImage(w, h);
	    revalidate(); // this will cause a new call to paintComponent
	    return;
	}

        grid.reset();
        selrect.reset();

        AffineTransform oldx = gfx.getTransform();
        Graphics2D img = (Graphics2D) bff.getGraphics();
        img.setPaint(BACKGROUND);
        img.fillRect(0, 0, w, h);
            
        // paint to buffer and then paint the buffer to the screen
        if (imageZoom) {
            img.translate(EXTRA_SPACE / 2, EXTRA_SPACE / 2);
            processNode(img, start, GFXOP);            
            gfx.scale(zoom, zoom);
        }
        // paint "directly" to screen
        else {
            img.scale(zoom, zoom);
            img.translate(EXTRA_SPACE / 2, EXTRA_SPACE / 2);
            processNode(img, start, GFXOP);
        }
        gfx.drawImage(bff, 0, 0, this);

        // selection is defined in absolute image coordinates, but it is drawn
        // in "mouse coordinates" (relative to the upper left corner of the 
        // parent component -> coordinate transform is needed
        try {
            AffineTransform tmp = oldx.createInverse();
            tmp.concatenate(gfx.getTransform());
            grid.adjust(tmp);
            selrect.adjust(tmp);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace(); // this should never happen!
        }

        // selection painted in "mouse coordinates" (in original graphics
        // coordinates)
        gfx.setTransform(oldx);
        if (grid.isSet()) {
            grid.draw(gfx, zoom);
        }
        if (selrect.isSet()) {            
            selrect.draw(gfx, zoom);
        }
    }
    
    /**
     * Painting dispatch method.
     * @param gfx
     * @param node
     * @param oper
     * @return
     */
    public Shape processNode(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
        if (oper.getDepth() > DEPTH_LIMIT) {
            out.println("DRAWING DEPTH LIMIT EXCEEDED!");
            return null;
        }
        
        Shape shape = null;
	String type = node.getType();
        try {
            if (null == type) {
                // normally this should not happen
            }            
            else switch (type) {
                case WORKINGSET:
                    shape = processWorkingSet(gfx, node, oper);
                    break;
                case DATAMASK:
                    shape = processDataMask(gfx, node, oper);
                    break;
                case ALARMMASK:
                    shape = processAlarmMask(gfx, node, oper);
                    break;
                case CONTAINER:
                    shape = processContainer(gfx, node, oper);
                    break;
                case SOFTKEYMASK:
                    shape = processSoftKeyMask(gfx, node, oper);
                    break;
                case KEY:
                    shape = processKey(gfx, node, oper);
                    break;
                case BUTTON:
                    shape = processButton(gfx, node, oper);
                    break;
                case INPUTBOOLEAN:
                    shape = processInputboolean(gfx, node, oper);
                    break;
                case INPUTSTRING:
                    shape = processInputString(gfx, node, oper);
                    break;
                case INPUTNUMBER:
                    shape = processInputNumber(gfx, node, oper);
                    break;
                case INPUTLIST:
                    shape = processInputList(gfx, node, oper);
                    break;
                case OUTPUTSTRING:
                    shape = processOutputString(gfx, node, oper);
                    break;
                case OUTPUTNUMBER:
                    shape = processOutputNumber(gfx, node, oper);
                    break;
                case LINE:
                    shape = processLine(gfx, node, oper);
                    break;
                case RECTANGLE:
                    shape = processRectangle(gfx, node, oper);
                    break;
                case ELLIPSE:
                    shape = processEllipse(gfx, node, oper);
                    break;
                case POLYGON:
                    shape = processPolygon(gfx, node, oper);
                    break;
                case METER:
                    shape = processMeter(gfx, node, oper);
                    break;
                case LINEARBARGRAPH:
                    shape = processLinearBarGraph(gfx, node, oper);
                    break;
                case ARCHEDBARGRAPH:
                    shape = processArchedBarGraph(gfx, node, oper);
                    break;
                case PICTUREGRAPHIC:
                    shape = processPictureGraphic(gfx, node, oper);
                    break;
                case OBJECTPOINTER:
                    shape = processObjectPointer(gfx, node, oper);
                    break;
                case AUXILIARYFUNCTION:
                    shape = processAuxiliaryFunction(gfx, node, oper);
                    break;
                case AUXILIARYINPUT:
                    shape = processAuxiliaryInput(gfx, node, oper);
                    break;
                default:
                    // node is not drawable, do nothing
                    break;
            }
        }
        catch (Exception ex) {
            System.err.println("### " + node.getType() + ": " + node.getName() + " ###");
            ex.printStackTrace();            
        }
        /*
        // the desired node is the last one matching the given point, however
        // this code is run when the recursion is already unfolding 
        // WILL NOT WORK CORRECTLY! THE EVALUATION ORDER IS NOT 100% CORRECT
        if (shape != null && pointedNode == null &&
                gfx.getTransform().createTransformedShape(shape).contains(point)) {
            
            pointedNode = node;
        }
        */
        
        // if the node is drawable (i.e. shape exists) and it is the
        // selected node, remember its position
        
        if (shape != null) {
            int nro = path.getPathCount();
            AffineTransform trans = gfx.getTransform();
            if (nro > 0) {
                XMLTreeNode last = (XMLTreeNode) path.getPathComponent(nro - 1);
                if (last.equals(node)) {
                    // last path component matches node
                    selrect.set(trans, shape, node);
                }
                else if (drawGrid && nro > 1) {
                    XMLTreeNode secondLast = (XMLTreeNode) path.getPathComponent(nro - 2);
                    
                    if (secondLast.equals(node) &&
                            !Utils.equals(last.getType(), WORKINGSET, DATAMASK, ALARMMASK, SOFTKEYMASK, KEY)) {                        
                        // second last path component matches node
                        grid.set(trans, shape, node);
                    }
                }
            }
        }        
        return shape;
    }
    
    /**
     * A helper method for changing the clip.
     * @param gfx
     * @param width
     * @param height
     * @return
     */
    static private Shape changeClip(Graphics2D gfx, int width, int height) {
        Shape oldc = gfx.getClip();
	gfx.clipRect(0, 0, width, height);
        return oldc;
    }
    
    /**
     * a helper method for restoring the clip. This method works exactly the 
     * same as changeClip method, but it uses different name (instead of 
     * overloading) to make the code more readable.
     * @param gfx
     * @param clip
     * @return
     */
    static private Shape restoreClip(Graphics2D gfx, Shape clip) {
        Shape oldc = gfx.getClip();
        gfx.setClip(clip);
        return oldc;
    }
    
    public Shape processWorkingSet(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintWorkingSet");
	Shape oldc = changeClip(gfx, sk_width, sk_height);
        
        boolean selectable = node.isSelectable();
        
        oper.opWorkingSet(gfx, node, sk_width, sk_height);
        
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 

	    // active_mask is a special child
	    if (nd.isType(DATAMASK, ALARMMASK)) {
                if (selectable && nd.getRole().equals(ACTIVE_MASK)) {
                    // draw mask below the working set designator
                    gfx.setClip(oldc);
                    AffineTransform oldx = gfx.getTransform();
                    gfx.translate(0, sk_height); 
                    processNode(gfx, nd, oper);
                    gfx.setTransform(oldx);
                    gfx.clipRect(0, 0, sk_width, sk_height);
                }
	    }
	    else if (nd.isType(LANGUAGE)) {
		// do nothing?
	    }
            else if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // this could be more specific, the main idea is to prevent 
            // processing of bogus nodes such as broken links
	    else if (nd.isType(OBJECTS)) {
		// paint children
		AffineTransform oldx = gfx.getTransform();
		gfx.translate(nd.getX(), nd.getY());
		processNode(gfx, nd, oper);
		gfx.setTransform(oldx);
	    }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processDataMask(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintDataMask");
	Shape oldc = changeClip(gfx, dimension, dimension);
        
        oper.opDataMask(gfx, node, dimension, dimension);
        
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 

	    // soft_key_mask is a special child
	    if (nd.isType(SOFTKEYMASK)) {
		// draw softkeymask on the right side of the mask
		gfx.setClip(oldc);
		AffineTransform oldx = gfx.getTransform();
		gfx.translate(dimension, 0);
		processNode(gfx, nd, oper);
		gfx.setTransform(oldx);
		gfx.clipRect(0, 0, dimension - 1, dimension - 1);
	    }
            else if (nd.isType(MACRO)) {
                // do nothing?                
            }
	    // this could be more specific
	    else if (nd.isType(OBJECTS)) {
		// paint children
		AffineTransform oldx = gfx.getTransform();
		gfx.translate(nd.getX(), nd.getY());
		processNode(gfx, nd, oper);
		gfx.setTransform(oldx);
	    }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processAlarmMask(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintAlarmMask");       

	Shape oldc = changeClip(gfx, dimension, dimension);
        
        oper.opAlarmMask(gfx, node, dimension, dimension);

        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 

	    // soft_key_mask is a special child
	    if (nd.isType(SOFTKEYMASK)) {
		// draw softkeymask on the right side of the mask
		gfx.setClip(oldc);
		AffineTransform oldx = gfx.getTransform();
		gfx.translate(dimension, 0);
		processNode(gfx, nd, oper);
		gfx.setTransform(oldx);
		gfx.clipRect(0, 0, dimension - 1, dimension - 1);
	    }
            else if (nd.isType(MACRO)) {
                // do nothing?                
            }
	    // this could be more specific
	    else if (nd.isType(OBJECTS)) {
		// paint children
		AffineTransform oldx = gfx.getTransform();
		gfx.translate(nd.getX(), nd.getY());
		processNode(gfx, nd, oper);
		gfx.setTransform(oldx);
	    }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processContainer(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintContainer");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());      
       
        oper.opContainer(gfx, node);
        
        if (!node.isHidden()) {        
            // draw children
            oper.incDepth();
            for (int i = 0, n = model.getChildCount(node); i < n; i++) {
                XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
                if (nd.isType(MACRO)) {
                    // do nothing?                
                }
                // this could be more specific
                else if (nd.isType(OBJECTS)) {
                    AffineTransform oldx = gfx.getTransform();
                    gfx.translate(nd.getX(), nd.getY());
                    processNode(gfx, nd, oper);
                    gfx.setTransform(oldx);
                }
            }
            oper.decDepth();
        }
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processSoftKeyMask(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintSoftKeyMask");
        int w = sk_width;
        int h = (model.getChildCount(node) + 1) * sk_height;
        Shape oldc = changeClip(gfx, w, h);	
        
        oper.opSoftKeyMask(gfx, node, w, h, sk_height);
        
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
            XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
            if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // these should all be softkeys
            else if (nd.isType(OBJECTS)) {
                // draw each softkey below the previous softkey
                AffineTransform oldx = gfx.getTransform();
                gfx.translate(0, sk_height * i);
                processNode(gfx, nd, oper);
                gfx.setTransform(oldx);
            }
	}
        oper.decDepth();
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processKey(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintKey");
	Shape oldc = changeClip(gfx, sk_width, sk_height);
        
        oper.opKey(gfx, node, sk_width, sk_height);

	// paint children
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
            if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // this could be more specific
            else if (nd.isType(OBJECTS)) {
                AffineTransform oldx = gfx.getTransform();
                gfx.translate(nd.getX(), nd.getY());
                processNode(gfx, nd, oper);
                gfx.setTransform(oldx);
            }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processButton(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintButton");
        int w = node.getWidth();
	int h = node.getHeight();
        Shape oldc = changeClip(gfx, w, h);

        oper.opButton(gfx, node);

	// paint children
        oper.incDepth();
	Shape oldc2 = gfx.getClip();
	gfx.clipRect(4, 4, w - 8, h - 8); // FIXME: magic numbers (are these from the standard?)
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
            if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // this could be more specific
            else if (nd.isType(OBJECTS)) {
                AffineTransform oldx = gfx.getTransform();
                gfx.translate(nd.getX() + 4, nd.getY() + 4);
                processNode(gfx, nd, oper);
                gfx.setTransform(oldx);
            }
	}
	gfx.setClip(oldc2);
        oper.decDepth();
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processInputboolean(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintInputboolean");

	int w = node.getWidth();
	Shape oldc = changeClip(gfx, w, w); // width == height
        
        oper.opInputBoolean(gfx, node);
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processInputString(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintInputString");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());

        try {
            oper.opInputString(gfx, node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processInputNumber(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintInputNumber: " + node.getName());
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        try {
            oper.opInputNumber(gfx, node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

	return restoreClip(gfx, oldc);
    }
    
    public Shape processInputList(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintInputList");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        oper.opInputList(gfx, node);
        
	// draw the child in the path
        oper.incDepth();
        if (oper.getDepth() < path.getPathCount()) {
            XMLTreeNode pathnode = (XMLTreeNode) path.getPathComponent(oper.getDepth());
            for (int i = 0, n = model.getChildCount(node); i < n; i++) {
                XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
                if (pathnode == nd) {
                    processNode(gfx, nd, oper);
                    
                    oper.decDepth();
                    return restoreClip(gfx, oldc);
                }
            }
        }
        
        // draw selected child
        int val = node.getValueInt();
        if (val < model.getChildCount(node)) {
            processNode(gfx, (XMLTreeNode) model.getChild(node, val), oper);
        }
        
        oper.decDepth();       
	return restoreClip(gfx, oldc);
    }
    
    public Shape processOutputString(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
        dmsg("paintOutputString");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        try {     
            oper.opOutputString(gfx, node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
	return restoreClip(gfx, oldc);
    }
        
    public Shape processOutputNumber(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintOutputNumber");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        try {
            oper.opOutputNumber(gfx, node);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processLine(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintLine");
	Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());

        oper.opLine(gfx, node);
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processRectangle(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintRectangle");
        Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        try {
            oper.opRectangle(gfx, node, std_bitmap_path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processEllipse(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintEllipse");
        Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
        
        try {
            oper.opEllipse(gfx, node, std_bitmap_path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return restoreClip(gfx, oldc);
    }
    
    // There is a problem with drawing thick lines if they don't fit inside the 
    // clipping rectangle.
    public Shape processPolygon(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintPolygon");
        Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());

        try {
            oper.opPolygon(gfx, node, std_bitmap_path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
     
        return restoreClip(gfx, oldc);
   }
    
   public Shape processMeter(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintMeter");
        int w = node.getWidth();
        Shape oldc = changeClip(gfx, w, w);
        
        oper.opMeter(gfx, node);
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processLinearBarGraph(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
        dmsg("paintLinearBarGraph");
        Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());
       
        oper.opLinearBarGraph(gfx, node);
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processArchedBarGraph(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintArchedBarGraph");        
        Shape oldc = changeClip(gfx, node.getWidth(), node.getHeight());

        oper.opArchedBarGraph(gfx, node);
        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processPictureGraphic(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintPictureGraphic");
        BufferedImage image;
        try {
            image = node.getImageFile();
            if (image == null) {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        int w = node.getWidth();
        int h = w * image.getHeight() / image.getWidth();

        Shape oldc = changeClip(gfx, w, h);

        oper.opPictureGraphic(gfx, node, image);
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processObjectPointer(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintObjectPointer");
               
	// should be only one?
        oper.incDepth();
        Shape shape = null;
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    shape = processNode(gfx, (XMLTreeNode) model.getChild(node, i), oper);
	}
        oper.decDepth();

        // this is a nasty special case - object pointer is processed after 
        // its children
        Shape oldc = gfx.getClip();
        gfx.setClip(shape);
        oper.opObjectPointer(gfx, node);        
        return restoreClip(gfx, oldc);
    }
    
    public Shape processAuxiliaryFunction(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintAuxiliaryFunction");
	Shape oldc = changeClip(gfx, sk_width, sk_height);
        
        oper.opAuxiliaryFunction(gfx, node, sk_width, sk_height);
        
	// paint children
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
            if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // this could be more specific
            else if (nd.isType(OBJECTS)) {
                AffineTransform oldx = gfx.getTransform();
                gfx.translate(nd.getX(), nd.getY());
                processNode(gfx, nd, oper);
                gfx.setTransform(oldx);
            }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }
    
    public Shape processAuxiliaryInput(Graphics2D gfx, XMLTreeNode node, ObjectOperations oper) {
	dmsg("paintAuxiliaryInput");
	Shape oldc = changeClip(gfx, sk_width, sk_height);
        
        oper.opAuxiliaryInput(gfx, node, sk_width, sk_height);
        
	// paint children
        oper.incDepth();
	for (int i = 0, n = model.getChildCount(node); i < n; i++) {
	    XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i);
            if (nd.isType(MACRO)) {
                // do nothing?                
            }
            // this could be more specific
            else if (nd.isType(OBJECTS)) {
                AffineTransform oldx = gfx.getTransform();
                gfx.translate(nd.getX(), nd.getY());
                processNode(gfx, nd, oper);
                gfx.setTransform(oldx);
            }
	}
        oper.decDepth();
        
	return restoreClip(gfx, oldc);
    }

    /**
     * Calculates required size by using a very limited recursion.
     * @param lim
     * @param node
     */
    void calcSize(Lim2D lim, XMLTreeNode node) {
	if (node.isType(WORKINGSET)) {
	    lim.set(sk_width, sk_height);
	    for (int i = 0, n = model.getChildCount(node); i < n; i++) {
		XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 
		if (nd.isType(DATAMASK, ALARMMASK)) {
		    lim.move(0, sk_height);
		    calcSize(lim, nd);                  
		    lim.move(0, -sk_height);
		}
	    }
	}
	else if (node.isType(DATAMASK, ALARMMASK)) {
	    lim.set(dimension, dimension);
	    for (int i = 0, n = model.getChildCount(node); i < n; i++) {
		XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 
		if (nd.isType(SOFTKEYMASK)) {
		    lim.move(dimension, 0);
		    calcSize(lim, nd);
		    lim.move(0, -sk_height);
		}
	    }
	}
	else if (node.isType(SOFTKEYMASK)) {
            // reserve roperm for one extra key
            lim.set(sk_width, (model.getChildCount(node) + 1) * sk_height);
	}
	else if (node.isType(KEY, AUXILIARYFUNCTION, AUXILIARYINPUT)) {
	    lim.set(sk_width, sk_height);
	}
	else if (node.isType(CONTAINER, BUTTON, 
                INPUTSTRING, INPUTNUMBER, INPUTLIST,
                OUTPUTSTRING, OUTPUTNUMBER, 
                LINE, RECTANGLE, ELLIPSE, POLYGON,
                LINEARBARGRAPH, ARCHEDBARGRAPH)) {
	    lim.set(node.getWidth(), node.getHeight());
	}
        else if (node.isType(INPUTBOOLEAN, METER)) {
            int w = node.getWidth();
            lim.set(w, w);
        }
        else if (node.isType(PICTUREGRAPHIC)) {
            try {
                BufferedImage image = node.getImageFile(); // for size calculation only
                if (image != null) {
                    int w = node.getWidth();
                    int h = w * image.getHeight() / image.getWidth();
                    lim.set(w, h);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
                lim.set(64, 64); // questimate picture size?
            }
        }
        else if (node.isType(OBJECTPOINTER)) {
            // should be only one
            for (int i = 0, n = model.getChildCount(node); i < n; i++) {
		XMLTreeNode nd = (XMLTreeNode) model.getChild(node, i); 
                calcSize(lim, nd);
            }
        }
    }

    //------------------------------------------------------------//
    
    private final ArrayList<TreeSelectionListener> listeners = new ArrayList<>();
    
    /**
     * Adds a listener.
     * @param l
     */
    public void addTreeSelectionListener(TreeSelectionListener l) {
        synchronized (listeners) {
            if (!listeners.contains(l)) {
                listeners.add(l);
            }
        }
    }

    /**
     * Removes a listener.
     * @param l
     * @return 
     */
    public boolean removeTreeSelectionListener(TreeSelectionListener l) {
        synchronized (listeners) {
            return listeners.remove(l);
        }
    }
    
    /**
     * Fires a tree selection event.
     * @param newPath
     */
    public void fireTreeSelection(TreePath newPath) {
        TreeSelectionEvent e = new TreeSelectionEvent(this,
                          newPath,
                          Utils.equalObjects(newPath, path),
                          path,
                          newPath);
        synchronized (listeners) {
            for (TreeSelectionListener l : listeners) {
                l.valueChanged(e);
            }
        }
    }
    //------------------------------------------------------------//

    /**
     * Invoked after a node (or a set of siblings) has changed in some way.
     */
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                regeneratePath();
                repaint();
            }
        });
    }
          
    /**
     * Invoked after nodes have been inserted into the tree.
     */
    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                regeneratePath();
                repaint();
            }
        });
    }
          
    /**
     * Invoked after nodes have been removed from the tree.
     */
    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        selrect.unSet();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                regeneratePath();
                repaint();
            }
        });
    }
          
    /**
     * Invoked after the tree has drastically changed structure from a
     * given node down.
     */
    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        selrect.unSet();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                regeneratePath();
                repaint();
            }
        });
    }

    //------------------------------------------------------------//

    private final Dimension preferredScrollableViewportSize =
	new Dimension(320, 320);

    public void setPreferredScrollableViewportSize(Dimension d) {
	preferredScrollableViewportSize.setSize(d);
    }

    /**
     * Returns the preferred size of the viewport for a view
     * component.
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
	return preferredScrollableViewportSize;
    }
	
    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one block of
     * rows or columns, depending on the value of orientation.
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, 
					   int orientation, 
					   int direction) {
	return 16;
    }
    
    /**
     * Return true if a viewport should always force the height of
     * this Scrollable to match the height of the viewport.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
	return false;
    }
	    
    /**
     * Return true if a viewport should always force the width of this
     * Scrollable to match the width of the viewport.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
	return false;
    }
    
    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row or
     * column, depending on the value of orientation.
     */      
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
					  int orientation, 
					  int direction) {
	return 16;
    }

    /**
     * Keylistener interface methods
     */
    @Override
    public void keyTyped(KeyEvent e) {       
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            System.out.println("DELETE: ");
            if (path != null && path.getPathCount() > 1) {
                
                XMLTreeNode node = (XMLTreeNode) path.getLastPathComponent();
                Element actual = node.actual();
                Element link = node.link();
                
                // actual object (or broken link)
                if (link == null || node.isType(INCLUDE_OBJECT)) {
                    actual.getParentNode().removeChild(actual);                    
                }
                // working link (link != null)
                else { 
                    link.getParentNode().removeChild(link);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
