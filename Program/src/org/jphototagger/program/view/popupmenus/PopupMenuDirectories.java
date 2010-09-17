/*
 * @(#)PopupMenuDirectories.java    Created on 2008-09-24
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

package org.jphototagger.program.view.popupmenus;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.event.util.KeyEventUtil;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class PopupMenuDirectories extends JPopupMenu {
    private static final long                serialVersionUID =
        4574458335277932153L;
    public static final PopupMenuDirectories INSTANCE         =
        new PopupMenuDirectories();
    private final JMenuItem itemAddToFavorites =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuDirectories.DisplayName.Action.AddToFavoriteDirectories"), AppLookAndFeel
                        .getIcon("icon_favorite.png"));
    private final JMenuItem itemCreateDirectory =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuDirectories.DisplayName.Action.CreateDirectory"), AppLookAndFeel
                        .getIcon("icon_folder_new.png"));
    private final JMenuItem itemRenameDirectory =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuDirectories.DisplayName.Action.RenameDirectory"), AppLookAndFeel
                        .ICON_RENAME);
    private final JMenuItem itemRefresh =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuDirectories.DisplayName.Action.Refresh"), AppLookAndFeel
                        .ICON_REFRESH);
    private final JMenuItem itemDeleteDirectory =
        new JMenuItem(
            JptBundle.INSTANCE
                .getString(
                    "PopupMenuDirectories.DisplayName.Action.DeleteDirectory"), AppLookAndFeel
                        .ICON_DELETE);
    private final JMenuItem menuItemExpandAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem menuItemCollapseAllSubitems =
        new JMenuItem(
            JptBundle.INSTANCE.getString(
                "MouseListenerTreeExpand.ItemCollapse"));
    private boolean  treeSelected = false;
    private File     directory;
    private TreePath path;

    private PopupMenuDirectories() {
        init();
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setTreePath(TreePath path) {
        this.path = path;
    }

    public TreePath getTreePath() {
        return path;
    }

    public JMenuItem getItemAddToFavorites() {
        return itemAddToFavorites;
    }

    public JMenuItem getItemCreateDirectory() {
        return itemCreateDirectory;
    }

    public JMenuItem getItemRenameDirectory() {
        return itemRenameDirectory;
    }

    public JMenuItem getItemDeleteDirectory() {
        return itemDeleteDirectory;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return menuItemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return menuItemExpandAllSubitems;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }

    private void addItems() {
        add(itemAddToFavorites);
        add(new Separator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new Separator());
        add(menuItemExpandAllSubitems);
        add(menuItemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemCreateDirectory.setAccelerator(
            KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteDirectory.setAccelerator(
            KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameDirectory.setAccelerator(
            KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
    }
}
