/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLoggingSystem;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-05
 */
public final class ControllerSendMail extends Controller {
    private static final String TO_ADDRESS_BUGS     = "eb@elmar-baumann.de";
    private static final String TO_ADDRESS_FEATURES = "eb@elmar-baumann.de";
    private static final String SUBJECT_BUGS        =
        JptBundle.INSTANCE.getString("ControllerSendMail.Subject.Bugs");
    private static final String SUBJECT_FEATURES =
        JptBundle.INSTANCE.getString("ControllerSendMail.Subject.Features");
    private final JMenuItem menuItemSendBugMail =
        GUI.INSTANCE.getAppFrame().getMenuItemSendBugMail();
    private final JMenuItem menuItemSendFeatureMail =
        GUI.INSTANCE.getAppFrame().getMenuItemSendFeatureMail();

    public ControllerSendMail() {
        listenToActionsOf(menuItemSendBugMail, menuItemSendFeatureMail);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return false;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        Object source = evt.getSource();

        return (source == menuItemSendBugMail)
               || (source == menuItemSendFeatureMail);
    }

    @Override
    protected void action(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == menuItemSendBugMail) {
            sendBugMail();
        } else if (source == menuItemSendFeatureMail) {
            sendFeatureMail();
        }
    }

    private void sendBugMail() {
        sendMail(
            TO_ADDRESS_BUGS, SUBJECT_BUGS,
            JptBundle.INSTANCE.getString(
                "ControllerSendMail.Info.AttachLogfile",
                AppLoggingSystem.getCurrentLogfileName()));
    }

    private void sendFeatureMail() {
        sendMail(TO_ADDRESS_FEATURES, SUBJECT_FEATURES, null);
    }

    @Override
    protected void action(KeyEvent evt) {}

    private void sendMail(String to, String subject, String body) {
        try {
            URI uri = getMailtoUri(to, subject, body);

            AppLogger.logInfo(ControllerSendMail.class,
                              "ControllerSendMail.Info.SendMail.Uri", uri);
            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            MessageDisplayer.error(null, "ControllerSendMail.Error.SendMail");
            AppLogger.logSevere(ControllerSendMail.class, ex);
        }
    }

    private URI getMailtoUri(String to, String subject, String body)
            throws URISyntaxException, UnsupportedEncodingException {
        String bodyPart = ((body == null) || body.trim().isEmpty())
                          ? ""
                          : "&body=" + body;

        return new URI("mailto", to + "?subject=" + subject + bodyPart, null);
    }
}
