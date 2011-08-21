package org.jphototagger.program.serviceprovider.core;

import java.io.File;
import java.util.Collection;

import org.jphototagger.api.image.ThumbnailsDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsDisplayer.class)
public final class ThumbnailsDisplayerImpl implements ThumbnailsDisplayer {

    @Override
    public void displayThumbnailsOfImageFiles(Collection<? extends File> imageFiles) {
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
