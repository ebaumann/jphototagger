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

package de.elmar_baumann.jpt.controller.actions;

import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.event.DatabaseProgramsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseProgramsListener;
import de.elmar_baumann.jpt.helper.ActionsHelper;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;

import javax.swing.JMenu;

/**
 * Listens to {@link DatabaseProgramsEvent}s and inserts or removes action from
 * the {@link PopupMenuThumbnails#getMenuActions()}.
 *
 * @author  Elmar Baumann
 * @version 2010-01-24
 */
public final class ControllerActionsMenuUpdater
        implements DatabaseProgramsListener {
    public ControllerActionsMenuUpdater() {
        PopupMenuThumbnails.INSTANCE.getMenuActions().setEnabled(
            DatabasePrograms.INSTANCE.hasAction());
        listen();
    }

    private void listen() {
        DatabasePrograms.INSTANCE.addListener(this);
    }

    @Override
    public void actionPerformed(DatabaseProgramsEvent event) {
        if (!event.getProgram().isAction()) {
            return;
        }

        JMenu actionMenu = PopupMenuThumbnails.INSTANCE.getMenuActions();

        if (event.getType().equals(
                DatabaseProgramsEvent.Type.PROGRAM_INSERTED)) {
            ActionsHelper.addAction(actionMenu, event.getProgram());
        } else if (event.getType().equals(
                DatabaseProgramsEvent.Type.PROGRAM_DELETED)) {
            ActionsHelper.removeAction(actionMenu, event.getProgram());
        }

        PopupMenuThumbnails.INSTANCE.getMenuActions().setEnabled(
            DatabasePrograms.INSTANCE.hasAction());
    }
}
