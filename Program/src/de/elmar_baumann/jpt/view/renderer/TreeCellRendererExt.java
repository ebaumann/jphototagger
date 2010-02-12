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
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-07
 */
public class TreeCellRendererExt extends DefaultTreeCellRenderer {

    private static final long serialVersionUID  = 7468243064122106211L;
    protected            int  popupHighLightRow = -1;

    protected void highlightRow(int row) {
        boolean rowHighlighted = popupHighLightRow >= 0;
        boolean popupRow       = row == popupHighLightRow;

        setOpaque(popupRow || rowHighlighted);
        
        if (popupRow) {
            setForeground(AppLookAndFeel.COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE);
            setBackground(AppLookAndFeel.COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE);
        } else if (rowHighlighted) {
            setForeground(AppLookAndFeel.COLOR_FOREGROUND_TREE_TEXT);
            setBackground(AppLookAndFeel.COLOR_BACKGROUND_TREE_TEXT);
        }
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
