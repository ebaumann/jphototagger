/*
 * @(#)ControllerExportGPSToKML.java    Created on 2010-08-20
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.image.metadata.exif.gps.GPSLocationExportUtil;
import org.jphototagger.program.image.metadata.exif.gps.KMLExporter;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;

import java.io.File;

import java.util.List;

import javax.swing.AbstractAction;
import org.jphototagger.program.view.ViewUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerExportGPSToKML extends AbstractAction {
    private static final long serialVersionUID = 6618409829007878958L;

    public ControllerExportGPSToKML() {
        super(JptBundle.INSTANCE.getString("ControllerExportGPS.Name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        processSelectedFiles();
    }

    private void processSelectedFiles() {
        final List<File> selFiles = ViewUtil.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            GPSLocationExportUtil.export(new KMLExporter(), selFiles);
        }
    }
}
