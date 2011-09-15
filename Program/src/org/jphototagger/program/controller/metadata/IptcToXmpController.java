package org.jphototagger.program.controller.metadata;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.IptcToXmpDialog;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Kontrolliert die Aktion: IPTC-Daten nach XMP schreiben.
 *
 * @author Elmar Baumann
 */
public final class IptcToXmpController implements ActionListener {

    public IptcToXmpController() {
        listen();
    }

    private void listen() {
        GUI.getAppFrame().getMenuItemToolIptcToXmp().addActionListener(this);
        ThumbnailsPopupMenu.INSTANCE.getItemIptcToXmp().addActionListener(this);
        GUI.getAppPanel().getButtonIptcToXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (usingSelectedFiles(evt)) {
            processSelectedFiles();
        } else {
            showIptcToXmpDialog();
        }
    }

    private void processSelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            IptcToXmpDialog dlg = new IptcToXmpDialog();

            dlg.setFiles(selFiles);
            dlg.setVisible(true);
        }
    }

    private boolean usingSelectedFiles(ActionEvent evt) {
        Object source = evt.getSource();

        return source.equals(ThumbnailsPopupMenu.INSTANCE.getItemIptcToXmp())
                || source.equals(GUI.getAppPanel().getButtonIptcToXmp());
    }

    private void showIptcToXmpDialog() {
        IptcToXmpDialog dlg = new IptcToXmpDialog();

        dlg.setVisible(true);
    }
}
