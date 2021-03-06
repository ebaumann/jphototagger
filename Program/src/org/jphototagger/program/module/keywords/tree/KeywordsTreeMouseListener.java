package org.jphototagger.program.module.keywords.tree;

import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.program.event.listener.TreeMouseListener;

/**
 * Do not use this class! Instead extend
 * {@code org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@code org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * Listens to mouse events in a {@code KeywordsPanel}'s tree and
 * shows the {@code KeywordsTreePopupMenu} when the popup trigger mouse
 * button is pressed.
 *
 * Also sets the selected tree path.
 *
 * @author Elmar Baumann
 */
public final class KeywordsTreeMouseListener extends TreeMouseListener {

    private final KeywordsTreePopupMenu popupMenu = KeywordsTreePopupMenu.INSTANCE;

    public KeywordsTreeMouseListener() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            TreePath treePathAtMouseCursor = TreeUtil.getTreePath(evt);
            boolean isHkNode = (treePathAtMouseCursor != null) && !TreeUtil.isRootItemPosition(evt)
                    && (treePathAtMouseCursor.getLastPathComponent() instanceof DefaultMutableTreeNode);
            JTree tree = (JTree) evt.getSource();

            popupMenu.setTree(tree);
            setTreePathsToPopupMenu(tree, treePathAtMouseCursor);
            setMenuItemsEnabled(isHkNode);
            popupMenu.getItemAdd().setEnabled(treePathAtMouseCursor != null);
            popupMenu.show(tree, evt.getX(), evt.getY());
        }
    }

    private void setTreePathsToPopupMenu(JTree tree, TreePath treePathAtMouseCursor) {
        popupMenu.setTreePathAtMouseCursor(treePathAtMouseCursor);

        if (treePathAtMouseCursor == null) {
            popupMenu.setSelectedTreePaths(null);

            return;
        }

        TreePath[] selectionPaths = tree.getSelectionPaths();

        popupMenu.setSelectedTreePaths((selectionPaths == null)
                ? new TreePath[]{treePathAtMouseCursor}
                : selectionPaths);
    }

    private void setMenuItemsEnabled(boolean hkNode) {
        popupMenu.getItemRemove().setEnabled(hkNode);
        popupMenu.getItemRename().setEnabled(hkNode);
        popupMenu.getItemToggleReal().setEnabled(hkNode);
        popupMenu.getItemAddToEditPanel().setEnabled(hkNode);
        popupMenu.getItemRemoveFromEditPanel().setEnabled(hkNode);
        popupMenu.getItemCut().setEnabled(hkNode);
        popupMenu.getItemPaste().setEnabled(hkNode && !KeywordTreeNodesClipboard.INSTANCE.isEmpty());
        popupMenu.getItemExpandAllSubitems().setEnabled(hkNode);
        popupMenu.getItemCollapseAllSubitems().setEnabled(hkNode);
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {
        // ignore
    }
}
