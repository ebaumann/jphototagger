package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.cache.ThumbnailCache;
import de.elmar_baumann.imv.cache.XmpCache;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Reacts to databse changes of thumbnails
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-15
 */
public final class ControllerThumbnailsDatabaseChanges
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerThumbnailsDatabaseChanges() {
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(final DatabaseImageEvent event) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                DatabaseImageEvent.Type eventType = event.getType();
                File file = event.getImageFile().getFile();
                if (eventType.equals(DatabaseImageEvent.Type.THUMBNAIL_UPDATED)) {
                    ThumbnailCache.INSTANCE.remove(file);
                    ThumbnailCache.INSTANCE.notifyUpdate(file);
                } else if (eventType.equals(DatabaseImageEvent.Type.IMAGEFILE_UPDATED)) {
                    // fixme: strange event for Xmp updates ... needs some cleanup
                    XmpCache.INSTANCE.remove(file);
                    XmpCache.INSTANCE.notifyUpdate(file);
                    ThumbnailCache.INSTANCE.remove(file);
                    ThumbnailCache.INSTANCE.notifyUpdate(file);
                } else if (eventType.equals(
                        DatabaseImageEvent.Type.IMAGEFILE_DELETED)) {
                    List<File> deleted = Collections.singletonList(
                            event.getImageFile().getFile());
                    thumbnailsPanel.remove(deleted);
                    // fixme: iterate over images and do same as above
                }
            }
        });
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
