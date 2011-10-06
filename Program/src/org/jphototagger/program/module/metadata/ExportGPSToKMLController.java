package org.jphototagger.program.module.metadata;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;

import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.image.metadata.exif.gps.GPSLocationExportUtil;
import org.jphototagger.program.image.metadata.exif.gps.KMLExporter;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExportGPSToKMLController extends AbstractAction {

    private static final long serialVersionUID = 6618409829007878958L;

    public ExportGPSToKMLController() {
        super(Bundle.getString(ExportGPSToKMLController.class, "ExportGPSController.Name"));
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
