/*
 * @(#)ControllerIptcToXmp.java    Created on 2008-09-30
 *
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

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.IptcToXmpDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;

/**
 * Kontrolliert die Aktion: IPTC-Daten nach XMP schreiben.
 *
 * @author  Elmar Baumann
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
    public void actionPerformed(ActionEvent evt) {
        if (usingSelectedFiles(evt)) {
            processSelectedFiles();
        } else {
            showIptcToXmpDialog();
        }
    }

    private void processSelectedFiles() {
        List<File> selFiles =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectedFiles();

        if (selFiles.size() > 0) {
            IptcToXmpDialog dlg = new IptcToXmpDialog();

            dlg.setFiles(selFiles);
            dlg.setVisible(true);
        }
    }

    private boolean usingSelectedFiles(ActionEvent evt) {
        Object source = evt.getSource();

        return source.equals(PopupMenuThumbnails.INSTANCE.getItemIptcToXmp())
               || source.equals(
                   GUI.INSTANCE.getAppPanel().getButtonIptcToXmp());
    }

    private void showIptcToXmpDialog() {
        IptcToXmpDialog dlg = new IptcToXmpDialog();

        dlg.setVisible(true);
    }
}
