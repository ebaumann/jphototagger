/*
 * JPhotoTagger tags and finds images fast.
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
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-10-17
 */
public final class ListCellRendererImageCollections extends DefaultListCellRenderer {

    private static final Icon              ICON_DEFAULT                  = AppLookAndFeel.getIcon("icon_imagecollection.png");
    private static final Color             SPECIAL_COLLECTION_FOREGROUND = Color.BLUE;
    private static final Map<Object, Icon> ICON_OF_VALUE                 = new HashMap<Object, Icon>();
    private static final long              serialVersionUID              = -431048760716078334L;
    private              int               tempSelRow                    = -1;

    {
        ICON_OF_VALUE.put(ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT, AppLookAndFeel.getIcon("icon_card.png"));
        ICON_OF_VALUE.put(ListModelImageCollections.NAME_IMAGE_COLLECTION_PICKED     , AppLookAndFeel.getIcon("icon_picked.png"));
        ICON_OF_VALUE.put(ListModelImageCollections.NAME_IMAGE_COLLECTION_REJECTED   , AppLookAndFeel.getIcon("icon_rejected.png"));
    }

    public ListCellRendererImageCollections() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel  label             = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean specialCollection = ListModelImageCollections.isSpecialCollection(value.toString());
        boolean tempSelExists     = tempSelRow>= 0;
        boolean isTempSelRow      = index == tempSelRow;

        label.setForeground(isTempSelRow || isSelected && !tempSelExists
                ? AppLookAndFeel.getListSelectionForeground()
                : specialCollection
                ? SPECIAL_COLLECTION_FOREGROUND
                : AppLookAndFeel.getListForeground()
                );

        label.setBackground(isTempSelRow || isSelected && !tempSelExists
                ? AppLookAndFeel.getListSelectionBackground()
                : AppLookAndFeel.getListBackground()
                );

        label.setIcon(getIconOfValue(value));
        return label;
    }

    private Icon getIconOfValue(Object value) {
        Icon icon = ICON_OF_VALUE.get(value);
        return icon == null ? ICON_DEFAULT : icon;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
