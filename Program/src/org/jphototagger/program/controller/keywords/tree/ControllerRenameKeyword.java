package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Keyword;
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
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

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
        final String newName = getName(keyword, DatabaseKeywords.INSTANCE, getHKPanel().getTree());

        if ((newName != null) &&!newName.trim().isEmpty()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    rename(node, keyword, newName);
                }
            });
        }
    }

    private void rename(DefaultMutableTreeNode node, Keyword keyword, String toName) {
        TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

        keyword.setName(toName);
        model.changed(node, keyword);
    }

    static String getName(Keyword keyword, DatabaseKeywords database, JTree tree) {
        String toName = null;
        String fromName = keyword.getName();
        boolean input = true;
        InputDialog dlg = new InputDialog(InputHelperDialog.INSTANCE,
                                          JptBundle.INSTANCE.getString("ControllerRenameKeyword.Input.Name", fromName),
                                          fromName, UserSettings.INSTANCE.getProperties(),
                                          ControllerRenameKeyword.class.getName());

        while (input && (toName == null)) {
            dlg.setVisible(true);
            toName = dlg.getInput();
            input = false;

            if (dlg.isAccepted() && (toName != null) &&!toName.trim().isEmpty()) {
                Keyword s = new Keyword(keyword.getId(), keyword.getIdParent(), toName.trim(), keyword.isReal());

                if (database.hasParentChildWithEqualName(s)) {
                    toName = null;
                    input = MessageDisplayer.confirmYesNo(null, "ControllerRenameKeyword.Confirm.Exists", s);
                }
            }
        }

        return toName;
    }
}
