package de.elmar_baumann.imv.event;

import java.io.File;

/**
 * Event: A file was renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class RenameFileEvent {
    
    private final File oldFile;
    private final File newFile;

    /**
     * Constructor.
     *
     * @param oldFile old file
     * @param newFile new file
     */
    public RenameFileEvent(File oldFile, File newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }

    /**
     * Returns the new file.
     *
     * @return new file
     */
    public File getNewFile() {
        return newFile;
    }

    /**
     * Returns the old file.
     *
     * @return old file
     */
    public File getOldFile() {
        return oldFile;
    }
}
