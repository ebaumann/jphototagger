package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import de.elmar_baumann.lib.event.HelpBrowserEvent;
import de.elmar_baumann.lib.event.listener.HelpBrowserListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Kontrolliert die Aktion: Hilfe anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public final class ControllerHelp implements ActionListener,
                                             HelpBrowserListener {

    private final HelpBrowser help = HelpBrowser.INSTANCE;
    private static final String KEY_CURRENT_URL =
            ControllerHelp.class.getName() + ".CurrentURL"; // NOI18N
    private String currentUrl =
            UserSettings.INSTANCE.getSettings().getString(KEY_CURRENT_URL);

    public ControllerHelp() {
        help.setContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
        help.setIconImages(AppIcons.getAppIcons());
        listen();
    }

    private void listen() {
        help.addActionListener(this);
    }

    @Override
    public void actionPerformed(HelpBrowserEvent action) {
        if (action.getType().equals(HelpBrowserEvent.Type.URL_CHANGED)) {
            setCurrentUrl(action);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showHelp();
    }

    private void setCurrentUrl(HelpBrowserEvent action) {
        URL url = action.getUrl();
        if (!url.getProtocol().startsWith("http")) {
            currentUrl = HelpBrowser.getLastPathComponent(url);
            UserSettings.INSTANCE.getSettings().setString(
                    currentUrl, KEY_CURRENT_URL);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void showHelp() {
        if (!currentUrl.isEmpty()) {
            help.setStartUrl(currentUrl);
        }
        if (help.isVisible()) {
            help.toFront();
        } else {
            help.setVisible(true);
        }
    }
}
