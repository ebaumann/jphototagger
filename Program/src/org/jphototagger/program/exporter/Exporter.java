package org.jphototagger.program.exporter;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Exporter {
    void exportFile(File file);

    FileFilter getFileFilter();

    String getDisplayName();

    Icon getIcon();

    String getDefaultFilename();
}
