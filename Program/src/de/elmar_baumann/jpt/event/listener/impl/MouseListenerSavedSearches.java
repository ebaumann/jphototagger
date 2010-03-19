/*
 * @(#)MouseListenerSavedSearches.java    Created on 2008-08-31
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

package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuSavedSearches;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * Beobachtet Mausklicks im JTree mit gespeicherten Suchen.
 *
 * @author  Elmar Baumann
 */
public final class MouseListenerSavedSearches extends MouseAdapter {
    private final PopupMenuSavedSearches popupMenu =
        PopupMenuSavedSearches.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            int     index  = ListUtil.getItemIndex(e);
            JList   list   = (JList) e.getSource();
            boolean isItem = index >= 0;

            if (isItem) {
                Object element = list.getModel().getElementAt(index);

                if (element instanceof SavedSearch) {
                    SavedSearch savedSearch = (SavedSearch) element;

                    popupMenu.setSavedSearch(savedSearch);
                }
            }

            popupMenu.getItemEdit().setEnabled(isItem);
            popupMenu.getItemDelete().setEnabled(isItem);
            popupMenu.getItemRename().setEnabled(isItem);
            popupMenu.show(list, e.getX(), e.getY());
        }
    }
}
