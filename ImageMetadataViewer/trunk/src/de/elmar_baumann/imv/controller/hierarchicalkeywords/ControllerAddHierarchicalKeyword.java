package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuHierarchicalKeywords#getMenuItemAdd()}
 * and on action adds a new keyword below the selected keyword.
 *
 * Also listens to key events into the tree and adds a new keyword below the
 * selected keyword if the keys Ctrl+N were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerAddHierarchicalKeyword
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerAddHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_N);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            add(node, (HierarchicalKeyword) userObject);
        } else if (isRootNode(node)) {
            add(node, null);
        }
    }

    private boolean isRootNode(Object node) {
        return getHKPanel().getTree().getModel().getRoot().equals(node);
    }

    private void add(
            DefaultMutableTreeNode parentNode, HierarchicalKeyword parentKeyword) {
        HierarchicalKeyword newKeyword =
                new HierarchicalKeyword(
                null, parentKeyword == null
                      ? null
                      : parentKeyword.getId(),
                Bundle.getString("ControllerAddHierarchicalKeyword.DefaultName"), // NOI18N
                true);
        JTree tree = getHKPanel().getTree();
        String name = ControllerRenameHierarchicalKeyword.getName(
                newKeyword, DatabaseHierarchicalKeywords.INSTANCE, tree);
        if (name != null && !name.trim().isEmpty()) {
            TreeModel tm = tree.getModel();
            if (tm instanceof TreeModelHierarchicalKeywords) {
                ((TreeModelHierarchicalKeywords) tm).addKeyword(parentNode, name);
                HierarchicalKeywordsTreePathExpander.expand(parentNode);
            } else {
                AppLog.logWarning(ControllerAddHierarchicalKeyword.class,
                        "ControllerAddHierarchicalKeyword.Error.Model"); // NOI18N
            }
        }
    }
}
