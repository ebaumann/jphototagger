package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListener;
import de.elmar_baumann.imv.event.listener.impl.ErrorListeners;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ErrorPopupPanel;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen. Diese wird ausgelöst von
 * einem Button des {@link de.elmar_baumann.imv.view.panels.AppPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-11
 */
public final class ControllerLogfileDialog implements ActionListener,
                                                      ErrorListener {

    private static final long ERROR_POPUP_MILLISECONDS = 2000;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JButton buttonLogfileDialog =
            appPanel.getButtonLogfileDialog();
    private static final Icon ICON_OK = AppIcons.getIcon("icon_ok.png"); // NOI18N
    private static final Icon ICON_ERROR = AppIcons.getIcon("icon_error.png"); // NOI18N

    public ControllerLogfileDialog() {
        buttonLogfileDialog.setIcon(ICON_OK);
        listen();
    }

    private void listen() {
        buttonLogfileDialog.addActionListener(this);
        ErrorListeners.INSTANCE.addErrorListener(this);
    }

    @Override
    public void error(ErrorEvent evt) {
        setError(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showLogfileDialog();
    }

    private void showLogfileDialog() {
        LogfileDialog dialog = new LogfileDialog(
                null,
                AppLog.getLogfileName(),
                UserSettings.INSTANCE.getLogfileFormatterClass());
        dialog.setVisible(true);
        setError(false);
    }

    private void setError(boolean error) {
        buttonLogfileDialog.setIcon(error
                                    ? ICON_ERROR
                                    : ICON_OK);
        buttonLogfileDialog.repaint();
        showErrorPopup();
    }

    private void showErrorPopup() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                PopupFactory factory = PopupFactory.getSharedInstance();
                ErrorPopupPanel errorPanel = new ErrorPopupPanel();
                int x = buttonLogfileDialog.getLocationOnScreen().x +
                        buttonLogfileDialog.getWidth();
                int y = buttonLogfileDialog.getLocationOnScreen().y -
                        buttonLogfileDialog.getHeight() - 10;
                Popup popup = factory.getPopup(buttonLogfileDialog, errorPanel,
                        x, y);
                popup.show();
                Thread thread = new Thread(new HidePopup(popup));
                thread.setName("Hiding error popup @ " + // NOI18N
                        ControllerLogfileDialog.class.getName());
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
