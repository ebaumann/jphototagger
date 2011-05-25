package org.jphototagger.plugin.cftc;

import org.jphototagger.lib.resource.Bundle;
import org.jphototagger.plugin.Plugin;
import org.jphototagger.plugin.PluginEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import javax.swing.JPanel;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.core.Storage;

/**
 * Copies into the system clipboard names of files.
 *
 * @author Elmar Baumann
 */
public final class CopyFilenamesToClipboard extends Plugin implements Serializable {

    private static final long serialVersionUID = 526527636923496736L;
    public static final String KEY_FILENAME_DELIMITER = CopyFilenamesToClipboard.class.getName() + ".KeyDelimiter";
    public static final String DEFAULT_FILENAME_DELIMITER = "\n";
    private String fileNameDelimiter = DEFAULT_FILENAME_DELIMITER;
    private static final transient Bundle BUNDLE = new Bundle("org/jphototagger/plugin/cftc/Bundle");

    @Override
    public String getName() {
        return BUNDLE.getString("CopyFilenamesToClipboard.Name");
    }

    @Override
    public String getDescription() {
        return BUNDLE.getString("CopyFilenamesToClipboard.Description");
    }

    @Override
    public JPanel getSettingsPanel() {
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

    @Override
    public void processFiles(List<File> files) {
        notifyPluginListeners(new PluginEvent(PluginEvent.Type.STARTED));
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

    private void notifyFinished(List<File> files) {
        PluginEvent evt = new PluginEvent(PluginEvent.Type.FINISHED_SUCCESS);

        evt.setProcessedFiles(files);
        notifyPluginListeners(evt);
    }

    private void setDelimiter() {
        Storage storage = ServiceLookup.lookup(Storage.class);

        if (storage != null) {
            String delimiter = storage.getString(KEY_FILENAME_DELIMITER);

            if (delimiter != null) {
                fileNameDelimiter = delimiter;
            }
        }
    }
}
