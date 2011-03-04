package org.jphototagger.program.view.popupmenus;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class PopupMenuImageCollections extends JPopupMenu {
    private static final long serialVersionUID = -3446852358941591602L;
    public static final PopupMenuImageCollections INSTANCE = new PopupMenuImageCollections();
    private final JMenuItem itemDelete =
        new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuImageCollections.DisplayName.Action.Delete"),
                      AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemRename =
        new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuImageCollections.DisplayName.Action.Rename"),
                      AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemCreate =
        new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuImageCollections.DisplayName.Action.Create"),
                      AppLookAndFeel.getIcon("icon_imagecollection.png"));
    private int itemIndex;

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
        setAccelerators();
    }

    private void addItems() {
        add(itemCreate);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemDelete.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemCreate.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
    }
}
