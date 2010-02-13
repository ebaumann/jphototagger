/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class ListCellRendererSavedSearches extends DefaultListCellRenderer {

    private static final Icon ICON              = AppLookAndFeel.getIcon("icon_search.png");
    private static final long serialVersionUID  = 3108457488446314020L;
    private              int  tempSelRow        = -1;

    public ListCellRendererSavedSearches() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel  label         = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean tempSelExists = tempSelRow>= 0;
        boolean isTempSelRow  = index == tempSelRow;

        label.setForeground(isTempSelRow || isSelected && !tempSelExists
                ? AppLookAndFeel.LIST_SELECTION_FOREGROUND
                : AppLookAndFeel.LIST_FOREGROUND
                );

        label.setBackground(isTempSelRow || isSelected && !tempSelExists
                ? AppLookAndFeel.LIST_SELECTION_BACKGROUND
                : AppLookAndFeel.LIST_BACKGROUND
                );

        label.setIcon(ICON);

        return label;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
