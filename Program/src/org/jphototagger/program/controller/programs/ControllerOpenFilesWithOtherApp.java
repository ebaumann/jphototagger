/*
 * @(#)ControllerOpenFilesWithOtherApp.java    Created on 2008-09-10
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

package org.jphototagger.program.controller.programs;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerOpenFilesWithOtherApp implements ActionListener {
    private final StartPrograms programStarter = new StartPrograms(null);

    public ControllerOpenFilesWithOtherApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.addActionListenerOpenFilesWithOtherApp(
            this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openFiles(PopupMenuThumbnails.INSTANCE.getProgram(evt.getSource()));
    }

    private void openFiles(Program program) {
        programStarter.startProgram(program, ViewUtil.getSelectedImageFiles());
    }
}
