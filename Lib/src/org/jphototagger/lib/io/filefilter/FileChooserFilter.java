package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.Serializable;

/**
 * Filter for {@code javax.swing.JFileChooser} created from a
 * {@code java.io.FileFilter}.
 *
 * @author Elmar Baumann
 */
public final class FileChooserFilter extends javax.swing.filechooser.FileFilter implements Serializable {

    private static final long serialVersionUID = 4872224829528009592L;
    private final java.io.FileFilter fileFilter;
    private final String description;

    public FileChooserFilter(java.io.FileFilter fileFilter, String description) {
        this.fileFilter = fileFilter;
        this.description = description;
    }

    /**
     * Returns {@code java.io.FileFilter#accept(java.io.File)} from this
     * instance.
     *
     * @param  f  file
     * @return true when accepted
     */
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || fileFilter.accept(f);
    }

    @Override
    public String getDescription() {
        return description;
    }
}
