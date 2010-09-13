/*
 * @(#)ControllerLogfileDialog.java    Created on 2008-09-11
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

package org.jphototagger.program.controller.misc;

import org.jphototagger.program.app.AppLoggingSystem;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.event.listener.ErrorListener;
import org.jphototagger.program.event.listener.impl.ErrorListeners;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.frames.AppFrame;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.dialog.LogfileDialog;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import org.jphototagger.program.UserSettings;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen.
 *
 * @author  Elmar Baumann
 */
public final class ControllerLogfileDialog extends MouseAdapter
        implements ActionListener, ErrorListener {
    private static final long   MILLISECONDS_ERROR_DISPLAY = 4000;
    private static final String LABEL_ERROR_TOOLTIP_TEXT   =
        JptBundle.INSTANCE.getString(
            "ControllerLogfileDialog.LabelErrorTooltipText");
    private static final String STATUSBAR_ERROR_TEXT =
        JptBundle.INSTANCE.getString("ControllerLogfileDialog.Error.Info");
    private final AppFrame  appFrame                =
        GUI.INSTANCE.getAppFrame();
    private final JMenuItem itemShowDlgErrorLogfile =
        appFrame.getMenuItemDisplayLogfile();
    private final JMenuItem itemShowDlgAllLogfile =
        appFrame.getMenuItemDisplayAllLogfile();
    private final JLabel labelError =
        GUI.INSTANCE.getAppPanel().getLabelError();

    public ControllerLogfileDialog() {
        listen();
    }

    private void listen() {
        itemShowDlgErrorLogfile.addActionListener(this);
        itemShowDlgAllLogfile.addActionListener(this);
        labelError.addMouseListener(this);
        ErrorListeners.INSTANCE.add(this);
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isLeftClick(evt)
                && itemShowDlgErrorLogfile.isEnabled()) {
            showLogfileDialog(AppLoggingSystem.getLogfilePathErrorMessages(),
                              XMLFormatter.class);
            labelError.setIcon(null);
            labelError.setToolTipText("");
        }
    }

    private void error() {
        GUI.INSTANCE.getAppPanel().setStatusbarText(STATUSBAR_ERROR_TEXT,
                MessageLabel.MessageType.ERROR, MILLISECONDS_ERROR_DISPLAY);
        itemShowDlgErrorLogfile.setEnabled(true);
        labelError.setIcon(AppLookAndFeel.getIcon("icon_error.png"));
        labelError.setToolTipText(LABEL_ERROR_TOOLTIP_TEXT);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == itemShowDlgErrorLogfile) {
            showLogfileDialog(AppLoggingSystem.getLogfilePathErrorMessages(),
                              XMLFormatter.class);
        } else if (source == itemShowDlgAllLogfile) {
            showLogfileDialog(AppLoggingSystem.geLogfilePathAllMessages(),
                              SimpleFormatter.class);
        }
    }

    private void showLogfileDialog(String logfilename,
                                   Class<?> formatterClass) {
        LogfileDialog dlg = new LogfileDialog(GUI.INSTANCE.getAppFrame(),
                                              logfilename, formatterClass);

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), null);
        dlg.setVisible(true);
    }

    @Override
    public void error(Object source, String message) {
        error();
    }
}
