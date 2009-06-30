package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.resource.Bundle;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popupmenü für gespeicherte Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public final class PopupMenuSavedSearches extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_DELETE =
            Bundle.getString("PopupMenuTreeSavedSearches.Action.Delete");
    private static final String DISPLAY_NAME_ACTION_EDIT =
            Bundle.getString("PopupMenuTreeSavedSearches.Action.Edit");
    private static final String DISPLAY_NAME_ACTION_NEW =
            Bundle.getString("PopupMenuTreeSavedSearches.Action.New");
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString("PopupMenuTreeSavedSearches.Action.Rename");
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
        addItems();
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

    private void addItems() {
        add(itemCreate);
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
        setIcons();
    }

    private void setIcons() {
        itemCreate.setIcon(AppIcons.getIcon("icon_add.png"));
        itemDelete.setIcon(AppIcons.getIcon("icon_remove.png"));
        itemEdit.setIcon(AppIcons.getIcon("icon_edit.png"));
        itemRename.setIcon(AppIcons.getIcon("icon_rename.png"));
    }
}
