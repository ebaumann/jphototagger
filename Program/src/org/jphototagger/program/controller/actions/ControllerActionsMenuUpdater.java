/*
 * @(#)ControllerActionsMenuUpdater.java    Created on 2010-01-24
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

package org.jphototagger.program.controller.actions;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;
import org.jphototagger.program.helper.ActionsHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import javax.swing.JMenu;

/**
 * Listens to {@link DatabasePrograms} events and inserts or removes actions
 * from the {@link PopupMenuThumbnails#getMenuActions()}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerActionsMenuUpdater
        implements DatabaseProgramsListener {
    private final JMenu actionMenu =
        PopupMenuThumbnails.INSTANCE.getMenuActions();

    public ControllerActionsMenuUpdater() {
        actionMenu.setEnabled(DatabasePrograms.INSTANCE.hasAction());
        listen();
    }

    private void listen() {
        DatabasePrograms.INSTANCE.addListener(this);
    }

    @Override
    public void programDeleted(Program program) {
        if (program.isAction()) {
            ActionsHelper.removeAction(actionMenu, program);
        }
    }

    @Override
    public void programInserted(Program program) {
        if (program.isAction()) {
            ActionsHelper.addAction(actionMenu, program);
        }
    }

    @Override
    public void programUpdated(Program program) {
        if (program.isAction()) {
            ActionsHelper.updateAction(actionMenu, program);
        }
    }
}
