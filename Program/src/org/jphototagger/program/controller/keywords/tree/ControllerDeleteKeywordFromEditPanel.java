package org.jphototagger.program.controller.keywords.tree;

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

import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the menu item
 * {@link PopupMenuKeywordsTree#getItemRemoveFromEditPanel()} and on action
 * removes the selected keyword from the edit panel.  If a keyword was already
 * present it is removed again.
 *
 * Also listens to key events and does the same if Ctrl+D was pressed.
 *
 * @author  Martin Pohlack
 */
public class ControllerDeleteKeywordFromEditPanel extends ControllerKeywords implements ActionListener, KeyListener {
    public ControllerDeleteKeywordFromEditPanel(KeywordsPanel panel) {
        super(panel);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
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
                remove(nodes.get(0));
            }
        });
    }

    private void remove(DefaultMutableTreeNode node) {
        String keyword = getKeyword(node);

        if (keyword != null) {
            removeFromEditPanel(keyword);
        }
    }

    public void removeFromEditPanel(String keyword) {
        EditMetadataPanels editPanels = GUI.getAppPanel().getEditMetadataPanels();
        JPanel panel = editPanels.getEditPanel(ColumnXmpDcSubjectsSubject.INSTANCE);

        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) panel;

            if (editPanel.isEditable()) {
                editPanel.removeText(keyword);
                editPanels.checkSaveOnChanges();
                KeywordsHelper.removeHighlightKeyword(keyword);
            } else {
                MessageDisplayer.error(null, "ControllerDeleteKeywordFromEditPanel.Error.EditDisabled");
            }
        } else {
            MessageDisplayer.error(null, "ControllerDeleteKeywordFromEditPanel.Error.NoEditPanel");
        }
    }

    private String getKeyword(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            Keyword keyword = (Keyword) userObject;

            if (keyword.isReal()) {
                return keyword.getName();
            }
        }

        return null;
    }
}
