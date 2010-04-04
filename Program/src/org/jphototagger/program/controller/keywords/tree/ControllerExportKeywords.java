/*
 * @(#)ControllerExportKeywords.java    Created on 2009-08-02
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.exporter.Exporter;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.KeywordExportDialog;
import org.jphototagger.program.view.frames.AppFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemExportKeywords()} and
 * on action performed exports keywords.
 *
 * @author  Elmar Baumann
 */
public final class ControllerExportKeywords implements ActionListener {
    public ControllerExportKeywords() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemExportKeywords()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        exportKeywords();
    }

    private void exportKeywords() {
        KeywordExportDialog dlg = new KeywordExportDialog();

        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            Exporter exporter = dlg.getExporter();

            assert exporter != null : "Exporter is null!";

            if (exporter != null) {
                exporter.exportFile(dlg.getFile());
            }
        }
    }
}
