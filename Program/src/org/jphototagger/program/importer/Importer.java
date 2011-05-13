package org.jphototagger.program.importer;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Importer {
    void importFile(File file);
    FileFilter getFileFilter();
    String getDisplayName();
    Icon getIcon();
    String getDefaultFilename();
}
