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
package de.elmar_baumann.jpt.view.popupmenus;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Popupmenü für gespeicherte Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-31
 */
public final class PopupMenuSavedSearches extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_DELETE =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Delete"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_EDIT =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Edit"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_NEW =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.New"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Rename"); // NOI18N
    private final JMenuItem itemDelete =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE);
    private final JMenuItem itemEdit = new JMenuItem(DISPLAY_NAME_ACTION_EDIT);
    private final JMenuItem itemCreate = new JMenuItem(DISPLAY_NAME_ACTION_NEW);
    private final JMenuItem itemRename =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME);
    private SavedSearch savedSearch;
    public static final PopupMenuSavedSearches INSTANCE =
            new PopupMenuSavedSearches();

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
        setIcons();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setIcons() {
        itemCreate.setIcon(AppLookAndFeel.getIcon("icon_add.png")); // NOI18N
        itemDelete.setIcon(AppLookAndFeel.getIcon("icon_remove.png")); // NOI18N
        itemEdit.setIcon(AppLookAndFeel.getIcon("icon_edit.png")); // NOI18N
        itemRename.setIcon(AppLookAndFeel.getIcon("icon_rename.png")); // NOI18N
    }

    private void setAccelerators() {
        itemCreate.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemEdit.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        itemDelete.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRename.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    }
}
