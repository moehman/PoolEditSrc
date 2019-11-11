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
package poolimporter;

import java.io.PrintStream;
import java.util.Map;
import org.w3c.dom.Document;

/**
 *
 * @author moehman
 */
abstract class VTAbstObj implements VTObject {
    String name;
    int id;
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }
    @Override
    abstract public void read(ByteReader br);
    @Override
    abstract public void emitXML(Map<Integer, String> map, PrintStream out);
    @Override
    abstract public void appendDoc(Map<Integer, String> map, Document doc);
    @Override
    abstract public String getType();
}
