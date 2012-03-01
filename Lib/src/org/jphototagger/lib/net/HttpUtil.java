package org.jphototagger.lib.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jphototagger.api.concurrent.CancelRequest;

/**
 * @author Elmar Baumann
 */
public final class HttpUtil {

    /**
     * Writes from a source URL into an output stream.
     *
     * @param source source URL
     * @param target buffer for writing the content of
     * <code>source</code>. Will be closed
     * @param cancelRequest canel request or null. If not null, reading will be terminated if {@code CancelRequest#isCancel()}
     * returns true
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
            inputStream = new BufferedInputStream(openConnectionCheckRedirects(connection));
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
            target.flush();
            target.close();
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Copied from http://docs.oracle.com/javase/1.4.2/docs/guide/deployment/deployment-guide/upgrade-guide/article-17.html
    private static InputStream openConnectionCheckRedirects(URLConnection c) throws IOException {
        boolean redir;
        int redirects = 0;
        InputStream in = null;
        do {
            if (c instanceof HttpURLConnection) {
                ((HttpURLConnection) c).setInstanceFollowRedirects(false);
            }
            // We want to open the input stream before getting headers
            // because getHeaderField() et al swallow IOExceptions.
            in = c.getInputStream();
            redir = false;
            if (c instanceof HttpURLConnection) {
                HttpURLConnection http = (HttpURLConnection) c;
                int stat = http.getResponseCode();
                if (stat >= 300 && stat <= 307 && stat != 306
                        && stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
                    URL base = http.getURL();
                    String loc = http.getHeaderField("Location");
                    URL target = null;
                    if (loc != null) {
                        target = new URL(base, loc);
                    }
                    http.disconnect();
                    // Redirection should be allowed only for HTTP and HTTPS
                    // and should be limited to 5 redirections at most.
                    if (target == null || !(target.getProtocol().equals("http")
                            || target.getProtocol().equals("https"))
                            || redirects >= 5) {
                        throw new SecurityException("illegal URL redirect");
                    }
                    redir = true;
                    c = target.openConnection();
                    redirects++;
                }
            }
        } while (redir);
        return in;
    }

    private HttpUtil() {
    }
}
