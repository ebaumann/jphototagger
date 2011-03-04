
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.jphototagger.program.controller.actions;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JTree;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TreeExpandCollapseAllAction extends AbstractAction {
    private static final long serialVersionUID = -2312693675896808948L;
    public static final String SELECTED_TEXT =
        JptBundle.INSTANCE.getString("TreeExpandCollapseAllAction.ToggleButton.Selected.Text");
    public static final String NOT_SELECTED_TEXT =
        JptBundle.INSTANCE.getString("TreeExpandCollapseAllAction.ToggleButton.NotSelected.Text");
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
        boolean selected = toggleButton.isSelected();

        TreeUtil.expandAll(tree, selected);
        toggleButton.setText(selected
                             ? SELECTED_TEXT
                             : NOT_SELECTED_TEXT);
    }
}
