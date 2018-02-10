package org.jphototagger.iptcmodule;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.TabInEditWindowDisplayedEvent;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.iptc.IptcPreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowComponentProvider.class)
public final class IptcMetaDataDisplayer implements MainWindowComponentProvider {

    private final IptcPanel iptcPanel = new IptcPanel();
    private File selectedFile;
    private boolean iptcPanelDisplayed;

    public IptcMetaDataDisplayer() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private static boolean isDisplayIptcPreferred() {
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        return preferences.containsKey(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                ? preferences.getBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                : false;
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(ThumbnailsSelectionChangedEvent evt) {
        List<File> selectedFiles = evt.getSelectedFiles();
        if (selectedFiles.size() == 1) {
            selectedFile = CollectionUtil.getFirstElement(selectedFiles);
            displaySelectedFile();
        } else {
            selectedFile = null;
            iptcPanel.removeAllRows();
        }
    }

    @EventSubscriber(eventClass = TabInEditWindowDisplayedEvent.class)
    public void tabInEditWindowDisplayed(TabInEditWindowDisplayedEvent evt) {
        Component selectedTabComponent = evt.getSelectedTabComponent();
        iptcPanelDisplayed = selectedTabComponent == iptcPanel;
        boolean isDisplayIptc = iptcPanelDisplayed && selectedFile != null;
        if (isDisplayIptc) {
            displaySelectedFile();
        }
    }

    private void displaySelectedFile() {
        if (selectedFile == null || !iptcPanelDisplayed) {
            return;
        }
        DisplayIptcMetaData displayIptcMetaData = new DisplayIptcMetaData(selectedFile, iptcPanel);
        EventQueueUtil.invokeInDispatchThread(displayIptcMetaData);
    }

    public static class DisplayIptcMetaData implements Runnable {

        private final File file;
        private final IptcPanel iptcPanel;
        private static final Logger LOGGER = Logger.getLogger(DisplayIptcMetaData.class.getName());

        public DisplayIptcMetaData(File file, IptcPanel iptcPanel) {
            this.file = file;
            this.iptcPanel = iptcPanel;
        }

        @Override
        public void run() {
            if (file == null) {
                return;
            }
            if (isDisplayIptc()) {
                LOGGER.log(Level.FINEST, "Updating IPTC metadata of image file ''{0}'' in GUI table", file);
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                iptcPanel.setFile(file);
                waitDisplayer.hide();
            }
        }

        private boolean isDisplayIptc() {
            MainWindowManager windowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            boolean iptcPanelSelected = windowManager.isEditComponentSelected(iptcPanel);
            return iptcPanelSelected && isDisplayIptcPreferred();
        }
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (selectedFile == null) {
            return;
        }
        String key = evt.getKey();
        if (IptcPreferencesKeys.KEY_DISPLAY_IPTC.equals(key)) {
           boolean isDisplay = (boolean) evt.getNewValue();
            if (isDisplay) {
                displaySelectedFile();
            } else {
                iptcPanel.removeAllRows();
            }
        } else if (IptcPreferencesKeys.KEY_IPTC_CHARSET.equals(key)) {
                displaySelectedFile();
        }
    }

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowSelectionComponents() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MainWindowComponent> getMainWindowEditComponents() {
        return Arrays.asList(new MainWindowComponent() {

            @Override
            public Component getComponent() {
                return iptcPanel;
            }

            @Override
            public Icon getSmallIcon() {
                return org.jphototagger.resources.Icons.getIcon("icon_file.png");
            }

            @Override
            public Icon getLargeIcon() {
                return null;
            }

            @Override
            public int getPosition() {
                return 1;
            }

            @Override
            public String getTitle() {
                return Bundle.getString(ModuleInstaller.class, "IptcPanel.WindowTitle");
            }

            @Override
            public String getTooltipText() {
                return null;
            }

            @Override
            public KeyStroke getOptionalSelectionAccelaratorKey() {
                return null;
            }
        });
    }
}
