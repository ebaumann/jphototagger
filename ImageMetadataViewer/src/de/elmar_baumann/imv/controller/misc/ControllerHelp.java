package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
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
public class ControllerHelp extends Controller implements ActionListener,
    HelpBrowserListener {

    private HelpBrowser help = HelpBrowser.getInstance();
    private static final String keyLastUrl = ControllerHelp.class.getName() + ".LastURL";
    private String lastUrl = PersistentSettings.getInstance().getString(keyLastUrl);

    public ControllerHelp() {
        help.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showHelp();
        }
    }

    private void showHelp() {
        help.setContentsUrl(Bundle.getString("Settings.PathHelpFileIndex"));
        if (!lastUrl.isEmpty()) {
            help.setStartUrl(lastUrl);
        }
        if (help.isVisible()) {
            help.toFront();
        } else {
            help.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(HelpBrowserAction action) {
        if (action.getType().equals(HelpBrowserAction.Type.UrlChanged)) {
            URL url = action.getUrl();
            if (!url.getProtocol().startsWith("http")) {
                lastUrl = HelpBrowser.getLastPathComponent(url);
                PersistentSettings.getInstance().setString(lastUrl, keyLastUrl);
            }
        }
    }
}
