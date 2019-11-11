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
package pooledit;

import java.io.File;
import java.util.regex.Matcher;
import javax.swing.JFileChooser;

/**
 *
 * @author moehman
 */
public class FileTools {
    /**
     * Strip redundant separators and converts them to File.separator in
     * the process.
     * @param path
     * @return
     */
    static public String stripRedundantSeparators(String path) {
        return path.replaceAll("(?<!^)(\\\\|/){2,}",
                Matcher.quoteReplacement(File.separator));
    }
    
    /**
     * Joins several paths together.
     * @param s
     * @return
     */
    static public String joinPaths(String ... s) {
        final String SEP = Character.toString(File.separatorChar);
        String rv = String.join(SEP, s);
        return stripRedundantSeparators(rv);
    }

    /**
     * Gets the path relative to the current working directory. 
     * @param path
     * @return
     */
    static public String getRelativePath(String path) {
        File cwd = new File("").getAbsoluteFile();
        String rv = cwd.toURI().relativize(new File(path).toURI()).getPath();
        return rv; //stripRedundantSeparators(rv);
    }
    
    /**
     * Gets a new JFileChooser which starts from the current working directory.
     * @return
     */
    static public JFileChooser getNewFileChooser() {
        File cwd = new File(".");
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(cwd);
        return fc;
    }
}
