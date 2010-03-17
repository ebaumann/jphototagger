/*
 * @(#)ListCellRendererExt.java    2010-01-07
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;

/**
 *
 *
 * @author  Elmar Baumann
 */
public class ListCellRendererExt extends DefaultListCellRenderer {
    private static final long serialVersionUID = 7531004273695822498L;
    protected int             tempSelRow       = -1;

    public ListCellRendererExt() {
        setOpaque(true);
    }

    protected void setColors(int index, boolean selected, JLabel label) {
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow  = index == tempSelRow;

        label.setForeground((isTempSelRow || (selected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionForeground()
                            : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (selected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionBackground()
                            : AppLookAndFeel.getListBackground());
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
