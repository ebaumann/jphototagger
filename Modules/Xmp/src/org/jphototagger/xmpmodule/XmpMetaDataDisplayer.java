package org.jphototagger.xmpmodule;

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
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowComponentProvider.class)
public final class XmpMetaDataDisplayer implements MainWindowComponentProvider {

    private final XmpPanel xmpPanel = new XmpPanel();
    private File selectedFile;
    private File displayedFile;
    private boolean xmpPanelDisplayed;

    public XmpMetaDataDisplayer() {
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
            xmpPanel.removeAllRows();
        }
    }

    @EventSubscriber(eventClass = TabInEditWindowDisplayedEvent.class)
    public void tabInEditWindowDisplayed(TabInEditWindowDisplayedEvent evt) {
        Component selectedTabComponent = evt.getSelectedTabComponent();
        xmpPanelDisplayed = selectedTabComponent == xmpPanel;
        boolean isDisplayXmp = xmpPanelDisplayed && selectedFile != null;
        if (isDisplayXmp) {
            displaySelectedFile();
        }
    }

    private void displaySelectedFile() {
        if (selectedFile == null || !xmpPanelDisplayed || ObjectUtil.equals(selectedFile, displayedFile)) {
            return;
        }

        displayedFile = selectedFile;
        DisplayIptcMetaData displayIptcMetaData = new DisplayIptcMetaData(selectedFile, xmpPanel);
        EventQueueUtil.invokeInDispatchThread(displayIptcMetaData);
    }

    public static class DisplayIptcMetaData implements Runnable {

        private final File file;
        private final XmpPanel xmpPanel;
        private static final Logger LOGGER = Logger.getLogger(DisplayIptcMetaData.class.getName());

        public DisplayIptcMetaData(File file, XmpPanel xmpPanel) {
            this.file = file;
            this.xmpPanel = xmpPanel;
        }

        @Override
        public void run() {
            if (file == null) {
                return;
            }

            if (isXmpPanelSelected()) {
                LOGGER.log(Level.FINEST, "Updating XMP metadata of image file ''{0}'' in GUI table", file);
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                xmpPanel.setFile(file);
                waitDisplayer.hide();
            }
        }

        private boolean isXmpPanelSelected() {
            MainWindowManager windowManager = Lookup.getDefault().lookup(MainWindowManager.class);
            return windowManager.isEditComponentSelected(xmpPanel);
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
                return xmpPanel;
            }

            @Override
            public Icon getSmallIcon() {
                return IconUtil.getImageIcon(ModuleInstaller.class, "xmp.png");
            }

            @Override
            public Icon getLargeIcon() {
                return null;
            }

            @Override
            public int getPosition() {
                return 2;
            }

            @Override
            public String getTitle() {
                return Bundle.getString(ModuleInstaller.class, "XmpPanel.WindowTitle");
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
