package org.jphototagger.lib.io.filefilter;

import org.jphototagger.lib.resource.JslBundle;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * BUG: Does not work.
 *
 * @author Elmar Baumann
 */
public final class ExecutableFileChooserFileFilter extends FileFilter
        implements Serializable {
    private static final long serialVersionUID = 8245956226000329520L;

    @Override
    public boolean accept(File f) {
        return f.canExecute();
    }

    @Override
    public String getDescription() {
        return JslBundle.INSTANCE.getString(
            "ExecutableFileChooserFileFilter.Description");
    }
}
