package de.elmar_baumann.imv.event;

import java.io.File;

/**
 * Event: A file was renamed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class RenameFileAction {
    
    private File oldFile;
    private File newFile;

    public RenameFileAction() {
    }

    public RenameFileAction(File oldFile, File newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }

    public File getNewFile() {
        return newFile;
    }

    public void setNewFile(File newFile) {
        this.newFile = newFile;
    }

    public File getOldFile() {
        return oldFile;
    }

    public void setOldFile(File oldFile) {
        this.oldFile = oldFile;
    }

}
