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
package de.elmar_baumann.imv.types;

/**
 * Dateitypen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-31
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
        String filenameLowercase = filename.toLowerCase();
        return filenameLowercase.endsWith(".jpg") || // NOI18N
            filenameLowercase.endsWith(".jpeg"); // NOI18N
    }

    /**
     * Liefert, ob eine Datei eine RAW-Datei ist. Es wird lediglich der Name
     * herangezogen (Endungen), nicht der interne Aufbau.
     * 
     * @param filename Dateiname
     * @return         true, wenn die Datei eine RAW-Datei ist
     */
    public static boolean isRawFile(String filename) {
        String filenameLowerCase = filename.toLowerCase();
        boolean isCommonImageFile =
            filenameLowerCase.endsWith("tif") || // NOI18N
            filenameLowerCase.endsWith("tiff") || // NOI18N
            filenameLowerCase.endsWith("jpg") || // NOI18N
            filenameLowerCase.endsWith("jpeg") || // NOI18N
            filenameLowerCase.endsWith("gif") || // NOI18N
            filenameLowerCase.endsWith("png");    // NOI18N
        return !isCommonImageFile;
    }

    private FileType() {}
}
