package org.jphototagger.exiftoolxtiw;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class, position = 1100000)
public final class ExifToolRemoveAllMetaDataFileProcessorPlugin implements FileProcessorPlugin {

    private final Settings settings = new Settings();
    private final ExifToolCommandModel model = new ExifToolCommandModel();

    public ExifToolRemoveAllMetaDataFileProcessorPlugin() {
        model.addToCommandTokens("-all=");
    }

    @Override
    public void processFiles(Collection<? extends File> files) {
        try {
            if (ExifToolCommon.checkExecute(settings) && confirmRemove()) {
                Worker worker = new Worker(model);

                model.setFiles(files);
                EventBus.publish(new FileProcessingStartedEvent(this));
                worker.execute();
            }
        } catch (Throwable t) {
            Logger.getLogger(ExifToolRemoveAllMetaDataFileProcessorPlugin.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private boolean confirmRemove() {
        String message = Bundle.getString(ExifToolRemoveAllMetaDataFileProcessorPlugin.class, "ExifToolRemoveAllMetaDataFileProcessorPlugin.ConfirmRemove");
        Frame frame = ComponentUtil.findFrameWithIcon();
        return MessageDisplayer.confirmYesNo(frame, message);
    }

    private static final class Worker extends SwingWorker<Void, Void> {

        private final ExifToolCommandModel model;

        private Worker(ExifToolCommandModel model) {
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
        return Bundle.getString(ExifToolRemoveAllMetaDataFileProcessorPlugin.class, "ExifToolRemoveAllMetaDataFileProcessorPlugin.Description");
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(ExifToolRemoveAllMetaDataFileProcessorPlugin.class, "ExifToolRemoveAllMetaDataFileProcessorPlugin.DisplayName");
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
