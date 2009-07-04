package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Popupmenü für gespeicherte Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class PopupMenuSavedSearches extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_DELETE =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Delete");
    private static final String DISPLAY_NAME_ACTION_EDIT =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Edit");
    private static final String DISPLAY_NAME_ACTION_NEW =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.New");
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString("PopupMenuSavedSearches.DisplayName.Action.Rename");
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
        itemCreate.setIcon(AppIcons.getIcon("icon_add.png"));
        itemDelete.setIcon(AppIcons.getIcon("icon_remove.png"));
        itemEdit.setIcon(AppIcons.getIcon("icon_edit.png"));
        itemRename.setIcon(AppIcons.getIcon("icon_rename.png"));
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
