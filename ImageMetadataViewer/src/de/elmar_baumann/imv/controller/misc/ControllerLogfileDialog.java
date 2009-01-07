package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
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
public final class ControllerLogfileDialog extends Controller
    implements ActionListener, ErrorListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JButton buttonLogfileDialog = appPanel.getButtonLogfileDialog();
    private static final String iconPath = AppSettings.getIconPath();
    private static final ImageIcon iconOk = IconUtil.getImageIcon(iconPath + "/icon_ok.png");
    private static final ImageIcon iconError = IconUtil.getImageIcon(iconPath + "/icon_error.png");

    public ControllerLogfileDialog() {
        buttonLogfileDialog.setIcon(iconOk);
        listenToActionSources();
    }

    private void listenToActionSources() {
        buttonLogfileDialog.addActionListener(this);
        ErrorListeners.getInstance().addErrorListener(this);
    }

    @Override
    public void error(ErrorEvent evt) {
        if (isControl()) {
            setError(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            showLogfileDialog();
        }
    }

    private void showLogfileDialog() {
        LogfileDialog dialog = new LogfileDialog(
            null,
            AppSettings.getLogfileName(),
            UserSettings.getInstance().getLogfileFormatterClass());
        dialog.setVisible(true);
        setError(false);
    }

    private void setError(boolean error) {
        buttonLogfileDialog.setIcon(error ? iconError : iconOk);
        buttonLogfileDialog.repaint();
    }
}
