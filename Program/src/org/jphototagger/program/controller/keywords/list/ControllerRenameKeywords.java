/*
 * @(#)ControllerRenameKeywords.java    Created on 2010-01-07
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.jphototagger.lib.dialog.InputDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

/**
 * Renames keywords of selected items whithin the keywords list.
 *
 * @author  Elmar Baumann
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

        return evt.getSource()
               == PopupMenuKeywordsList.INSTANCE.getItemRename();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        int size = keywords.size();

        if (size == 1) {
            String fromName = keywords.get(0);
            String toName   = getNewName(fromName);

            if ((toName != null) &&!toName.equalsIgnoreCase(fromName)) {
                KeywordsHelper.renameDcSubject(fromName, toName);
            }
        } else if (size > 1) {
            MessageDisplayer.information(
                null, "ControllerRenameKeywords.Info.MultipleSelected");
        }
    }

    private String getNewName(String fromName) {
        assert (fromName != null) && (fromName.trim().length() > 0) : fromName;

        boolean     finished = false;
        InputDialog dlg      =
            new InputDialog(
                InputHelperDialog.INSTANCE,
                JptBundle.INSTANCE.getString(
                    "ControllerRenameKeywords.Info.Input"), fromName,
                        UserSettings.INSTANCE.getProperties(),
                        "ControllerRenameKeyword.Input");

        while (!finished) {
            dlg.setVisible(true);
            finished = !dlg.isAccepted();

            if (dlg.isAccepted()) {
                String  newName = dlg.getInput();
                boolean equals  = (newName != null) &&!newName.trim().isEmpty()
                                  && newName.equalsIgnoreCase(fromName);

                if (equals) {
                    finished = !MessageDisplayer.confirmYesNo(dlg,
                            "ControllerRenameKeywords.Confirm.NewName");
                } else {
                    return newName;
                }
            }
        }

        return null;
    }
}
