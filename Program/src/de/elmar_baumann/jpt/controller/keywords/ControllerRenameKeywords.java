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
package de.elmar_baumann.jpt.controller.keywords;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.helper.RenameXmpMetadata;
import de.elmar_baumann.jpt.model.ListModelKeywords;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuKeywords;
import de.elmar_baumann.lib.dialog.InputDialog;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Renames keywords of selected items whithin the keywords list.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-07
 */
public final class ControllerRenameKeywords extends ControllerKeywords {

    @Override
    protected boolean myKey(KeyEvent e) {
        return e.getKeyCode() ==  KeyEvent.VK_F2;
    }

    @Override
    protected boolean myActionSource(Object o) {
        return o == PopupMenuKeywords.INSTANCE.getItemRename();
    }

    @Override
    protected void action(List<String> keywords) {
        new Rename(keywords).start();
    }

    private final class Rename extends Thread {
        private final List<String> oldNames = new ArrayList<String>();

        public Rename(Collection<String> oldNames) {
            this.oldNames.addAll(oldNames);
            setName("Reanaming Keywords @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            if (!confirmMultipleRenames()) return;

            for (String oldName : oldNames) {
                rename(oldName);
            }
        }

        private boolean confirmMultipleRenames() {
            int count = oldNames.size();
            if (count > 1) {
                return MessageDisplayer.confirmYesNo(null,
                         "ControllerRenameKeyword.Confirm.MultipleKeywords", count);
            }
            return true;
        }

        private void rename(String oldName) {
            String newName = getNewName(oldName);

            if (newName != null) {
                Set<String> files = DatabaseImageFiles.INSTANCE.getFilenamesOfDcSubject(oldName);

                if (files.size() <= 0) return;

                new RenameXmpMetadata(
                        files,
                        ColumnXmpDcSubjectsSubject.INSTANCE,
                        oldName,
                        newName).run(); // No separate thread!

                getModel().renameKeyword(oldName, newName);
                ((TreeModelHierarchicalKeywords) GUI.INSTANCE.getAppPanel().
                        getTreeEditKeywords().getModel()).setAllRenamed(oldName, newName);
            }
        }

        private String getNewName(String oldName) {
            assert oldName != null && oldName.trim().length() > 0 : oldName;

            boolean           finished = !MessageDisplayer.confirmYesNo(null, "ControllerRenameKeyword.Confirm", oldName);
            InputDialog       dlg      = new InputDialog(Bundle.getString("ControllerRenameKeyword.Info.Input"), oldName, UserSettings.INSTANCE.getProperties(), "ControllerRenameKeyword.Input");
            ListModelKeywords model    = getModel();

            dlg.setIconImages(AppLookAndFeel.getAppIcons());
            while (!finished) {
                dlg.setVisible(true);
                finished = !dlg.isAccepted();
                if (dlg.isAccepted()) {
                    String  newName = dlg.getInput();
                    boolean equals  = newName != null && !newName.trim().isEmpty() && newName.equalsIgnoreCase(oldName);
                    if (equals) {
                        finished = !MessageDisplayer.confirmYesNo(dlg, "ControllerRenameKeyword.Confirm.NewName");
                    } else {
                        boolean exists = model.existsKeyword(newName);
                        if (exists) {
                            finished = !MessageDisplayer.confirmYesNo(dlg, "ControllerRenameKeyword.Confirm.NewNameExists");
                        } else {
                            return newName;
                        }
                    }
                }
            }
            return null;
        }
    }
}
