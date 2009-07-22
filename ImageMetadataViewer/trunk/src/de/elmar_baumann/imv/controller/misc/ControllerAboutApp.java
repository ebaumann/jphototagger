package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.app.AppInfo;
import de.elmar_baumann.imv.app.MessageDisplayer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
public final class ControllerAboutApp implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        showAbout();
    }

    private void showAbout() {
        MessageDisplayer.information(null,
                "ControllerAboutApp.Info.About", // NOI18N
                AppInfo.APP_NAME, AppInfo.APP_VERSION);
    }
}
