package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

/**
 * Listens to {@link AppPanel#getToggleButtonSelKeywords()} and expands or
 * collapses all keyword nodes on action.
 *
 * @author Elmar Baumann
 */
public final class ControllerToggleButtonSelKeywords implements ActionListener {
    private final JToggleButton toggleButtonExpandAllNodesSelKeywords = GUI.getAppPanel().getToggleButtonSelKeywords();

    public ControllerToggleButtonSelKeywords() {
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
                ? JptBundle.INSTANCE.getString("KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                : JptBundle.INSTANCE.getString("KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
    }
}
