package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.editmetadata.EditRepeatableTextEntryPanel;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.openide.util.Lookup;

/**
 * Listens to the menu item
 * {@code KeywordsTreePopupMenu#getItemRemoveFromEditPanel()} and on action
 * removes the selected keyword from the edit panel.  If a keyword was already
 * present it is removed again.
 *
 * Also listens to key events and does the same if Ctrl+D was pressed.
 *
 * @author  Martin Pohlack
 */
public class DeleteKeywordFromEditPanelController extends KeywordsController implements ActionListener, KeyListener {

    public DeleteKeywordFromEditPanelController(KeywordsPanel panel) {
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
        SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);
        JPanel panel = editor.getEditPanelForMetaDataValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);

        if (panel instanceof EditRepeatableTextEntryPanel) {
            EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) panel;

            if (editPanel.isEditable()) {
                editPanel.removeText(keyword);
                editor.saveIfDirtyAndInputIsSaveEarly();
                KeywordsUtil.removeHighlightKeyword(keyword);
            } else {
                String message = Bundle.getString(DeleteKeywordFromEditPanelController.class, "DeleteKeywordFromEditPanelController.Error.EditDisabled");
                MessageDisplayer.error(null, message);
            }
        } else {
            String message = Bundle.getString(DeleteKeywordFromEditPanelController.class, "DeleteKeywordFromEditPanelController.Error.NoEditPanel");
            MessageDisplayer.error(null, message);
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
