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
package objectview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 * @author mohman
 */
public class LineAttributes {
    private final Color color;
    private final Stroke stroke;
    
    /**
     * Gets the line color.
     * @return
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Gets the line stroke.
     * @return
     */
    public Stroke getStroke() {
        return stroke;
    }
    
    /** 
     * Creates a new instance of LineAttributes.
     * @param color
     * @param stroke
     */
    public LineAttributes(Color color, Stroke stroke) {
        this.color = color;
        this.stroke = stroke;
    }
    
    /**
     * Applies these line attributes to the given graphics context.
     * @param gfx
     */
    public void apply(Graphics2D gfx) {
        gfx.setColor(color);
        gfx.setStroke(stroke);
    }
}
