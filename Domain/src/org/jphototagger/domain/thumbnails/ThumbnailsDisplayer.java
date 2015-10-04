package org.jphototagger.domain.thumbnails;

import java.awt.Point;
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

    void setDisplayFlag(ThumbnailFlag flag, boolean display);

    boolean isDisplayFlag(ThumbnailFlag flag);

    void applyThumbnailsPanelSettings(ThumbnailsPanelSettings settings);

    List<File> getSelectedFiles();

    void setSelectedFiles(Collection<? extends File> files);

    List<File> getDisplayedFiles();

    Point getViewPosition();

    void refresh();

    void showMessagePopup(String text, Object owner);

    void hideMessagePopup(Object owner);
}
