package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.ErrorListener;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen. Diese wird ausgelöst von
 * einem Button des {@link de.elmar_baumann.imv.view.panels.AppPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/11
 */
public final class ControllerLogfileDialog implements ActionListener, ErrorListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JButton buttonLogfileDialog = appPanel.getButtonLogfileDialog();
    private static final String iconPath = AppIcons.getIconPath();
    private static final ImageIcon iconOk = IconUtil.getImageIcon(iconPath + "/icon_ok.png");
    private static final ImageIcon iconError = IconUtil.getImageIcon(iconPath + "/icon_error.png");

    public ControllerLogfileDialog() {
        buttonLogfileDialog.setIcon(iconOk);
        listen();
    }

    private void listen() {
        buttonLogfileDialog.addActionListener(this);
        ErrorListeners.getInstance().addErrorListener(this);
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
                UserSettings.getInstance().getLogfileFormatterClass());
        dialog.setVisible(true);
        setError(false);
    }

    private void setError(boolean error) {
        buttonLogfileDialog.setIcon(error ? iconError : iconOk);
        buttonLogfileDialog.repaint();
    }
}
