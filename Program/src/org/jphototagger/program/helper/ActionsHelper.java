/*
 * @(#)ActionsHelper.java    Created on 2010-01-24
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

package org.jphototagger.program.helper;

import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.ProgressBar;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ActionsHelper {
    public static JMenu actionsAsMenu() {
        List<Program> actions =
            DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);
        JMenu menu = new JMenu(
                         JptBundle.INSTANCE.getString(
                             "ActionsHelper.ActionMenu.DisplayName"));

        for (Program action : actions) {
            menu.add(new JMenuItem(new ActionStarter(action, action)));
        }

        return menu;
    }

    private static void reorderActions(JMenu actionsMenu) {
        actionsMenu.removeAll();

        List<Program> actions =
            DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);

        for (Program action : actions) {
            actionsMenu.add(new JMenuItem(new ActionStarter(action, action)));
        }
    }

    public static boolean existsAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        return getIndexOfAction(actionsMenu, action) >= 0;
    }

    public static void addAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        actionsMenu.add(new ActionStarter(action, action));
        actionsMenu.setEnabled(true);
    }

    public static void removeAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        int index = getIndexOfAction(actionsMenu, action);

        if (index >= 0) {
            actionsMenu.remove(index);
            actionsMenu.setEnabled(actionsMenu.getItemCount() > 0);
        }
    }

    public static void updateAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        int index = getIndexOfAction(actionsMenu, action);
        int seqNr = action.getSequenceNumber();

        if ((index >= 0) && (index == seqNr)) {
            Action a = actionsMenu.getItem(index).getAction();

            if (a instanceof ActionStarter) {
                Program actionProgram = ((ActionStarter) a).getAction();

                actionProgram.set(action);
                setNameAndIcon(a, actionProgram);
            }
        } else if ((index >= 0) && (index != seqNr)) {
            reorderActions(actionsMenu);
        }
    }

    private static void setNameAndIcon(Action action, Program ap) {
        action.putValue(Action.NAME, ap.getAlias());

        if (ap.getFile().exists()) {
            action.putValue(Action.SMALL_ICON,
                            IconUtil.getSystemIcon(ap.getFile()));
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
        private final Object            pBarOwner;

        ActionStarter(Program action, Object progressBarOwner) {
            this.action           = action;
            this.pBarOwner = progressBarOwner;
            setNameAndIcon(this, action);
        }

        public Program getAction() {
            return action;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            if (!tnPanel.isFileSelected()) {
                return;
            }

            StartPrograms starter = new StartPrograms(
                                        ProgressBar.INSTANCE.getResource(
                                            pBarOwner));

            starter.startProgram(action, tnPanel.getSelectedFiles());
            ProgressBar.INSTANCE.releaseResource(pBarOwner);
        }
    }


    private ActionsHelper() {}
}
