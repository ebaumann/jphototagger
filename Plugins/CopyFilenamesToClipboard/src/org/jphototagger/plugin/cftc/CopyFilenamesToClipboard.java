package org.jphototagger.plugin.cftc;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;

import javax.swing.Icon;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.plugin.fileprocessor.FileProcessedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingFinishedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessingStartedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpContentProvider;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.plugin.AbstractFileProcessorPlugin;

/**
 * Copies into the system clipboard names of files.
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileProcessorPlugin.class)
public final class CopyFilenamesToClipboard extends AbstractFileProcessorPlugin implements Serializable, HelpContentProvider {

    private static final long serialVersionUID = 1L;
    public static final String KEY_FILENAME_DELIMITER = CopyFilenamesToClipboard.class.getName() + ".KeyDelimiter";
    public static final String DEFAULT_FILENAME_DELIMITER = "\n";
    private static final Icon ICON = IconUtil.getImageIcon(CopyFilenamesToClipboard.class, "icon.png");
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
    public String getHelpContentUrl() {
        return "/org/jphototagger/plugin/cftc/help/contents.xml";
    }
    @Override
    public Icon getSmallIcon() {
        return ICON;
    }

    @Override
    public void processFiles(Collection<? extends File> files) {
        EventBus.publish(new FileProcessingStartedEvent(this));
        setDelimiter();

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (File file : files) {
            sb.append(index == 0
                    ? ""
                    : fileNameDelimiter).append(file.getAbsolutePath());
            EventBus.publish(new FileProcessedEvent(this, file, false));
            index++;
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        EventBus.publish(new FileProcessingFinishedEvent(this, true));
    }

    private void setDelimiter() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            String delimiter = storage.getString(KEY_FILENAME_DELIMITER);

            if (delimiter != null) {
                fileNameDelimiter = delimiter;
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
