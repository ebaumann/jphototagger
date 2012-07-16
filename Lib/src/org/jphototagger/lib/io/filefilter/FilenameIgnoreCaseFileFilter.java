package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Elmar Baumann
 */
public final class FilenameIgnoreCaseFileFilter implements FileFilter {

    private final String filename;

    /**
     * @param filename to compare with
     */
    public FilenameIgnoreCaseFileFilter(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }
        this.filename = filename;
    }

    /**
     * @param file
     * @return true if the <em>filename</em> is equals ignoring the case
     */
    @Override
    public boolean accept(File file) {
        String name = file.getName();
        return filename.equalsIgnoreCase(name);
    }
}
