package org.jphototagger.program.controller.thumbnail;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.event.AppWillExitEvent;
import org.jphototagger.domain.event.listener.ThumbnailsPanelListener;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.ComparatorFilesNoSort;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsPanelPersistence implements ThumbnailsPanelListener {

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
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void thumbnailsSelectionChanged() {
        writeSelectionToProperties();
    }

    @Override
    public void thumbnailsChanged() {
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

    private void writeSelectionToProperties() {
        UserSettings.INSTANCE.getSettings().setStringCollection(
                KEY_SELECTED_FILES, FileUtil.getAbsolutePathnames(GUI.getSelectedImageFiles()));
        UserSettings.INSTANCE.writeToFile();
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
        persistentSelectedFiles =
                FileUtil.getStringsAsFiles(UserSettings.INSTANCE.getSettings().getStringCollection(KEY_SELECTED_FILES));
        readSortFromProperties();
    }

    @SuppressWarnings("unchecked")
    private void readSortFromProperties() {
        GUI.getThumbnailsPanel().setFileSortComparator(getFileSortComparator());
    }

    public void setFileSortComparator(Comparator<File> cmp) {
        Class<?> sortClass = cmp.getClass();

        if (!sortClass.equals(ComparatorFilesNoSort.class)) {
            UserSettings.INSTANCE.getSettings().set(KEY_SORT, sortClass.getName());
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
        if (UserSettings.INSTANCE.getProperties().containsKey(KEY_SORT)) {
            try {
                String className = UserSettings.INSTANCE.getSettings().getString(KEY_SORT);

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
                        UserSettings.INSTANCE.getSettings().applySettings(
                                KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, GUI.getAppPanel().getScrollPaneThumbnailsPanel());
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
        UserSettings.INSTANCE.getSettings().set(KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION, GUI.getAppPanel().getScrollPaneThumbnailsPanel());
        UserSettings.INSTANCE.writeToFile();
    }
}
