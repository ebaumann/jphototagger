package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Format with the name of a filname excluded the postfix and the parents.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public final class FilenameFormatFileName extends FilenameFormat {

    @Override
    public String format() {
        String filename = getFile().getName();
        int index = filename.lastIndexOf("."); // NOI18N
        return index > 0
               ? filename.substring(0, index)
               : filename;
    }

    @Override
    public String toString() {
        return Bundle.getString("FilenameFormatName.String"); // NOI18N
    }
}
