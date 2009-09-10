package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuHierarchicalKeywords;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link PopupMenuHierarchicalKeywords#getMenuItemRemove()}
 * and on action removes from the tree the selected hierarchical keyword.
 *
 * Also listens to key events into the tree and removes the selected
 * hierarchical keyword if the delete key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-12
 */
public class ControllerRemoveHierarchicalKeyword
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerRemoveHierarchicalKeyword(HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            delete(node, (HierarchicalKeyword) userObject);
        } else {
            MessageDisplayer.error(getHKPanel().getTree(),
                    "ControllerDeleteHierarchicalKeyword.Error.Node", // NOI18N
                    node);
        }
    }

    private void delete(
            DefaultMutableTreeNode node, HierarchicalKeyword keyword) {
        TreeModel tm = getHKPanel().getTree().getModel();
        if (tm instanceof TreeModelHierarchicalKeywords) {
            if (MessageDisplayer.confirm(
                    getHKPanel(),
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
}
