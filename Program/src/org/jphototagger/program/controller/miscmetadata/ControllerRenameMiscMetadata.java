/*
 * @(#)ControllerRenameMiscMetadata.java    Created on 2010-03-15
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.miscmetadata;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.helper.RenameDeleteXmpValue;
import org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerRenameMiscMetadata extends ControllerMiscMetadata {

    private final JMenuItem itemRename;

    public ControllerRenameMiscMetadata(PopupMenuMiscMetadata popup) {
        if (popup == null) {
            throw new NullPointerException("popup == null");
        }

        itemRename = popup.getItemRename();
        popup.addListener(itemRename, this);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected void action(Column column, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        RenameDeleteXmpValue.rename(column, value);
    }
}
