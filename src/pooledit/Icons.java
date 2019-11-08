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
package pooledit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author mohman
 */
public abstract class Icons implements Icon {

    private static final int ICON_SIZE = 14;
    private static final int SYMBOL_SIZE = 8;
    private static final int OFFSET = (ICON_SIZE - SYMBOL_SIZE) / 2;
    private static final int MIDDLE = SYMBOL_SIZE / 2;
    
    @Override
    public int getIconHeight() { return ICON_SIZE; }
    @Override
    public int getIconWidth()  { return ICON_SIZE; }
    
    static public final Icon ZOOM_MINUS_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, SYMBOL_SIZE, SYMBOL_SIZE);
            g.drawLine(2, MIDDLE, SYMBOL_SIZE - 2, MIDDLE);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon ZOOM_PLUS_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, SYMBOL_SIZE, SYMBOL_SIZE);
            g.drawLine(2, MIDDLE, SYMBOL_SIZE - 2, MIDDLE);
            g.drawLine(MIDDLE, 2, MIDDLE, SYMBOL_SIZE - 2);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon DRAW_BORDERS_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, SYMBOL_SIZE, SYMBOL_SIZE);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon IMAGE_ZOOM_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.drawRect(0, 0, SYMBOL_SIZE, SYMBOL_SIZE);
            g.drawLine(2, 2, SYMBOL_SIZE - 2, 2);
            g.drawLine(2, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2, 2);
            g.drawLine(2, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon DRAW_GRID_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.drawRect(0, 0, MIDDLE, MIDDLE);
            g.drawRect(MIDDLE, 0, MIDDLE, MIDDLE);
            g.drawRect(0, MIDDLE, MIDDLE, MIDDLE);
            g.drawRect(MIDDLE, MIDDLE, MIDDLE, MIDDLE);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon XML_PARSE_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.fillPolygon(new int[] {SYMBOL_SIZE, MIDDLE, 0},
                    new int[] {SYMBOL_SIZE, 0, SYMBOL_SIZE}, 3);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon XML_GENERATE_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.fillPolygon(new int[] {0, MIDDLE, SYMBOL_SIZE},
                    new int[] {0, SYMBOL_SIZE, 0}, 3);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    static public final Icon CLEAR_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x + OFFSET, y + OFFSET);
            g.drawRect(0, 0, SYMBOL_SIZE, SYMBOL_SIZE);
            g.drawLine(2, 2, SYMBOL_SIZE - 2, 2);
            g.drawLine(2, 2, 2, SYMBOL_SIZE - 2);
            g.drawLine(2, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2);
            g.translate(-(x + OFFSET), -(y + OFFSET));
        }
    };
    
    /**
     * Custom view icon.
     */
    static public final Icon VIEW_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            int off = (ICON_SIZE - SYMBOL_SIZE) / 2;
            g.setColor(new Color(70, 70, 70));
            g.fillRect(x + off, y + off, SYMBOL_SIZE, SYMBOL_SIZE);

            g.setColor(new Color(100, 230, 100));
            g.fillRect(x + off + 1, y + off + 1, SYMBOL_SIZE - 2, SYMBOL_SIZE - 2);

            g.setColor(oldColor);
        }
    };

    /**
     * Custom view button icon.
     */
    static public final Icon BUTTON_ICON = new Icons() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            
            int off = (ICON_SIZE - SYMBOL_SIZE) / 2;
            g.setColor(Color.BLACK);
            g.fillOval(x + off, y + off, SYMBOL_SIZE, SYMBOL_SIZE);
            
            g.setColor(oldColor);
        }
    };
    
    /**
     * Custom view button icon.
     */
    static public final Icon BUTTON_ROLLOVER_ICON = new Icons() {
        @Override
         public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();

            g.setColor(Color.GRAY);
            g.drawRect(x, y, ICON_SIZE - 1, ICON_SIZE - 1);

            int off = (ICON_SIZE - SYMBOL_SIZE) / 2;
            g.setColor(Color.RED /*Color.BLACK*/);
            g.fillOval(x + off, y + off, SYMBOL_SIZE, SYMBOL_SIZE);

            g.setColor(oldColor);
        }
    };
    
    static public final Icon POOLEDIT_LOGO = Utils.createImageIcon("/images/pooleditlogo.png");

}
