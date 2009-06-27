package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelEvent;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public final class ControllerThumbnailsPanelPersistence
        implements ThumbnailsPanelListener, AppExitListener {

    private boolean propertiesRead = false;
    private static final String keySelectedFiles =
            "de.elmar_baumann.imv.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles"; // NOI18N
    private static final String keySort =
            "de.elmar_baumann.imv.view.controller.ControllerThumbnailsPanelPersistence.Sort"; // NOI18N
    private static final String keyThumbnailPanelViewportViewPosition =
            "de.elmar_baumann.imv.view.panels.controller.ViewportViewPosition"; // NOI18N
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();
    private List<File> persistentSelectedFiles;

    public ControllerThumbnailsPanelPersistence() {
        listen();
        readProperties();
    }

    private void listen() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        GUI.INSTANCE.getAppFrame().addAppExitListener(this);
    }

    @Override
    public void selectionChanged(ThumbnailsPanelEvent action) {
        writeSelectionToProperties();
    }

    @Override
    public void thumbnailsChanged() {
        checkFirstChange();
        UserSettings.INSTANCE.getSettings().setString(thumbnailsPanel.getSort().
                name(), keySort);
    }

    private void checkFirstChange() {
        if (!propertiesRead) {
            readSelectedFilesFromProperties();
            readViewportViewPositionFromProperties();
            propertiesRead = true;
        }
    }

    private void writeSelectionToProperties() {
        UserSettings.INSTANCE.getSettings().setStringArray(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()),
                keySelectedFiles);
    }

    private void readSelectedFilesFromProperties() {
        List<Integer> indices = new ArrayList<Integer>();
        for (File file : persistentSelectedFiles) {
            int index = thumbnailsPanel.getIndexOf(file);
            if (index >= 0) {
                indices.add(index);
            }
        }
        thumbnailsPanel.setSelected(indices);
    }

    private void readProperties() {
        persistentSelectedFiles = FileUtil.getAsFiles(
                UserSettings.INSTANCE.getSettings().getStringArray(
                keySelectedFiles));
        readSortFromProperties();
    }

    private void readSortFromProperties() {
        String name = UserSettings.INSTANCE.getSettings().getString(keySort);
        try {
            if (!name.isEmpty()) {
                thumbnailsPanel.setSort(FileSort.valueOf(name));
            }
        } catch (Exception ex) {
            AppLog.logWarning(ControllerThumbnailsPanelPersistence.class, ex);
        }
    }

    private void readViewportViewPositionFromProperties() {
        UserSettings.INSTANCE.getSettings().getScrollPane(
                GUI.INSTANCE.getAppPanel().getScrollPaneThumbnailsPanel(),
                keyThumbnailPanelViewportViewPosition);
    }

    @Override
    public void appWillExit() {
        writeViewportViewPositionToProperties();
    }

    private void writeViewportViewPositionToProperties() {
        UserSettings.INSTANCE.getSettings().setScrollPane(
                GUI.INSTANCE.getAppPanel().getScrollPaneThumbnailsPanel(),
                keyThumbnailPanelViewportViewPosition);
    }
}
