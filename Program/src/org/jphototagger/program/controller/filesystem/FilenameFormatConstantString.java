package org.jphototagger.program.controller.filesystem;

import org.jphototagger.lib.util.Bundle;

/**
 * Filename format that returns exactly a set string (formats nothing).
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatConstantString extends FilenameFormat {

    /**
     * Creates an instance with the string.
     *
     * @param string string
     */
    public FilenameFormatConstantString(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        setFormat(string);
    }

    /**
     * Returns {@link FilenameFormat#getFormat()} (the string set in the
     * constructor).
     *
     * @return format
     */
    @Override
    public String format() {
        return getFormat();
    }

    @Override
    public String toString() {
        return Bundle.getString(FilenameFormatConstantString.class, "FilenameFormatConstantString.String");
    }

    private FilenameFormatConstantString() {
    }
}
