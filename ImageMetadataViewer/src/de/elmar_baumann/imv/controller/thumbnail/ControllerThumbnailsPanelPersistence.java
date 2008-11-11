package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.event.ThumbnailsPanelAction;
import de.elmar_baumann.imv.event.ThumbnailsPanelListener;
import de.elmar_baumann.imv.io.FileSort;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Applies persistent settings to the thumbnails panel.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public class ControllerThumbnailsPanelPersistence extends Controller
    implements ThumbnailsPanelListener, AppExitListener {

    private boolean onceApplied = false;
    private static final String keySelectedFiles = "de.elmar_baumann.imv.view.controller.ControllerThumbnailsPanelPersistence.SelectedFiles"; // NOI18N
    private static final String keySort = "de.elmar_baumann.imv.view.controller.ControllerThumbnailsPanelPersistence.Sort"; // NOI18N
    private static final String keyThumbnailPanelViewportViewPosition = "de.elmar_baumann.imv.view.panels.controller.ViewportViewPosition"; // NOI18N
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private List<File> persistentSelectedFiles;

    public ControllerThumbnailsPanelPersistence() {
        thumbnailsPanel.addThumbnailsPanelListener(this);
        Panels.getInstance().getAppFrame().addAppExitListener(this);
        readPersistent();
    }

    @Override
    public void selectionChanged(ThumbnailsPanelAction action) {
        writePersistentSelection();
    }

    @Override
    public void thumbnailsChanged() {
        checkFirstChange();
        PersistentSettings.getInstance().setString(thumbnailsPanel.getSort().name(), keySort);
    }

    private void checkFirstChange() {
        if (!onceApplied) {
            readPersistentSelectedFiles();
            readPersistentViewportViewPosition();
            readPersistentViewportViewPosition();
            onceApplied = true;
        }
    }

    private void writePersistentSelection() {
        PersistentSettings.getInstance().setStringArray(
            FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()), keySelectedFiles);
    }

    private void readPersistentSelectedFiles() {
        List<Integer> indices = new ArrayList<Integer>();
        for (File file : persistentSelectedFiles) {
            int index = thumbnailsPanel.getIndexOf(file);
            if (index >= 0) {
                indices.add(index);
            }
        }
        thumbnailsPanel.setSelected(indices);
    }

    private void readPersistent() {
        persistentSelectedFiles = FileUtil.getAsFiles(
            PersistentSettings.getInstance().getStringArray(keySelectedFiles));
        readPersistentSort();
    }

    private void readPersistentSort() {
        String name = PersistentSettings.getInstance().getString(keySort);
        try {
            if (!name.isEmpty()) {
                thumbnailsPanel.setSort(FileSort.valueOf(name));
            }
        } catch (Exception ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
        }
    }

    private void readPersistentViewportViewPosition() {
        PersistentSettings.getInstance().getScrollPane(
            Panels.getInstance().getAppPanel().getScrollPaneThumbnailsPanel(),
            keyThumbnailPanelViewportViewPosition);
    }

    @Override
    public void appWillExit() {
        writePersistentViewportViewPosition();
    }

    private void writePersistentViewportViewPosition() {
        PersistentSettings.getInstance().setScrollPane(
            Panels.getInstance().getAppPanel().getScrollPaneThumbnailsPanel(),
            keyThumbnailPanelViewportViewPosition);
    }
}
