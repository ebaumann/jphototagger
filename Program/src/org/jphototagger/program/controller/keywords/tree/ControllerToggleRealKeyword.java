package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemToggleReal()}
 * and toggles the real property of a keyword.
 *
 * @author  Martin Pohlack
 */
public class ControllerToggleRealKeyword extends ControllerKeywords implements ActionListener, KeyListener {
    public ControllerToggleRealKeyword(KeywordsPanel panel) {
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
            final TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

            keyword.setReal(!keyword.isReal());
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    model.changed(node, keyword);
                }
            });
        } else {
            MessageDisplayer.error(null, "ControllerToggleRealKeyword.Error.Node", node);
        }
    }
}
