/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.cache.ThumbnailCache;
import de.elmar_baumann.jpt.cache.XmpCache;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseListener;
import de.elmar_baumann.jpt.event.DatabaseProgramEvent;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
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
