package org.jphototagger.program.image.thumbnail.creator;

import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExternalThumbnailCreator {

    FileFilter getThumbnailCreatorFileFilter();
    String getThumbnailCreationCommand(String creationProgramFilePath);
    String getDownloadUrl();
    String getDisplayName();
}
