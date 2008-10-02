package de.elmar_baumann.lib.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Externes (nicht in der JVM stattfindendes).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/02
 */
public class External {

    /**
     * Führt ein externes Programm aus und liest dessen Output.
     * 
     * @param  command Kommando, z.B. <code>/bin/ls -l /home</code>
     * @return         Ausgabe des Programms oder null bei Misserfolg
     */
    public static byte[] executeGetOutput(String command) {
        final int buffersize = 100 * 1024;
        byte[] returnBytes = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            InputStream stream = process.getInputStream();
            byte[] buffer = new byte[buffersize];
            int bytesRead = -1;
            boolean finished = false;

            while (!finished) {
                bytesRead = stream.read(buffer, 0, buffersize);
                if (bytesRead > 0) {
                    if (returnBytes == null) {
                        returnBytes = new byte[bytesRead];
                        System.arraycopy(buffer, 0, returnBytes, 0, bytesRead);
                    } else {
                        returnBytes = appendByteArray(returnBytes, buffer,
                            bytesRead);
                    }
                }
                finished = bytesRead < 0;
            }
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(External.class.getName()).
                    log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(External.class.getName()).
                log(Level.SEVERE, null, ex);
        }
        return returnBytes;
    }

    private static byte[] appendByteArray(byte[] left, byte[] right, int count) {
        byte[] newArray = new byte[left.length + count];
        System.arraycopy(left, 0, newArray, 0, left.length);
        System.arraycopy(right, 0, newArray, left.length, count);
        return newArray;
    }
}
