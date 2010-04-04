/*
 * @(#)MouseListenerImageCollections.java    Created on 2008-09-08
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

import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerImageCollections extends MouseAdapter {
    private final PopupMenuImageCollections popupMenu =
        PopupMenuImageCollections.INSTANCE;

    @Override
    public void mousePressed(MouseEvent evt) {
        int index = ListUtil.getItemIndex(evt);

        popupMenu.setItemIndex(index);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            JList   list                = (JList) evt.getSource();
            boolean isItem              = index >= 0;
            boolean isSpecialCollection = isSpecialCollection(list, index);

            popupMenu.getItemDelete().setEnabled(isItem &&!isSpecialCollection);
            popupMenu.getItemRename().setEnabled(isItem &&!isSpecialCollection);
            popupMenu.show(list, evt.getX(), evt.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        ((JList) evt.getSource()).setCursor(Cursor.getDefaultCursor());
    }

    private boolean isSpecialCollection(JList list, int index) {
        if (index < 0) {
            return false;
        }

        Object o = list.getModel().getElementAt(index);

        if (o != null) {
            return ListModelImageCollections.isSpecialCollection(o.toString());
        }

        return false;
    }
}
