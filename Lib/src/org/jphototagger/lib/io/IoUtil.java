package org.jphototagger.lib.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.util.SystemProperties;

/**
 * @author Elmar Baumann
 */
public final class IoUtil {

    /**
     * Catches IOExceptions thrown while closing.
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

    /**
     * @param path e.g. <code>"/org/jphototagger/lib/io/texttemplate.txt"</code>
     * @return Resource as String with system dependend line separators
     * @throws IOException
     */
    public static String getTextResource(String path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        BufferedReader br = null;
        String newline = SystemProperties.getLineSeparator();
        try {
            InputStream is = IoUtil.class.getResourceAsStream(path);
            InputStreamReader isr = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            String line;
            boolean isFirstLine = true;
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(isFirstLine ? "" : newline);
                sb.append(line);
                isFirstLine = false;
            }
            if (sb.length() > 0) {
                sb.append(newline);
            }
            return sb.toString();
        } finally {
            close(br);
        }
    }

    public static void fromIsToOs(InputStream is, OutputStream os) throws IOException, InterruptedException {
        if (is == null) {
            throw new NullPointerException("is == null");
        }
        if (os == null) {
            throw new NullPointerException("os == null");
        }
        byte[] buffer = new byte[1024];
        int len = is.read(buffer);
        while (len != -1) {
            os.write(buffer, 0, len);
            len = is.read(buffer);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    /**
     * @param is1 will be closed
     * @param is2 will be closed
     * @return true if all bytes equals
     * @throws IOException
     */
    public static boolean contentEquals(InputStream is1, InputStream is2) throws IOException {
        // Original from http://stackoverflow.com/questions/4245863/fast-way-to-compare-inputstreams, partly modified
        // Alternative: org.apache.commons.io.IOUtils#contentEquals(): http://svn.apache.org/repos/asf/commons/proper/io/trunk/src/main/java/org/apache/commons/io/IOUtils.java
        if (is1 == null) {
            throw new NullPointerException("is1 == null");
        }
        if (is2 == null) {
            throw new NullPointerException("is2 == null");
        }
        int bufSize = 64 * 1024;
        byte[] buf1 = new byte[bufSize];
        byte[] buf2 = new byte[bufSize];
        try {
            DataInputStream d2 = new DataInputStream(is2);
            int len;
            while ((len = is1.read(buf1)) > 0) {
                d2.readFully(buf2, 0, len);
                for (int i = 0; i < len; i++) {
                    if (buf1[i] != buf2[i]) {
                        return false;
                    }
                }
            }
            return d2.read() < 0; // is the end of the second file also.
        } finally {
            is1.close();
            is2.close();
        }
    }

    private IoUtil() {
    }
}
