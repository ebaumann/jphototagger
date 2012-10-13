package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.keywords.KeywordsUtil;

/**
 * Deletes keywords of selected items whithin the keywords list.
 *
 * @author Elmar Baumann
 */
public final class DeleteKeywordsController extends KeywordsListController {

    public DeleteKeywordsController() {
        listenToActionsOf(KeywordsListPopupMenu.INSTANCE.getItemDelete());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == KeywordsListPopupMenu.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        int size = keywords.size();

        if (size == 1) {
            String keyword = keywords.get(0);
            String message = Bundle.getString(DeleteKeywordsController.class, "DeleteKeywordsController.List.Confirm.Delete", keyword);

            if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, message)) {
                KeywordsUtil.deleteDcSubject(keyword);
            }
        } else if (size > 1) {
            String message = Bundle.getString(DeleteKeywordsController.class, "DeleteKeywordsController.List.Confirm.DeleteMultiple", size);

            if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, message)) {
                for (String keyword : keywords) {
                    KeywordsUtil.deleteDcSubject(keyword);
                }
            }
        }
    }
}
