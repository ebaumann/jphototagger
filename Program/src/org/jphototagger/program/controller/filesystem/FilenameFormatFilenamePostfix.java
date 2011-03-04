package org.jphototagger.program.controller.filesystem;

/**
 * Returns the postfix of a file.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatFilenamePostfix extends FilenameFormat {

    /**
     * Postfix prepanded by a period.
     *
     * @return postfix
     */
    @Override
    public String format() {
        String filename = getFile().getName();
        int index = filename.lastIndexOf('.');

        return (index >= 0)
               ? filename.substring(index)
               : "";
    }
}
