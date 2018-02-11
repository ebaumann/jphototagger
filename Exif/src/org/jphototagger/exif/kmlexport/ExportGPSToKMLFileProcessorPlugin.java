package org.jphototagger.exif.kmlexport;

import java.awt.Component;
import java.io.File;
import java.util.Collection;
import javax.swing.Icon;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class, position = 10000)
public final class ExportGPSToKMLFileProcessorPlugin implements FileProcessorPlugin {

    @Override
    public void processFiles(Collection<? extends File> files) {
        KMLExporter exporter = new KMLExporter();

        GPSLocationExportUtil.export(exporter, files);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getDescription() {
        return Bundle.getString(ExportGPSToKMLFileProcessorPlugin.class, "ExportGPSToKMLFileProcessorPlugin.Description");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(ExportGPSToKMLFileProcessorPlugin.class, "ExportGPSToKMLFileProcessorPlugin.Name");
    }

    @Override
    public Component getSettingsComponent() {
        return new ExportGPSToKMLSettingsPanel();
    }

    @Override
    public Icon getSmallIcon() {
        return Icons.getIcon("icon_gps.png");
    }

    @Override
    public Icon getLargeIcon() {
        return Icons.getIcon("icon_gps-48.png");
    }
}
