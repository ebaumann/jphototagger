package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.keywords.KeywordsPanel;

/**
 * Listens to the menu item {@code KeywordsTreePopupMenu#getItemRemove()} and on action removes from the tree the
 * selected keyword.
 *
 * Also listens to key events into the tree and removes the selected keyword if the delete key was pressed.
 *
 * @author Elmar Baumann
 */
public class DeleteKeywordsFromTreeController extends KeywordsController implements ActionListener, KeyListener {

    public DeleteKeywordsFromTreeController(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return true;
    }

    @Override
    protected void localAction(final List<DefaultMutableTreeNode> nodes) {
        if (!ensureNoChild(nodes) || !confirmDeleteMultiple(nodes)) {
            return;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteKeywords(nodes);
            }
        });
    }

    private void deleteKeywords(List<DefaultMutableTreeNode> nodes) {
        for (DefaultMutableTreeNode node : nodes) {
            Object userObject = node.getUserObject();
            if (userObject instanceof Keyword) {
                delete(node, (Keyword) userObject, nodes.size() == 1);
            } else {
                String message = Bundle.getString(DeleteKeywordsFromTreeController.class, "DeleteKeywordsFromTreeController.Tree.Error.Node", node);
                MessageDisplayer.error(null, message);
            }
        }
    }

    private void delete(DefaultMutableTreeNode node, Keyword keyword, boolean confirm) {
        InputHelperDialog parentComponent = InputHelperDialog.INSTANCE;
        String message = Bundle.getString(DeleteKeywordsFromTreeController.class, "DeleteKeywordsFromTreeController.Tree.Confirm.Delete", keyword);
        if (!confirm || (confirm && MessageDisplayer.confirmYesNo(parentComponent, message))) {
            ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class).delete(node);
        }
    }

    private boolean confirmDeleteMultiple(List<DefaultMutableTreeNode> nodes) {
        int size = nodes.size();
        if (size <= 1) {
            return true;
        }
        InputHelperDialog parentComponent = InputHelperDialog.INSTANCE;
        String message = Bundle.getString(DeleteKeywordsFromTreeController.class, "DeleteKeywordsFromTreeController.Tree.Confirm.MultipleKeywords", size);
        return MessageDisplayer.confirmYesNo(parentComponent, message);
    }
}
