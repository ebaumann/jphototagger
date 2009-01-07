package de.elmar_baumann.imv.event;

import java.io.File;

/**
 * Event: A file was renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class RenameFileAction {
    
    private final File oldFile;
    private final File newFile;

    public RenameFileAction(File oldFile, File newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }

    public File getNewFile() {
        return newFile;
    }

    public File getOldFile() {
        return oldFile;
    }
}
