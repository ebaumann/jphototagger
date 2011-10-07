package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.keywords.KeywordsPanel;

/**
 * Abstract root class for some controllers in this packages.  Contains some
 * common helper methods.
 *
 * @author  Martin Pohlack
 */
public abstract class KeywordsController implements ActionListener, KeyListener {

    private final KeywordsPanel panel;

    abstract protected boolean myKey(KeyEvent evt);

    abstract protected void localAction(List<DefaultMutableTreeNode> nodes);

    abstract protected boolean canHandleMultipleNodes();

    public KeywordsController(KeywordsPanel _panel) {
        if (_panel == null) {
            throw new NullPointerException("_panel == null");
        }

        panel = _panel;
        listen();
    }

    private void listen() {

        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuKeywords()
        panel.getTree().addKeyListener(this);
    }

    protected KeywordsPanel getHKPanel() {
        return panel;
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (myKey(evt)) {
            List<DefaultMutableTreeNode> selNodes = getSelectedNodes(evt);

            if ((selNodes != null) && !selNodes.isEmpty() && checkNodeCount(selNodes)) {
                localAction(selNodes);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        List<DefaultMutableTreeNode> selNodes = getSelectedNodes(evt);

        if ((selNodes != null) && !selNodes.isEmpty() && checkNodeCount(selNodes)) {
            localAction(selNodes);
        }
    }

    protected List<DefaultMutableTreeNode> getSelectedNodes(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        TreePath[] selectedTreePaths = KeywordsTreePopupMenu.INSTANCE.isMouseCursorInSelection()
                ? KeywordsTreePopupMenu.INSTANCE.getSelectedTreePaths()
                : KeywordsTreePopupMenu.INSTANCE.isMouseOverTreePath()
                ? new TreePath[]{KeywordsTreePopupMenu.INSTANCE.getTreePathAtMouseCursor()}
                : null;

        if (selectedTreePaths == null) {
            return null;
        }

        List<DefaultMutableTreeNode> selectedNodes = new ArrayList<DefaultMutableTreeNode>();

        for (TreePath selectedTreePath : selectedTreePaths) {
            Object node = selectedTreePath.getLastPathComponent();

            if (node instanceof DefaultMutableTreeNode) {
                selectedNodes.add((DefaultMutableTreeNode) node);
            }
        }

        return selectedNodes;
    }

    protected List<DefaultMutableTreeNode> getSelectedNodes(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (evt.getComponent() instanceof JTree) {
            JTree tree = (JTree) evt.getComponent();

            if (tree.isSelectionEmpty()) {
                return null;
            }

            List<DefaultMutableTreeNode> selNodes = new ArrayList<DefaultMutableTreeNode>();

            for (TreePath selPath : tree.getSelectionPaths()) {
                Object node = selPath.getLastPathComponent();

                if (node instanceof DefaultMutableTreeNode) {
                    selNodes.add((DefaultMutableTreeNode) node);
                }
            }

            return selNodes;
        }

        return null;
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }

    protected boolean ensureNoChild(List<DefaultMutableTreeNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes == null");
        }

        int size = nodes.size();

        if (size <= 1) {
            return true;
        }

        for (int i = 0; i < size; i++) {
            DefaultMutableTreeNode parent = nodes.get(i);

            for (int j = 0; j < size; j++) {
                if (j != i) {
                    DefaultMutableTreeNode node = nodes.get(j);

                    if (TreeUtil.isAbove(parent, node)) {
                        String message = Bundle.getString(KeywordsController.class, "ControllerDeleteKeywords.Tree.Error.IsChild");
                        MessageDisplayer.error(null, message);

                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkNodeCount(Collection<DefaultMutableTreeNode> coll) {
        if (!canHandleMultipleNodes() && (coll.size() > 1)) {
            String message = Bundle.getString(KeywordsController.class, "KeywordsController.Error.MultiSelection");
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }
}
