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
package color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author mohman
 */
public class ColorIcon implements Icon {
    final int SIZE = 11;
    private Color color;
    
    /**
     * Private constructor, use getInstance() instead.
     */
    private ColorIcon() {
    }
    
    /**
     *
     * @return
     */
    static public ColorIcon getInstance() {
        return getInstance(Color.BLACK);
    }
    
    /**
     *
     * @param color
     * @return
     */
    static public ColorIcon getInstance(Color color) {
        ColorIcon ci = new ColorIcon();
        ci.setColor(color);
        return ci;
    }
    
    /**
     * Sets color.
     * @param color
     */
    public void setColor(Color color) {
	this.color = color;
    }
        
    /**
     * Gets icon height.
     * @return
     */
    @Override
    public int getIconHeight() {
	return SIZE;
    }
    
    /**
     * Gets icon width.
     * @return
     */
    @Override
    public int getIconWidth() {
	return SIZE;
    }
    
    /**
     * Paints the icon.
     * @param c
     * @param g
     * @param x
     * @param y
     */
    @Override
    public void paintIcon(Component c, 
			  Graphics g, 
			  int x, int y) {
	g.setColor(color);
	g.fillRect(x, y, SIZE, SIZE);
	g.setColor(Color.BLACK);
	g.drawRect(x, y, SIZE - 1, SIZE - 1);	
    }
}
