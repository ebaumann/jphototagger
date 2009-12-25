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
package de.elmar_baumann.jpt.image.metadata.exif.entry;

import de.elmar_baumann.lib.generics.Pair;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Copyright as specified in the EXIF standard, tag 33432 (8298.H).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-09
 */
public final class ExifCopyright {

    /**
     * Returns the copyright information of the image's photographer.
     *
     * @param  rawValue  raw value
     * @return           copyright information or empty string if this
     *                   information is not in the raw value
     *
     */
    public static String getPhotographerCopyright(byte[] rawValue) {
        Pair<Integer, Integer> photographerOffsets = getPhotographerOffsets(
                rawValue);
        return string(rawValue, photographerOffsets.getFirst(),
                photographerOffsets.getSecond());
    }

    /**
     * Returns the copyright information of the image's editor.
     *
     * @param  rawValue  raw value
     * @return           copyright information or empty string if this
     *                   information is not in the raw value
     *
     */
    public static String getEditorCopyright(byte[] rawValue) {
        Pair<Integer, Integer> editorOffsets = getEditorOffsets(rawValue);
        return string(rawValue, editorOffsets.getFirst(),
                editorOffsets.getSecond());
    }

    private static String string(byte[] ba, int first, int last) {
        if (first < 0 || first > ba.length || last < first || last > ba.length)
            return "";
        return new String(Arrays.copyOfRange(ba, first, last), Charset.forName(
                "US-ASCII"));
    }

    private static Pair<Integer, Integer> getPhotographerOffsets(byte[] rawValue) {
        if (rawValue.length < 2) return new Pair<Integer, Integer>(-1, -1);
        boolean end = false;
        int i = 0;
        while (!end && i < rawValue.length) {
            end = rawValue[i++] == 0x0;
        }
        return new Pair<Integer, Integer>(0, i - 1);
    }

    private static Pair<Integer, Integer> getEditorOffsets(byte[] rawValue) {
        if (rawValue.length < 3) return new Pair<Integer, Integer>(-1, -1);
        Pair<Integer, Integer> photographerOffsets = getPhotographerOffsets(
                rawValue);
        if (photographerOffsets.getFirst() == -1 ||
                photographerOffsets.getSecond() == rawValue.length)
            return new Pair<Integer, Integer>(-1, -1);
        return new Pair<Integer, Integer>(photographerOffsets.getSecond() + 1,
                rawValue.length - 1);
    }

    private ExifCopyright() {
    }
}
