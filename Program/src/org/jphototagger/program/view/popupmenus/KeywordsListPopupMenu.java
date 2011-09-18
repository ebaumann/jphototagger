package org.jphototagger.program.view.popupmenus;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;

import org.jdesktop.swingx.JXList;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * Popup menu for a keywords list, such as
 * {@code org.jphototagger.program.view.panels.KeywordsPanel#getList()}.
 *
 * @author Elmar Baumann
 */
public final class KeywordsListPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = -552638878495121120L;
    public static final KeywordsListPopupMenu INSTANCE = new KeywordsListPopupMenu();
    private final JMenuItem itemInsert = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.Action.Insert"), AppLookAndFeel.ICON_NEW);
    private final JMenuItem itemRename = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.Action.Rename"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemRemoveFromEditPanel = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.ActionRemoveFromEditPanel"));
    private final JMenuItem itemEditSynonyms = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.Action.EditSynonyms"), AppLookAndFeel.ICON_EDIT);
    private final JMenuItem itemDisplayImages = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.Action.DisplayImages"));
    private final JMenuItem itemDelete = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.Action.Delete"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemAddToEditPanel = new JMenuItem(Bundle.getString(KeywordsListPopupMenu.class, "KeywordsListPopupMenu.DisplayName.ActionAddToEditPanel"));
    private JXList list;
    private int selIndex;

    private KeywordsListPopupMenu() {
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

    public JMenuItem getItemDisplayImages() {
        return itemDisplayImages;
    }

    public JMenuItem getItemEditSynonyms() {
        return itemEditSynonyms;
    }

    public JMenuItem getItemInsert() {
        return itemInsert;
    }

    public JMenuItem getItemAddToEditPanel() {
        return itemAddToEditPanel;
    }

    public JMenuItem getItemRemoveFromEditPanel() {
        return itemRemoveFromEditPanel;
    }

    private void addItems() {
        add(itemInsert);
        add(itemRename);
        add(itemDelete);
        add(new Separator());
        add(itemEditSynonyms);
        add(new Separator());
        add(itemAddToEditPanel);
        add(itemRemoveFromEditPanel);
        add(new Separator());
        add(itemDisplayImages);
    }

    private void setAccelerators() {
        itemInsert.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemDelete.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemEditSynonyms.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcutWithAltDown(KeyEvent.VK_S));
        itemAddToEditPanel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_B));
        itemRemoveFromEditPanel.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_BACK_SPACE));
    }
}