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

import java.awt.Component;
import javax.swing.Icon;
import net.infonode.docking.View;

/**
 *
 * @author mohman
 */
public class DynamicView extends View {
    private int id;

    /**
    * Constructor.
    * @param title     the view title
    * @param icon      the view icon
    * @param component the view component
    * @param id        the view id
    */
    public DynamicView(String title, Icon icon, Component component, int id) {
        super(title, icon, component);
        this.id = id;
    }

    /**
    * Returns the view id.
    * @return the view id
    */
    public int getId() {
        return id;
    }
}
