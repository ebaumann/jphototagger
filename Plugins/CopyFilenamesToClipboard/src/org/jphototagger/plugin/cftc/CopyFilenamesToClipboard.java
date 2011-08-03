package org.jphototagger.plugin.cftc;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;

import javax.swing.Icon;

import org.jphototagger.api.core.Storage;
import org.jphototagger.api.plugin.FileProcessorPlugin;
import org.jphototagger.api.plugin.FileProcessorPluginEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.plugin.AbstractFileProcessorPlugin;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Copies into the system clipboard names of files.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class CopyFilenamesToClipboard extends AbstractFileProcessorPlugin implements Serializable {

    private static final long serialVersionUID = 526527636923496736L;
    public static final String KEY_FILENAME_DELIMITER = CopyFilenamesToClipboard.class.getName() + ".KeyDelimiter";
    public static final String DEFAULT_FILENAME_DELIMITER = "\n";
    private String fileNameDelimiter = DEFAULT_FILENAME_DELIMITER;

    @Override
    public String getDisplayName() {
        return Bundle.getString(CopyFilenamesToClipboard.class, "CopyFilenamesToClipboard.Name");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(CopyFilenamesToClipboard.class, "CopyFilenamesToClipboard.Description");
    }

    @Override
    public Component getSettingsComponent() {
        return new SettingsPanel();
    }

    @Override
    public String getHelpContentsPath() {
        return "/org/jphototagger/plugin/cftc/help/contents.xml";
    }

    @Override
    public String getFirstHelpPageName() {
        return "index.html";
    }

    public Icon getIcon() {
        return null;
    }

    @Override
    public void processFiles(Collection<? extends File> files) {
        notifyFileProcessorPluginListeners(new FileProcessorPluginEvent(FileProcessorPluginEvent.Type.PROCESSING_STARTED));
        setDelimiter();

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (File file : files) {
            sb.append(index == 0
                    ? ""
                    : fileNameDelimiter).append(file.getAbsolutePath());
            index++;
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        notifyFinished(files);
    }

    private void notifyFinished(Collection<? extends File> files) {
        FileProcessorPluginEvent evt = new FileProcessorPluginEvent(FileProcessorPluginEvent.Type.PROCESSING_FINISHED_SUCCESS);

        evt.setProcessedFiles(files);
        notifyFileProcessorPluginListeners(evt);
    }

    private void setDelimiter() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        if (storage != null) {
            String delimiter = storage.getString(KEY_FILENAME_DELIMITER);

            if (delimiter != null) {
                fileNameDelimiter = delimiter;
            }
        }
    }
}
