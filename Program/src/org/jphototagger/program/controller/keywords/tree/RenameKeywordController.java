package org.jphototagger.program.controller.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openide.util.Lookup;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.KeywordsTreeModel;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.KeywordsTreePopupMenu;

/**
 * Listens to the menu item {@link KeywordsTreePopupMenu#getItemRename()}
 * and on action renames in the tree the selected keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author Elmar Baumann
 */
public class RenameKeywordController extends KeywordsController implements ActionListener, KeyListener {

    public RenameKeywordController(KeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(List<DefaultMutableTreeNode> nodes) {
        DefaultMutableTreeNode node = nodes.get(0);
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            renameKeyword(node, (Keyword) userObject);
        } else {
            String message = Bundle.getString(RenameKeywordController.class, "RenameKeywordController.Error.Node", node);
            MessageDisplayer.error(null, message);
        }
    }

    private void renameKeyword(final DefaultMutableTreeNode node, final Keyword keyword) {
        final String newName = getName(keyword);

        if ((newName != null) && !newName.trim().isEmpty()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    rename(node, keyword, newName);
                }
            });
        }
    }

    private void rename(DefaultMutableTreeNode node, Keyword keyword, String toName) {
        KeywordsTreeModel keywordsTreeModel = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);

        keyword.setName(toName);
        keywordsTreeModel.changed(node, keyword);
    }

    static String getName(Keyword keyword) {
        String toName = null;
        String fromName = keyword.getName();
        InputDialog inputDialog = createInputDialog(fromName);
        boolean input = true;
        KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);

        while (input) {
            inputDialog.setVisible(true);

            if (!inputDialog.isAccepted()) {
                return null;
            }

            toName = inputDialog.getInput();

            if (!StringUtil.hasContent(toName)) {
                return null;
            }

            toName = toName.trim();

            if (toName.equals(fromName)) {
                return null;
            }

            Keyword newKeyword = createKeywordFromExistingKeyword(keyword, toName);

            if (repo.hasParentChildKeywordWithEqualName(newKeyword)) {
                toName = null;
                String message = Bundle.getString(RenameKeywordController.class, "RenameKeywordController.Confirm.Exists", newKeyword);
                input = MessageDisplayer.confirmYesNo(null, message);
            } else {
                return toName;
            }
        }

        return toName;
    }

    private static InputDialog createInputDialog(String input) {
        JDialog owner = InputHelperDialog.INSTANCE;
        String info = Bundle.getString(RenameKeywordController.class, "RenameKeywordController.Input.Name", input);
        String storageKey = RenameKeywordController.class.getName();
        InputDialog inputDialog = new InputDialog(owner, info, input);

        inputDialog.setStorageKey(storageKey);

        return inputDialog;
    }

    private static Keyword createKeywordFromExistingKeyword(Keyword keyword, String newName) {
        Long keywordId = keyword.getId();
        Long parentKeywordId = keyword.getIdParent();
        String newKeywordName = newName.trim();
        Boolean isReal = keyword.isReal();

        return new Keyword(keywordId, parentKeywordId, newKeywordName, isReal);
    }
}
