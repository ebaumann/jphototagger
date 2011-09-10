package org.jphototagger.domain.repository;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.api.core.PositionProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Exporter extends PositionProvider {

    void exportFile(File file);

    FileFilter getFileFilter();

    String getDisplayName();

    Icon getIcon();

    String getDefaultFilename();

    boolean isJPhotoTaggerData();
}
