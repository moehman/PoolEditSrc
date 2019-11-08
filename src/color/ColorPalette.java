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

/**
 *
 * @author mohman
 */
public class ColorPalette {
    
    static private final String[] NAMES = {
	"black", "white", "green", "teal",
	"maroon", "purple", "olive", "silver",
	"grey", "blue", "lime", "cyan", 
        "red", "magenta", "yellow", "navy"
    };

    static private final int[] VALUES_8BIT = {
	0x000000, 0xFFFFFF, 0x009900, 0x009999, //0
	0x990000, 0x990099, 0x999900, 0xCCCCCC, //4
	0x999999, 0x0000FF, 0x00FF00, 0x00FFFF, //8
	0xFF0000, 0xFF00FF, 0xFFFF00, 0x000099, //12
	0x000000, 0x000033, 0x000066, 0x000099, //16
	0x0000CC, 0x0000FF, 0x003300, 0x003333, //20
	0x003366, 0x003399, 0x0033CC, 0x0033FF, //24
	0x006600, 0x006633, 0x006666, 0x006699, //28
	0x0066CC, 0x0066FF, 0x009900, 0x009933, //32
	0x009966, 0x009999, 0x0099CC, 0x0099FF, //36
	0x00CC00, 0x00CC33, 0x00CC66, 0x00CC99, //40
	0x00CCCC, 0x00CCFF, 0x00FF00, 0x00FF33, //44
	0x00FF66, 0x00FF99, 0x00FFCC, 0x00FFFF, //48
	0x330000, 0x330033, 0x330066, 0x330099, //52
	0x3300CC, 0x3300FF, 0x333300, 0x333333, //56
	0x333366, 0x333399, 0x3333CC, 0x3333FF, //60
	0x336600, 0x336633, 0x336666, 0x336699, //64
	0x3366CC, 0x3366FF, 0x339900, 0x339933, //68
	0x339966, 0x339999, 0x3399CC, 0x3399FF, //72
	0x33CC00, 0x33CC33, 0x33CC66, 0x33CC99, //76
	0x33CCCC, 0x33CCFF, 0x33FF00, 0x33FF33, //80
	0x33FF66, 0x33FF99, 0x33FFCC, 0x33FFFF, //84
	0x660000, 0x660033, 0x660066, 0x660099, //88
	0x6600CC, 0x6600FF, 0x663300, 0x663333, //92
	0x663366, 0x663399, 0x6633CC, 0x6633FF, //96
	0x666600, 0x666633, 0x666666, 0x666699, //100
	0x6666CC, 0x6666FF, 0x669900, 0x669933, //104
	0x669966, 0x669999, 0x6699CC, 0x6699FF, //108
	0x66CC00, 0x66CC33, 0x66CC66, 0x66CC99, //112
	0x66CCCC, 0x66CCFF, 0x66FF00, 0x66FF33, //116
	0x66FF66, 0x66FF99, 0x66FFCC, 0x66FFFF, //120
	0x990000, 0x990033, 0x990066, 0x990099, //124
	0x9900CC, 0x9900FF, 0x993300, 0x993333, //128
	0x993366, 0x993399, 0x9933CC, 0x9933FF, //132
	0x996600, 0x996633, 0x996666, 0x996699, //136
	0x9966CC, 0x9966FF, 0x999900, 0x999933, //140
	0x999966, 0x999999, 0x9999CC, 0x9999FF, //144
	0x99CC00, 0x99CC33, 0x99CC66, 0x99CC99, //148
	0x99CCCC, 0x99CCFF, 0x99FF00, 0x99FF33,
	0x99FF66, 0x99FF99, 0x99FFCC, 0x99FFFF,
	0xCC0000, 0xCC0033, 0xCC0066, 0xCC0099,
	0xCC00CC, 0xCC00FF, 0xCC3300, 0xCC3333,
	0xCC3366, 0xCC3399, 0xCC33CC, 0xCC33FF,
	0xCC6600, 0xCC6633, 0xCC6666, 0xCC6699,
	0xCC66CC, 0xCC66FF, 0xCC9900, 0xCC9933,
	0xCC9966, 0xCC9999, 0xCC99CC, 0xCC99FF,
	0xCCCC00, 0xCCCC33, 0xCCCC66, 0xCCCC99,
	0xCCCCCC, 0xCCCCFF, 0xCCFF00, 0xCCFF33,
	0xCCFF66, 0xCCFF99, 0xCCFFCC, 0xCCFFFF,
	0xFF0000, 0xFF0033, 0xFF0066, 0xFF0099,
	0xFF00CC, 0xFF00FF, 0xFF3300, 0xFF3333,
	0xFF3366, 0xFF3399, 0xFF33CC, 0xFF33FF,
	0xFF6600, 0xFF6633, 0xFF6666, 0xFF6699,
	0xFF66CC, 0xFF66FF, 0xFF9900, 0xFF9933,
	0xFF9966, 0xFF9999, 0xFF99CC, 0xFF99FF,
	0xFFCC00, 0xFFCC33, 0xFFCC66, 0xFFCC99,
	0xFFCCCC, 0xFFCCFF, 0xFFFF00, 0xFFFF33,
	0xFFFF66, 0xFFFF99, 0xFFFFCC, 0xFFFFFF
    };
    static private final int[] VALUES_4BIT = new int[VALUES_8BIT.length];
    static private final int[] VALUES_1BIT = new int[VALUES_8BIT.length];
        
    static private final Color[] COLORS_8BIT = new Color[VALUES_8BIT.length];
    static private final Color[] COLORS_4BIT = new Color[VALUES_8BIT.length];
    static private final Color[] COLORS_1BIT = new Color[VALUES_8BIT.length];
    static private final String[] ALL_NAMES = new String[VALUES_8BIT.length];
    
    static public final int COLOR_8BIT = 8;
    static public final int COLOR_4BIT = 4;
    static public final int COLOR_1BIT = 1;
    
    static {
        // init names
        for (int i = 0, n = VALUES_8BIT.length; i < n; i++) {
            ALL_NAMES[i] = getName(i);
        }
        // init 8-bit colors
	for (int i = 0, n = VALUES_8BIT.length; i < n; i++) {
            Color c = new Color(VALUES_8BIT[i]);
            VALUES_8BIT[i] = c.getRGB(); // this may seem lika a circular definition, but it makes sure that alpha is 0xFF
	    COLORS_8BIT[i] = c;
        }
        // init 4-bit colors
        for (int i = 0, n = VALUES_8BIT.length; i < n; i++) {
            Color c = findClosestColor(COLORS_8BIT[i], COLORS_8BIT, 16);
            VALUES_4BIT[i] = c.getRGB();
            COLORS_4BIT[i] = c;
        }
        // init 1-bit colors
        for (int i = 0, n = VALUES_8BIT.length; i < n; i++) {
            Color c = findClosestColor(COLORS_8BIT[i], COLORS_8BIT, 2);
            VALUES_1BIT[i] = c.getRGB();
            COLORS_1BIT[i] = c;
        }
        
        /*
        // some quick tests
        for (int i = 0; i < 0xFFFFFF; i++) {
            int ind_a = getPaletteIndexFast(i);
            int a = getColorRGB(i, COLOR_8BIT);
            int ind_b = getPaletteIndex(i);
            int b = getColor(ind_b, COLOR_8BIT).getRGB();            
            if (a != b) {
            //if (ind_a != ind_b) {
                System.out.format("I: %d, A: 0x%X B: 0x%X IND_A: %d IND_B: %d NEAREST: 0x%X\n", i, a, b, ind_a, ind_b, getNearestColor(i));
                break;
            }
        }
        System.out.println("PASSED " + VALUES_8BIT.length);
         */
    }

    /**
     * Finds the closes color, used for color array initializations.
     * @param color
     * @param colorSpace
     * @param colorDepth
     * @return
     */
    static public Color findClosestColor(Color color, Color[] colorSpace, int colorDepth) {
        int minIndex = 0;
        int minDist = calculateDistance(colorSpace[minIndex], color);
        for (int i = 0; i < colorDepth; i++) {
            int dist = calculateDistance(colorSpace[i], color);
            if (dist < minDist) {
                minDist = dist;
                minIndex = i;
            }
        }
        return colorSpace[minIndex];    
    }
   
    /**
     * Returns how far given colors are
     * FIXME: which metric is more reasonable? Use hue-space instead of rgb-space?
     * @param c1
     * @param c2
     * @return
     */
    static private int calculateDistance(Color c1, Color c2) {
        
        return Math.abs(c1.getRed() - c2.getRed()) +                
                Math.abs(c1.getGreen() - c2.getGreen()) +
                Math.abs(c1.getBlue() - c2.getBlue());
        /*
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return dr * dr + dg * dg + db * db;
         */
    }
    
    /**
     * Returns the nearest color in the palette - given the color depth.
     * This method is used for object colors, but not for image data.
     * @param index
     * @param colorDepth
     * @return
     */
    static public Color getColor(int index, int colorDepth) {
        switch (colorDepth) {
            case COLOR_1BIT:
                return COLORS_1BIT[index];
            case COLOR_4BIT:
                return COLORS_4BIT[index];
            case COLOR_8BIT:
                return COLORS_8BIT[index];
            default:
                throw new IllegalStateException("invalid colorDepth (" + colorDepth + ")");
        }
    }

    /**
     * Returns the nearest color (argb) in the palette - given the color depth. 
     * Alpha is always 0xFF. This method is used for image data. It should be
     * as fast as possible!
     * @param rgb
     * @param colorDepth
     * @return
     */
    static public int getColorRGB(int rgb, int colorDepth) {
        int r3 = ((((rgb >> 16) & 0xFF) + 26) / 51);
        int g3 = ((((rgb >> 8) & 0xFF) + 26) / 51); // why 26? why not 25?
        int b3 = ((((rgb) & 0xFF) + 26) / 51);
        int index = 36 * r3 + 6 * g3 + b3 + 16;
        switch (colorDepth) {
            case COLOR_1BIT:
                return VALUES_1BIT[index];
            case COLOR_4BIT:
                return VALUES_4BIT[index];
            case COLOR_8BIT:
                return VALUES_8BIT[index];
            default:
                throw new IllegalStateException("invalid colorDepth (" + colorDepth + ")");
        }
    }
    
    /**
     * Returns the specified color.
     * @param name
     * @param colorDepth
     * @return
     */
    static public Color getColor(String name, int colorDepth) {
	return getColor(getIndex(name), colorDepth);
    }

    /**
     * Returns the 8-bit color.
     * @param name
     * @return
     */
    static public Color getColor8Bit(String name) {
	return getColor(getIndex(name), COLOR_8BIT);
    }
    
    /**
     * Returns the name of the specified color.
     * @param index
     * @return
     */
    static public String getName(int index) {
	if (index < NAMES.length) {
	    return NAMES[index];
	}
	else {
	    return Integer.toString(index);
	}
    }

    /**
     * Returns the index of the specified color.
     * @param name
     * @return
     */
    static public int getIndex(String name) {
	for (int i = 0, n = NAMES.length; i < n; i++) {
	    if (NAMES[i].equals(name)) {
		return i;
	    }
	}
	try {
	    return Integer.parseInt(name);
	}
	catch (NumberFormatException ex) {
	    return 0;
	}
    }

    /**
     * Returns the normalized name, e.g. 0 -> black, 1 -> white, ..., 
     * 16 -> 16, 17 -> 17, ...
     * @param name
     * @return
     */
    static public String normalizeName(String name) {
	return getName(getIndex(name));
    }

    /**
     * Returns the names of the first 16 colors.
     * @return
     */
    static public String[] getColorNames() {
	return NAMES;
    }

    /**
     * Returns the names of all colors.
     * @return
     */
    static public String[] getAllColorNames() {
        return ALL_NAMES;
    }
    
    /**
     * Returns the number of colors.
     * @return
     */
    static public int getNroColors() {
	return VALUES_8BIT.length;
    }
    
    /**
     * Returns the nearest color (argb) in the palette.
     * @param rgb
     * @return
     */
    static public int getNearestColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        return getNearestColor(r, g, b);
    }
    
    /**
     * Returns the nearest color (argb) in the palette. Alpha is always 0xFF.
     * @param r
     * @param g
     * @param b
     * @return
     */
    static public int getNearestColor(int r, int g, int b) {
        r = ((r + 26) / 51) * 51;
        g = ((g + 26) / 51) * 51; // why 26? why not 25?
        b = ((b + 26) / 51) * 51;
        return (0xFF << 24) + (r << 16) + (g << 8) + b;
    }
    
    /**
     * Returns the index of the nearest color in the palette.
     * @param rgb
     * @return
     */
    static public int getPaletteIndex(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        return getPaletteIndex(r, g, b);
    }
    
    /**
     * Returns the index of the nearest color in the palette.
     * @param r
     * @param g
     * @param b
     * @return
     */
    static public int getPaletteIndex(int r, int g, int b){
        int color = getNearestColor(r, g, b);
        for (int i = 0; i < VALUES_8BIT.length; i++) {
            if (color == VALUES_8BIT[i]) {
                return i;
            }
        }
        return 0;
    }
    
    /**
     * Returns the index of the nearest color in the palette very fast.
     * Indices 0..15 are never returned (as they are repeated later in the
     * palette).
     * @param rgb
     * @return
     */
    static public int getPaletteIndexFast(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return getPaletteIndexFast(r, g, b);
    }    
    
    /**
     * Returns the index of the nearest color in the palette very fast.
     * Indices 0..15 are never returned (as they are repeated later in the
     * palette).
     * @param r
     * @param g
     * @param b
     * @return
     */
    static public int getPaletteIndexFast(int r, int g, int b) {
        int r3 = (r + 26) / 51;
        int g3 = (g + 26) / 51; // why 26? why not 25?
        int b3 = (b + 26) / 51;
        return 36 * r3 + 6 * g3 + b3 + 16;
    }
}
