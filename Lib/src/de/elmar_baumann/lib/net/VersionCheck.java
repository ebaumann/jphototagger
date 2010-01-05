/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.net;

import de.elmar_baumann.lib.util.Version;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-05
 */
public final class VersionCheck {

    /**
     * Compares in a simple way two Versions for e.g. to check whether a new
     * application version is available on the web.
     * <p>
     * Converts a HTTP stream into a string, searches for the first occurence
     * of the substring <code>&lt;span class="version"&gt;</code> and grabs
     * the version string until the next <code>&lt;/span&gt;</code>. This
     * string must be parsable by
     * {@link Version#parseVersion(java.lang.String, java.lang.String)}.
     *
     * @param httpUrl          HTTP URL, e.g. <code>http://mysite.com/version.html</code>
     * @param versionDelimiter delimiter in the version string within the Stream
     *                         of <code>httpUrl</code>
     * @param compareToVersion version to compare the got HTTP version info
     * @return                 true if the HTTP version is newer than <code>version</code>
     *
     * @throws MalformedURLException     if the URL string is bad formed
     * @throws IOException               on read/write errors
     * @throws NumberFormatException     if the version are not delimited integers
     * @throws IllegalArgumentException  if the version does not contain 2 up to
     *                                   4 integer numbers
     */
    public static boolean existsNewer(
            String  httpUrl,
            String  versionDelimiter,
            Version compareToVersion
            )
            throws MalformedURLException, IOException, NumberFormatException, IllegalArgumentException {

        URL                   url = new URL(httpUrl);
        ByteArrayOutputStream os  = new ByteArrayOutputStream(10 * 1024);

        HttpUtil.write(url, os);

        String content    = os.toString();
        int    beginIndex = content.indexOf("<span class=\"version\">");

        if (beginIndex >= 0) {

            int endIndex = content.indexOf("</span>", beginIndex + 26); // <span class="version">x.x.x

            if (endIndex <= beginIndex) return false;

            String  versionString = content.substring(beginIndex + 22, endIndex);
            Version urlVersion    = Version.parseVersion(versionString, versionDelimiter);

            return urlVersion.compareTo(compareToVersion) > 0;
        }
        return false;
    }

    private VersionCheck() {
    }
}
