/*
 * @(#)ActionsMenuUpdater.java    Created on 2010-01-24
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

import java.awt.EventQueue;

import javax.swing.JMenu;

/**
 * Listens to {@link DatabasePrograms} events and inserts or removes actions
 * from the {@link PopupMenuThumbnails#getMenuActions()}.
 *
 * @author Elmar Baumann
 */
public final class ActionsMenuUpdater
        implements DatabaseProgramsListener {
    public ActionsMenuUpdater() {
        setMenuItemEnabled();
        listen();
    }

    private void listen() {
        DatabasePrograms.INSTANCE.addListener(this);
    }

    private void setMenuItemEnabled() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JMenu actionMenu =
                    PopupMenuThumbnails.INSTANCE.getMenuActions();

                actionMenu.setEnabled(DatabasePrograms.INSTANCE.hasAction());
            }
        });
    }

    @Override
    public void programDeleted(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu =
                        PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.removeAction(actionMenu, program);
                    setMenuItemEnabled();
                }
            });
        }
    }

    @Override
    public void programInserted(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu =
                        PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.addAction(actionMenu, program);
                }
            });
        }
    }

    @Override
    public void programUpdated(final Program program) {
        if (program.isAction()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JMenu actionMenu =
                        PopupMenuThumbnails.INSTANCE.getMenuActions();

                    ActionsHelper.updateAction(actionMenu, program);
                }
            });
        }
    }
}
