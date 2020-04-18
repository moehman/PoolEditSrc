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
import java.awt.image.BufferedImage;
import java.io.IOException;
import treemodel.XMLTreeNode;

/**
 *
 * @author mohman
 */
public interface ObjectOperations {
    void setDepth(int depth);
    
    void incDepth();
    
    void decDepth();
    
    int getDepth();
    
    void opAlarmMask(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opArchedBarGraph(Graphics2D gfx, XMLTreeNode node);

    void opAuxiliaryFunction(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opAuxiliaryInput(Graphics2D gfx, XMLTreeNode node, int w, int h);
    
    void opAuxiliaryFunction2(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opAuxiliaryInput2(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opButton(Graphics2D gfx, XMLTreeNode node);

    void opContainer(Graphics2D gfx, XMLTreeNode node);

    void opDataMask(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opEllipse(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException;

    void opInputBoolean(Graphics2D gfx, XMLTreeNode node);

    void opInputList(Graphics2D gfx, XMLTreeNode node);

    void opInputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException;

    void opInputString(Graphics2D gfx, XMLTreeNode node) throws IOException;

    void opKey(Graphics2D gfx, XMLTreeNode node, int w, int h);

    void opLine(Graphics2D gfx, XMLTreeNode node);

    void opLinearBarGraph(Graphics2D gfx, XMLTreeNode node);

    void opMeter(Graphics2D gfx, XMLTreeNode node);

    void opObjectPointer(Graphics2D gfx, XMLTreeNode node);

    void opOutputNumber(Graphics2D gfx, XMLTreeNode node) throws IOException;

    void opOutputString(Graphics2D gfx, XMLTreeNode node) throws IOException;

    void opPictureGraphic(Graphics2D gfx, XMLTreeNode node, BufferedImage image);

    void opPolygon(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException;

    void opRectangle(Graphics2D gfx, XMLTreeNode node, String imagepath) throws IOException;

    void opSoftKeyMask(Graphics2D gfx, XMLTreeNode node, int w, int h, int sk_height);

    void opWorkingSet(Graphics2D gfx, XMLTreeNode node, int w, int h);    
}
