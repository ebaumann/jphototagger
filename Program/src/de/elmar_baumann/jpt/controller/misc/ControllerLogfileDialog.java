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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.app.AppLoggingSystem;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.event.listener.ErrorListener;
import de.elmar_baumann.jpt.event.listener.impl.ErrorListeners;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.lib.componentutil.MessageLabel;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import de.elmar_baumann.lib.event.util.MouseEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;

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
    public void mouseClicked(MouseEvent e) {
        if (MouseEventUtil.isLeftClick(e)
                && itemShowDlgErrorLogfile.isEnabled()) {
            showLogfileDialog(AppLoggingSystem.getCurrentLogfileName(),
                              XMLFormatter.class);
            labelError.setIcon(null);
            labelError.setToolTipText("");
        }
    }

    private void error() {
        GUI.INSTANCE.getAppPanel().setStatusbarText(STATUSBAR_ERROR_TEXT,
                MessageLabel.MessageType.ERROR, MILLISECONDS_ERROR_DISPLAY);
        itemShowDlgErrorLogfile.setEnabled(true);
        labelError.setIcon(AppLookAndFeel.getIcon("icon_error12.png"));
        labelError.setToolTipText(LABEL_ERROR_TOOLTIP_TEXT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == itemShowDlgErrorLogfile) {
            showLogfileDialog(AppLoggingSystem.getCurrentLogfileName(),
                              XMLFormatter.class);
        } else if (source == itemShowDlgAllLogfile) {
            showLogfileDialog(AppLoggingSystem.getCurrentAllLogifleName(),
                              SimpleFormatter.class);
        }
    }

    private void showLogfileDialog(String logfilename,
                                   Class<?> formatterClass) {
        LogfileDialog dialog = new LogfileDialog(GUI.INSTANCE.getAppFrame(),
                                   logfilename, formatterClass);

        dialog.addWindowListener(new SizeAndLocationController());
        dialog.setVisible(true);
    }

    @Override
    public void error(Object source, String message) {
        error();
    }
}
