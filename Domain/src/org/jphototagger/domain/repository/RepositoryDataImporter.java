package org.jphototagger.domain.repository;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface RepositoryDataImporter extends PositionProvider {

    void importFromFile(File file);

    FileFilter getFileFilter();

    String getDisplayName();

    Icon getIcon();

    String getDefaultFilename();

    boolean isJPhotoTaggerData();
}
