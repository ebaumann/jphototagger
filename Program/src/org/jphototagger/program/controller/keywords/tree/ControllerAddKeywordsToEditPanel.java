package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsTree;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the menu item
 * {@link PopupMenuKeywordsTree#getItemAddToEditPanel()}
 * and on action inserts the selected keyword and it's real parents into the
 * edit panel.
 *
 * Also listens to key events and does the same if Ctrl+B were pressed.
 *
 * @author Elmar Baumann
 */
public class ControllerAddKeywordsToEditPanel extends ControllerKeywords implements ActionListener, KeyListener {
    public ControllerAddKeywordsToEditPanel(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_B);
    }

    @Override
    protected boolean canHandleMultipleNodes() {
        return false;
    }

    @Override
    protected void localAction(List<DefaultMutableTreeNode> nodes) {
        final DefaultMutableTreeNode node = nodes.get(0);
        final List<String> keywordNames = new ArrayList<String>();

        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                addParentKeywords(node, keywordNames);
                addToEditPanel(keywordNames);
            }
        });
    }

    private void addToEditPanel(List<String> keywordNames) {
        EditMetadataPanels editPanels = GUI.getAppPanel().getEditMetadataPanels();
        JPanel panel = editPanels.getEditPanel(ColumnXmpDcSubjectsSubject.INSTANCE);

        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) panel;

            if (editPanel.isEditable()) {
                for (String keywordName : keywordNames) {
                    editPanel.addText(keywordName);
                }

                KeywordsHelper.addHighlightKeywords(keywordNames);
                editPanels.checkSaveOnChanges();
            } else {
                MessageDisplayer.error(null, "ControllerAddKeywordsToEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(null, "ControllerAddKeywordsToEditPanel.Error.NoEditPanel");
        }
    }

    private void addParentKeywords(DefaultMutableTreeNode node, List<String> keywords) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            Keyword keyword = (Keyword) userObject;

            if (keyword.isReal()) {
                keywords.add(keyword.getName());
            }
        }

        TreeNode parent = node.getParent();

        if ((parent == null) || getHKPanel().getTree().getModel().getRoot().equals(parent)) {
            return;
        }

        if (parent instanceof DefaultMutableTreeNode) {
            addParentKeywords((DefaultMutableTreeNode) parent, keywords);
        }
    }
}
