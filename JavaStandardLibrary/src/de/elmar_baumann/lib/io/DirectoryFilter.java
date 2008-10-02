package de.elmar_baumann.lib.io;

import java.io.File;

/**
 * Dateifilter, der nur Verzeichnisse akzeptiert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/23
 */
public class DirectoryFilter implements java.io.FileFilter {

    @Override
    public boolean accept(File file) {
        return file.isDirectory();
    }
}
