/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.thumbnail;

import de.elmar_baumann.jpt.cache.ThumbnailCache;
import de.elmar_baumann.jpt.cache.XmpCache;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Reacts to databse changes of thumbnails
 *
 * @author  Elmar Baumann
 * @version 2008-10-15
 */
public final class ControllerThumbnailsDatabaseChanges
        implements DatabaseImageFilesListener {

    private final ThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerThumbnailsDatabaseChanges() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    @Override
    public void actionPerformed(final DatabaseImageFilesEvent event) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                DatabaseImageFilesEvent.Type eventType = event.getType();
                File file = event.getImageFile().getFile();
                if (eventType.equals(DatabaseImageFilesEvent.Type.THUMBNAIL_UPDATED)) {
                    ThumbnailCache.INSTANCE.remove(file);
                    ThumbnailCache.INSTANCE.notifyUpdate(file);
                } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_UPDATED)) {
                    // fixme: strange event for Xmp updates ... needs some cleanup
                    XmpCache.INSTANCE.remove(file);
                    XmpCache.INSTANCE.notifyUpdate(file);
                    ThumbnailCache.INSTANCE.remove(file);
                    ThumbnailCache.INSTANCE.notifyUpdate(file);
                } else if (eventType.equals(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED)) {
                    List<File> deleted = Collections.singletonList(event.getImageFile().getFile());
                    thumbnailsPanel.remove(deleted);
                    // fixme: iterate over images and do same as above
                }
            }
        });
    }
}
