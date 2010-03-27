/*
 * @(#)PopupMenuImageCollections.java    Created on 2008-09-08
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
public final class PopupMenuImageCollections extends JPopupMenu {
    private static final long                     serialVersionUID =
        -3446852358941591602L;
    public static final PopupMenuImageCollections INSTANCE         =
        new PopupMenuImageCollections();
    private final JMenuItem itemDelete =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuImageCollections.DisplayName.Action.Delete"), AppLookAndFeel
                        .ICON_DELETE);
    private final JMenuItem itemRename =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuImageCollections.DisplayName.Action.Rename"), AppLookAndFeel
                        .ICON_RENAME);
    private final JMenuItem itemCreate =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuImageCollections.DisplayName.Action.Create"), AppLookAndFeel
                        .getIcon("icon_imagecollection.png"));
    public int itemIndex;

    private PopupMenuImageCollections() {
        init();
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemCreate() {
        return itemCreate;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemCreate.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
    }
}
