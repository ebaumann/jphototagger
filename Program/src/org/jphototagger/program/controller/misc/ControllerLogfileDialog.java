package org.jphototagger.program.controller.misc;

import org.jphototagger.lib.componentutil.MessageLabel;
import org.jphototagger.lib.dialog.LogfileDialog;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.app.AppLoggingSystem;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.event.listener.ErrorListener;
import org.jphototagger.program.event.listener.impl.ErrorListeners;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen.
 *
 * @author Elmar Baumann
 */
public final class ControllerLogfileDialog extends MouseAdapter implements ActionListener, ErrorListener {
    private static final long MILLISECONDS_ERROR_DISPLAY = 4000;
    private static final String LABEL_ERROR_TOOLTIP_TEXT =
        JptBundle.INSTANCE.getString("ControllerLogfileDialog.LabelErrorTooltipText");
    private static final String STATUSBAR_ERROR_TEXT =
        JptBundle.INSTANCE.getString("ControllerLogfileDialog.Error.Info");

    public ControllerLogfileDialog() {
        listen();
    }

    private void listen() {
        getItemErrorLogfile().addActionListener(this);
        getItemAllLogfile().addActionListener(this);
        GUI.getAppPanel().getLabelError().addMouseListener(this);
        ErrorListeners.INSTANCE.add(this);
    }

    private JMenuItem getItemAllLogfile() {
        return GUI.getAppFrame().getMenuItemDisplayAllLogfile();
    }

    private JMenuItem getItemErrorLogfile() {
        return GUI.getAppFrame().getMenuItemDisplayLogfile();
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

    private void error() {
        GUI.getAppPanel().setStatusbarText(STATUSBAR_ERROR_TEXT, MessageLabel.MessageType.ERROR,
                                           MILLISECONDS_ERROR_DISPLAY);
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
    public void error(Object source, String message) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                error();
            }
        });
    }
}
