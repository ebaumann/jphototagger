package org.jphototagger.program.module.keywords.tree;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.MiscMetadataPopupMenu} does.
 *
 * Popup menu for the tree in a {@code KeywordsPanel}.
 *
 * @author Elmar Baumann
 */
public final class KeywordsTreePopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;
    public static final KeywordsTreePopupMenu INSTANCE = new KeywordsTreePopupMenu();
    private final JMenuItem itemAdd = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionAddKeyword"), Icons.ICON_NEW);
    private final JMenuItem itemAddToEditPanel = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionAddToEditPanel"));
    private final JMenuItem itemRemove = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionRemoveKeyword"), Icons.ICON_DELETE);
    private final JMenuItem itemRename = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionRenameKeyword"), Icons.ICON_RENAME);
    private final JMenuItem itemToggleReal = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionToggleReal"));
    private final JMenuItem itemRemoveFromEditPanel = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionRemoveFromEditPanel"));
    private final JMenuItem itemPaste = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionPaste"), Icons.ICON_PASTE);
    private final JMenuItem itemExpandAllSubitems = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.ItemExpand"));
    private final JMenuItem itemDisplayImagesKw = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionDisplayImagesKw"));
    private final JMenuItem itemDisplayImages = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionDisplayImages"));
    private final JMenuItem itemCut = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionCut"), Icons.ICON_CUT);
    private final JMenuItem itemCopy = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.ActionCopy"), Icons.ICON_COPY);
    private final JMenuItem itemCollapseAllSubitems = UiFactory.menuItem(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.ItemCollapse"));
    private JTree tree;
    private TreePath treePathAtMouseCursor;
    private TreePath[] selectedTreePaths;

    private KeywordsTreePopupMenu() {
        init();
    }

    public JMenuItem getItemAdd() {
        return itemAdd;
    }

    public JMenuItem getItemRemove() {
        return itemRemove;
    }

    public JMenuItem getItemRename() {
        return itemRename;
    }

    public JMenuItem getItemToggleReal() {
        return itemToggleReal;
    }

    public JMenuItem getItemAddToEditPanel() {
        return itemAddToEditPanel;
    }

    public JMenuItem getItemRemoveFromEditPanel() {
        return itemRemoveFromEditPanel;
    }

    public JMenuItem getItemCut() {
        return itemCut;
    }

    public JMenuItem getItemCopy() {
        return itemCopy;
    }

    public JMenuItem getItemPaste() {
        return itemPaste;
    }

    public JMenuItem getItemDisplayImages() {
        return itemDisplayImages;
    }

    public JMenuItem getItemDisplayImagesKw() {
        return itemDisplayImagesKw;
    }

    public JMenuItem getItemCollapseAllSubitems() {
        return itemCollapseAllSubitems;
    }

    public JMenuItem getItemExpandAllSubitems() {
        return itemExpandAllSubitems;
    }

    public void setTreePathAtMouseCursor(TreePath path) {
        this.treePathAtMouseCursor = path;
    }

    public TreePath getTreePathAtMouseCursor() {
        return treePathAtMouseCursor;
    }

    public void setSelectedTreePaths(TreePath[] treePaths) {
        selectedTreePaths = (treePaths == null)
                ? null
                : Arrays.copyOf(treePaths, treePaths.length);
    }

    public TreePath[] getSelectedTreePaths() {
        return (selectedTreePaths == null)
                ? null
                : Arrays.copyOf(selectedTreePaths, selectedTreePaths.length);
    }

    public boolean isMouseCursorInSelection() {
        if (treePathAtMouseCursor == null || selectedTreePaths == null) {
            return false;
        }

        for (TreePath treePath : selectedTreePaths) {
            if (treePath.equals(treePathAtMouseCursor)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMouseOverTreePath() {
        return treePathAtMouseCursor != null;
    }

    public JTree getTree() {
        return tree;
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    private void init() {
        addItems();
        setAccelerators();
    }

    private void addItems() {
        add(itemAddToEditPanel);
        add(itemRemoveFromEditPanel);

        JMenu menuEdit = org.jphototagger.resources.UiFactory.menu(Bundle.getString(KeywordsTreePopupMenu.class, "KeywordsTreePopupMenu.DisplayName.MenuEdit"));

        menuEdit.add(itemAdd);
        menuEdit.add(itemRemove);
        menuEdit.add(itemRename);
        menuEdit.add(itemToggleReal);
        menuEdit.addSeparator();
        menuEdit.add(itemCopy);
        menuEdit.add(itemCut);
        menuEdit.add(itemPaste);
        addSeparator();
        add(menuEdit);
        addSeparator();
        add(itemExpandAllSubitems);
        add(itemCollapseAllSubitems);
        addSeparator();
        add(itemDisplayImages);
        add(itemDisplayImagesKw);
    }

    private void setAccelerators() {
        itemAdd.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_N));
        itemRemove.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_DELETE));
        itemRename.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_F2));
        itemToggleReal.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_R));
        itemAddToEditPanel.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_B));
        itemRemoveFromEditPanel.setAccelerator(KeyEventUtil.getKeyStroke(KeyEvent.VK_BACK_SPACE));
        itemCopy.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_C));
        itemCut.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_X));
        itemPaste.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_V));
    }
}
