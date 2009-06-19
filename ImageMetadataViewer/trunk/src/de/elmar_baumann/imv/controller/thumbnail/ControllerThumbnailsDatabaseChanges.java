package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Reacts to databse changes of thumbnails
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public final class ControllerThumbnailsDatabaseChanges
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();

    public ControllerThumbnailsDatabaseChanges() {
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        DatabaseImageEvent.Type eventType = event.getType();
        if (eventType.equals(DatabaseImageEvent.Type.THUMBNAIL_UPDATED)) {
            thumbnailsPanel.repaint(event.getImageFile().getFile());
        } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_DELETED)) {
            removeThumbnails(event);
        }
    }

    private void removeThumbnails(DatabaseImageEvent action) {
        List<File> deleted = Collections.singletonList(
                action.getImageFile().getFile());
        thumbnailsPanel.remove(deleted);
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // nothing to do
    }
}
