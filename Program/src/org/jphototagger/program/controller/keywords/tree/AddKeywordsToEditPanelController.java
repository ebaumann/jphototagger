package org.jphototagger.program.controller.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;
import org.jphototagger.program.view.panels.KeywordsPanel;

/**
 * Listens to the menu item
 * {@code KeywordsTreePopupMenu#getItemAddToEditPanel()}
 * and on action inserts the selected keyword and it's real parents into the
 * edit panel.
 *
 * Also listens to key events and does the same if Ctrl+B were pressed.
 *
 * @author Elmar Baumann
 */
public class AddKeywordsToEditPanelController extends KeywordsController implements ActionListener, KeyListener {

    public AddKeywordsToEditPanelController(KeywordsPanel panel) {
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
        JPanel panel = editPanels.getEditPanel(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);

        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) panel;

            if (editPanel.isEditable()) {
                for (String keywordName : keywordNames) {
                    editPanel.addText(keywordName);
                }

                KeywordsHelper.addHighlightKeywords(keywordNames);
                editPanels.checkSaveOnChanges();
            } else {
                String message = Bundle.getString(AddKeywordsToEditPanelController.class, "AddKeywordsToEditPanelController.Error.EditDisabled");
                MessageDisplayer.error(null, message);
            }
        } else {
            String message = Bundle.getString(AddKeywordsToEditPanelController.class, "AddKeywordsToEditPanelController.Error.NoEditPanel");
            MessageDisplayer.error(null, message);
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
