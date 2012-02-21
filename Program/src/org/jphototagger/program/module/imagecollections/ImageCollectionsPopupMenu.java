package org.jphototagger.program.module.imagecollections;

import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon ICON_IMAGE_COLLECTION = AppLookAndFeel.getIcon("icon_imagecollection.png");
    public static final ImageCollectionsPopupMenu INSTANCE = new ImageCollectionsPopupMenu();
    private final JMenuItem itemDelete = new JMenuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Delete"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemRename = new JMenuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Rename"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemCreate = new JMenuItem(Bundle.getString(ImageCollectionsPopupMenu.class, "ImageCollectionsPopupMenu.DisplayName.Action.Create"), ICON_IMAGE_COLLECTION);
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
