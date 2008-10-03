package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Hilfe anzeigen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public class ControllerHelp extends Controller implements ActionListener {

    private HelpBrowser help = HelpBrowser.getInstance();

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showHelp();
        }
    }

    private void showHelp() {
        help.setContentsUri(Bundle.getString("ControllerHelp.PathHelpFileIndex"));
        if (help.isVisible()) {
            help.toFront();
        } else {
            help.setVisible(true);
        }
    }
}
