package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

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
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    private final DatabaseHierarchicalKeywords db =
            DatabaseHierarchicalKeywords.INSTANCE;

    public ControllerRenameHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_F2);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            renameKeyword(node, (HierarchicalKeyword) userObject);
        } else {
            MessageDisplayer.error(getHKPanel().getTree(),
                    "ControllerRenameHierarchicalKeyword.Error.Node", node); // NOI18N
        }
    }

    private void renameKeyword(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = getHKPanel().getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            String newName = getName(keyword, db, getHKPanel().getTree());
            if (newName != null && !newName.trim().isEmpty()) {
                keyword.setKeyword(newName);
                ((TreeModelHierarchicalKeywords) tm).changed(node, keyword);
            }
        } else {
            AppLog.logWarning(ControllerRenameHierarchicalKeyword.class,
                    "ControllerRenameHierarchicalKeyword.Error.Model"); // NOI18N
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
}
