package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
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
 * and on action renames in the tree the selected hierarchical sukeywordbject.
 *
 * Also listens to key events into the tree and renames the selected
 * hierarchical keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/12
 */
public class ControllerRenameHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panel =
            HierarchicalKeywordsDialog.INSTANCE.getPanel();
    private final DatabaseHierarchicalKeywords db =
            DatabaseHierarchicalKeywords.INSTANCE;

    public ControllerRenameHierarchicalKeyword() {
        listen();
    }

    private void listen() {
        panel.getMenuItemRename().addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F2 ||
                KeyEventUtil.isControl(e, KeyEvent.VK_R)) {
            rename();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        rename();
    }

    private void rename() {
        JTree tree = panel.getTree();
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            MessageDisplayer.error(
                    "ControllerRenameHierarchicalKeyword.Error.NoPathSelected");
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode keywordNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = keywordNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    renameKeyword(keywordNode, (HierarchicalKeyword) userObject);
                } else {
                    MessageDisplayer.error(
                            "ControllerRenameHierarchicalKeyword.Error.Node",
                            node);
                }
            }
        }
    }

    private void renameKeyword(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            String newName = getName(keyword, db);
            if (newName != null && !newName.trim().isEmpty()) {
                keyword.setKeyword(newName);
                ((TreeModelHierarchicalKeywords) tm).changed(node, keyword);
            }
        } else {
            AppLog.logWarning(ControllerRenameHierarchicalKeyword.class,
                    Bundle.getString(
                    "ControllerRenameHierarchicalKeyword.Error.Model"));
        }
    }

    static String getName(
            HierarchicalKeyword keyword, DatabaseHierarchicalKeywords database) {
        String newName = null;
        String oldName = keyword.getKeyword();
        boolean confirmed = true;
        while (newName == null && confirmed) {
            newName = JOptionPane.showInputDialog(Bundle.getString(
                    "ControllerRenameHierarchicalKeyword.Input.Name", oldName),
                    oldName);
            confirmed = newName != null;
            if (newName != null && !newName.trim().isEmpty()) {
                HierarchicalKeyword s = new HierarchicalKeyword(
                        keyword.getId(), keyword.getIdParent(), newName.trim());
                if (database.parentHasChild(s)) {
                    newName = null;
                    confirmed = MessageDisplayer.confirm(
                            "ControllerRenameHierarchicalKeyword.Confirm.Exists",
                            false, keyword) == JOptionPane.YES_OPTION;
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
