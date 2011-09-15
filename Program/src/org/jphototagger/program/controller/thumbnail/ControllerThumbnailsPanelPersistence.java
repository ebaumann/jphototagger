package org.jphototagger.program.controller.thumbnail;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.domain.event.AppWillExitEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.FileUnsortedComparator;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.Lookup;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsPanelPersistence {

    private static final String KEY_SELECTED_FILES = "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles";
    private static final String KEY_SORT = "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.Sort";
    private static final String KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION = "org.jphototagger.program.view.panels.controller.ViewportViewPosition";
    private volatile boolean propertiesRead;
    private List<File> persistentSelectedFiles = new ArrayList<File>();

    public ControllerThumbnailsPanelPersistence() {
        listen();
        readProperties();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        writeSelectionToProperties(evt.getSelectedImageFiles());
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        checkFirstChange();
    }

    private void checkFirstChange() {
        synchronized (this) {
            if (propertiesRead) {
                return;
            }

            propertiesRead = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                readSelectedFilesFromProperties();
                readViewportViewPositionFromProperties();
            }
        });
    }

    private void writeSelectionToProperties(List<File> selectedImageFiles) {
        List<String> absolutePathnames = FileUtil.getAbsolutePathnames(selectedImageFiles);
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setStringCollection(KEY_SELECTED_FILES, absolutePathnames);
    }

    private void readSelectedFilesFromProperties() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        List<Integer> indices = new ArrayList<Integer>();

        for (File file : persistentSelectedFiles) {
            int index = tnPanel.getIndexOf(file);

            if (index >= 0) {
                indices.add(index);
            }
        }

        tnPanel.setSelectedIndices(indices);
    }

    private void readProperties() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        persistentSelectedFiles = FileUtil.getStringsAsFiles(storage.getStringCollection(KEY_SELECTED_FILES));
        readSortFromProperties();
    }

    @SuppressWarnings("unchecked")
    private void readSortFromProperties() {
        GUI.getThumbnailsPanel().setFileSortComparator(getFileSortComparator());
    }

    public void setFileSortComparator(Comparator<File> cmp) {
        Class<?> sortClass = cmp.getClass();

        if (!sortClass.equals(FileUnsortedComparator.class)) {
            Storage storage = Lookup.getDefault().lookup(Storage.class);

            storage.setString(KEY_SORT, sortClass.getName());
        }
    }

    /**
     * Returns the file sort comparator from the user settings.
     *
     * @return sort comparator or if not defined the comparator of
     *         {@link FileSort#NAMES_ASCENDING}
     */
    @SuppressWarnings("unchecked")
    public static Comparator<File> getFileSortComparator() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        if (storage.containsKey(KEY_SORT)) {
            try {
                String className = storage.getString(KEY_SORT);

                return (Comparator<File>) Class.forName(className).newInstance();
            } catch (Exception ex) {
                Logger.getLogger(ControllerThumbnailsPanelPersistence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return FileSort.NAMES_ASCENDING.getComparator();
    }

    private void readViewportViewPositionFromProperties() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Waiting until TN panel size was calculated
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    Logger.getLogger(ControllerThumbnailsPanelPersistence.class.getName()).log(Level.SEVERE, null, ex);
                }

                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        Storage storage = Lookup.getDefault().lookup(Storage.class);
                        AppPanel appPanel = GUI.getAppPanel();
                        JScrollPane scrollPaneThumbnailsPanel = appPanel.getScrollPaneThumbnailsPanel();

                        storage.applyScrollPaneSettings(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, scrollPaneThumbnailsPanel);
                    }
                });
            }
        }, "JPhotoTagger: Restoring viewport position").start();
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        writeViewportViewPositionToProperties();
    }

    private void writeViewportViewPositionToProperties() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setScrollPane(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, GUI.getAppPanel().getScrollPaneThumbnailsPanel());
    }
}
