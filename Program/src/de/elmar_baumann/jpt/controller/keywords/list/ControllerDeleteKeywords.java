/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.keywords.list;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.helper.RenameXmpMetadata;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywordsList;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Deletes keywords of selected items whithin the keywords list.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
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
        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemDelete();
    }

    @Override
    protected void action(List<String> keywords) {
        new Delete(keywords).start();
    }

    private final class Delete extends Thread {
        private final List<String> keywords = new ArrayList<String>();

        public Delete(Collection<String> oldNames) {
            this.keywords.addAll(oldNames);
            setName("Deleting Keywords @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            if (!confirmMultipleDeletes()) return;

            for (String keyword : keywords) {
                delete(keyword, keywords.size() == 1);
            }
        }

        private boolean confirmMultipleDeletes() {
            int count = keywords.size();
            if (count > 1) {
                return MessageDisplayer.confirmYesNo(null,
                         "ControllerDeleteKeywords.List.Confirm.MultipleKeywords", count);
            }
            return true;
        }

        private void delete(String keyword, boolean confirm) {

            if (!confirm ||
                 confirm && MessageDisplayer.confirmYesNo(null, "ControllerDeleteKeywords.List.Confirm.Delete", keyword)
                ) {
                Set<String> files = DatabaseImageFiles.INSTANCE.getFilenamesOfDcSubject(keyword);

                if (files.size() <= 0) return;

                new RenameXmpMetadata(
                        files,
                        ColumnXmpDcSubjectsSubject.INSTANCE,
                        keyword,
                        "").run(); // No separate thread!

                ModelFactory.INSTANCE.getModel(TreeModelKeywords.class).removeRootItemWithoutChildren(keyword);
                getModel().delete(keyword);
            }
        }
    }
}
