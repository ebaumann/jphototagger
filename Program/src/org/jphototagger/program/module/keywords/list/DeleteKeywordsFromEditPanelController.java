package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JMenuItem;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.module.keywords.tree.DeleteKeywordFromEditPanelController;

/**
 * @author Elmar Baumann
 */
public final class DeleteKeywordsFromEditPanelController extends KeywordsListController {

    public DeleteKeywordsFromEditPanelController() {
        listenToActionsOf(getMenuItem());
    }

    private JMenuItem getMenuItem() {
        return KeywordsListPopupMenu.INSTANCE.getItemRemoveFromEditPanel();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);

        if (editor.isEditable()) {
            DeleteKeywordFromEditPanelController ctrl =
                    ControllerFactory.INSTANCE.getController(DeleteKeywordFromEditPanelController.class);

            for (String keyword : keywords) {
                ctrl.removeFromEditPanel(keyword);
            }
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_BACK_SPACE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == getMenuItem();
    }
}
