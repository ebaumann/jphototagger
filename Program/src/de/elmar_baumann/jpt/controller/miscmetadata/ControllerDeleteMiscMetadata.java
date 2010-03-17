/*
 * @(#)ControllerDeleteMiscMetadata.java    2010-03-15
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

package de.elmar_baumann.jpt.controller.miscmetadata;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.helper.RenameDeleteXmpValue;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuMiscMetadata;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteMiscMetadata extends ControllerMiscMetadata {
    private final JMenuItem itemDelete =
        PopupMenuMiscMetadata.INSTANCE.getItemDelete();

    public ControllerDeleteMiscMetadata() {
        listenToActionsOf(itemDelete);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == itemDelete;
    }

    @Override
    protected void action(Column column, String value) {
        RenameDeleteXmpValue.delete(column, value);
    }
}
