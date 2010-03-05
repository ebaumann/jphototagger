/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.IptcToXmpDialog;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Kontrolliert die Aktion: IPTC-Daten nach XMP schreiben.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-30
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
