package de.elmar_baumann.lib.io.filefilter;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import java.io.FileFilter;

/**
 * Accepts all Files, rejects directories.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-05-22
 */
public final class AcceptAllFilesFilter implements FileFilter {

    public static AcceptAllFilesFilter INSTANCE = new AcceptAllFilesFilter();

    private AcceptAllFilesFilter() {
    }

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() ? true : false;
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser() {
        return new FileChooserFilter(this,
                Bundle.getString("AcceptAllFilesFilter.Description")); // NOI18N
    }
}
