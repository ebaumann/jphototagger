package org.jphototagger.lib.swing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class TreeExpandCollapseAllAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    public static final String SELECTED_TEXT = Bundle.getString(TreeExpandCollapseAllAction.class, "TreeExpandCollapseAllAction.ToggleButton.Selected.Text");
    public static final String NOT_SELECTED_TEXT = Bundle.getString(TreeExpandCollapseAllAction.class, "TreeExpandCollapseAllAction.ToggleButton.NotSelected.Text");
    private final JTree tree;
    private final JToggleButton toggleButton;

    public TreeExpandCollapseAllAction(JToggleButton toggleButton, JTree tree) {
        super(NOT_SELECTED_TEXT);

        if (toggleButton == null) {
            throw new NullPointerException("toggleButton == null");
        }

        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        this.toggleButton = toggleButton;
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent ignored) {
        boolean expand = toggleButton.isSelected();

        TreeUtil.expandAll(tree, expand);

        // When root handle is invisible, all nodes would disappear when
        // collapsing it
        if (!expand && !tree.isRootVisible()) {
            TreeModel model = tree.getModel();
            if (model != null) {
                Object root = model.getRoot();
                if (root instanceof TreeNode) {
                    TreeNode rootNode = (TreeNode) root;
                    tree.expandPath(new TreePath(rootNode));
                }
            }
        }

        toggleButton.setText(expand
                ? SELECTED_TEXT
                : NOT_SELECTED_TEXT);
    }
}
