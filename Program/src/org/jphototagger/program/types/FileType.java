/*
 * @(#)FileType.java    Created on 2008-08-31
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.types;

/**
 * Dateitypen.
 *
 * @author  Elmar Baumann
 */
public final class FileType {

    /**
     * Liefert, ob eine Datei eine JPEG-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen .jpg oder .jpeg), nicht der interne Aufbau.
     *
     * @param filename Dateiname
     * @return         true, wenn die Datei eine JPEG-Datei ist
     */
    public static boolean isJpegFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        String filenameLowercase = filename.toLowerCase();

        return filenameLowercase.endsWith(".jpg")
               || filenameLowercase.endsWith(".jpeg");
    }

    /**
     * Liefert, ob eine Datei eine RAW-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen), nicht der interne Aufbau.
     *
     * @param filename Dateiname
     * @return         true, wenn die Datei eine RAW-Datei ist
     */
    public static boolean isRawFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        String  filenameLowerCase = filename.toLowerCase();
        boolean isCommonImageFile = filenameLowerCase.endsWith("tif")
                                    || filenameLowerCase.endsWith("tiff")
                                    || filenameLowerCase.endsWith("jpg")
                                    || filenameLowerCase.endsWith("jpeg")
                                    || filenameLowerCase.endsWith("gif")
                                    || filenameLowerCase.endsWith("png");

        return !isCommonImageFile;
    }

    private FileType() {}
}
