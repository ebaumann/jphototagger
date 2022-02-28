package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to {@code AppPanel#getToggleButtonSelKeywords()} and expands or
 * collapses all keyword nodes on action.
 *
 * @author Elmar Baumann
 */
public final class ToggleButtonExpandKeywordsTreeController implements ActionListener {

    private final JToggleButton toggleButtonExpandAllNodesSelKeywords = GUI.getAppPanel().getToggleButtonSelKeywords();

    public ToggleButtonExpandKeywordsTreeController() {
        listen();
    }

    private void listen() {
        toggleButtonExpandAllNodesSelKeywords.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean expand = toggleButtonExpandAllNodesSelKeywords.isSelected();
        JTree tree = GUI.getAppPanel().getTreeSelKeywords();

        TreeUtil.expandAll(tree, expand);

        // Because root handle is invisible, all nodes would disappear when
        // collapsing it
        if (!expand && !tree.isRootVisible()) {
            tree.expandPath(new TreePath((TreeNode) tree.getModel().getRoot()));
        }

        toggleButtonExpandAllNodesSelKeywords.setText(expand
                ? Bundle.getString(ToggleButtonExpandKeywordsTreeController.class, "ToggleButtonExpandKeywordsTreeController.ButtonToggleExpandAllNodes.Selected")
                : Bundle.getString(ToggleButtonExpandKeywordsTreeController.class, "ToggleButtonExpandKeywordsTreeController.ButtonToggleExpandAllNodes.DeSelected"));
    }
}
