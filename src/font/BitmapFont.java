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
package font;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.net.URL;

/**
 *
 * @author mohman
 */
public class BitmapFont {
    
    static public final int JUSTIFICATION_LEFT = 0;
    static public final int JUSTIFICATION_MIDDLE = 1;
    static public final int JUSTIFICATION_RIGHT = 2;
    
    static private final String[] NAMES = {
        "6x8", "8x8", "8x12",
        "12x16", "16x16", "16x24",
        "24x32", "32x32", "32x48",
        "48x64", "64x64", "64x96",
        "96x128", "128x128", "128x192"
    };
    
    /*
    static private final String[] TYPES = {
        "latin1", "latin9"
    };
     */
    
    static private final String[] STYLES = {
        "Bold", "Crossed Out", "Underlined", "Italic",
        "Inverted", "Flashing Inverted", "Flashing Background"
    };

    static private final Dimension[] DIMENSIONS = 
	new Dimension[NAMES.length];
    
    static private final BufferedImage[] BITMAPS = 
	new BufferedImage[NAMES.length];

    static private final String PATH = "/images/";

    static private final String[] FILENAMES = {
        "font6x8.png", "font8x8_.png", "font8x12_.png",
        "font12x16_.png", "font16x16_.png", "font16x24_.png",
        "font24x32_.png", "font32x32_.png", "font32x48_.png",
        "font48x64_.png", "font64x64_.png", "font64x96_.png",
        "font96x128_.png", "font128x128_.png", "font128x192_.png"
    };

    /** Needed for changing font color */
    static private byte[][] lookupdata = new byte[4][256];
    static private final LookupTable ltab = new ByteLookupTable(0, lookupdata);
    static private final LookupOp lop = new LookupOp(ltab, null);
    
    static {
	// create dimensions
	for (int i = 0, n = NAMES.length; i < n; i++) {
	    Scanner s = new Scanner(NAMES[i]).useDelimiter("x");
	    DIMENSIONS[i] = new Dimension(s.nextInt(), s.nextInt());
	}
        // use identity transform to the alpha channel
	for (int i = 0; i < 256; i++) {
	    lookupdata[3][i] = (byte) i; 
	}
    }
    
    /**
     * Gets font names (6x8, 8x8, etc).
     * @return
     */
    static public String[] getNames() {
        return NAMES;
    }
    
    /*
    static public String[] getTypes() {
        return TYPES;
    }
    */
    
    /**
     * Gets the specified bitmap.
     * @param index
     * @return
     * @throws java.io.IOException
     */
    static public BufferedImage getBitmap(int index) throws IOException {
        // load bitmaps lazily
        if (BITMAPS[index] != null) {
            return BITMAPS[index];
        }
        else {
            URL url = BitmapFont.class.getResource(PATH + FILENAMES[index]);
            if (url != null) {
                URLConnection conn = url.openConnection();
                BITMAPS[index] = ImageIO.read(conn.getInputStream());
            }
            return BITMAPS[index];
        }
    }
    
    /**
     * Maps font names to font indices.
     * FIXME: name can't be a number
     * @param font
     * @return
     */
    static public int nameToIndex(String font) {
	for (int i = 0, n = NAMES.length; i < n; i++) {
	    if (font.equals(NAMES[i])) {
		return i;
	    }
	}
	throw new RuntimeException();
    }

    /**
     * Maps font indices to font dimensions.
     * @param index
     * @return
     */
    static public Dimension indexToDimension(int index) {
        return nameToDimension(indexToName(index));
    }
    
    /*
     * Works, but is very slow for bigger fonts!
     *
    static public void changeFontColor(String font, Color fontColor) throws IOException {       
        int fontIndex = nameToIndex(font);
        BufferedImage img = getBitmap(fontIndex);
        for(int y = 0; y < img.getHeight(); y++)
            for(int x = 0; x < img.getWidth(); x++) {
                int alpha = img.getRGB(x, y) >>> 24;
                if(alpha == 0xFF) { 
                    int color = fontColor.getRGB();
                    img.setRGB(x,y, color);
                }
            }
    }
    */

    /**
     * 
     * @param font
     * @param color
     * @throws java.io.IOException
     */
    static public void changeFontColor(String font, Color color) throws IOException {
        // map every color to the target color
        Arrays.fill(lookupdata[0], (byte) color.getRed());
        Arrays.fill(lookupdata[1], (byte) color.getGreen());
        Arrays.fill(lookupdata[2], (byte) color.getBlue());
    }
    
    /**
     * Maps font indices to font names.
     * @param index
     * @return
     */
    static public String indexToName(int index) {
	return NAMES[index];
    }

    /**
     * Maps font name to font dimension.
     * @param font
     * @return
     */
    static public Dimension nameToDimension(String font) {
	return DIMENSIONS[nameToIndex(font)];
    }

    /**
     * Paints character with the specified font (name).
     * @param g
     * @param ch
     * @param font the name of the font
     * @param x
     * @param y
     * @throws java.io.IOException
     */
    static public void paintChar(Graphics g, char ch,
				 String font, int x, int y) throws IOException {
	paintChar(g, ch, nameToIndex(font), x, y);
    }

    /**
     * Paints character with the specified font (index).
     * @param g
     * @param ch
     * @param font the index of the font
     * @param x
     * @param y
     * @throws java.io.IOException
     */
    static public void paintChar(Graphics g, char ch, 
				 int font, int x, int y) throws IOException {
	Graphics2D gfx = (Graphics2D) g;
	Dimension d = DIMENSIONS[font];
	int sx = d.width * (ch % 16);
	int sy = d.height * (ch / 16);
	BufferedImage img = 
	    getBitmap(font).getSubimage(sx, sy, d.width, d.height);
       
	gfx.drawImage(img, lop, x, y);
    }
   
    /**
     * Paints text with the specified font (name).
     * @param g
     * @param str
     * @param font the name of the font
     * @param x
     * @param y
     * @throws java.io.IOException
     */
    static public void paintString(Graphics g, String str, String font, 
            int x, int y) throws IOException {
        
	paintString(g, str, nameToIndex(font), x, y);
    }
    
    /**
     * Paints text with the specified font (index).
     * @param g
     * @param str
     * @param font the index of the font
     * @param x
     * @param y
     * @throws java.io.IOException
     */
    static public void paintString(Graphics g, String str, 
				   int font, int x, int y) throws IOException {
	
	int w = DIMENSIONS[font].width;
	for (int i = 0, n = str.length(); i < n; i++) {
	    paintChar(g, str.charAt(i), font, x + w * i, y);
	}	
    }
    
    /**
     * Paints text with the specified font (name) and justification.
     * @param g
     * @param str
     * @param font the name of the font
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    static public void paintString(Graphics g, String str, String font, 
            int x, int y, int width, int justification) throws IOException {
        
        paintString(g, str, nameToIndex(font), x, y, width, justification);
    }
    
    /**
     * Paints text with the specified font (index) and justification.
     * @param g
     * @param str
     * @param font the index of the font
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    static public void paintString(Graphics g, String str, int font, 
            int x, int y, int width, int justification) throws IOException {
  
        int offset = width - str.length() * DIMENSIONS[font].width;
        switch (justification) {
            case JUSTIFICATION_LEFT:
                paintString(g, str, font, x, y);
                break;
            case JUSTIFICATION_MIDDLE:
                paintString(g, str, font, x + offset / 2, y);
                break;
            case JUSTIFICATION_RIGHT:
                paintString(g, str, font, x + offset, y);
                break;
        }
    }
    
    /**
     * Wraps text to the next line after the specified width.
     * @param g
     * @param str
     * @param font the name of the font
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    static public void paintWrapString(Graphics g, String str, String font, 
            int x, int y, int width, int justification) throws IOException {
        
        paintWrapString(g, str, nameToIndex(font), x, y, width, justification);
    }

    /**
     * Wraps text to the next line after the specified width.
     * @param g
     * @param str
     * @param font the index of the font
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    static public void paintWrapString(Graphics g, String str, int font, 
            int x, int y, int width, int justification) throws IOException {
	
	Dimension dim = DIMENSIONS[font];
	int cols = width / dim.width;
	int len;
	int i;

	while ((len = str.length()) > cols) {
	    // insert line break at the last possible white space
	    for (i = cols; i >= 0; i--) {
		if (Character.isWhitespace(str.charAt(i))) {
                    paintString(g, str.substring(0, i), font, x, y, width, justification);
		    str = str.substring(i + 1);
		    break;
		}
	    }
	    // force line break
	    if (i < 0) {
		paintString(g, str.substring(0, cols), font, x, y, width, justification);
		str = str.substring(cols);
	    }
	    y += dim.height;
	}
        paintString(g, str, font, x, y, width, justification);        
    }
   
    private final String name;
    private Color color;
    
    /**
     * Constructor.
     * @param name
     * @param color
     */
    public BitmapFont(String name, Color color) { 
        this.name = name;
        this.color = color;
    }
    
    /**
     * Sets font color.
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Gets font color.
     * @return
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Gets font dimension.
     * @return
     */
    public Dimension getDimension(){
        return nameToDimension(name);
    }
    
    /**
     * Paints the specified value with this font.
     * @param g
     * @param value
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    public void paint(Graphics g, String value, 
            int x, int y, int width, int justification) throws IOException {
        
        changeFontColor(name, color);
        paintString(g, value, name, x, y, width, justification);
    }
    
    /**
     * Paints the specified value with this font.
     * @param g
     * @param value
     * @param x
     * @param y
     * @param width
     * @param justification
     * @throws java.io.IOException
     */
    public void paintWrap(Graphics g, String value, 
            int x, int y, int width, int justification) throws IOException {
        
        changeFontColor(name, color);
        paintWrapString(g, value, name, x, y, width, justification);
    }
}
