/*
 * @(#)ControllerActionExecutor.java    2008-11-06
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

package de.elmar_baumann.jpt.controller.actions;

import de.elmar_baumann.jpt.event.listener.ProgramActionListener;
import de.elmar_baumann.jpt.event.ProgramEvent;
import de.elmar_baumann.jpt.helper.StartPrograms;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.ActionsDialog;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;

/**
 * Executes actions of the dialog
 * {@link de.elmar_baumann.jpt.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerActionExecutor implements ProgramActionListener {
    private final ThumbnailsPanel thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final ActionsDialog actionsDialog  = ActionsDialog.INSTANCE;
    private final StartPrograms programStarter =
        new StartPrograms(actionsDialog.getProgressBar(this));    // no other executor expected

    public ControllerActionExecutor() {
        listen();
    }

    private void listen() {
        actionsDialog.addActionListener(this);
    }

    @Override
    public void actionPerformed(ProgramEvent evt) {
        if (evt.getType().equals(ProgramEvent.Type.PROGRAM_EXECUTED)) {
            programStarter.startProgram(evt.getProgram(),
                                        thumbnailsPanel.getSelectedFiles());
        }
    }
}
