package org.jphototagger.program.module.directories;

import java.awt.event.KeyEvent;
import java.io.File;
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
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu} does.
 *
 * @author Elmar Baumann
 */
public final class DirectoriesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    public static final ImageIcon ICON_FAVORITE = Icons.getIcon("icon_favorite.png");
    public static final ImageIcon ICON_FOLDER_NEW = Icons.getIcon("icon_folder_new.png");
    private final JMenuItem itemAddToFavorites = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.AddToFavoriteDirectories"), ICON_FAVORITE);
    private final JMenuItem itemCreateDirectory = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.CreateDirectory"), ICON_FOLDER_NEW);
    private final JMenuItem itemRenameDirectory = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.RenameDirectory"), Icons.ICON_RENAME);
    private final JMenuItem itemRefresh = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.Refresh"), Icons.ICON_REFRESH);
    private final JMenuItem itemDeleteDirectory = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.DisplayName.Action.DeleteDirectory"), Icons.ICON_DELETE);
    private final JMenuItem itemExpandAllSubitems = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemExpand"));
    private final JMenuItem itemCollapseAllSubitems = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.ItemCollapse"));
    private final JMenuItem itemOpenInDesktop = UiFactory.menuItem(Bundle.getString(DirectoriesPopupMenu.class, "DirectoriesPopupMenu.Action.OpenInDesktop"));
    private boolean treeSelected = false;
    private File directory;
    private TreePath path;
    public static final DirectoriesPopupMenu INSTANCE = new DirectoriesPopupMenu();

    private DirectoriesPopupMenu() {
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
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    public JMenuItem getItemOpenInDesktop() {
        return itemOpenInDesktop;
    }

    private void init() {
        org.jphototagger.resources.UiFactory.configure(this);
        addItems();
        setAccelerators();
        setItemsEnabled();
        listen();
    }

    public boolean isTreeSelected() {
        return treeSelected;
    }

    public void setTreeSelected(boolean treeSelected) {
        this.treeSelected = treeSelected;
    }

    private void addItems() {
        add(itemAddToFavorites);
        add(itemOpenInDesktop);
        add(new Separator());
        add(itemCreateDirectory);
        add(itemRenameDirectory);
        add(itemDeleteDirectory);
        add(new Separator());
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        add(new Separator());
        add(itemRefresh);
    }

    private void setAccelerators() {
        itemOpenInDesktop.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_O));
        itemCreateDirectory.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemDeleteDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRenameDirectory.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
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
        itemDeleteDirectory.setEnabled(enabled);
    }
}
