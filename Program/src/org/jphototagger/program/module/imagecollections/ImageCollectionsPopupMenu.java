package org.jphototagger.program.module.imagecollections;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon ICON_IMAGE_COLLECTION = Icons.getIcon("icon_imagecollection.png");
    public static final ImageCollectionsPopupMenu INSTANCE = new ImageCollectionsPopupMenu();
    private final JMenuItem itemDelete = UiFactory.menuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Delete"), Icons.ICON_DELETE);
    private final JMenuItem itemRename = UiFactory.menuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Rename"), Icons.ICON_RENAME);
    private final JMenuItem itemCreate = UiFactory.menuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Create"), ICON_IMAGE_COLLECTION);
    private int itemIndex;

    private ImageCollectionsPopupMenu() {
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
        UiFactory.configure(this);
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
