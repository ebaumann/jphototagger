package org.jphototagger.exiftoolxtiw;

import java.awt.Component;
import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class, position = 1000000)
public final class ExifToolProcessFilesPlugin implements FileProcessorPlugin {

    @Override
    public void processFiles(Collection<? extends File> files) {
        try {
            ExifTooolXmpToImageWriterModel model = new ExifTooolXmpToImageWriterModel();
            if (ExifToolCommon.checkExecute(model)) {
                model.setImageFiles(files);
                Worker worker = new Worker(model);
                worker.execute();
            }
        } catch (Throwable t) {
            Logger.getLogger(ExifToolProcessFilesPlugin.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private final class Worker extends SwingWorker<Void, Void> {

        private final ExifTooolXmpToImageWriterModel model;

        private Worker(ExifTooolXmpToImageWriterModel model) {
            this.model = model;
        }

        @Override
        protected Void doInBackground() throws Exception {
            model.execute();
            return null;
        }

        @Override
        protected void done() {
            boolean success = false;
            try {
                get();
                success = true;
            } catch (Throwable t) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, t);
            } finally {
                EventBus.publish(new FileProcessingFinishedEvent(this, success));
            }
        }
    }

    @Override
    public Component getSettingsComponent() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getDescription() {
        return Bundle.getString(ExifToolProcessFilesPlugin.class, "ExifToolProcessFilesPlugin.Description");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(ExifToolProcessFilesPlugin.class, "ExifToolProcessFilesPlugin.DisplayName");
    }

    @Override
    public Icon getSmallIcon() {
        return null;
    }

    @Override
    public Icon getLargeIcon() {
        return null;
    }
}
