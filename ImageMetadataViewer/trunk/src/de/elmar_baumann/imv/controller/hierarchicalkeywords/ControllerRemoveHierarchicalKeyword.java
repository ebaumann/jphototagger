package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

    public ControllerRemoveHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
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
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        TreePath path = PopupMenuHierarchicalKeywords.INSTANCE.getTreePath();
        Object node = path.getLastPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                delete(treeNode, (HierarchicalKeyword) userObject);
            } else {
                MessageDisplayer.error(panel.getTree(),
                        "ControllerDeleteHierarchicalKeyword.Error.Node", // NOI18N
                        node);
            }
        }
    }

    private void delete(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = panel.getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            if (MessageDisplayer.confirm(
                    panel,
                    "ControllerDeleteHierarchicalKeyword.Confirm.Delete", // NOI18N
                    MessageDisplayer.CancelButton.HIDE, keyword).equals(
                    MessageDisplayer.ConfirmAction.YES)) {
                ((TreeModelHierarchicalKeywords) tm).removeKeyword(node);
            }
        } else {
            AppLog.logWarning(ControllerRemoveHierarchicalKeyword.class,
                    "ControllerDeleteHierarchicalKeyword.Error.Model"); // NOI18N
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
