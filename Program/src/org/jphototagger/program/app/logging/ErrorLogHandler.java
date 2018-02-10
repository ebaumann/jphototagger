package org.jphototagger.program.app.logging;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.StatusLineElementProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public final class ErrorLogHandler extends Handler implements MouseListener, StatusLineElementProvider {

    static final String PREF_KEY_ERROR_TEXTS_NOT_IN_GUI = "AppLoggingSystemSettings.ErrorTextsNotInGui";
    private static final long MILLISECONDS_ERROR_DISPLAY = 4000;
    private static final String LABEL_ERROR_TOOLTIP_TEXT = Bundle.getString(ErrorLogHandler.class, "ErrorLogHandler.ErrorLabel.TooltipText");
    private static final String STATUSBAR_INFO_TEXT = Bundle.getString(ErrorLogHandler.class, "ErrorLogHandler.StatusbBar.InfoText");
    private static final int MIN_LOG_LEVEL_VALUE = Level.WARNING.intValue();
    private final Collection<String> ignoreErrorMessages = new CopyOnWriteArrayList<>();
    private final JLabel errorLabel = new JLabel();

    public ErrorLogHandler() {
        initIgnoreErrorMessages();
        listen();
    }

    private void initIgnoreErrorMessages() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            ignoreErrorMessages.addAll(prefs.getStringCollection(PREF_KEY_ERROR_TEXTS_NOT_IN_GUI));
        }
    }

    private void listen() {
        errorLabel.addMouseListener(this);
        Logger.getLogger("").addHandler(this);
        // Separately from Root Logger "" because JPhotoTagger's logging system doesn't use parent handlers
        Logger.getLogger("org.jphototagger").addHandler(this);
        AnnotationProcessor.process(this);
    }

    private boolean isIgnore(String message) {
        for (String userMessage : ignoreErrorMessages) {
            if (message.contains(userMessage)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void publish(LogRecord record) {
        int recordLevelValue = record.getLevel().intValue();
        boolean isError = recordLevelValue >= MIN_LOG_LEVEL_VALUE;
        if (isError && !isIgnore(resolveMessage(record))) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    showErrorLabel();
                }
            });
        }
    }

    private String resolveMessage(LogRecord record) {
        String message = record.getMessage();
        if (message == null) {
            Throwable thrown = record.getThrown();
            String thrownMessage = thrown == null ? "" : thrown.getMessage();
            return thrown == null
                    ? ""
                    : thrownMessage == null
                    ? ""
                    : thrownMessage;
        }
        Object[] parameters = record.getParameters();
        if (parameters == null) {
            return message;
        }
        return MessageFormat.format(message, parameters);
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
        errorLabel.setIcon(Icons.getIcon("icon_error.png"));
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

    @Override
    public int getPosition() {
        return 500;
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    @SuppressWarnings("unchecked")
    public void preferencesChangedEvent(PreferencesChangedEvent e) {
        if (PREF_KEY_ERROR_TEXTS_NOT_IN_GUI.equals(e.getKey())) {
            ignoreErrorMessages.clear();
            ignoreErrorMessages.addAll((Collection<? extends String>)e.getNewValue());
        }
    }
}
