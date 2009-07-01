package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popupmenü für den Tree mit Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public final class PopupMenuImageCollections extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_DELETE =
            Bundle.getString("PopupMenuImageCollections.DisplayName.Action.Delete");
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString("PopupMenuImageCollections.DisplayName.Action.Rename");
    private final JMenuItem itemDelete =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE);
    private final JMenuItem itemRename =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME);
    private String imageCollectionName;
    public static final PopupMenuImageCollections INSTANCE =
            new PopupMenuImageCollections();

    private PopupMenuImageCollections() {
        addItems();
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    /**
     * Setzt den Namen der Bildsamlung.
     * 
     * @param name Name. Default: null.
     */
    public void setImageCollectionName(String name) {
        imageCollectionName = name;
    }

    /**
     * Liefert den Namen der gespeicherten Bildsamlung.
     * 
     * @return Name
     */
    public String getImageCollectionName() {
        return imageCollectionName;
    }

    private void addItems() {
        add(itemRename);
        add(itemDelete);
        setIcons();
    }

    private void setIcons() {
        itemDelete.setIcon(AppIcons.getIcon("icon_remove.png"));
        itemRename.setIcon(AppIcons.getIcon("icon_rename.png"));
    }
}
