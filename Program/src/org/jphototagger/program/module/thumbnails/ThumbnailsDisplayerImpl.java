package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsDisplayer.class)
public final class ThumbnailsDisplayerImpl implements ThumbnailsDisplayer {

    @Override
    public void displayFiles(final Collection<? extends File> imageFiles, final OriginOfDisplayedThumbnails origin) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getThumbnailsPanel().setFiles(imageFiles, origin);
            }
        });
    }

    @Override
    public boolean isDisplayFile(File file) {
        return GUI.getThumbnailsPanel().containsFile(file);
    }

    @Override
    public void removeFilesFromDisplay(final Collection<? extends File> filesToRemove) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getThumbnailsPanel().removeFiles(filesToRemove);
            }
        });
    }

    @Override
    public boolean isMetaDataOverlay() {
        return GUI.getThumbnailsPanel().isMetaDataOverlay();
    }

    @Override
    public void setMetaDataOverlay(final boolean overlay) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                GUI.getThumbnailsPanel().setMetaDataOverlay(overlay);
            }
        });
    }
}
