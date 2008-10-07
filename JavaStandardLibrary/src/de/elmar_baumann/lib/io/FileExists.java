package de.elmar_baumann.lib.io;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Returns whether a file exists whitin a maximum amount of tim. Workaround for
 * Windows: If a network drive is disconnected, <code>File.exists()</code> ist
 * very slow.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/07
 */
public class FileExists extends Thread {

    private File file;
    private boolean exists = false;
    private static final long maxMillis = 1000;

    private FileExists(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        exists = file.exists();
    }

    /**
     * Returns whether a file exists.
     * 
     * @param  file File
     * @return true if exists, false if not or after a timeout
     */
    public static boolean exists(File file) {
        FileExists thread = new FileExists(file);
        thread.start();
        try {
            thread.join(maxMillis);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileExists.class.getName()).log(Level.SEVERE, null, ex);
        }
        return thread.exists;
    }
}
