package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.lib.generics.Pair;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Copyright as specified in the EXIF standard, tag 33432 (8298.H).
 *
 * @author Elmar Baumann
 */
public final class ExifCopyright {
    private final String photographerCopyright;
    private final String editorCopyright;

    public ExifCopyright(byte[] photographerCopyright, byte[] editorCopyright) {
        if (photographerCopyright == null) {
            throw new NullPointerException("photographerCopyright == null");
        }

        if (editorCopyright == null) {
            throw new NullPointerException("editorCopyright == null");
        }

        this.photographerCopyright = photographerCopyright(photographerCopyright);
        this.editorCopyright = editorCopyright(editorCopyright);
    }

    public String editorCopyright() {
        return editorCopyright;
    }

    public String photographerCopyright() {
        return photographerCopyright;
    }

    /**
     * Returns the copyright information of the image's photographer.
     *
     * @param  rawValue  raw value
     * @return           copyright information or empty string if this
     *                   information is not in the raw value
     *
     */
    public static String photographerCopyright(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        Pair<Integer, Integer> photographerOffsets = photographerOffsets(rawValue);

        return string(rawValue, photographerOffsets.getFirst(), photographerOffsets.getSecond());
    }

    /**
     * Returns the copyright information of the image's editor.
     *
     * @param  rawValue  raw value
     * @return           copyright information or empty string if this
     *                   information is not in the raw value
     *
     */
    public static String editorCopyright(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }

        Pair<Integer, Integer> editorOffsets = editorOffsets(rawValue);

        return string(rawValue, editorOffsets.getFirst(), editorOffsets.getSecond());
    }

    private static String string(byte[] ba, int first, int last) {
        if ((first < 0) || (first > ba.length) || (last < first) || (last > ba.length)) {
            return "";
        }

        return new String(Arrays.copyOfRange(ba, first, last), Charset.forName("US-ASCII"));
    }

    private static Pair<Integer, Integer> photographerOffsets(byte[] rawValue) {
        if (rawValue.length < 2) {
            return new Pair<Integer, Integer>(-1, -1);
        }

        boolean end = false;
        int i = 0;

        while (!end && (i < rawValue.length)) {
            end = rawValue[i++] == 0x0;
        }

        return new Pair<Integer, Integer>(0, i - 1);
    }

    private static Pair<Integer, Integer> editorOffsets(byte[] rawValue) {
        if (rawValue.length < 3) {
            return new Pair<Integer, Integer>(-1, -1);
        }

        Pair<Integer, Integer> photographerOffsets = photographerOffsets(rawValue);

        if ((photographerOffsets.getFirst() == -1) || (photographerOffsets.getSecond() == rawValue.length)) {
            return new Pair<Integer, Integer>(-1, -1);
        }

        return new Pair<Integer, Integer>(photographerOffsets.getSecond() + 1, rawValue.length - 1);
    }
}
