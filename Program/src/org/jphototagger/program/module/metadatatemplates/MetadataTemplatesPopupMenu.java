package org.jphototagger.program.module.metadatatemplates;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * Popup menu for {@code MetadataTemplate}s.
 *
 * @author Elmar Baumann
 */
public final class MetadataTemplatesPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    private static final ImageIcon ICON_IMAGE = Icons.getIcon("icon_image.png");
    public static final MetadataTemplatesPopupMenu INSTANCE = new MetadataTemplatesPopupMenu();
    private final JMenuItem itemSetToSelImages = UiFactory.menuItem(Bundle.getString(MetadataTemplatesPopupMenu.class, "MetadataTemplatesPopupMenu.DisplayName.Action.SetToSelImages"), ICON_IMAGE);
    private final JMenuItem itemRename = UiFactory.menuItem(Bundle.getString(MetadataTemplatesPopupMenu.class, "MetadataTemplatesPopupMenu.DisplayName.Action.Rename"), Icons.ICON_RENAME);
    private final JMenuItem itemEdit = UiFactory.menuItem(Bundle.getString(MetadataTemplatesPopupMenu.class, "MetadataTemplatesPopupMenu.DisplayName.Action.Edit"), Icons.ICON_EDIT);
    private final JMenuItem itemDelete = UiFactory.menuItem(Bundle.getString(MetadataTemplatesPopupMenu.class, "MetadataTemplatesPopupMenu.DisplayName.Action.Delete"), Icons.ICON_DELETE);
    private final JMenuItem itemAdd = UiFactory.menuItem(Bundle.getString(MetadataTemplatesPopupMenu.class, "MetadataTemplatesPopupMenu.DisplayName.Action.Add"), Icons.ICON_NEW);
    private JXList list;
    private int selIndex;

    private MetadataTemplatesPopupMenu() {
        addItems();
        setAccelerators();
    }

    public int getSelIndex() {
        return selIndex;
    }

    public void setSelIndex(int selIndex) {
        this.selIndex = selIndex;
    }

    public JXList getList() {
        return list;
    }

    public void setList(JXList list) {
        this.list = list;
    }

    public JMenuItem getItemDelete() {
        return itemDelete;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemAdd() {
        return itemAdd;
    }

    public JMenuItem getItemEdit() {
        return itemEdit;
    }

    public JMenuItem getItemSetToSelImages() {
        return itemSetToSelImages;
    }

    private void addItems() {
        add(itemSetToSelImages);
        add(new Separator());
        add(itemAdd);
        add(new Separator());
        add(itemEdit);
        add(itemRename);
        add(itemDelete);
    }

    private void setAccelerators() {
        itemSetToSelImages.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_INSERT));
        itemAdd.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemEdit.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_E));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemDelete.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
    }
}
