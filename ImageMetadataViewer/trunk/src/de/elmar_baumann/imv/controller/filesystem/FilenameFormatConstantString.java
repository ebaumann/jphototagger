package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Filename format that returns exactly a set string (formats nothing).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public final class FilenameFormatConstantString extends FilenameFormat {

    public FilenameFormatConstantString(String string) {
        setFormat(string);
    }

    /**
     * Returns false.
     * 
     * @return false
     */
    @Override
    public boolean isDynamic() {
        return false;
    }

    /**
     * Returns {@link FilenameFormat#getFormat()}.
     * 
     * @return format
     */
    @Override
    public String format() {
        return getFormat();
    }

    @Override
    public String toString() {
        return Bundle.getString("FilenameFormatConstantString.String"); // NOI18N
    }

    private FilenameFormatConstantString() {
    }
}
