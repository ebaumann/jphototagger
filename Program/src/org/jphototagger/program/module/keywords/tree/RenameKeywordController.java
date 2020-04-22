package org.jphototagger.program.module.keywords.tree;

import com.jgoodies.common.base.Objects;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.InputDialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.openide.util.Lookup;

/**
 * Listens to the menu item {@code KeywordsTreePopupMenu#getItemRename()}
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

            if (parentHasOtherChildWithName(keyword, toName)) {
                toName = null;
                String message = Bundle.getString(RenameKeywordController.class, "RenameKeywordController.Confirm.Exists", toName);
                input = MessageDisplayer.confirmYesNo(null, message);
            } else {
                return toName;
            }
        }

        return toName;
    }

    /**
     * @param keyword
     * @param name
     *
     * @return true, if the parent of that keyword has a different child keyword
     *         with a specific name (different: a keyword with an ID not equals
     *         with the ID of that keyword)
     */
    private static boolean parentHasOtherChildWithName(Keyword keyword, String name) {
        KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);

        Long parentId = keyword.getIdParent();

        Collection<Keyword> children = parentId == null
                ? repo.findRootKeywords()
                : repo.findChildKeywords(parentId);

        for (Keyword child : children) {
            boolean namesEquals = Objects.equals(name, child.getName());
            boolean idsDifferent = !Objects.equals(keyword.getId(), child.getId());
            if (namesEquals && idsDifferent) {
                return true;
            }
        }

        return false;
    }

    private static InputDialog createInputDialog(String input) {
        InputHelperDialog owner = InputHelperDialog.INSTANCE;
        String info = Bundle.getString(RenameKeywordController.class, "RenameKeywordController.Input.Name", input);
        String preferencesKey = RenameKeywordController.class.getName();
        InputDialog inputDialog = new InputDialog(owner, info, input);

        inputDialog.setPreferencesKey(preferencesKey);

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
