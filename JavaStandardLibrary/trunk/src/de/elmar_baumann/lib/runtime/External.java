package de.elmar_baumann.lib.runtime;

import de.elmar_baumann.lib.template.Pair;
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
public final class External {

    private enum Stream {

        STANDARD_ERROR,
        STANDARD_IN,
        STANDARD_OUT,
    }

    /**
     * Führt ein externes Programm aus und liefert dessen Output.
     * 
     * @param  command Kommando, z.B. <code>/bin/ls -l /home</code>
     * @return         Ausgabe des Programms oder null bei Misserfolg oder
     *                 keiner Ausgabe. Das erste Element des Paars ist die
     *                 Standardausgabe, das zweite die Standardfehlerausgabe.
     */
    public static Pair<byte[], byte[]> executeGetOutput(String command) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command);
            return new Pair<byte[], byte[]>(
                getStream(process, Stream.STANDARD_OUT),
                getStream(process, Stream.STANDARD_ERROR));
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Pair<byte[], byte[]>(null, null);
    }

    private static byte[] getStream(Process process, Stream s) {
        assert s.equals(Stream.STANDARD_ERROR) || s.equals(Stream.STANDARD_OUT);
        final int buffersize = 100 * 1024;
        byte[] returnBytes = null;
        try {
            InputStream stream = s.equals(Stream.STANDARD_OUT)
                ? process.getInputStream()
                : process.getErrorStream();
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

    private External() {
    }
}
