/*
 * @(#)ControllerActionExecutor.java    Created on 2008-11-06
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

package org.jphototagger.program.controller.actions;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.event.listener.ProgramActionListener;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.ActionsDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Executes actions of the dialog
 * {@link org.jphototagger.program.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerActionExecutor implements ProgramActionListener {
    private final ThumbnailsPanel tnPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final ActionsDialog actionsDialog = ActionsDialog.INSTANCE;

    // No other executor expected, so this instance gets the progress bar
    private final StartPrograms programStarter =
        new StartPrograms(actionsDialog.getPanelActions().getProgressBar(this));

    public ControllerActionExecutor() {
        listen();
    }

    private void listen() {
        actionsDialog.getPanelActions().addListener(this);
    }

    @Override
    public void programShallBeExecuted(Program program) {
        programStarter.startProgram(program, tnPanel.getSelectedFiles());
    }
}
