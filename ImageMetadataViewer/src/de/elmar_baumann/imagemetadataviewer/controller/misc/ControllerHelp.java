package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Kontrolliert die Aktion: Hilfe anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public class ControllerHelp extends Controller implements ActionListener {

    private HelpBrowser help = HelpBrowser.getInstance();
    private URL url = this.getClass().getResource(Bundle.getString("ControllerHelp.PathHelpFileIndex")); // NOI18N

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showHelp();
        }
    }

    private void showHelp() {
        help.showUrl(url);
        if (help.isVisible()) {
            help.toFront();
        } else {
            help.setVisible(true);
        }
    }
}
