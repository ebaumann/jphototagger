package org.jphototagger.program.controller.keywords.tree;

import java.util.Properties;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.domain.Keyword;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.StringUtil;

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
            MessageDisplayer.error(null, "ControllerRenameKeyword.Error.Node", node);
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
                input = MessageDisplayer.confirmYesNo(null, "ControllerRenameKeyword.Confirm.Exists", newKeyword);
            } else {
                return toName;
            }
        }

        return toName;
    }

    private static InputDialog createInputDialog(String input) {
        JDialog owner = InputHelperDialog.INSTANCE;
        String info = JptBundle.INSTANCE.getString("ControllerRenameKeyword.Input.Name", input);
        Properties properties = UserSettings.INSTANCE.getProperties();
        String propertyKey = ControllerRenameKeyword.class.getName();

        return new InputDialog(owner, info, input, properties, propertyKey);
    }

    private static Keyword createKeywordFromExistingKeyword(Keyword keyword, String newName) {
        Long keywordId = keyword.getId();
        Long parentKeywordId = keyword.getIdParent();
        String newKeywordName = newName.trim();
        Boolean isReal = keyword.isReal();

        return new Keyword(keywordId, parentKeywordId, newKeywordName, isReal);
    }
}
