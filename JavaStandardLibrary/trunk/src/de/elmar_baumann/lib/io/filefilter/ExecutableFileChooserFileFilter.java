package de.elmar_baumann.lib.io.filefilter;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * BUG: Does not work.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/01
 */
public final class ExecutableFileChooserFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return f.canExecute();
    }

    @Override
    public String getDescription() {
        return Bundle.getString("ExecutableFileChooserFileFilter.Description");
    }

}
