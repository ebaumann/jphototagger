package org.jphototagger.lib.componentutil;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Content;

/**
 * Contains whatever is selected in a {@code JTree}.
 * <p>
 * Handles temporary mouse selections (left click on a not selected tree item),
 * if the tree has a popup menu
 * ({@code javax.swing.JComponent#getComponentPopupMenu()}).
 * <p>
 * The content are arrays of {@code TreePath} instances or null.
 *
 * @author Elmar Baumann
 */
public final class TreeSelectionContent extends Content<TreePath[]> implements TreeSelectionListener, MouseListener {

    private final JTree tree;
    private final JPopupMenu popup;

    public TreeSelectionContent(JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.tree = tree;
        popup = tree.getComponentPopupMenu();
        tree.addMouseListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        setTreePaths(tree.getSelectionPaths());
    }

    private void setTreePaths(TreePath[] paths) {
        if (paths == null) {
            remove();
        } else {
            set(paths);
        }
    }

    private void displayPopup(MouseEvent evt) {
        if (TreeUtil.isMouseOverTreePath(evt, tree)) {
            setTreePaths(evt);
            popup.show(tree, evt.getX(), evt.getY());
        }
    }

    private boolean setTreePaths(MouseEvent evt) {
        assert TreeUtil.isMouseOverTreePath(evt, tree);

        TreePath mouseCursorPath = TreeUtil.getTreePath(evt);

        if (tree.isPathSelected(mouseCursorPath)) {
            setTreePaths(tree.getSelectionPaths());
        } else {
            setTreePaths(new TreePath[]{mouseCursorPath});
        }

        return true;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if ((popup != null) && SystemUtil.isWindows() && MouseEventUtil.isPopupTrigger(evt)) {
            displayPopup(evt);
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if ((popup != null) && SystemUtil.isMac() && MouseEventUtil.isPopupTrigger(evt)) {
            displayPopup(evt);
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        // ignore
    }
}
