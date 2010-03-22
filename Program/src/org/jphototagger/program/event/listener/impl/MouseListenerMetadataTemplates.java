/*
 * @(#)MouseListenerMetadataTemplates.java    Created on 2010-01-07
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuMetadataTemplates;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerMetadataTemplates extends MouseListenerList {
    public MouseListenerMetadataTemplates() {
        setPopupAlways(true);
    }

    @Override
    protected void showPopup(JList list, int x, int y) {
        assert list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION;
        PopupMenuMetadataTemplates.INSTANCE.setSelIndex(getIndex());
        PopupMenuMetadataTemplates.INSTANCE.setList(list);
        enableItems();
        PopupMenuMetadataTemplates.INSTANCE.show(list, x, y);
    }

    private void enableItems() {
        boolean clickOnItem = getIndex() >= 0;

        PopupMenuMetadataTemplates.INSTANCE.getItemDelete().setEnabled(
            clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemEdit().setEnabled(
            clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemRename().setEnabled(
            clickOnItem);
        PopupMenuMetadataTemplates.INSTANCE.getItemSetToSelImages().setEnabled(
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectionCount()
            > 0);
    }
}
