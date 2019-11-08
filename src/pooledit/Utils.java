/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pooledit;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author mohman
 */
public class Utils {

    /**
     * Creates a new image by loading it from a file. Loading is done using
     * URLConnection which can load images inside the jar package.
     * @param path
     * @return
     */
    static public ImageIcon createImageIcon(String path) {
        ImageIcon icon = null;
        URL url = Utils.class.getResource(path);
        if (url != null) {
            try {
                URLConnection conn = url.openConnection();
                BufferedImage img = ImageIO.read(conn.getInputStream());
                icon = new ImageIcon(img);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return icon;
    }

    /**
     * Checks whether the options string contain the specified option. 
     * Example: optionsContain("bold+invertedflashing", "inverted");
     * @param options
     * @param option
     * @return
     */    
    static public boolean optionsContain(String options, String option) {
        int i = -1;
        while ((i = options.indexOf(option, i + 1)) >= 0) {
            if (i > 0 && options.charAt(i - 1) != '+') {
                continue;
            }
            int pos = i + option.length();
            int len = options.length();
            if (pos < len && options.charAt(pos) != '+') {
                continue;
            }
            return true;
        }
        return false;
    }
    /**
     * Removes the specified option from the options list. 
     * @param options
     * @param option
     * @return
     */
    static public String optionsRemove(String options, String option) {
        int i = -1;
        while ((i = options.indexOf(option, i + 1)) >= 0) {
            if (i > 0 && options.charAt(i - 1) != '+') {
                continue;
            }
            int pos = i + option.length();
            int len = options.length();
            if (pos < len && options.charAt(pos) != '+') {
                continue;
            }
            String s = (i > 0) ? options.substring(0, i - 1) : "";
            String t = (pos < len) ? options.substring(pos + 1) : "";
            return t.isEmpty() ? s : s.isEmpty() ? t : s + "+" + t;
        }
        return options;
    }
    
    /**
     * Returns true, if at least one of the later values equals the first 
     * value.
     * @param value
     * @param values
     * @return
     */
    public static boolean equals(String value, String ... values) {
        for (int i = 0, n = values.length; i < n; i++) {
            if (value.equals(values[i])) {
                return true;
            }
        }
        return false;
    }  

    /**
     * Returns the index to the later value that is equal to the first 
     * value. If there is no such value, this method returns -1.
     * @param name
     * @param names
     * @return
     */
    public static int indexEquals(String name, String ... names) {
	for (int i = 0, n = names.length; i < n; i++) {
	    if (name.equals(names[i])) {
		return i;
	    }
	}
	return -1;
    }
    
    /**
     * Check whether two objects are equals. Unlike in o1.equals(o2), both 
     * objects can be null, equalObjects(null, null) return true.    
     * @param o1
     * @param o2
     * @return
     */
    public static boolean equalObjects(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
    }

    /*
    public static void main(String[] args) {
        System.out.println("Testing...");
        
        System.out.println("optionsContain(ab+a, a): " + optionsContain("ab+a", "a"));
        System.out.println("optionsContain(ab+c, a): " + optionsContain("ab+c", "a"));
        System.out.println("optionsContain(ab+b, b): " + optionsContain("ab+b", "b"));
        System.out.println("optionsContain(ab+c, b): " + optionsContain("ab+c", "b"));
        
        System.out.println("optionsContain(a+ab, a): " + optionsContain("a+ab", "a"));
        System.out.println("optionsContain(c+ab, a): " + optionsContain("c+ab", "a"));
        System.out.println("optionsContain(b+ab, b): " + optionsContain("b+ab", "b"));
        System.out.println("optionsContain(c+ab, b): " + optionsContain("c+ab", "b"));
        
        System.out.println("optionsContain(ab+aa+a+ca, a): " + optionsContain("ab+aa+a+ca", "a"));
        System.out.println("optionsContain(ab+c+aa+ca, a): " + optionsContain("ab+c+aa+ca", "a"));
    }
    */
    /*
    public static void main(String[] args) {
        System.out.println("Testing...");
        
        System.out.println("optionsRemove(ab+a, a): " + optionsRemove("ab+a", "a"));
        System.out.println("optionsRemove(a+ab, a): " + optionsRemove("a+ab", "a"));
        System.out.println("optionsRemove(ab+a+ca, a): " + optionsRemove("ab+a+ca", "a"));
        System.out.println("optionsRemove(ba+a+ac, a): " + optionsRemove("ba+a+ac", "a"));
    }
    */
}
