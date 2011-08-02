package org.jphototagger.program.controller.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;

/**
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemRename()}
 * and on action renames in the tree the selected keyword.
 *
 * Also listens to key events into the tree and renames the selected
 * keyword if the keys F2 or Ctrl+R were pressed.
 *
 * @author Elmar Baumann
 */
public class ControllerRenameKeyword extends ControllerKeywords implements ActionListener, KeyListener {

    public ControllerRenameKeyword(KeywordsPanel _panel) {
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
            String message = Bundle.getString(ControllerRenameKeyword.class, "ControllerRenameKeyword.Error.Node", node);
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
        TreeModelKeywords keywordsTreeModel = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

        keyword.setName(toName);
        keywordsTreeModel.changed(node, keyword);
    }

    static String getName(Keyword keyword) {
        String toName = null;
        String fromName = keyword.getName();
        InputDialog inputDialog = createInputDialog(fromName);
        boolean input = true;

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

            if (DatabaseKeywords.INSTANCE.hasParentChildWithEqualName(newKeyword)) {
                toName = null;
                String message = Bundle.getString(ControllerRenameKeyword.class, "ControllerRenameKeyword.Confirm.Exists", newKeyword);
                input = MessageDisplayer.confirmYesNo(null, message);
            } else {
                return toName;
            }
        }

        return toName;
    }

    private static InputDialog createInputDialog(String input) {
        JDialog owner = InputHelperDialog.INSTANCE;
        String info = Bundle.getString(ControllerRenameKeyword.class, "ControllerRenameKeyword.Input.Name", input);
        Properties properties = UserSettings.INSTANCE.getProperties();
        String propertyKey = ControllerRenameKeyword.class.getName();

        return new InputDialog(owner, info, input);
    }

    private static Keyword createKeywordFromExistingKeyword(Keyword keyword, String newName) {
        Long keywordId = keyword.getId();
        Long parentKeywordId = keyword.getIdParent();
        String newKeywordName = newName.trim();
        Boolean isReal = keyword.isReal();

        return new Keyword(keywordId, parentKeywordId, newKeywordName, isReal);
    }
}
