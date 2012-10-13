package org.jphototagger.lib.io.filefilter;

import java.io.File;
import java.io.Serializable;
import javax.swing.filechooser.FileFilter;
import org.jphototagger.lib.util.Bundle;

/**
 * BUG: Does not work.
 *
 * @author Elmar Baumann
 */
public final class ExecutableFileChooserFileFilter extends FileFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean accept(File f) {
        return f.canExecute();
    }

    @Override
    public String getDescription() {
        return Bundle.getString(ExecutableFileChooserFileFilter.class, "ExecutableFileChooserFileFilter.Description");
    }
}
