/*
 * @(#)JptEventQueue.java    Created on 2010-04-02
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

package org.jphototagger.program.app;

import org.jphototagger.lib.dialog.LongMessageDialog;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.awt.AWTEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * JPhotoTagger's event queue.
 * <p>
 * Displays on uncaught exceptions a dialog with information about the cause.
 *
 * @author Elmar Baumann
 */
public final class JptEventQueue extends java.awt.EventQueue {
    @Override
    protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Throwable t) {
            t.printStackTrace();

            LongMessageDialog dlg =
                new LongMessageDialog(GUI.INSTANCE.getAppFrame(), true,
                                      UserSettings.INSTANCE.getSettings(),
                                      null);

            dlg.setTitle(
                JptBundle.INSTANCE.getString("JptEventQueue.Error.Title"));
            dlg.setErrorIcon();
            dlg.setMail(AppInfo.MAIL_TO_ADDRESS_BUGS,
                        AppInfo.MAIL_SUBJECT_BUGS);
            dlg.setShortMessage(
                JptBundle.INSTANCE.getString("JptEventQueue.Error.Message"));
            dlg.setLongMessage(createMessage(t));
            dlg.setVisible(true);
        }
    }

    private String createMessage(Throwable t) {
        String message = t.getLocalizedMessage();

        if ((message == null) || (message.length() == 0)) {
            message = "No message: " + t.getClass();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream           ps   = new PrintStream(baos);

        t.printStackTrace(ps);

        return message + "\n" + baos.toString();
    }
}
