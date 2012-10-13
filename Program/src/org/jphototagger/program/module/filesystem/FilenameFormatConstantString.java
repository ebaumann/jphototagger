package org.jphototagger.program.module.filesystem;

import org.jphototagger.lib.util.Bundle;

/**
 * Filename format that returns exactly a set string (formats nothing).
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatConstantString extends FilenameFormat {

    public FilenameFormatConstantString() {
        this("");
    }

    public FilenameFormatConstantString(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        setFormat(string);
    }

    /**
     * Returns {@code FilenameFormat#getFormat()} (the string set in the constructor).
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
}
