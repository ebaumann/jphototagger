package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.controller.keywords.tree.KeywordTreeNodesClipboard;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Do not use this class! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * Listens to mouse events in a {@link KeywordsPanel}'s tree and
 * shows the {@link PopupMenuKeywordsTree} when the popup trigger mouse
 * button is pressed.
 *
 * Also sets the selected tree path.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerKeywordsTree extends MouseListenerTree {
    private final PopupMenuKeywordsTree popupMenu = PopupMenuKeywordsTree.INSTANCE;

    public MouseListenerKeywordsTree() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            TreePath mouseCursorPath = TreeUtil.getTreePath(evt);
            boolean isHkNode = (mouseCursorPath != null) &&!TreeUtil.isRootItemPosition(evt)
                               && (mouseCursorPath.getLastPathComponent() instanceof DefaultMutableTreeNode);
            JTree tree = (JTree) evt.getSource();

            popupMenu.setTree(tree);
            setTreePathsToPopupMenu(tree, mouseCursorPath);
            setMenuItemsEnabled(isHkNode);
            popupMenu.getItemAdd().setEnabled(mouseCursorPath != null);
            popupMenu.show(tree, evt.getX(), evt.getY());
        }
    }

    private void setTreePathsToPopupMenu(JTree tree, TreePath mouseCursorPath) {
        popupMenu.setTreePath(mouseCursorPath);

        if (mouseCursorPath == null) {
            popupMenu.setTreePaths(null);

            return;
        }

        TreePath[] selPaths = tree.getSelectionPaths();

        popupMenu.setTreePaths((selPaths == null)
                               ? new TreePath[] { mouseCursorPath }
                               : selPaths);
    }

    private void setMenuItemsEnabled(boolean hkNode) {
        popupMenu.getItemRemove().setEnabled(hkNode);
        popupMenu.getItemRename().setEnabled(hkNode);
        popupMenu.getItemToggleReal().setEnabled(hkNode);
        popupMenu.getItemAddToEditPanel().setEnabled(hkNode);
        popupMenu.getItemRemoveFromEditPanel().setEnabled(hkNode);
        popupMenu.getItemCut().setEnabled(hkNode);
        popupMenu.getItemPaste().setEnabled(hkNode &&!KeywordTreeNodesClipboard.INSTANCE.isEmpty());
        popupMenu.getItemExpandAllSubitems().setEnabled(hkNode);
        popupMenu.getItemCollapseAllSubitems().setEnabled(hkNode);
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {

        // ignore
    }
}
