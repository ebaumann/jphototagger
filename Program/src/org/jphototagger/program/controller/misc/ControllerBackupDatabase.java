/*
 * @(#)ControllerBackupDatabase.java    Created on 2010-03-07
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

package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.helper.BackupDatabase;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;




import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ControllerBackupDatabase extends Controller {
    private final JMenuItem       menuItemBackupDb =
        GUI.INSTANCE.getAppFrame().getMenuItemBackupDatabase();

    public ControllerBackupDatabase() {
        listenToActionsOf(menuItemBackupDb);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == menuItemBackupDb;
    }

    @Override
    protected void action(ActionEvent evt) {
        addBackupTask();
    }

    @Override
    protected void action(KeyEvent evt) {

        // Ignore
    }

    private void addBackupTask() {
        MessageDisplayer.information(null,
                                     "ControllerBackupDatabase.Info.ChooseDir");
        AppLifeCycle.INSTANCE.addFinalTask(BackupDatabase.INSTANCE);
    }
}
