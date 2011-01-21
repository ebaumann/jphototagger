package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.image.metadata.exif.gps.GPSLocationExportUtil;
import org.jphototagger.program.image.metadata.exif.gps.KMLExporter;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;

import java.io.File;

import java.util.List;

import javax.swing.AbstractAction;
import org.jphototagger.program.resource.GUI;

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
        final List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            GPSLocationExportUtil.export(new KMLExporter(), selFiles);
        }
    }
}
