/*
 * @(#)PopupMenuSavedSearches.java    Created on 2008-08-31
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

package org.jphototagger.program.view.popupmenus;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author  Elmar Baumann
 */
public final class PopupMenuSavedSearches extends JPopupMenu {
    private static final long                  serialVersionUID =
        3540766100829834971L;
    public static final PopupMenuSavedSearches INSTANCE         =
        new PopupMenuSavedSearches();
    private final JMenuItem itemDelete =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuSavedSearches.DisplayName.Action.Delete"), AppLookAndFeel
                        .ICON_DELETE);
    private final JMenuItem itemEdit =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuSavedSearches.DisplayName.Action.Edit"), AppLookAndFeel
                        .ICON_EDIT);
    private final JMenuItem itemCreate =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuSavedSearches.DisplayName.Action.New"), AppLookAndFeel
                        .ICON_NEW);
    private final JMenuItem itemRename =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuSavedSearches.DisplayName.Action.Rename"), AppLookAndFeel
                        .ICON_RENAME);
    private transient SavedSearch savedSearch;

    private PopupMenuSavedSearches() {
        init();
    }

    public JMenuItem getItemCreate() {
        return itemCreate;
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemEdit() {
        return itemEdit;
    }

    /**
     * Setzt die gespeicherte Suche.
     *
     * @param savedSearch Gespeicherte Suche. Default: null.
     */
    public void setSavedSearch(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }

    /**
     * Liefert die gespeicherte Suche.
     *
     * @return Gespeicherte Suche
     */
    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemCreate.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemEdit.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemDelete.setAccelerator(
            KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
    }
}
