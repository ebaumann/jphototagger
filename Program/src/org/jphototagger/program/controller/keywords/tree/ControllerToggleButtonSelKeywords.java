package org.jphototagger.program.controller.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;

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
                ? Bundle.getString(ControllerToggleButtonSelKeywords.class, "KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                : Bundle.getString(ControllerToggleButtonSelKeywords.class, "KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
    }
}
