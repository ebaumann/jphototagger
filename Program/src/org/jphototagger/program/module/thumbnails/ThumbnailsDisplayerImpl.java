package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.image.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.api.image.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsDisplayer.class)
public final class ThumbnailsDisplayerImpl implements ThumbnailsDisplayer {

    @Override
    public void displayThumbnails(Collection<? extends File> imageFiles, OriginOfDisplayedThumbnails origin) {
        ThumbnailsDisplayer thumbnailsDisplayer = new ThumbnailsDisplayer(imageFiles, origin);

        EventQueueUtil.invokeInDispatchThread(thumbnailsDisplayer);
    }

    private static class ThumbnailsDisplayer implements Runnable {

        private final Collection<? extends File> imageFiles;
        private final OriginOfDisplayedThumbnails origin;

        ThumbnailsDisplayer(Collection<? extends File> imageFiles, OriginOfDisplayedThumbnails origin) {
            this.imageFiles = imageFiles;
            this.origin = origin;
        }

        @Override
        public void run() {
            ThumbnailsPanel thumbnailsPanel = GUI.getThumbnailsPanel();

            thumbnailsPanel.setFiles(imageFiles, origin);
        }
    }
}
