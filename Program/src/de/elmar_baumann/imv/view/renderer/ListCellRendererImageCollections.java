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
package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.model.ListModelImageCollections;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class ListCellRendererImageCollections extends DefaultListCellRenderer {

    private static final Icon ICON_DEFAULT =
            AppLookAndFeel.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Color COLOR_FOREGROUND_PREV_IMPORT = Color.BLUE;
    private static final Map<Object, Icon> ICON_OF_VALUE =
            new HashMap<Object, Icon>();
    private int popupHighLightRow = -1;

    {
        ICON_OF_VALUE.put(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT,
                AppLookAndFeel.getIcon("icon_card.png")); // NOI18N
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED,
                AppLookAndFeel.getIcon("icon_picked.png")); // NOI18N
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED,
                AppLookAndFeel.getIcon("icon_rejected.png")); // NOI18N
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (ListModelImageCollections.isSpecialCollection(value.toString())
                && !isSelected) {
            label.setForeground(COLOR_FOREGROUND_PREV_IMPORT);
        }
        if (index == popupHighLightRow) {
            label.setForeground(AppLookAndFeel.COLOR_FOREGROUND_POPUP_HIGHLIGHT_LIST);
            label.setBackground(AppLookAndFeel.COLOR_BACKGROUND_POPUP_HIGHLIGHT_LIST);
            label.setOpaque(true);
        }
        label.setIcon(getIconOfValue(value));
        return label;
    }

    private Icon getIconOfValue(Object value) {
        Icon icon = ICON_OF_VALUE.get(value);
        return icon == null
               ? ICON_DEFAULT
               : icon;
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
