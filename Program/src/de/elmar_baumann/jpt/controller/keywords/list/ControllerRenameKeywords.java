/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import de.elmar_baumann.lib.dialog.InputDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Renames keywords of selected items whithin the keywords list.
 *
 * @author  Elmar Baumann
 * @version 2010-01-07
 */
public final class ControllerRenameKeywords extends ControllerKeywords {

    public ControllerRenameKeywords() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemRename());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() ==  KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemRename();
    }

    @Override
    protected void action(List<String> keywords) {
        int size = keywords.size();
        if (size == 1) {
            String oldName = keywords.get(0);
            String newName = getNewName(oldName);
            if (newName != null && !newName.equalsIgnoreCase(oldName)) {
                KeywordsHelper.renameKeyword(oldName, newName);
            }
        } else if (size > 1) {
            MessageDisplayer.information(null, "ControllerRenameKeywords.Info.MultipleSelected");
        }
    }

    private String getNewName(String oldName) {
        assert oldName != null && oldName.trim().length() > 0 : oldName;

        boolean     finished = false;
        InputDialog dlg      = new InputDialog(InputHelperDialog.INSTANCE, JptBundle.INSTANCE.getString("ControllerRenameKeywords.Info.Input"), oldName, UserSettings.INSTANCE.getProperties(), "ControllerRenameKeyword.Input");

        while (!finished) {
            dlg.setVisible(true);
            finished = !dlg.isAccepted();
            if (dlg.isAccepted()) {
                String  newName = dlg.getInput();
                boolean equals  = newName != null && !newName.trim().isEmpty() && newName.equalsIgnoreCase(oldName);
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
