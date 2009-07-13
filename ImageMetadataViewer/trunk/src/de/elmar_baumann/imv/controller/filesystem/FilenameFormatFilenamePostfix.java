package de.elmar_baumann.imv.controller.filesystem;

/**
 * Returns the postfix of a file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
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
        int index = filename.lastIndexOf("."); // NOI18N
        return index >= 0
               ? filename.substring(index)
               : ""; // NOI18N
    }
}
