package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Popupmenü für den Tree mit Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-08
 */
public final class PopupMenuImageCollections extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_DELETE =
            Bundle.getString(
            "PopupMenuImageCollections.DisplayName.Action.Delete"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString(
            "PopupMenuImageCollections.DisplayName.Action.Rename"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_CREATE =
            Bundle.getString(
            "PopupMenuImageCollections.DisplayName.Action.Create"); // NOI18N
    private final JMenuItem itemDelete =
            new JMenuItem(DISPLAY_NAME_ACTION_DELETE);
    private final JMenuItem itemRename =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME);
    private final JMenuItem itemCreate =
            new JMenuItem(DISPLAY_NAME_ACTION_CREATE);
    public static final PopupMenuImageCollections INSTANCE =
            new PopupMenuImageCollections();
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
        setIcons();
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemRename);
        add(itemDelete);
    }

    private void setIcons() {
        itemDelete.setIcon(AppIcons.getIcon("icon_remove.png")); // NOI18N
        itemRename.setIcon(AppIcons.getIcon("icon_rename.png")); // NOI18N
        itemCreate.setIcon(AppIcons.getIcon("icon_add.png")); // NOI18N
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemRename.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemCreate.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
    }
}
