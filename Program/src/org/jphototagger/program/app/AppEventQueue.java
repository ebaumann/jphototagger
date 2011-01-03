/*
 * @(#)AppEventQueue.java    Created on 2010-04-02
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

package org.jphototagger.program.app;

import org.jphototagger.lib.dialog.LongMessageDialog;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.AWTEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * JPhotoTagger's event queue.
 * <p>
 * Catches throwables and displays a dialog with information about the cause.
 *
 * @author Elmar Baumann
 */
public final class AppEventQueue extends java.awt.EventQueue {
    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable t) {
            AppLogger.logSevere(AppEventQueue.class, t);
            getDialog(t).setVisible(true);
            hideWaitDisplay();
        }
    }

    private LongMessageDialog getDialog(Throwable t) {
        LongMessageDialog dlg = new LongMessageDialog(GUI.getAppFrame(), true,
                                    UserSettings.INSTANCE.getSettings(), null);

        dlg.setTitle(JptBundle.INSTANCE.getString("AppEventQueue.Error.Title"));
        dlg.setErrorIcon();
        dlg.setMail(AppInfo.MAIL_TO_ADDRESS_BUGS, AppInfo.MAIL_SUBJECT_BUGS);
        dlg.setShortMessage(
            JptBundle.INSTANCE.getString("AppEventQueue.Error.Message"));
        dlg.setLongMessage(createMessage(t));

        return dlg;
    }

    private String createMessage(Throwable t) {
        String                message = AppLogger.getMessage(t);
        ByteArrayOutputStream baos    = new ByteArrayOutputStream();
        PrintStream           ps      = new PrintStream(baos);

        t.printStackTrace(ps);

        return message + "\n" + baos.toString();
    }

    private void hideWaitDisplay() {
        if (WaitDisplay.isShow()) {
            WaitDisplay.hide();
        }
    }
}
