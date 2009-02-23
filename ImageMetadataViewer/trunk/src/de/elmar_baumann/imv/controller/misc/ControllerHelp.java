package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import de.elmar_baumann.lib.event.HelpBrowserAction;
import de.elmar_baumann.lib.event.HelpBrowserListener;
import de.elmar_baumann.lib.persistence.PersistentSettings;
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
    private static final String keyCurrentUrl = ControllerHelp.class.getName() + ".CurrentURL";
    private String currentUrl = PersistentSettings.INSTANCE.getString(keyCurrentUrl);

    public ControllerHelp() {
        help.setContentsUrl(Bundle.getString("Help.Url.Contents"));
        listen();
    }

    private void listen() {
        help.addActionListener(this);
    }

    @Override
    public void actionPerformed(HelpBrowserAction action) {
        if (action.getType().equals(HelpBrowserAction.Type.URL_CHANGED)) {
            setCurrentUrl(action);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showHelp();
    }

    private void setCurrentUrl(HelpBrowserAction action) {
        URL url = action.getUrl();
        if (!url.getProtocol().startsWith("http")) {
            currentUrl = HelpBrowser.getLastPathComponent(url);
            PersistentSettings.INSTANCE.setString(currentUrl, keyCurrentUrl);
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
