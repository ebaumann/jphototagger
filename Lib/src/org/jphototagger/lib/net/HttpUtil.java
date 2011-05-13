package org.jphototagger.lib.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utils for the Hypertext Transfer Protocol.
 *
 * @author Elmar Baumann
 */
public final class HttpUtil {

    /**
     * Writes from a source URL into an output stream.
     *
     * @param  source       source URL
     * @param  target       buffer for writing the content of
     *                      <code>source</code>
     * @param cancelRequest canel request or null. If not null, reading will be
     *                      terminated if {@link CancelRequest#isCancel()}
     *                      returns true
     * @throws IOException on read/write errors
     */
    public static void write(URL source, OutputStream target, CancelRequest cancelRequest) throws IOException {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        HttpURLConnection connection = null;
        BufferedInputStream inputStream = null;

        try {
            connection = (HttpURLConnection) source.openConnection();
            connection.setRequestProperty("Accept-Encoding", "zip, jar, exe");
            connection.connect();
            inputStream = new BufferedInputStream(connection.getInputStream());

            boolean cancel = false;

            for (int singleByte = inputStream.read(); !cancel && (singleByte != -1); singleByte = inputStream.read()) {
                target.write(singleByte);
                cancel = (cancelRequest == null)
                         ? false
                         : cancelRequest.isCancel();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (target != null) {
                target.flush();
                target.close();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpUtil() {}
}
