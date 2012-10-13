package org.jphototagger.lib.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.jphototagger.lib.util.Version;

/**
 * Grabs version information from the net.
 *
 * @author Elmar Baumann
 */
public final class NetVersion {

    /**
     * Returns a Version from a version file over the HTTP.
     * <p>
     * Converts a HTTP stream into a string, searches for the first occurence
     * of the substring <code>&lt;span class="version"&gt;</code> and grabs
     * the version string until the next <code>&lt;/span&gt;</code>. This
     * string must be parsable by
     * {@code Version#parseVersion(java.lang.String, java.lang.String)}.
     *
     * @param httpUrl          HTTP URL, e.g.
     *                         <code>http://mysite.com/version.html</code>
     * @param versionDelimiter delimiter in the version string within the Stream
     *                         of <code>httpUrl</code>
     * @return                 Version or null if the version format is not
     *                         as descriped whitin span tags
     *
     * @throws MalformedURLException     if the URL string is bad formed
     * @throws IOException               on read/write errors
     * @throws NumberFormatException     if the version are not delimited
     *                                   integers
     * @throws IllegalArgumentException  if the version does not contain 2 up to
     *                                   4 integer numbers
     */
    public static Version getOverHttp(String httpUrl, String versionDelimiter) throws MalformedURLException, IOException, NumberFormatException, IllegalArgumentException {
        if (httpUrl == null) {
            throw new NullPointerException("httpUrl == null");
        }

        if (versionDelimiter == null) {
            throw new NullPointerException("versionDelimiter == null");
        }

        URL url = new URL(httpUrl);
        ByteArrayOutputStream os = new ByteArrayOutputStream(10 * 1024);

        HttpUtil.write(url, os, null);

        String content = os.toString();
        int beginIndex = content.indexOf("<span class=\"version\">");

        if (beginIndex >= 0) {
            int endIndex = content.indexOf("</span>", beginIndex + 1);

            if (endIndex <= beginIndex) {
                return null;
            }

            String versionString = content.substring(beginIndex + 22, endIndex);

            return Version.parseVersion(versionString, versionDelimiter);
        }

        return null;
    }

    private NetVersion() {
    }
}
