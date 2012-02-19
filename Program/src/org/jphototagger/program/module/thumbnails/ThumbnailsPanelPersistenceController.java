package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.applifecycle.AppWillExitEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.MainWindowThumbnailsComponent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.resource.GUI;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailsPanelPersistenceController {

    private static final String KEY_SELECTED_FILES = "org.jphototagger.program.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles";
    private volatile boolean propertiesRead;
    private List<File> persistentSelectedFiles = new ArrayList<File>();

    public ThumbnailsPanelPersistenceController() {
        listen();
        readProperties();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        writeSelectionToProperties(evt.getSelectedFiles());
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setStringCollection(KEY_SELECTED_FILES, absolutePathnames);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        persistentSelectedFiles = FileUtil.getStringsAsFiles(prefs.getStringCollection(KEY_SELECTED_FILES));
    }

    void readViewportViewPositionFromProperties() {
        MainWindowThumbnailsComponent component = Lookup.getDefault().lookup(MainWindowThumbnailsComponent.class);
        component.restoreViewportPosition();
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        writeViewportViewPositionToProperties();
    }

    private void writeViewportViewPositionToProperties() {
        MainWindowThumbnailsComponent component = Lookup.getDefault().lookup(MainWindowThumbnailsComponent.class);
        component.persistViewportPosition();
    }
}
