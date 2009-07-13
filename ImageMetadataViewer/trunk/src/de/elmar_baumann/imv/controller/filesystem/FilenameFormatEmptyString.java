package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * A format which is always an empty string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class FilenameFormatEmptyString extends FilenameFormat {

    @Override
    public String toString() {
        return Bundle.getString("FilenameFormatEmpty.String"); // NOI18N
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
     * Returns an empty String.
     * 
     * @return ""
     */
    @Override
    public String format() {
        return "";
    }
}
