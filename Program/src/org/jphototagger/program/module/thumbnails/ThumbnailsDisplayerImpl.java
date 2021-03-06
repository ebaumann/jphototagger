package org.jphototagger.program.module.thumbnails;

import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.util.Collection;
import java.util.List;
import org.jphototagger.domain.thumbnails.MainWindowThumbnailsComponent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.ThumbnailFlag;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = ThumbnailsDisplayer.class),
    @ServiceProvider(service = MainWindowThumbnailsComponent.class)
})
public final class ThumbnailsDisplayerImpl implements ThumbnailsDisplayer, MainWindowThumbnailsComponent, ThumbnailsPanelProvider {

    private static final ThumbnailsAreaPanel THUMBNAILS_AREA_PANEL = new ThumbnailsAreaPanel(); // Has to be static!

    @Override
    public void displayFiles(final Collection<? extends File> imageFiles, final OriginOfDisplayedThumbnails origin) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                THUMBNAILS_AREA_PANEL.getThumbnailsPanel().setFiles(imageFiles, origin);
            }
        });
    }

    @Override
    public boolean isDisplayFile(File file) {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().containsFile(file);
    }

    @Override
    public void removeFilesFromDisplay(final Collection<? extends File> filesToRemove) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                THUMBNAILS_AREA_PANEL.getThumbnailsPanel().removeFiles(filesToRemove);
            }
        });
    }

    @Override
    public boolean isMetaDataOverlay() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().isMetaDataOverlay();
    }

    @Override
    public void setMetaDataOverlay(final boolean overlay) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                THUMBNAILS_AREA_PANEL.getThumbnailsPanel().setMetaDataOverlay(overlay);
            }
        });
    }

    @Override
    public void applyThumbnailsPanelSettings(final ThumbnailsPanelSettings settings) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                THUMBNAILS_AREA_PANEL.getThumbnailsPanel().applyThumbnailsPanelSettings(settings);
            }
        });
    }

    @Override
    public List<File> getSelectedFiles() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().getSelectedFiles();
    }

    @Override
    public void setSelectedFiles(Collection<? extends File> files) {
        THUMBNAILS_AREA_PANEL.getThumbnailsPanel().setSelectedFiles(files);
    }

    @Override
    public List<File> getDisplayedFiles() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().getFiles();
    }

    @Override
    public ThumbnailsPanel getThumbnailsPanel() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel();
    }

    @Override
    public Component getThumbnailsComponent() {
        return THUMBNAILS_AREA_PANEL;
    }

    @Override
    public void persistViewportPosition() {
        THUMBNAILS_AREA_PANEL.persistViewportPosition();
    }

    @Override
    public void restoreViewportPosition() {
        THUMBNAILS_AREA_PANEL.restoreViewportPosition();
    }

    @Override
    public void validateViewportPosition() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                THUMBNAILS_AREA_PANEL.validateViewportPosition();
            }
        });
    }

    @Override
    public Component getThumbnailsDisplayingComponent() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel();
    }

    @Override
    public void refresh() {
        THUMBNAILS_AREA_PANEL.getThumbnailsPanel().refresh();
    }

    @Override
    public void setDisplayFlag(ThumbnailFlag flag, boolean display) {
        THUMBNAILS_AREA_PANEL.getThumbnailsPanel().setDisplayFlag(flag, display);
    }

    @Override
    public boolean isDisplayFlag(ThumbnailFlag flag) {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().isDisplayFlag(flag);
    }

    @Override
    public Point getViewPosition() {
        return THUMBNAILS_AREA_PANEL.getThumbnailsPanel().getViewPosition();
}

    @Override
    public void showMessagePopup(String text, Object owner) {
        THUMBNAILS_AREA_PANEL.getThumbnailsPanel().showMessagePopup(text, owner);
    }

    @Override
    public void hideMessagePopup(Object owner) {
        THUMBNAILS_AREA_PANEL.getThumbnailsPanel().hideMessagePopup(owner);
    }
}
