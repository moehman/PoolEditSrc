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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.tree.TreePath;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public class PointObjectOperations implements ObjectOperations {
    
    private final Point point = new Point();
    private final ArrayList<XMLTreeNode> currentPath = new ArrayList<>();
    private TreePath pointedPath;
    private int depth = 0;
    
    /**
     * Constructor.
     * @param x
     * @param y
     */
    public PointObjectOperations(int x, int y) {
        point.setLocation(x, y);
    }

    /**
     * Gets the path to the pointed object.
     * @return
     */
    public TreePath getPointedPath() {
        return pointedPath;
    }
    
    /**
     * Checks, whether the point is inside the specified node.
     * @param gfx
     * @param node
     */
    private void check(Graphics2D gfx, XMLTreeNode node) {
        for (int i = currentPath.size() - 1; i >= depth; i--) {
            currentPath.remove(i);
        }
        currentPath.add(node);

        Shape clip = gfx.getClip();
        if (gfx.getTransform().createTransformedShape(clip).contains(point)) {           
            pointedPath = new TreePath(currentPath.toArray());
        }
        //System.out.println("checking node: " + node + "(" + pointedNode + ")");
    }
    
    /**
     * Sets the depth.
     * @param depth
     */
    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    /**
     * Increments the depth by one.
     */
    @Override
    public void incDepth() {
        depth++;
    }

    /**
     * Decrements the depth by one.
     */
    @Override
    public void decDepth() {
        depth--;
        if (depth < 0) {
            throw new RuntimeException();
        }
    }
    
    /**
     * Gets the depth.
     * @return
     */
    @Override
    public int getDepth() {
        return depth;
    }
    
    @Override
    public void opAlarmMask(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }

    @Override
    public void opArchedBarGraph(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opAuxiliaryFunction(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }

    @Override
    public void opAuxiliaryInput(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }

    @Override
    public void opButton(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opContainer(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opDataMask(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }

    @Override
    public void opEllipse(Graphics2D gfx, XMLTreeNode node, String imagepath) {
        check(gfx, node);
    }

    @Override
    public void opInputBoolean(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opInputList(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opInputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException {
        check(gfx, node);
    }

    @Override
    public void opInputString(Graphics2D gfx, XMLTreeNode node) throws IOException {
        check(gfx, node);
    }

    @Override
    public void opKey(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }

    @Override
    public void opLine(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opLinearBarGraph(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opMeter(Graphics2D gfx, XMLTreeNode node) {
        check(gfx, node);
    }

    @Override
    public void opObjectPointer(Graphics2D gfx, XMLTreeNode node) {
        // check(gfx, node); // no check
    }

    @Override
    public void opOutputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException {
        check(gfx, node);
    }

    @Override
    public void opOutputString(Graphics2D gfx, XMLTreeNode node) throws IOException {
        check(gfx, node);
    }

    @Override
    public void opPictureGraphic(Graphics2D gfx, XMLTreeNode node, BufferedImage image) {
        check(gfx, node);
    }

    @Override
    public void opPolygon(Graphics2D gfx, XMLTreeNode node, String imagepath) {
        check(gfx, node);
    }

    @Override
    public void opRectangle(Graphics2D gfx, XMLTreeNode node, String imagepath) {
        check(gfx, node);
    }

    @Override
    public void opSoftKeyMask(Graphics2D gfx, XMLTreeNode node, int w, int h, int sk_height) {
        check(gfx, node);
    }

    @Override
    public void opWorkingSet(Graphics2D gfx, XMLTreeNode node, int w, int h) {
        check(gfx, node);
    }
}
