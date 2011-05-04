package org.jphototagger.lib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IoUtil {

    /**
     * Catches IOException.
     * 
     * @param closable maybe null
     */
    public static void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException ex) {
                Logger.getLogger(IoUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private IoUtil() {
    }
}
