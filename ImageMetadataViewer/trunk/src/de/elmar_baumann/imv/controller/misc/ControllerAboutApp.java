package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.AppInfo;
import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public final class ControllerAboutApp extends Controller implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            showAbout();
        }
    }

    private void showAbout() {
        MessageFormat message = new MessageFormat(Bundle.getString("ControllerAppAbout.InformationMessage.About")); // NOI18N
        Object[] params = new Object[]{AppInfo.appName, AppInfo.appVersion};
        JOptionPane.showMessageDialog(null,
            message.format(params),
            Bundle.getString("ControllerAppAbout.InformationMessage.About.Title"), // NOI18N
            JOptionPane.INFORMATION_MESSAGE,
            AppSettings.getMediumAppIcon());
    }
}
