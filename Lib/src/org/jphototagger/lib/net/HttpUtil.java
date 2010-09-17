/*
 * @(#)HttpUtil.java    Created on 2010-01-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
    public static void write(URL source, OutputStream target,
                             CancelRequest cancelRequest)
            throws IOException {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        HttpURLConnection   connection  = null;
        BufferedInputStream inputStream = null;

        try {
            connection = (HttpURLConnection) source.openConnection();
            connection.setRequestProperty("Accept-Encoding", "zip, jar, exe");
            connection.connect();
            inputStream = new BufferedInputStream(connection.getInputStream());

            boolean cancel = false;

            for (int singleByte = inputStream.read();
                    !cancel && (singleByte != -1);
                    singleByte = inputStream.read()) {
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
