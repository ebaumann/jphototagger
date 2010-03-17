/*
 * @(#)ActionsHelper.java    2010-01-24
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ActionsHelper {
    public static JMenu actionsAsMenu() {
        List<Program> actions =
            DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);
        JMenu menu = new JMenu(
                         JptBundle.INSTANCE.getString(
                             "ActionsHelper.ActionMenu.DisplayName"));

        for (Program action : actions) {
            menu.add(new JMenuItem(new ActionStarter(action)));
        }

        return menu;
    }

    public static boolean existsAction(JMenu actionsMenu, Program action) {
        return getIndexOfAction(actionsMenu, action) >= 0;
    }

    public static void addAction(JMenu actionsMenu, Program action) {
        actionsMenu.add(new ActionStarter(action));
    }

    public static void removeAction(JMenu actionsMenu, Program action) {
        int index = getIndexOfAction(actionsMenu, action);

        if (index >= 0) {
            actionsMenu.remove(index);
        }
    }

    private static int getIndexOfAction(JMenu actionsMenu, Program action) {
        int itemCount = actionsMenu.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            Action a = actionsMenu.getItem(i).getAction();

            if (a instanceof ActionStarter) {
                Program actionProgram = ((ActionStarter) a).getAction();

                if (actionProgram.equals(action)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static class ActionStarter extends AbstractAction {
        private static final long       serialVersionUID = 1L;
        private final transient Program action;

        public ActionStarter(Program action) {
            super(action.getAlias());
            this.action = action;
        }

        public Program getAction() {
            return action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ThumbnailsPanel thumbnailsPanel =
                GUI.INSTANCE.getAppPanel().getPanelThumbnails();

            if (thumbnailsPanel.getSelectionCount() <= 0) {
                return;
            }

            ProgressBar   progressBar = ProgressBar.INSTANCE;
            StartPrograms starter     =
                new StartPrograms(progressBar.getResource(this));

            starter.startProgram(action, thumbnailsPanel.getSelectedFiles());
            progressBar.releaseResource(this);
        }
    }


    private ActionsHelper() {}
}
