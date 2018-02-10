package org.jphototagger.exifmodule;

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
import org.jphototagger.api.windows.MainWindowComponent;
import org.jphototagger.api.windows.MainWindowComponentProvider;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.TabInEditWindowDisplayedEvent;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowComponentProvider.class)
public final class ExifMetaDataDisplayer implements MainWindowComponentProvider {

    private final ExifPanel exifPanel = new ExifPanel();
    private File selectedFile;
    private File displayedFile;
    private boolean exifPanelDisplayed;

    public ExifMetaDataDisplayer() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(ThumbnailsSelectionChangedEvent evt) {
        List<File> selectedFiles = evt.getSelectedFiles();
        if (selectedFiles.size() == 1) {
            selectedFile = CollectionUtil.getFirstElement(selectedFiles);
            displaySelectedFile();
        } else {
            selectedFile = null;
            displayedFile = null;
            exifPanel.removeAllRows();
        }
    }

    @EventSubscriber(eventClass=TabInEditWindowDisplayedEvent.class)
    public void tabInEditWindowDisplayed(TabInEditWindowDisplayedEvent evt) {
        Component selectedTabComponent = evt.getSelectedTabComponent();
        exifPanelDisplayed = selectedTabComponent == exifPanel;
        boolean isDisplayExif = exifPanelDisplayed && selectedFile != null;
        if (isDisplayExif) {
            displaySelectedFile();
        }
    }

    private void displaySelectedFile() {
        if (selectedFile == null || !exifPanelDisplayed || ObjectUtil.equals(selectedFile, displayedFile)) {
            return;
        }

        displayedFile = selectedFile;
        DisplayExifMetaData displayExifMetaData = new DisplayExifMetaData(selectedFile, exifPanel);
        EventQueueUtil.invokeInDispatchThread(displayExifMetaData);
    }

    public static class DisplayExifMetaData implements Runnable {

        private final File file;
        private final ExifPanel exifPanel;
        private static final Logger LOGGER = Logger.getLogger(DisplayExifMetaData.class.getName());

        public DisplayExifMetaData(File file, ExifPanel exifPanel) {
            this.file = file;
            this.exifPanel = exifPanel;
        }

        @Override
        public void run() {
            if (file == null) {
                return;
            }

            if (isExifPanelSelected()) {
                LOGGER.log(Level.FINEST, "Updating EXIF metadata of image file ''{0}'' in GUI table", file);
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                exifPanel.setFile(file);
                waitDisplayer.hide();
            }
        }

        private boolean isExifPanelSelected() {
            MainWindowManager windowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            return windowManager.isEditComponentSelected(exifPanel);
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
                return exifPanel;
            }

            @Override
            public Icon getSmallIcon() {
                return org.jphototagger.resources.Icons.getIcon("icon_exif.png");
            }

            @Override
            public Icon getLargeIcon() {
                return null;
            }

            @Override
            public int getPosition() {
                return 0;
            }

            @Override
            public String getTitle() {
                return Bundle.getString(ModuleInstaller.class, "ExifPanel.WindowTitle");
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
