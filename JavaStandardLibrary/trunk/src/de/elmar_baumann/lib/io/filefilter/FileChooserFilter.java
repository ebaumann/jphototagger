package de.elmar_baumann.lib.io.filefilter;

import java.io.File;

/**
 * Filter for {@link javax.swing.JFileChooser} created from a
 * {@link java.io.FileFilter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-05-30
 */
public final class FileChooserFilter extends javax.swing.filechooser.FileFilter {

    private final java.io.FileFilter fileFilter;
    private final String description;

    public FileChooserFilter(java.io.FileFilter fileFilter, String description) {
        this.fileFilter = fileFilter;
        this.description = description;
    }

    /**
     * Returns {@link java.io.FileFilter#accept(java.io.File)} from this
     * instance.
     * 
     * @param  f  file
     * @return true when accepted
     */
    @Override
    public boolean accept(File f) {
        return fileFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return description;
    }
}
