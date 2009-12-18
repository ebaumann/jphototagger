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

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.AppLoggingSystem;
import de.elmar_baumann.jpt.event.ErrorEvent;
import de.elmar_baumann.jpt.event.listener.ErrorListener;
import de.elmar_baumann.jpt.event.listener.impl.ErrorListeners;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ErrorPopupPanel;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-11
 */
public final class ControllerLogfileDialog implements ActionListener,
                                                      ErrorListener {

    private static final long ERROR_POPUP_MILLISECONDS = 2000;
    private static final Icon ICON_OK = AppLookAndFeel.getIcon("icon_ok.png"); // NOI18N
    private static final Icon ICON_ERROR = AppLookAndFeel.getIcon("icon_error.png"); // NOI18N

    public ControllerLogfileDialog() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemDisplayLogfile().addActionListener(this);
        ErrorListeners.INSTANCE.addErrorListener(this);
    }

    @Override
    public void error(ErrorEvent evt) {
        showErrorPopup();
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

    private void showErrorPopup() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                PopupFactory    factory         = PopupFactory.getSharedInstance();
                ErrorPopupPanel errorPanel      = new ErrorPopupPanel();
                Component       targetComponent = GUI.INSTANCE.getAppPanel().getLabelInfo();
                int             x               = targetComponent.getLocationOnScreen().x;
                int             y               = targetComponent.getLocationOnScreen().y - targetComponent.getHeight() - 10;
                Popup           popup           = factory.getPopup(targetComponent, errorPanel, x, y);
                popup.show();
                Thread thread = new Thread(new HidePopup(popup));
                thread.setName("Hiding error popup @ " + ControllerLogfileDialog.class); // NOI18N
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("Showing error popup"); // NOI18N
        thread.start();
    }

    private class HidePopup implements Runnable {

        private final Popup popup;

        public HidePopup(Popup popup) {
            this.popup = popup;

        }

        @Override
        public void run() {
            try {
                Thread.sleep(ERROR_POPUP_MILLISECONDS);
            } catch (InterruptedException ex) {
                AppLog.logSevere(ControllerLogfileDialog.class, ex);
            }
            popup.hide();
        }
    }
}
