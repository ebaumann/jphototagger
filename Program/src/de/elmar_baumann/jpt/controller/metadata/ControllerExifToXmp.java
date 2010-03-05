/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.helper.SetExifToXmp;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-07
 */
public final class ControllerExifToXmp implements ActionListener {

    public ControllerExifToXmp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemExifToXmp().addActionListener(this);
        GUI.INSTANCE.getAppPanel().getButtonExifToXmp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        processSelectedFiles();
    }

    private void processSelectedFiles() {
        final List<File> selectedFiles = GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectedFiles();

        if (selectedFiles.size() > 0) {
            new SetExifToXmp(FileUtil.getAsFilenames(selectedFiles), true).start();
        }
    }
}
