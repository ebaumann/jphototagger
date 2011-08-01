package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import org.jphototagger.lib.util.Bundle;

/**
 * Accepts all Files, rejects directories.
 *
 * @author Elmar Baumann
 */
public final class AcceptAllFilesFilter implements FileFilter, Serializable {
    public static final AcceptAllFilesFilter INSTANCE = new AcceptAllFilesFilter();
    private static final long serialVersionUID = 6297923800725402735L;

    private AcceptAllFilesFilter() {}

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile()
               ? true
               : false;
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser() {
        return new FileChooserFilter(this, Bundle.getString(AcceptAllFilesFilter.class, "AcceptAllFilesFilter.Description"));
    }
}
