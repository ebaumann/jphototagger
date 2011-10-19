package org.jphototagger.program.app.logging;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JLabel;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.StatusLineElementProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public final class ErrorLogHandler extends Handler implements MouseListener, StatusLineElementProvider {

    private static final long MILLISECONDS_ERROR_DISPLAY = 4000;
    private static final String LABEL_ERROR_TOOLTIP_TEXT = Bundle.getString(ErrorLogHandler.class, "ErrorLogHandler.ErrorLabel.TooltipText");
    private static final String STATUSBAR_INFO_TEXT = Bundle.getString(ErrorLogHandler.class, "ErrorLogHandler.StatusbBar.InfoText");
    private static final int MIN_LOG_LEVEL_VALUE = Level.WARNING.intValue();
    private final JLabel errorLabel = new JLabel();

    public ErrorLogHandler() {
        listen();
    }

    private void listen() {
        errorLabel.addMouseListener(this);
        Logger.getLogger("").addHandler(this);
        // Separately from Root Logger "" because JPhotoTagger's logging system doesn't use parent handlers
        Logger.getLogger("org.jphototagger").addHandler(this);
    }

    @Override
    public void publish(LogRecord record) {
        int recordLevelValue = record.getLevel().intValue();
        boolean isError = recordLevelValue >= MIN_LOG_LEVEL_VALUE;

        if (isError) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    showErrorLabel();
                }
            });
        }
    }

    @Override
    public void flush() {
        // ignore
    }

    @Override
    public void close() throws SecurityException {
        // ignore
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isLeftClick(evt)) {
            ShowErrorLogfileAction.showErrorLogfile();

            errorLabel.setIcon(null);
            errorLabel.setToolTipText("");
        }
    }

    private void showErrorLabel() {
        MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);

        mainWindowManager.setMainWindowStatusbarText(STATUSBAR_INFO_TEXT, MessageType.ERROR, MILLISECONDS_ERROR_DISPLAY);

        errorLabel.setIcon(IconUtil.getImageIcon(ErrorLogHandler.class, "error.png"));
        errorLabel.setToolTipText(LABEL_ERROR_TOOLTIP_TEXT);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignore
    }

    @Override
    public Component getStatusLineElement() {
        return errorLabel;
    }
}
