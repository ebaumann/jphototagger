package org.jphototagger.lib.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.util.SystemProperties;

/**
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

    /**
     * @param path e.g. <code>"/org/jphototagger/lib/io/texttemplate.txt"</code>
     * @return Resource as String with system dependend line separators
     * @throws IOException
     */
    public static String getTextResource(String path) throws IOException {
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

    private IoUtil() {
    }
}
