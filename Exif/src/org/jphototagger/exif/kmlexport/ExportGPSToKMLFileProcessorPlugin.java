package org.jphototagger.exif.kmlexport;

import java.awt.Component;
import java.io.File;
import java.util.Collection;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.plugin.AbstractFileProcessorPlugin;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class ExportGPSToKMLFileProcessorPlugin extends AbstractFileProcessorPlugin {

    private static final long serialVersionUID = 1L;

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
}
