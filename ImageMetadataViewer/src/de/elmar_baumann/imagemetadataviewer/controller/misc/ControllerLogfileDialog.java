package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.event.ErrorEvent;
import de.elmar_baumann.imagemetadataviewer.event.ErrorListener;
import de.elmar_baumann.imagemetadataviewer.event.listener.ErrorListeners;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.lib.dialog.LogfileDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Kontrolliert die Aktion: Logfiledialog anzeigen. Diese wird ausgelöst von
 * einem Button des {@link de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/11
 */
public class ControllerLogfileDialog extends Controller
    implements ActionListener, ErrorListener {

    private static final Color colorOk = new Color(193, 209, 169);
    private static final Color colorError = new Color(237, 77, 77);
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JButton buttonLogfileDialog = appPanel.getButtonLogfileDialog();

    public ControllerLogfileDialog() {
        buttonLogfileDialog.setBackground(colorOk);
        listenToActionSource();
    }

    private void listenToActionSource() {
        buttonLogfileDialog.addActionListener(this);
        ErrorListeners.getInstance().addErrorListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
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

    @Override
    public void error(ErrorEvent evt) {
        if (isStarted()) {
            setError(true);
        }
    }

    private void setError(boolean error) {
        buttonLogfileDialog.setBackground(error ? colorError : colorOk);
        buttonLogfileDialog.repaint();
    }
}
