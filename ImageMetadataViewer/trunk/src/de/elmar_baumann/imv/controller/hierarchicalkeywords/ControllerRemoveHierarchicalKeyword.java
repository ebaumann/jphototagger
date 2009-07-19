package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalKeywordsPanel#getMenuItemRemove()}
 * and on action removes from the tree the selected hierarchical keyword.
 *
 * Also listens to key events into the tree and removes the selected
 * hierarchical keyword if the delete key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRemoveHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panel;

    public ControllerRemoveHierarchicalKeyword() {
        panel = HierarchicalKeywordsDialog.INSTANCE.getPanel();
        listen();
    }

    public ControllerRemoveHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {
        panel.getMenuItemRemove().addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        JTree tree = panel.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerDeleteHierarchicalKeyword.Error.NoPathSelected"); // NOI18N
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode keywordNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = keywordNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    delete(keywordNode, (HierarchicalKeyword) userObject);
                } else {
                    MessageDisplayer.error(
                            "ControllerDeleteHierarchicalKeyword.Error.Node", // NOI18N
                            node);
                }
            }
        }
    }

    private void delete(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            if (MessageDisplayer.confirm(
                    "ControllerDeleteHierarchicalKeyword.Confirm.Delete", // NOI18N
                    MessageDisplayer.CancelButton.HIDE, keyword).equals(
                    MessageDisplayer.ConfirmAction.YES)) {
                ((TreeModelHierarchicalKeywords) tm).removeKeyword(node);
            }
        } else {
            AppLog.logWarning(ControllerRemoveHierarchicalKeyword.class,
                    Bundle.getString(
                    "ControllerDeleteHierarchicalKeyword.Error.Model")); // NOI18N
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
