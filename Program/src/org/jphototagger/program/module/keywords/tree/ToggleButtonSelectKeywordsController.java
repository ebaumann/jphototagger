package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to {@code AppPanel#getToggleButtonSelKeywords()} and expands or
 * collapses all keyword nodes on action.
 *
 * @author Elmar Baumann
 */
public final class ToggleButtonSelectKeywordsController implements ActionListener {

    private final JToggleButton toggleButtonExpandAllNodesSelKeywords = GUI.getAppPanel().getToggleButtonSelKeywords();

    public ToggleButtonSelectKeywordsController() {
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
                ? Bundle.getString(ToggleButtonSelectKeywordsController.class, "KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                : Bundle.getString(ToggleButtonSelectKeywordsController.class, "KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
    }
}
