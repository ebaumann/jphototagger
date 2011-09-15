package org.jphototagger.program.controller.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JDialog;

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.KeywordsListPopupMenu;

/**
 * Renames keywords of selected items whithin the keywords list.
 *
 * @author Elmar Baumann
 */
public final class RenameKeywordsController extends KeywordsController {

    public RenameKeywordsController() {
        listenToActionsOf(KeywordsListPopupMenu.INSTANCE.getItemRename());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == KeywordsListPopupMenu.INSTANCE.getItemRename();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        int size = keywords.size();

        if (size == 1) {
            String fromName = keywords.get(0);
            String toName = getNewName(fromName);

            if ((toName != null) && !toName.equalsIgnoreCase(fromName)) {
                KeywordsHelper.renameDcSubject(fromName, toName);
            }
        } else if (size > 1) {
            String message = Bundle.getString(RenameKeywordsController.class, "RenameKeywordsController.Info.MultipleSelected");
            MessageDisplayer.information(null, message);
        }
    }

    private String getNewName(String fromName) {
        assert (fromName != null) && (fromName.trim().length() > 0) : fromName;

        boolean finished = false;
        JDialog owner = InputHelperDialog.INSTANCE;
        String info = Bundle.getString(RenameKeywordsController.class, "RenameKeywordsController.Info.Input");
        String input = fromName;
        InputDialog dlg = new InputDialog(owner, info, input);

        while (!finished) {
            dlg.setVisible(true);
            finished = !dlg.isAccepted();

            if (dlg.isAccepted()) {
                String newName = dlg.getInput();
                boolean equals = (newName != null) && !newName.trim().isEmpty() && newName.equalsIgnoreCase(fromName);

                if (equals) {
                    String message = Bundle.getString(RenameKeywordsController.class, "RenameKeywordsController.Confirm.NewName");

                    finished = !MessageDisplayer.confirmYesNo(dlg, message);
                } else {
                    return newName;
                }
            }
        }

        return null;
    }
}
