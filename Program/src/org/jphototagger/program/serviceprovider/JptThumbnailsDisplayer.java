package org.jphototagger.program.serviceprovider;

import java.io.File;
import java.util.Collection;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.services.core.ThumbnailsDisplayer;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class JptThumbnailsDisplayer implements ThumbnailsDisplayer {

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

            thumbnailsPanel.setFiles(imageFiles, Content.UNDEFINED);
        }
    }
}
