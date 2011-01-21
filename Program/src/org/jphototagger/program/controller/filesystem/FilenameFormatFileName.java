package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.resource.JptBundle;

/**
 * Format with the name of a filname excluded the postfix and the parents.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatFileName extends FilenameFormat {
    @Override
    public String format() {
        String filename = getFile().getName();
        int    index    = filename.lastIndexOf('.');

        return (index > 0)
               ? filename.substring(0, index)
               : filename;
    }

    @Override
    public String toString() {
        return JptBundle.INSTANCE.getString("FilenameFormatFileName.String");
    }
}
