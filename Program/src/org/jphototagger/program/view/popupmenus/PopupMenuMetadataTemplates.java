package org.jphototagger.program.view.popupmenus;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;

import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Popup menu for {@link MetadataTemplate}s.
 *
 * @author Elmar Baumann
 */
public final class PopupMenuMetadataTemplates extends JPopupMenu {
    private static final long serialVersionUID = 5476440706471574353L;
    public static final PopupMenuMetadataTemplates INSTANCE = new PopupMenuMetadataTemplates();
    private final JMenuItem itemSetToSelImages = new JMenuItem(Bundle.getString(PopupMenuMetadataTemplates.class, "PopupMenuMetadataTemplates.DisplayName.Action.SetToSelImages"), AppLookAndFeel.getIcon("icon_image.png"));
    private final JMenuItem itemRename = new JMenuItem(Bundle.getString(PopupMenuMetadataTemplates.class, "PopupMenuMetadataTemplates.DisplayName.Action.Rename"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemEdit = new JMenuItem(Bundle.getString(PopupMenuMetadataTemplates.class, "PopupMenuMetadataTemplates.DisplayName.Action.Edit"), AppLookAndFeel.ICON_EDIT);
    private final JMenuItem itemDelete = new JMenuItem(Bundle.getString(PopupMenuMetadataTemplates.class, "PopupMenuMetadataTemplates.DisplayName.Action.Delete"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemAdd = new JMenuItem(Bundle.getString(PopupMenuMetadataTemplates.class, "PopupMenuMetadataTemplates.DisplayName.Action.Add"), AppLookAndFeel.ICON_NEW);
    private JXList list;
    private int selIndex;

    private PopupMenuMetadataTemplates() {
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
