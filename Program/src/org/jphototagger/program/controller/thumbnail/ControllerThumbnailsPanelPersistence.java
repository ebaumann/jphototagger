package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.comparator.ComparatorFilesNoSort;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.AppExitListener;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsPanelPersistence implements ThumbnailsPanelListener, AppExitListener {
    private static final String KEY_SELECTED_FILES =
        "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles";
    private static final String KEY_SORT =
        "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.Sort";
    private static final String KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION =
        "org.jphototagger.program.view.panels.controller.ViewportViewPosition";
    private volatile boolean propertiesRead;
    private List<File> persistentSelectedFiles = new ArrayList<File>();

    public ControllerThumbnailsPanelPersistence() {
        listen();
        readProperties();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addThumbnailsPanelListener(this);
        AppLifeCycle.INSTANCE.addAppExitListener(this);
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

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                readSelectedFilesFromProperties();
                readViewportViewPositionFromProperties();
            }
        });
    }

    private void writeSelectionToProperties() {
        UserSettings.INSTANCE.getSettings().setStringCollection(
            FileUtil.getAbsolutePathnames(GUI.getSelectedImageFiles()), KEY_SELECTED_FILES);
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
            FileUtil.getAsFiles(UserSettings.INSTANCE.getSettings().getStringCollection(KEY_SELECTED_FILES));
        readSortFromProperties();
    }

    @SuppressWarnings("unchecked")
    private void readSortFromProperties() {
        GUI.getThumbnailsPanel().setFileSortComparator(getFileSortComparator());
    }

    public void setFileSortComparator(Comparator<File> cmp) {
        Class<?> sortClass = cmp.getClass();

        if (!sortClass.equals(ComparatorFilesNoSort.class)) {
            UserSettings.INSTANCE.getSettings().set(sortClass.getName(), KEY_SORT);
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
                AppLogger.logSevere(ControllerThumbnailsPanelPersistence.class, ex);
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
                    AppLogger.logSevere(getClass(), ex);
                }

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        UserSettings.INSTANCE.getSettings().applySettings(
                            GUI.getAppPanel().getScrollPaneThumbnailsPanel(),
                            KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION);
                    }
                });
            }
        }, "JPhotoTagger: Restoring viewport position").start();
    }

    @Override
    public void appWillExit() {
        writeViewportViewPositionToProperties();
    }

    private void writeViewportViewPositionToProperties() {
        UserSettings.INSTANCE.getSettings().set(GUI.getAppPanel().getScrollPaneThumbnailsPanel(),
                KEY_THUMBNAIL_PANEL_VIEWPORT_VIEW_POSITION);
        UserSettings.INSTANCE.writeToFile();
    }
}
