/*
 * @(#)ControllerOpenFilesWithStandardApp.java    Created on 2008-09-10
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

package org.jphototagger.program.controller.programs;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.SettingsDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author  Elmar Baumann
 */
public final class ControllerOpenFilesWithStandardApp
        implements ActionListener {
    public ControllerOpenFilesWithStandardApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemOpenFilesWithStandardApp()
            .addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (checkOpenAppIsDefined()) {
            openFiles();
        }
    }

    private void openFiles() {
        ThumbnailsPanel tnPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

        if (!tnPanel.isFileSelected()) {
            return;
        }

        Program program =
            DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

        if (program != null) {
            new StartPrograms(null).startProgram(program,
                              tnPanel.getSelectedFiles());
        }
    }

    private boolean checkOpenAppIsDefined() {
        if (DatabasePrograms.INSTANCE.getDefaultImageOpenProgram() == null) {
            SettingsDialog dlg = SettingsDialog.INSTANCE;

            dlg.selectTab(SettingsDialog.Tab.PROGRAMS);
            ComponentUtil.show(dlg);

            return false;
        }

        return true;
    }
}
