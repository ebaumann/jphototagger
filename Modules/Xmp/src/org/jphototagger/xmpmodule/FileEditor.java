package org.jphototagger.xmpmodule;

import java.io.File;

/**
 * @author Elmar Baumann
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
