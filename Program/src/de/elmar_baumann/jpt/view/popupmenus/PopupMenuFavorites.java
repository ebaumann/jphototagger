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
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * Menü für Aktionen in der Liste mit den Favoritenverzeichnissen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class PopupMenuFavorites extends JPopupMenu {

    private static final long               serialVersionUID           = -7344945087460562958L;
    private final        JMenuItem          itemInsertFavorite         = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.InsertFavorite")        , AppLookAndFeel.getIcon("icon_add.png"));
    private final        JMenuItem          itemUpdateFavorite         = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.UpdateFavorite")        , AppLookAndFeel.getIcon("icon_edit.png"));
    private final        JMenuItem          itemDeleteFavorite         = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.DeleteFavorite")        , AppLookAndFeel.getIcon("icon_remove.png"));
    private final        JMenuItem          itemOpenInFolders          = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.OpenInFolders")         , AppLookAndFeel.getIcon("icon_folder.png"));
    private final        JMenuItem          itemRefresh                = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.Refresh")               , AppLookAndFeel.getIcon("icon_refresh.png"));
    private final        JMenuItem          itemMoveUp                 = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.MoveUp")                , AppLookAndFeel.getIcon("icon_move_up.png"));
    private final        JMenuItem          itemAddFilesystemFolder    = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.AddFilesystemFolder"));
    private final        JMenuItem          itemRenameFilesystemFolder = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.RenameFilesystemFolder"));
    private final        JMenuItem          itemDeleteFilesystemFolder = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.DeleteFilesystemFolder"));
    private final        JMenuItem          itemMoveDown               = new JMenuItem(Bundle.getString("PopupMenuFavorites.DisplayName.Action.MoveDown")              , AppLookAndFeel.getIcon("icon_move_down.png"));
    private final        JMenuItem          itemExpandAllSubitems      = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemExpand"));
    private final        JMenuItem          itemCollapseAllSubitems    = new JMenuItem(Bundle.getString("MouseListenerTreeExpand.ItemCollapse"));
    private              TreePath           treePath;
    private              Favorite  favoriteDirectory;
    public static final  PopupMenuFavorites INSTANCE                   = new PopupMenuFavorites();

    public JMenuItem getItemDeleteFavorite() {
        return itemDeleteFavorite;
    }

    public JMenuItem getItemInsertFavorite() {
        return itemInsertFavorite;
    }

    public JMenuItem getItemUpdateFavorite() {
        return itemUpdateFavorite;
    }

    public JMenuItem getItemOpenInFolders() {
        return itemOpenInFolders;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public JMenuItem getItemAddFilesystemFolder() {
        return itemAddFilesystemFolder;
    }

    public JMenuItem getItemDeleteFilesystemFolder() {
        return itemDeleteFilesystemFolder;
    }

    public JMenuItem getItemMoveDown() {
        return itemMoveDown;
    }

    public JMenuItem getItemMoveUp() {
        return itemMoveUp;
    }

    public JMenuItem getItemRenameFilesystemFolder() {
        return itemRenameFilesystemFolder;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePath(TreePath treePath) {
        this.treePath = treePath;
    }

    public Favorite getFavorite() {
        return favoriteDirectory;
    }

    public void setFavoriteDirectory(Favorite favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    private PopupMenuFavorites() {
        init();
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemInsertFavorite);
        add(itemUpdateFavorite);
        add(itemDeleteFavorite);
        add(itemMoveUp);
        add(itemMoveDown);
        add(new JSeparator());
        add(itemOpenInFolders);
        add(new JSeparator());
        add(itemAddFilesystemFolder);
        add(itemRenameFilesystemFolder);
        add(itemDeleteFilesystemFolder);
        add(new JSeparator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        add(new JSeparator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemUpdateFavorite        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        itemInsertFavorite        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        itemOpenInFolders         .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        itemAddFilesystemFolder   .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        itemDeleteFavorite        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemDeleteFilesystemFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRenameFilesystemFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemRefresh               .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }
}
