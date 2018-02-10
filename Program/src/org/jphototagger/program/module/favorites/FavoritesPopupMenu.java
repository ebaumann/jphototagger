package org.jphototagger.program.module.favorites;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu} does.
 *
 * @author Elmar Baumann
 */
public final class FavoritesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon ICON_ARROW_DOWN = Icons.getIcon("icon_arrow_down.png");
    private static final ImageIcon ICON_ARROW_UP = Icons.getIcon("icon_arrow_up.png");
    private static final ImageIcon ICON_FOLDER = Icons.getIcon("icon_folder.png");
    private static final ImageIcon ICON_FOLDER_NEW = Icons.getIcon("icon_folder_new.png");
    public static final FavoritesPopupMenu INSTANCE = new FavoritesPopupMenu();
    private final JMenuItem itemInsertFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.InsertFavorite"), AppLookAndFeel.ICON_NEW);
    private final JMenuItem itemUpdateFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.UpdateFavorite"), AppLookAndFeel.ICON_EDIT);
    private final JMenuItem itemRenameFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.RenameFilesystemFolder"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemRefresh = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.Refresh"), AppLookAndFeel.ICON_REFRESH);
    private final JMenuItem itemOpenInFolders = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.OpenInFolders"), ICON_FOLDER);
    private final JMenuItem itemMoveUp = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.MoveUp"), ICON_ARROW_UP);
    private final JMenuItem itemMoveDown = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.MoveDown"), ICON_ARROW_DOWN);
    private final JMenuItem itemExpandAllSubitems = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.ItemExpand"));
    private final JMenuItem itemDeleteFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.DeleteFilesystemFolder"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemDeleteFavorite = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.DeleteFavorite"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemCollapseAllSubitems = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.ItemCollapse"));
    private final JMenuItem itemAddFilesystemFolder = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.DisplayName.Action.AddFilesystemFolder"), ICON_FOLDER_NEW);
    private final JMenuItem itemOpenInDesktop = new JMenuItem(Bundle.getString(FavoritesPopupMenu.class, "FavoritesPopupMenu.Action.OpenInDesktop"));
    private transient Favorite favoriteDirectory;
    private TreePath treePath;

    private FavoritesPopupMenu() {
        init();
    }

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

    JMenuItem getItemOpenInDesktop() {
        return itemOpenInDesktop;
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

    private void init() {
        addItems();
        setAccelerators();
        setItemsEnabled();
        listen();
    }

    private void addItems() {
        add(itemInsertFavorite);
        add(itemUpdateFavorite);
        add(itemDeleteFavorite);
        add(itemMoveUp);
        add(itemMoveDown);
        add(new Separator());
        add(itemOpenInDesktop);
        add(itemOpenInFolders);
        add(new Separator());
        add(itemAddFilesystemFolder);
        add(itemRenameFilesystemFolder);
        add(itemDeleteFilesystemFolder);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemUpdateFavorite.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemInsertFavorite.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_I));
        itemOpenInFolders.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_O));
        itemAddFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteFavorite.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemDeleteFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameFilesystemFolder.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemRefresh.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F5));
    }

    private void setItemsEnabled() {
        setDeleteDirectoryEnabled();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent e) {
        if (PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES.equals(e.getKey())) {
            setDeleteDirectoryEnabled();
        }
    }

    private void setDeleteDirectoryEnabled() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean enabled = prefs != null && prefs.containsKey(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                : true;
        itemDeleteFilesystemFolder.setEnabled(enabled);
    }
}
