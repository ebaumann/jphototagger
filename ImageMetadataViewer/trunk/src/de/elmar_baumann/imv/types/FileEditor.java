package de.elmar_baumann.imv.types;

import java.io.File;

/**
 * Edits a file.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-05-22
 */
public class FileEditor {

    private volatile boolean confirmOverwrite;

    public void setConfirmOverwrite(boolean overwrite) {
        this.confirmOverwrite = overwrite;
    }

    public boolean getConfirmOverwrite() {
        return confirmOverwrite;
    }

    /**
     * Does nothing. Specialized classes can do here something.
     *
     * @param file  file to edit
     */
    public void edit(File file) {
    }
}
