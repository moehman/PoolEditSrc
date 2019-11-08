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

import static pooledit.Definitions.*;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import multidom.SingleDOM;
import objecttree.ObjectTreeCellRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mohman
 */
public class DragLabel extends JLabel {
    
    private final String type;
    private final SingleDOM libdoc;
    
    /**
     * Constructor.
     * @param type
     * @param libdoc
     */
    public DragLabel(String type, SingleDOM libdoc) {
        super(ObjectTreeCellRenderer.getIcon(type));
        this.type = type;
        this.libdoc = libdoc;
    }
    
    /** 
     * Gets the XML description of the first element with a matching type.
     * @return
     */
    public String getXML() {
        Document doc = libdoc.actual();
        List<Element> list = Tools.getChildElementList(doc.getDocumentElement());
        for (Element e : list) {
            if (e.getNodeName().equals(type)) {
                Map namemap = Tools.createNameMap(doc);
                return Tools.writeToStringNoDec(Tools.createMergedElementRecursive(e, namemap));
            }
        }
        return null;
    }
}
