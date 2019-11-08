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

import color.ColorPalette;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Autlab
 */
public class PictureConverter {

    /** 
     * Creates a new instance of PictureConverter 
     */
    private PictureConverter() {
    }
    
    /**
     * Converts the specified image to base64 encoded string, which is the
     * embedded XML image format.
     */
    public static String convertToString(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int pictureData[] = new int[width * height];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pictureData[y * width + x] = ColorPalette.getPaletteIndex(image.getRGB(x, y)); 
            }
        }
        char[] base64 = convertToBase64(pictureData);
        
        return new String(base64);
    }    
    
    public static BufferedImage applyTransparencyAndReduceColors(BufferedImage image, 
            Color transparencyColor, boolean transparent, 
            boolean reduceImages, int colorDepth) {
        
        int width = image.getWidth();
        int height = image.getHeight();
        if (transparent) {
            BufferedImage image2 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (image.getRGB(x, y) == transparencyColor.getRGB()) {
                        image2.setRGB(x, y, 0);
                    } 
                    else if (reduceImages) {
                        image2.setRGB(x, y, 0xFF << 24 | ColorPalette.getColorRGB(image.getRGB(x, y), colorDepth)); 
                    }
                    else {
                        image2.setRGB(x, y, image.getRGB(x, y));
                    }
                }
            }
            image = image2;
        }
        else if (reduceImages) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {               
                    image.setRGB(x, y, ColorPalette.getColorRGB(image.getRGB(x, y), colorDepth)); 
                }
            }
        }
        return image;
    }
    
    /*
    public static void  reduceColorsToPalette(BufferedImage image, int colorDepth) {
        for (int y = 0, height = image.getHeight(); y < height; y++) {
            for (int x = 0, width = image.getWidth(); x < width; x++) {               
                image.setRGB(x, y, ColorPalette.getColorRGB(image.getRGB(x, y), colorDepth)); 
            }
        }
        System.out.println("CONVERTING");
    }
     */
    /*
    public static BufferedImage reduceColorsToPalette(BufferedImage image, int colorDepth) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {               
                output.setRGB(x, y, ColorPalette.getColorRGB(image.getRGB(x, y), colorDepth)); 
                //output.setRGB(x, y, ColorPalette.getNearestColor(image.getRGB(x, y))); 
            }
        }
        System.out.println("CONVERTING");
        return output;
    }
    */
    
    /**
     * Returns the character that is equalent to given index. 
     * e.g. index=0 -> returns A
     */
    public static char indexToBase64(int index) {
        if (index >= 0 && index < 26) {
            return (char) ('A' +  index);
        }
        else if (index >= 26 && index < 52) {
            return (char) ('a' - 26 + index);
        }
        else if (index >= 52 && index < 62) {
            return (char) ('0' - 52 + index);
        }
        else if (index == 62) {
            return '+';
        }
        else if (index == 63) {
            return '/';
        } 
        else {
            throw new IllegalArgumentException("The value of the index should " +
                    "be [0, 63] (" + index + ")");
        }
    }
    
    //converts data to base64, data must be bytes!!
    public static char[] convertToBase64(int data[]) {
        int length64 = (data.length * 4 + 2) / 3;
        char base64[] = new char[length64];
        
        for (int i = 0; i < length64; i++) {
            int highI = i * 3 / 4;
            int lowI = (i + 1) * 3 / 4;
            int high = data[highI];
            int low = lowI < data.length ? data[lowI] : 0; // pad with zeros
            int index;
            switch (i % 4) {
                case 0: // [H7][H6][H5][H4][H3][H2]
                    index = high >> 2;
                    break;
                case 1: // [H1][H0][L7][L6][L5][L4]
                    index = ((high & 0x03) << 4) + (low >> 4);
                    break;
                case 2: // [H3][H2][H1][H0][L7][L6]
                    index = ((high & 0x0F) << 2) + (low >> 6);
                    break;
                default: // [H5][H4][H3][H2][H1][H0]
                    index = high & 0x3F;
                    break;
            }
            base64[i] = indexToBase64(index);
        }        
        return base64;
    }
    
    /************************* Convert back **************************/
    
    public static int base64toIndex(char base64) {
        if (base64 >= 'A' && base64 <= 'Z') {
            return base64 - 'A';
        }
        else if (base64 >= 'a' && base64 <= 'z') {
            return base64 - 'a' + 26;
        }
        else if (base64 >= '0' && base64 <= '9') {
            return base64 - '0' + 52;
        }
        else if (base64 == '+') {
            return 62;
        }
        else if (base64 == '/') {
            return 63;
        }
        else {
            throw new IllegalArgumentException("The value of the base64 should " +
                    "be a valid character (" + base64 + ")");
        }
    }
    
    public static int[] convertFromBase64(char base64[]) {
        int length = base64.length * 3 / 4;
        int data[] = new int[length];
        
        for (int i = 0; i < length; i++) {
            int index = i * 4 / 3;
            int high = base64toIndex(base64[index]);
            int low = base64toIndex(base64[index + 1]);
            
            switch (i % 3) {
                case 0: // [H5][H4][H3][H2][H1][H0][L5][L4]
                    data[i] = (high << 2) + (low >> 4);
                    break;
                case 1: // [H3][H2][H1][H0][L5][L4][L3][L2]
                    data[i] = ((high & 0x0F) << 4) + (low >> 2);
                    break;
                default: // [H1][H0][L5][L4][L3][L2][L1][L0]
                    data[i] = ((high & 0x03) << 6) + low;
                    break;
            }
        }
        return data;
     }
}
