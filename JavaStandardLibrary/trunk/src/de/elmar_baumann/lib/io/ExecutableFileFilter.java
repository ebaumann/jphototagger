package de.elmar_baumann.lib.io;

import java.io.File;

/**
 * Accepts only executable files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/01
 */
public final class ExecutableFileFilter implements java.io.FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.canExecute();
    }

    /**
     * Returns a file filter for f file chooser.
     *
     * @param  description  description
     * @return file filter
     */
    public javax.swing.filechooser.FileFilter forFileChooser(String description) {
        return new FileChooserFilter(this, description);
    }
}
