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
package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.StartPrograms;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerOpenFilesWithOtherApp implements ActionListener {

    private final PopupMenuThumbnails popupMenu;
    private final ThumbnailsPanel thumbnailsPanel;
    private final StartPrograms executor;

    public ControllerOpenFilesWithOtherApp() {
        popupMenu = PopupMenuThumbnails.INSTANCE;
        listen();
        thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
        executor = new StartPrograms(null);
    }

    private void listen() {
        popupMenu.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openFiles(popupMenu.getProgram(e.getSource()));
    }

    private void openFiles(Program program) {
        executor.startProgram(program, thumbnailsPanel.getSelectedFiles());
    }
}
