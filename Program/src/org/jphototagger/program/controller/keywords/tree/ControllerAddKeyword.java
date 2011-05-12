package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.event.util.KeyEventUtil;
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
 * Listens to the menu item {@link PopupMenuKeywordsTree#getItemAdd()}
 * and on action adds a new keyword below the selected keyword.
 *
 * Also listens to key events into the tree and adds a new keyword below the
 * selected keyword if the keys Ctrl+N were pressed.
 *
 * @author Elmar Baumann
 */
public class ControllerAddKeyword extends ControllerKeywords implements ActionListener, KeyListener {
    public ControllerAddKeyword(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N);
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(final List<DefaultMutableTreeNode> nodes) {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                addKeyword(nodes.get(0));
            }
        });
    }

    private boolean isRootNode(Object node) {
        return ModelFactory.INSTANCE.getModel(TreeModelKeywords.class).getRoot().equals(node);
    }

    private void addKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            add(node, (Keyword) userObject);
        } else if (isRootNode(node)) {
            add(node, null);
        }
    }

    private void add(DefaultMutableTreeNode parentNode, Keyword parentKeyword) {
        Keyword newKeyword = new Keyword(null, (parentKeyword == null)
                ? null
                : parentKeyword.getId(), "", true);
        JTree tree = getHKPanel().getTree();
        String name = getName(newKeyword, tree);

        if ((name != null) &&!name.trim().isEmpty()) {
            ModelFactory.INSTANCE.getModel(TreeModelKeywords.class).insert(parentNode, name, true, true);
            KeywordsTreePathExpander.expand(getHKPanel().getTree(), parentNode);
        }
    }

    static String getName(Keyword keyword, JTree tree) {
        String newName = null;
        boolean input = true;
        DatabaseKeywords db = DatabaseKeywords.INSTANCE;
        InputDialog dlg = new InputDialog(InputHelperDialog.INSTANCE,
                                          JptBundle.INSTANCE.getString("ControllerAddKeyword.Input.Name"), "",
                                          UserSettings.INSTANCE.getProperties(), ControllerAddKeyword.class.getName());

        while (input && (newName == null)) {
            dlg.setVisible(true);
            newName = dlg.getInput();
            input = false;

            if (dlg.isAccepted() && (newName != null) &&!newName.trim().isEmpty()) {
                Keyword s = new Keyword(keyword.getId(), keyword.getIdParent(), newName.trim(), keyword.isReal());

                if (db.hasParentChildWithEqualName(s)) {
                    newName = null;
                    input = MessageDisplayer.confirmYesNo(null, "ControllerAddKeyword.Confirm.Exists", s);
                }
            }
        }

        return newName;
    }
}
