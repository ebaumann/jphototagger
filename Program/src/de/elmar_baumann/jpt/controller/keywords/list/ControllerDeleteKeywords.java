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

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

/**
 * Deletes keywords of selected items whithin the keywords list.
 *
 * @author  Elmar Baumann
 * @version 2010-01-07
 */
public final class ControllerDeleteKeywords extends ControllerKeywords {
    public ControllerDeleteKeywords() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemDelete());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource()
               == PopupMenuKeywordsList.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(List<String> keywords) {
        int size = keywords.size();

        if (size == 1) {
            String keyword = keywords.get(0);

            if (MessageDisplayer.confirmYesNo(
                    InputHelperDialog.INSTANCE,
                    "ControllerDeleteKeywords.List.Confirm.Delete", keyword)) {
                KeywordsHelper.deleteKeyword(keyword);
            }
        } else if (size > 1) {
            if (MessageDisplayer.confirmYesNo(
                    InputHelperDialog.INSTANCE,
                    "ControllerDeleteKeywords.List.Confirm.DeleteMultiple",
                    size)) {
                for (String keyword : keywords) {
                    KeywordsHelper.deleteKeyword(keyword);
                }
            }
        }
    }
}
