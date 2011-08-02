package org.jphototagger.program.controller.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

/**
 * Deletes keywords of selected items whithin the keywords list.
 *
 * @author Elmar Baumann
 */
public final class ControllerDeleteKeywords extends ControllerKeywords {
    public ControllerDeleteKeywords() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemDelete());
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

        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        int size = keywords.size();

        if (size == 1) {
            String keyword = keywords.get(0);
            String message = Bundle.getString(ControllerDeleteKeywords.class, "ControllerDeleteKeywords.List.Confirm.Delete", keyword);

            if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, message)) {
                KeywordsHelper.deleteDcSubject(keyword);
            }
        } else if (size > 1) {
            String message = Bundle.getString(ControllerDeleteKeywords.class, "ControllerDeleteKeywords.List.Confirm.DeleteMultiple", size);

            if (MessageDisplayer.confirmYesNo(InputHelperDialog.INSTANCE, message)) {
                for (String keyword : keywords) {
                    KeywordsHelper.deleteDcSubject(keyword);
                }
            }
        }
    }
}
