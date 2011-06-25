package org.jphototagger.program.controller.keywords.tree;

import java.util.Properties;
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
import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.StringUtil;

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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addKeyword(nodes.get(0));
            }
        });
    }

    private boolean isRootNode(Object node) {
        TreeModelKeywords keywordsTreeModel = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
        Object rootNode = keywordsTreeModel.getRoot();

        return rootNode.equals(node);
    }

    private void addKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            addKeywordToParentNode(node, (Keyword) userObject);
        } else if (isRootNode(node)) {
            addKeywordToParentNode(node, null);
        }
    }

    private void addKeywordToParentNode(DefaultMutableTreeNode parentNode, Keyword parentKeyword) {
        Keyword newKeyword = createChildKeywordForParentKeyword(parentKeyword);
        String keywordName = getName(newKeyword);

        if ((keywordName != null) && !keywordName.trim().isEmpty()) {
            TreeModelKeywords keywordsTreeModel = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
            JTree keywordsTree = getHKPanel().getTree();
            boolean isReal = true;
            boolean errorMessageIfExists = true;

            keywordsTreeModel.insert(parentNode, keywordName, isReal, errorMessageIfExists);
            KeywordsTreePathExpander.expand(keywordsTree, parentNode);
        }
    }

    private Keyword createChildKeywordForParentKeyword(Keyword parentKeyword) {
        Long parentKeywordId = parentKeyword == null ? null : parentKeyword.getId();
        Long childKeywordId = null;
        String keywordName = "";
        boolean isReal = true;

        return new Keyword(childKeywordId, parentKeywordId, keywordName, isReal);
    }

    static String getName(Keyword keyword) {
        String newName = null;
        InputDialog inputDialog = createInputDialog();
        boolean input = true;

        while (input) {
            inputDialog.setVisible(true);

            if (!inputDialog.isAccepted()) {
                return null;
            }

            newName = inputDialog.getInput();

            if (!StringUtil.hasContent(newName)) {
                return null;
            }

            Keyword newKeyword = createKeywordFromExistingKeyword(keyword, newName);

            if (DatabaseKeywords.INSTANCE.hasParentChildWithEqualName(newKeyword)) {
                newName = null;
                input = MessageDisplayer.confirmYesNo(null, "ControllerAddKeyword.Confirm.Exists", newKeyword);
            } else {
                return newName;
            }
        }

        return newName;
    }

    private static InputDialog createInputDialog() {
        JDialog owner = InputHelperDialog.INSTANCE;
        String info = JptBundle.INSTANCE.getString("ControllerAddKeyword.Input.Name");
        String input = "";
        Properties properties = UserSettings.INSTANCE.getProperties();
        String propertyKey = ControllerAddKeyword.class.getName();

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
