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
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class PopupMenuDirectories extends JPopupMenu {

    private final        JMenuItem            itemAddToFavorites          = new JMenuItem(Bundle.getString("PopupMenuDirectories.DisplayName.Action.AddToFavoriteDirectories"), AppLookAndFeel.getIcon("icon_favorite.png"));
    private final        JMenuItem            itemCreateDirectory         = new JMenuItem(Bundle.getString("PopupMenuDirectories.DisplayName.Action.CreateDirectory")         , AppLookAndFeel.getIcon("icon_folder_add.png"));
    private final        JMenuItem            itemRenameDirectory         = new JMenuItem(Bundle.getString("PopupMenuDirectories.DisplayName.Action.RenameDirectory")         , AppLookAndFeel.getIcon("icon_folder_rename.png"));
    private final        JMenuItem            itemDeleteDirectory         = new JMenuItem(Bundle.getString("PopupMenuDirectories.DisplayName.Action.DeleteDirectory")         , AppLookAndFeel.getIcon("icon_folder_delete.png"));
    private final        JMenuItem            itemRefresh                 = new JMenuItem(Bundle.getString("PopupMenuDirectories.DisplayName.Action.Refresh")                 , AppLookAndFeel.getIcon("icon_refresh.png"));
    private final        JMenuItem            menuItemExpandAllSubitems   = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemExpand"));
    private final        JMenuItem            menuItemCollapseAllSubitems = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemCollapse"));
    private              TreePath             path;
    private              String               directoryName;
    private              boolean              treeSelected                = false;
    public static final  PopupMenuDirectories INSTANCE                    = new PopupMenuDirectories();

    /**
     * Liefert den ausgewählten Verzeichnisnamen.
     * 
     * @return Name oder null, wenn nicht auf ein Verzeichnis geklickt wurde
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Setzt den Verzeichnisnamen, auf den geklickt wurde.
     * 
     * @param directoryName  Verzeichnisname
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
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

    public JMenuItem getMenuItemCollapseAllSubitems() {
        return menuItemCollapseAllSubitems;
    }

    public JMenuItem getMenuItemExpandAllSubitems() {
        return menuItemExpandAllSubitems;
    }

    private PopupMenuDirectories() {
        init();
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
        add(new JSeparator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new JSeparator());
        add(menuItemExpandAllSubitems);
        add(menuItemCollapseAllSubitems);
        add(new JSeparator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemCreateDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemDeleteDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRenameDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }
}
