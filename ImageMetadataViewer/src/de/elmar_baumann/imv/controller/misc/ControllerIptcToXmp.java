package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.IptcToXmpDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: IPTC-Daten nach XMP schreiben.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/30
 */
public class ControllerIptcToXmp extends Controller implements ActionListener {

    public ControllerIptcToXmp() {
        Panels.getInstance().getAppFrame().getMenuItemToolIptcToXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            IptcToXmpDialog dialog = new IptcToXmpDialog();
            dialog.setVisible(true);
        }
    }
}
