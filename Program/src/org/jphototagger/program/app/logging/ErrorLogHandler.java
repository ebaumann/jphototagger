package org.jphototagger.program.app.logging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.dialog.LogfileDialog;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class ErrorLogHandler extends Handler implements ActionListener, MouseListener {

    private static final long MILLISECONDS_ERROR_DISPLAY = 4000;
    private static final String LABEL_ERROR_TOOLTIP_TEXT = Bundle.getString(ErrorLogHandler.class, "ControllerLogfileDialog.LabelErrorTooltipText");
    private static final String STATUSBAR_ERROR_TEXT = Bundle.getString(ErrorLogHandler.class, "ControllerLogfileDialog.Error.Info");
    private static final int MIN_LOG_LEVEL_VALUE = Level.WARNING.intValue();

    public ErrorLogHandler() {
        listen();
    }

    private void listen() {
        getItemErrorLogfile().addActionListener(this);
        getItemAllLogfile().addActionListener(this);
        GUI.getAppPanel().getLabelError().addMouseListener(this);
        Logger.getLogger("").addHandler(this);
         // Separately from Root Logger "" because JPhotoTagger's logging system doesn't use parent handlers
        Logger.getLogger("org.jphototagger").addHandler(this);
    }

    private JMenuItem getItemAllLogfile() {
        return GUI.getAppFrame().getMenuItemDisplayAllLogfile();
    }

    private JMenuItem getItemErrorLogfile() {
        return GUI.getAppFrame().getMenuItemDisplayLogfile();
    }

    @Override
    public void publish(LogRecord record) {
        int value = record.getLevel().intValue();
        boolean isLog = value >= MIN_LOG_LEVEL_VALUE;

        if (isLog) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    showErrorLabel();
                }
            });
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isLeftClick(evt) && getItemErrorLogfile().isEnabled()) {
            showLogfileDialog(AppLoggingSystem.getLogfilePathErrorMessages(), XMLFormatter.class);

            JLabel labelError = GUI.getAppPanel().getLabelError();

            labelError.setIcon(null);
            labelError.setToolTipText("");
        }
    }

    private void showErrorLabel() {
        GUI.getAppPanel().setStatusbarText(STATUSBAR_ERROR_TEXT, MessageLabel.MessageType.ERROR, MILLISECONDS_ERROR_DISPLAY);
        getItemErrorLogfile().setEnabled(true);

        JLabel labelError = GUI.getAppPanel().getLabelError();

        labelError.setIcon(AppLookAndFeel.getIcon("icon_error.png"));
        labelError.setToolTipText(LABEL_ERROR_TOOLTIP_TEXT);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();

        if (source == getItemErrorLogfile()) {
            showLogfileDialog(AppLoggingSystem.getLogfilePathErrorMessages(), XMLFormatter.class);
        } else if (source == getItemAllLogfile()) {
            showLogfileDialog(AppLoggingSystem.geLogfilePathAllMessages(), SimpleFormatter.class);
        }
    }

    private void showLogfileDialog(String logfilename, Class<?> formatterClass) {
        LogfileDialog dlg = new LogfileDialog(GUI.getAppFrame(), logfilename, formatterClass);

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), null);
        dlg.setVisible(true);
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
    public void flush() {
        // ignore
    }

    @Override
    public void close() throws SecurityException {
        // ignore
    }
}