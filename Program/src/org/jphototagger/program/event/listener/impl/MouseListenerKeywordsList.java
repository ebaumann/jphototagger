/*
 * @(#)MouseListenerKeywordsList.java    Created on 2010-01-07
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

import javax.swing.JList;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerKeywordsList extends MouseListenerList {
    private final PopupMenuKeywordsList popup = PopupMenuKeywordsList.INSTANCE;

    @Override
    protected void showPopup(JList list, int x, int y) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        popup.setSelIndex(getIndex());
        popup.setList(list);
        setEnabled();
        popup.show(list, x, y);
    }

    private void setEnabled() {
        boolean editable =
            GUI.INSTANCE.getAppPanel().getEditMetadataPanels().isEditable();

        popup.getItemAddToEditPanel().setEnabled(editable);
        popup.getItemRemoveFromEditPanel().setEnabled(editable);
    }
}
