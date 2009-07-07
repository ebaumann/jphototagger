package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.IptcToXmpDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: IPTC-Daten nach XMP schreiben.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/30
 */
public final class ControllerIptcToXmp implements ActionListener {

    public ControllerIptcToXmp() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemToolIptcToXmp().addActionListener(
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showIptcToXmpDialog();
    }

    private void showIptcToXmpDialog() {
        IptcToXmpDialog dialog = new IptcToXmpDialog();
        dialog.setVisible(true);
    }
}
