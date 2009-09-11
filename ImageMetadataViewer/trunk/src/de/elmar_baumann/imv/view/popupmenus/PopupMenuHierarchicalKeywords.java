package de.elmar_baumann.imv.view.popupmenus;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

/**
 * Popup menu for the tree in a {@link HierarchicalKeywordsPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
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
    private static final String DISPLAY_NAME_ACTION_CUT =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionCut"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_PASTE =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionPaste"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_DISPLAY_IMAGES =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionDisplayImages"); // NOI18N
    private static final String DISPLAY_NAME_ACTION_DISPLAY_IMAGES_KW =
            Bundle.getString(
            "PopupMenuHierarchicalKeywords.DisplayName.ActionDisplayImagesKw"); // NOI18N
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
    private final JMenuItem menuItemCut =
            new JMenuItem(DISPLAY_NAME_ACTION_CUT);
    private final JMenuItem menuItemPaste =
            new JMenuItem(DISPLAY_NAME_ACTION_PASTE);
    private final JMenuItem menuItemDisplayImages =
            new JMenuItem(DISPLAY_NAME_ACTION_DISPLAY_IMAGES);
    private final JMenuItem menuItemDisplayImagesKw =
            new JMenuItem(DISPLAY_NAME_ACTION_DISPLAY_IMAGES_KW);
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

    public JMenuItem getMenuItemCut() {
        return menuItemCut;
    }

    public JMenuItem getMenuItemPaste() {
        return menuItemPaste;
    }

    public JMenuItem getMenuItemDisplayImages() {
        return menuItemDisplayImages;
    }

    public JMenuItem getMenuItemDisplayImagesKw() {
        return menuItemDisplayImagesKw;
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
        add(menuItemAddToEditPanel);
        add(menuItemRemoveFromEditPanel);
        addSeparator();
        add(menuItemAdd);
        add(menuItemRemove);
        add(menuItemRename);
        add(menuItemToggleReal);
        addSeparator();
        add(menuItemCut);
        add(menuItemPaste);
        addSeparator();
        add(menuItemDisplayImages);
        add(menuItemDisplayImagesKw);
    }

    private void setIcons() {
        menuItemAdd.setIcon(AppLookAndFeel.getIcon("icon_add.png")); // NOI18N
        menuItemRemove.setIcon(AppLookAndFeel.getIcon("icon_remove.png")); // NOI18N
        menuItemRename.setIcon(AppLookAndFeel.getIcon("icon_rename.png")); // NOI18N
        menuItemToggleReal.setIcon(AppLookAndFeel.getIcon(
                "icon_keyword_real_helper.png")); // NOI18N
        menuItemAddToEditPanel.setIcon(AppLookAndFeel.getIcon("icon_edit.png")); // NOI18N
        menuItemRemoveFromEditPanel.setIcon(AppLookAndFeel.getIcon("icon_delete.png")); // NOI18N
        menuItemCut.setIcon(AppLookAndFeel.getIcon("icon_cut_to_clipboard.png")); // NOI18N
        menuItemPaste.setIcon(AppLookAndFeel.getIcon("icon_paste_from_clipboard.png")); // NOI18N
        menuItemDisplayImages.setIcon(AppLookAndFeel.getIcon("icon_thumbnails.png")); // NOI18N
        menuItemDisplayImagesKw.setIcon(AppLookAndFeel.getIcon("icon_thumbnails.png")); // NOI18N
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
        menuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                java.awt.event.InputEvent.CTRL_MASK));
        menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                java.awt.event.InputEvent.CTRL_MASK));
    }
}
