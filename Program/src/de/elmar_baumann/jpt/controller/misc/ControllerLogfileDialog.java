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
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLoggingSystem;
import de.elmar_baumann.jpt.event.ErrorEvent;
import de.elmar_baumann.jpt.event.listener.ErrorListener;
import de.elmar_baumann.jpt.event.listener.impl.ErrorListeners;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-11
 */
public final class ControllerLogfileDialog implements ActionListener,
                                                      ErrorListener {

    private static final long      MILLISECONDS_ERROR_DISPLAY = 4000;
    private final        JMenuItem itemShowDlg = GUI.INSTANCE.getAppFrame().getMenuItemDisplayLogfile();

    public ControllerLogfileDialog() {
        listen();
    }

    private void listen() {
        itemShowDlg.addActionListener(this);
        ErrorListeners.INSTANCE.addErrorListener(this);
    }

    @Override
    public void error(ErrorEvent evt) {
        GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString("Error.Info"), true, MILLISECONDS_ERROR_DISPLAY);
        itemShowDlg.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showLogfileDialog();
    }

    private void showLogfileDialog() {
        LogfileDialog dialog = new LogfileDialog(
                null,
                AppLoggingSystem.getCurrentLogfileName(),
                UserSettings.INSTANCE.getLogfileFormatterClass());
        dialog.setVisible(true);
    }

}
