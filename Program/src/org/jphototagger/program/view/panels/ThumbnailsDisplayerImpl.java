package org.jphototagger.program.view.panels;

import java.io.File;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.image.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
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
    public void displayThumbnailsOfFiles(Collection<? extends File> imageFiles) {
        ThumbnailsDisplayer thumbnailsDisplayer = new ThumbnailsDisplayer(imageFiles);

        EventQueueUtil.invokeInDispatchThread(thumbnailsDisplayer);
    }

    private static class ThumbnailsDisplayer implements Runnable {

        private final Collection<? extends File> imageFiles;

        ThumbnailsDisplayer(Collection<? extends File> imageFiles) {
            this.imageFiles = imageFiles;
        }

        @Override
        public void run() {
            ThumbnailsPanel thumbnailsPanel = GUI.getThumbnailsPanel();

            thumbnailsPanel.setFiles(imageFiles, TypeOfDisplayedImages.UNDEFINED);
        }
    }
}
