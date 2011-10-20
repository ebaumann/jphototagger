package org.jphototagger.program.help;

import java.awt.Desktop;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
final class SendMail {

    private static final Logger LOGGER = Logger.getLogger(SendMail.class.getName());

    static void sendMail(String to, String subject, String body) {
        try {
            URI uri = getMailtoUri(to, subject, body);

            LOGGER.log(Level.INFO, "Sending email to this URI: ''{0}''", uri);
            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            Logger.getLogger(ShowHelpAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = Bundle.getString(ShowHelpAction.class, "SendMail.Error.SendMail");
            MessageDisplayer.error(null, message);
        }
    }

    static URI getMailtoUri(String to, String subject, String body) throws URISyntaxException, UnsupportedEncodingException {
        String bodyPart = ((body == null) || body.trim().isEmpty())
                ? ""
                : "&body=" + body;

        return new URI("mailto", to + "?subject=" + subject + bodyPart, null);
    }

    private SendMail() {
    }
}
