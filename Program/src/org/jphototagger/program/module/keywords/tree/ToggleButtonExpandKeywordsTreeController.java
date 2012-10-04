package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToggleButton;
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
        boolean selected = toggleButtonExpandAllNodesSelKeywords.isSelected();

        TreeUtil.expandAll(GUI.getAppPanel().getTreeSelKeywords(), selected);
        toggleButtonExpandAllNodesSelKeywords.setText(selected
                ? Bundle.getString(ToggleButtonExpandKeywordsTreeController.class, "ToggleButtonExpandKeywordsTreeController.ButtonToggleExpandAllNodes.Selected")
                : Bundle.getString(ToggleButtonExpandKeywordsTreeController.class, "ToggleButtonExpandKeywordsTreeController.ButtonToggleExpandAllNodes.DeSelected"));
    }
}
