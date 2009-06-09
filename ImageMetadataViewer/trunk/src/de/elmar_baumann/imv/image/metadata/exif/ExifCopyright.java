package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.lib.template.Pair;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Copyright as specified in the EXIF standard, tag 33432 (8298.H).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/09
 */
public final class ExifCopyright {

    public static String getPhotographerCopyright(byte[] rawValue) {
        Pair<Integer, Integer> photographerOffsets = getPhotographerOffsets(rawValue);
        return string(rawValue, photographerOffsets.getFirst(),
                photographerOffsets.getSecond());
    }

    public static String getEditorCopyright(byte[] rawValue) {
        Pair<Integer, Integer> editorOffsets = getEditorOffsets(rawValue);
        return string(rawValue, editorOffsets.getFirst(),
                editorOffsets.getSecond());
    }

    private static String string(byte[] ba, int first, int last) {
        if (first < 0 || first > ba.length || last < first || last > ba.length)
            return "";
        return new String(Arrays.copyOfRange(ba, first, last), Charset.forName("US-ASCII"));
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
        Pair<Integer, Integer> photographerOffsets = getPhotographerOffsets(rawValue);
        if (photographerOffsets.getFirst() == -1 ||
                photographerOffsets.getSecond() == rawValue.length)
            return new Pair<Integer, Integer>(-1, -1);
        return new Pair<Integer, Integer>(photographerOffsets.getSecond() + 1,
                rawValue.length - 1);
    }

    private ExifCopyright() {
    }
}
