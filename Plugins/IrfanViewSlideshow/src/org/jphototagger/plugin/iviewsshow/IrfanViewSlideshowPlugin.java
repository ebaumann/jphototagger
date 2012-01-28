package org.jphototagger.plugin.iviewsshow;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.bushe.swing.event.EventBus;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.lib.io.IoUtil;
import org.jphototagger.lib.plugin.AbstractFileProcessorPlugin;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.lib.util.SystemUtil;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class IrfanViewSlideshowPlugin extends AbstractFileProcessorPlugin implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Icon ICON = IconUtil.getImageIcon("/org/jphototagger/plugin/iviewsshow/icon.png");
    private final IrfanViewSlideshowCommand slideshowCommand = new IrfanViewSlideshowCommand();

    @Override
    public String getDisplayName() {
        return Bundle.getString(IrfanViewSlideshowPlugin.class, "IrfanViewSlideshowPlugin.Name");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(IrfanViewSlideshowPlugin.class, "IrfanViewSlideshowPlugin.Description");
    }

    @Override
    public Component getSettingsComponent() {
        return new IrfanViewSlideshowSettingsPanel();
    }

    @Override
    public Icon getSmallIcon() {
        return ICON;
    }

    @Override
    public void processFiles(Collection<? extends File> files) {
        EventBus.publish(new FileProcessingStartedEvent(this));
        File slideshowFile = TemporaryStorage.INSTANCE.getNotExistingSlideshowFile();

        String command = slideshowCommand.getCommandForFile(slideshowFile.getAbsolutePath());

        if (command == null) {
            Logger.getLogger(IrfanViewSlideshowPlugin.class.getName()).log(Level.WARNING, "Could not get the IrfanView slideshow command");
        } else {
            StringBuilder sb = new StringBuilder();
            String lineSeparator = SystemProperties.getLineSeparator();

            for (File file : files) {
                sb.append(file.getAbsolutePath());
                sb.append(lineSeparator);
            }

            Writer writer = null;

            try {
                writer = new BufferedWriter(new FileWriter(slideshowFile));
                writer.write(sb.toString());
                writer.flush();
                Logger.getLogger(IrfanViewSlideshowPlugin.class.getName()).log(Level.INFO, "Calling IrfanView: ''{0}''", command);
                Runtime.getRuntime().exec(command);
            } catch (Throwable t) {
                Logger.getLogger(IrfanViewSlideshowPlugin.class.getName()).log(Level.SEVERE, null, t);
            } finally {
                IoUtil.close(writer);
            }
        }

        EventBus.publish(new FileProcessingFinishedEvent(this, true));
    }

    @Override
    public boolean isAvailable() {
        return SystemUtil.isWindows();
    }
}
