package org.jphototagger.program.view.popupmenus;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.KeywordsPanel;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * Popup menu for the tree in a {@link KeywordsPanel}.
 *
 * @author Elmar Baumann
 */
public final class PopupMenuKeywordsTree extends JPopupMenu {
    private static final long serialVersionUID = 2140903704744267916L;
    public static final PopupMenuKeywordsTree INSTANCE = new PopupMenuKeywordsTree();
    private final JMenuItem itemAdd = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionAddKeyword"), AppLookAndFeel.ICON_NEW);
    private final JMenuItem itemAddToEditPanel = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionAddToEditPanel"));
    private final JMenuItem itemRemove = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionRemoveKeyword"), AppLookAndFeel.ICON_DELETE);
    private final JMenuItem itemRename = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionRenameKeyword"), AppLookAndFeel.ICON_RENAME);
    private final JMenuItem itemToggleReal = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionToggleReal"));
    private final JMenuItem itemRemoveFromEditPanel = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionRemoveFromEditPanel"));
    private final JMenuItem itemPaste = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionPaste"), AppLookAndFeel.ICON_PASTE);
    private final JMenuItem itemExpandAllSubitems = new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem itemDisplayImagesKw = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionDisplayImagesKw"));
    private final JMenuItem itemDisplayImages = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionDisplayImages"));
    private final JMenuItem itemCut = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionCut"), AppLookAndFeel.ICON_CUT);
    private final JMenuItem itemCopy = new JMenuItem(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.ActionCopy"), AppLookAndFeel.ICON_COPY);
    private final JMenuItem itemCollapseAllSubitems = new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemCollapse"));
    private JTree tree;
    private TreePath treePathAtMouseCursor;
    private TreePath[] selectedTreePaths;

    private PopupMenuKeywordsTree() {
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

        JMenu menuEdit = new JMenu(JptBundle.INSTANCE.getString("PopupMenuKeywordsTree.DisplayName.MenuEdit"));

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
