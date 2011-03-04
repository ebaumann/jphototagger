package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.Serializable;

/**
 * Accepts only executable files.
 *
 * @author Elmar Baumann
 */
public final class ExecutableFileFilter implements java.io.FileFilter, Serializable {
    private static final long serialVersionUID = -3143807596793335212L;

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
