package org.jphototagger.program.app;

import org.jphototagger.lib.dialog.LongMessageDialog;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.AWTEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JPhotoTagger's event queue.
 * <p>
 * Catches throwables and displays a dialog with information about the cause.
 *
 * @author Elmar Baumann
 */
public final class AppEventQueue extends java.awt.EventQueue {
    private static final String NEWLINE = SystemProperties.getLineSeparator();
    private static final String FILE_ENCODING = SystemProperties.getFileEncoding();

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
        LongMessageDialog dlg = new LongMessageDialog(GUI.getAppFrame(), true, UserSettings.INSTANCE.getSettings(),
                                    null);

        dlg.setTitle(JptBundle.INSTANCE.getString("AppEventQueue.Error.Title"));
        dlg.setErrorIcon();
        dlg.setMail(AppInfo.MAIL_TO_ADDRESS_BUGS, AppInfo.MAIL_SUBJECT_BUGS);
        dlg.setShortMessage(JptBundle.INSTANCE.getString("AppEventQueue.Error.Message"));
        dlg.setLongMessage(createMessage(t));

        return dlg;
    }

    private String createMessage(Throwable t) {
        String message = AppLogger.getMessage(t);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        t.printStackTrace(ps);

        return message + NEWLINE + baos.toString() + NEWLINE + getAllMessages();
    }

    private String getAllMessages() {
        File logfileAllMessages = new File(AppLoggingSystem.geLogfilePathAllMessages());

        try {
            return FileUtil.getContentAsString(logfileAllMessages, FILE_ENCODING);
        } catch (IOException ex) {
            Logger.getLogger(AppEventQueue.class.getName()).log(Level.SEVERE, null, ex);

            return "";
        }
    }

    private void hideWaitDisplay() {
        if (WaitDisplay.isShow()) {
            WaitDisplay.hide();
        }
    }
}
