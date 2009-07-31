package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalKeywordsPanel#getMenuItemRename()}
 * and on action renames in the tree the selected hierarchical keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * hierarchical keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRenameHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panel;
    private final DatabaseHierarchicalKeywords db =
            DatabaseHierarchicalKeywords.INSTANCE;

    public ControllerRenameHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {
        // Listening to singleton popup menu via ActionListenerFactory#
        // listenToPopupMenuHierarchicalKeywords()
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F2) {
            rename();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        rename();
    }

    private void rename() {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        if (path == null) {
            MessageDisplayer.error(panel.getTree(),
                    "ControllerRenameHierarchicalKeyword.Error.NoPathSelected"); // NOI18N
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode keywordNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = keywordNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    renameKeyword(keywordNode, (HierarchicalKeyword) userObject);
                } else {
                    MessageDisplayer.error(panel.getTree(),
                            "ControllerRenameHierarchicalKeyword.Error.Node", // NOI18N
                            node);
                }
            }
        }
    }

    private void renameKeyword(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            String newName = getName(keyword, db, panel.getTree());
            if (newName != null && !newName.trim().isEmpty()) {
                keyword.setKeyword(newName);
                ((TreeModelHierarchicalKeywords) tm).changed(node, keyword);
            }
        } else {
            AppLog.logWarning(ControllerRenameHierarchicalKeyword.class,
                    Bundle.getString(
                    "ControllerRenameHierarchicalKeyword.Error.Model")); // NOI18N
        }
    }

    static String getName(
            HierarchicalKeyword keyword,
            DatabaseHierarchicalKeywords database,
            JTree tree) {

        String newName = null;
        String oldName = keyword.getKeyword();
        boolean confirmed = true;
        while (newName == null && confirmed) {
            newName = JOptionPane.showInputDialog(tree, Bundle.getString(
                    "ControllerRenameHierarchicalKeyword.Input.Name", oldName), // NOI18N
                    oldName);
            confirmed = newName != null;
            if (newName != null && !newName.trim().isEmpty()) {
                HierarchicalKeyword s = new HierarchicalKeyword(keyword.getId(),
                        keyword.getIdParent(), newName.trim(), keyword.isReal());
                if (database.parentHasChild(s)) {
                    newName = null;
                    confirmed = MessageDisplayer.confirm(tree,
                            "ControllerRenameHierarchicalKeyword.Confirm.Exists", // NOI18N
                            MessageDisplayer.CancelButton.HIDE, keyword).equals(
                            MessageDisplayer.ConfirmAction.YES);
                }
            }
        }
        return newName;
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
