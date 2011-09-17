package org.jphototagger.program.controller.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.KeywordsTreeModel;
import org.jphototagger.program.view.panels.KeywordsPanel;

/**
 * Listens to the menu item {@code KeywordsTreePopupMenu#getItemToggleReal()}
 * and toggles the real property of a keyword.
 *
 * @author  Martin Pohlack
 */
public class ToggleRealKeywordController extends KeywordsController implements ActionListener, KeyListener {

    public ToggleRealKeywordController(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_R);
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(List<DefaultMutableTreeNode> nodes) {
        final DefaultMutableTreeNode node = nodes.get(0);
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            final Keyword keyword = (Keyword) userObject;
            final KeywordsTreeModel model = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);

            keyword.setReal(!keyword.isReal());
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    model.changed(node, keyword);
                }
            });
        } else {
            String message = Bundle.getString(ToggleRealKeywordController.class, "ToggleRealKeywordController.Error.Node", node);
            MessageDisplayer.error(null, message);
        }
    }
}
