package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.resource.JptBundle;

/**
 * A format which is always an empty string.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatEmptyString extends FilenameFormat {

    /**
     * Returns an empty String.
     *
     * @return ""
     */
    @Override
    public String format() {
        return "";
    }

    @Override
    public String toString() {
        return JptBundle.INSTANCE.getString("FilenameFormatEmptyString.String");
    }
}
