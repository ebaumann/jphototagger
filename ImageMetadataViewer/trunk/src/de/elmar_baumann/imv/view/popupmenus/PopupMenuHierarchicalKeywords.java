package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author  Elmar Baumann <ebaumann@feitsch.de>
 * @version 2009-07-29
 */
public final class PopupMenuHierarchicalKeywords extends JPopupMenu {

    private static final String DISPLAY_NAME_ACTION_ADD_KEYWORD =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionAddKeyword"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_REMOVE =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionRemoveKeyword"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_RENAME =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionRenameKeyword"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_TOGGLE_REAL =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionToggleReal"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_ADD_TO_EDIT_PANEL =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionAddToEditPanel"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_REMOVE_FROM_EDIT_PANEL =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionRemoveFromEditPanel"); // NOI18N
    private final JMenuItem menuItemAdd =
            new JMenuItem(DISPLAY_NAME_ACTION_ADD_KEYWORD);
    private final JMenuItem menuItemAddToEditPanel =
            new JMenuItem(DISPLAY_NAME_ACTION_ADD_TO_EDIT_PANEL);
    private final JMenuItem menuItemRemove =
            new JMenuItem(DISPLAY_NAME_ACTION_REMOVE);
    private final JMenuItem menuItemRename =
            new JMenuItem(DISPLAY_NAME_ACTION_RENAME);
    private final JMenuItem menuItemToggleReal =
            new JMenuItem(DISPLAY_NAME_ACTION_TOGGLE_REAL);
    private final JMenuItem menuItemRemoveFromEditPanel =
            new JMenuItem(DISPLAY_NAME_ACTION_REMOVE_FROM_EDIT_PANEL);
    public static final PopupMenuHierarchicalKeywords INSTANCE =
            new PopupMenuHierarchicalKeywords();
    private TreePath path;

    public JMenuItem getMenuItemAdd() {
        return menuItemAdd;
    }

    public JMenuItem getMenuItemRemove() {
        return menuItemRemove;
    }

    public JMenuItem getMenuItemRename() {
        return menuItemRename;
    }

    public JMenuItem getMenuItemToggleReal() {
        return menuItemToggleReal;
    }

    public JMenuItem getMenuItemAddToEditPanel() {
        return menuItemAddToEditPanel;
    }

    public JMenuItem getMenuItemRemoveFromEditPanel() {
        return menuItemRemoveFromEditPanel;
    }

    private PopupMenuHierarchicalKeywords() {
        init();
    }

    public void setTreePath(TreePath path) {
        this.path = path;
    }

    public TreePath getTreePath() {
        return path;
    }

    private void init() {
        addItems();
        setIcons();
        setAccelerators();
    }

    private void addItems() {
        add(menuItemAdd);
        add(menuItemRemove);
        add(menuItemRename);
        add(menuItemToggleReal);
        add(menuItemAddToEditPanel);
        add(menuItemRemoveFromEditPanel);
    }

    private void setIcons() {
        menuItemAdd.setIcon(AppIcons.getIcon("icon_add.png")); // NOI18N
        menuItemRemove.setIcon(AppIcons.getIcon("icon_remove.png")); // NOI18N
        menuItemRename.setIcon(AppIcons.getIcon("icon_rename.png")); // NOI18N
        menuItemToggleReal.setIcon(AppIcons.getIcon(
                "icon_keyword_real_helper.png")); // NOI18N
        menuItemAddToEditPanel.setIcon(AppIcons.getIcon("icon_edit.png")); // NOI18N
        menuItemRemoveFromEditPanel.setIcon(AppIcons.getIcon("icon_delete.png")); // NOI18N
    }

    private void setAccelerators() {
        menuItemAdd.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menuItemRemove.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemRename.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menuItemToggleReal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                java.awt.event.InputEvent.CTRL_MASK));
        menuItemAddToEditPanel.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        menuItemRemoveFromEditPanel.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
    }
}
