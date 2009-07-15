package de.elmar_baumann.imv.controller.metadata;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.IptcToXmpDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

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
        PopupMenuThumbnails.INSTANCE.getItemIptcToXmp().addActionListener(this);
        GUI.INSTANCE.getAppPanel().getButtonIptcToXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (usingSelectedFiles(e)) {
            processSelectedFiles();
        } else {
            showIptcToXmpDialog();
        }
    }

    private void processSelectedFiles() {
        List<File> selectedFiles = GUI.INSTANCE.getAppPanel().getPanelThumbnails().
                getSelectedFiles();
        if (selectedFiles.size() > 0) {
            IptcToXmpDialog dialog = new IptcToXmpDialog();
            dialog.setFiles(selectedFiles);
            dialog.setVisible(true);
        }
    }

    private boolean usingSelectedFiles(ActionEvent e) {
        Object source = e.getSource();
        return source.equals(
                PopupMenuThumbnails.INSTANCE.getItemIptcToXmp()) ||
                source.equals(GUI.INSTANCE.getAppPanel().getButtonIptcToXmp());
    }

    private void showIptcToXmpDialog() {
        IptcToXmpDialog dialog = new IptcToXmpDialog();
        dialog.setVisible(true);
    }
}
