package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link HierarchicalKeywordsPanel#getMenuItemAdd()}
 * and on action adds a new keyword below the selected keyword.
 *
 * Also listens to key events into the tree and adds a new keyword below the
 * selected keyword if the keys Ctrl+N were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerAddHierarchicalKeyword
        implements ActionListener, KeyListener {

    private final HierarchicalKeywordsPanel panel;

    public ControllerAddHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        panel = _panel;
        listen();
    }

    private void listen() {
        PopupMenuHierarchicalKeywords.INSTANCE.getMenuItemAdd().
                addActionListener(this);
        panel.getTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N)) {
            add();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        add();
    }

    private void add() {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        if (path == null) {
            MessageDisplayer.error(panel.getTree(),
                    "ControllerAddHierarchicalKeyword.Error.NoPathSelected"); // NOI18N
        } else {
            Object node = path.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode keywordNode =
                        (DefaultMutableTreeNode) node;
                Object userObject = keywordNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    add(keywordNode, (HierarchicalKeyword) userObject);
                } else if (isRootNode(node)) {
                    add((DefaultMutableTreeNode) panel.getTree().getModel().
                            getRoot(), null);
                }
            }
        }
    }

    private boolean isRootNode(Object node) {
        return panel.getTree().getModel().getRoot().equals(node);
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
        JTree tree = panel.getTree();
        String name = ControllerRenameHierarchicalKeyword.getName(newKeyword,
                DatabaseHierarchicalKeywords.INSTANCE, tree);
        if (name != null && !name.trim().isEmpty()) {
            TreeModel tm = tree.getModel();
            if (tm instanceof TreeModelHierarchicalKeywords) {
                ((TreeModelHierarchicalKeywords) tm).addKeyword(parentNode, name);
                tree.expandPath(new TreePath(parentNode.getPath()));
            } else {
                AppLog.logWarning(ControllerAddHierarchicalKeyword.class,
                        Bundle.getString(
                        "ControllerAddHierarchicalKeyword.Error.Model")); // NOI18N
            }
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
