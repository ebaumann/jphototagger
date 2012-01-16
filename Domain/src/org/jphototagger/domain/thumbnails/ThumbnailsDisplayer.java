package org.jphototagger.domain.thumbnails;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public interface ThumbnailsDisplayer {

    void displayFiles(Collection<? extends File> files, OriginOfDisplayedThumbnails origin);

    boolean isDisplayFile(File file);

    void removeFilesFromDisplay(Collection<? extends File> filesToRemove);

    void setMetaDataOverlay(boolean overlay);

    boolean isMetaDataOverlay();

    void applyThumbnailsPanelSettings(ThumbnailsPanelSettings settings);

    List<File> getSelectedFiles();

    List<File> getDisplayedFiles();

    void refresh();
}
