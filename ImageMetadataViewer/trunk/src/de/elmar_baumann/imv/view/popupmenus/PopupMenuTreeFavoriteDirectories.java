package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Menü für Aktionen in der Liste mit den Favoritenverzeichnissen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class PopupMenuTreeFavoriteDirectories extends JPopupMenu {

    private static final String ACTION_INSERT = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.Insert");
    private static final String ACTION_UPDATE = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.Update");
    private static final String ACTION_DELETE = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.Delete");
    private static final String ACTION_MOVE_UP = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.MoveUp");
    private static final String ACTION_MOVE_DOWN = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.MoveDown");
    private static final String ACTION_OPEN_IN_FOLDERS = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.OpenInFolders");
    private static final String ACTION_REFRESH = Bundle.getString(
            "PopupMenuListFavoriteDirectories.Action.Refresh");
    private final JMenuItem itemInsert = new JMenuItem(ACTION_INSERT);
    private final JMenuItem itemUpdate = new JMenuItem(ACTION_UPDATE);
    private final JMenuItem itemDelete = new JMenuItem(ACTION_DELETE);
    private final JMenuItem itemOpenInFolders = new JMenuItem(
            ACTION_OPEN_IN_FOLDERS);
    private final JMenuItem itemRefresh = new JMenuItem(ACTION_REFRESH);
    private final JMenuItem itemMoveUp = new JMenuItem(ACTION_MOVE_UP);
    private final JMenuItem itemMoveDown = new JMenuItem(ACTION_MOVE_DOWN);
    private FavoriteDirectory favoriteDirectory;
    public static final PopupMenuTreeFavoriteDirectories INSTANCE =
            new PopupMenuTreeFavoriteDirectories();

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemInsert() {
        return itemInsert;
    }

    public JMenuItem getItemUpdate() {
        return itemUpdate;
    }

    public JMenuItem getItemOpenInFolders() {
        return itemOpenInFolders;
    }

    public JMenuItem getItemRefresh() {
        return itemRefresh;
    }

    public FavoriteDirectory getFavoriteDirectory() {
        return favoriteDirectory;
    }

    public void setFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        this.favoriteDirectory = favoriteDirectory;
    }

    public synchronized void addActionListenerInsert(ActionListener listener) {
        itemInsert.addActionListener(listener);
    }

    public synchronized void addActionListenerDelete(ActionListener listener) {
        itemDelete.addActionListener(listener);
    }

    public synchronized void addActionListenerUpdate(ActionListener listener) {
        itemUpdate.addActionListener(listener);
    }

    public synchronized void addActionListenerRefresh(ActionListener listener) {
        itemRefresh.addActionListener(listener);
    }

    public synchronized void addActionListenerMoveUp(ActionListener listener) {
        itemMoveUp.addActionListener(listener);
    }

    public synchronized void addActionListenerMoveDown(ActionListener listener) {
        itemMoveDown.addActionListener(listener);
    }

    public synchronized void addActionListenerOpenInFolders(
            ActionListener listener) {
        itemOpenInFolders.addActionListener(listener);
    }

    public boolean isMoveUp(Object source) {
        return itemMoveUp.equals(source);
    }

    public boolean isMoveDown(Object source) {
        return itemMoveDown.equals(source);
    }

    public boolean isRefresh(Object source) {
        return itemRefresh.equals(source);
    }

    public void setEnabledInsert(boolean enabled) {
        itemInsert.setEnabled(enabled);
    }

    public void setEnabledDelete(boolean enabled) {
        itemDelete.setEnabled(enabled);
    }

    public void setEnabledUpdate(boolean enabled) {
        itemUpdate.setEnabled(enabled);
    }

    public void setEnabledMoveUp(boolean enabled) {
        itemMoveUp.setEnabled(enabled);
    }

    public void setEnabledMoveDown(boolean enabled) {
        itemMoveDown.setEnabled(enabled);
    }

    public void setEnabledOpenInFolders(boolean enabled) {
        itemOpenInFolders.setEnabled(enabled);
    }

    private PopupMenuTreeFavoriteDirectories() {
        init();
    }

    private void init() {
        add(itemInsert);
        add(itemUpdate);
        add(itemDelete);
        add(itemMoveUp);
        add(itemMoveDown);
        //add(itemOpenInFolders);
        add(itemRefresh);
        setIcons();
    }

    private void setIcons() {
        itemDelete.setIcon(AppIcons.getIcon("icon_remove.png"));
        itemInsert.setIcon(AppIcons.getIcon("icon_add.png"));
        itemMoveDown.setIcon(AppIcons.getIcon("icon_move_down.png"));
        itemMoveUp.setIcon(AppIcons.getIcon("icon_move_up.png"));
        itemOpenInFolders.setIcon(AppIcons.getIcon("icon_folder.png"));
        itemRefresh.setIcon(AppIcons.getIcon("icon_refresh.png"));
        itemUpdate.setIcon(AppIcons.getIcon("icon_edit.png"));
    }
}
