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
package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.importer.KeywordImporter;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.KeywordImportDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemImportKeywords()} and
 * on action performed imports keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class ControllerImportKeywords implements ActionListener {

    public ControllerImportKeywords() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemImportKeywords().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        importKeywords();
    }

    private void importKeywords() {
        KeywordImportDialog dlg = new KeywordImportDialog();
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            KeywordImporter importer = dlg.getImporter();
            assert importer != null : "Importer is null!";
            if (importer != null) {
                importer.getPaths(dlg.getFile());
            }
        }
    }
}
