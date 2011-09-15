package org.jphototagger.domain.repository;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;
import org.jphototagger.api.collections.PositionProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Importer extends PositionProvider {

    void importFile(File file);

    FileFilter getFileFilter();

    String getDisplayName();

    Icon getIcon();

    String getDefaultFilename();

    boolean isJPhotoTaggerData();
}
