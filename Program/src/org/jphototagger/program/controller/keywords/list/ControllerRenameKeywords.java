package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Renames keywords of selected items whithin the keywords list.
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameKeywords extends ControllerKeywords {
    public ControllerRenameKeywords() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemRename());
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

        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemRename();
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

            if ((toName != null) &&!toName.equalsIgnoreCase(fromName)) {
                KeywordsHelper.renameDcSubject(fromName, toName);
            }
        } else if (size > 1) {
            MessageDisplayer.information(null, "ControllerRenameKeywords.Info.MultipleSelected");
        }
    }

    private String getNewName(String fromName) {
        assert(fromName != null) && (fromName.trim().length() > 0) : fromName;

        boolean finished = false;
        InputDialog dlg = new InputDialog(InputHelperDialog.INSTANCE,
                                          JptBundle.INSTANCE.getString("ControllerRenameKeywords.Info.Input"),
                                          fromName, UserSettings.INSTANCE.getProperties(),
                                          "ControllerRenameKeyword.Input");

        while (!finished) {
            dlg.setVisible(true);
            finished = !dlg.isAccepted();

            if (dlg.isAccepted()) {
                String newName = dlg.getInput();
                boolean equals = (newName != null) &&!newName.trim().isEmpty() && newName.equalsIgnoreCase(fromName);

                if (equals) {
                    finished = !MessageDisplayer.confirmYesNo(dlg, "ControllerRenameKeywords.Confirm.NewName");
                } else {
                    return newName;
                }
            }
        }

        return null;
    }
}
