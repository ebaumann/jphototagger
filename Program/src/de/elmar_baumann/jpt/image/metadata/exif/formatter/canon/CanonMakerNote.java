package de.elmar_baumann.jpt.image.metadata.exif.formatter.canon;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata.IfdType;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTags;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-13
 */
public final class CanonMakerNote {

    static byte[] getRawValueOfTag(File file, int tag, CanonIfd ifd) {
        CanonIfd.Entry entry = ifd.getEntryOfTag(tag);
        if (entry == null) return null;

        int  valueOffset    = entry.getValueOffset();
        int  valueByteCount = entry.dataType().bitCount() / 8 * entry.getValueNumber();
        int  minFileLength  = valueOffset + valueByteCount + 1;
        long fileLength     = file.length();

        assert fileLength >= minFileLength;
        if (fileLength < minFileLength) return null;
        try {
            byte[]           rawValue = new byte[valueByteCount];
            RandomAccessFile raf      = new RandomAccessFile(file, "r");

            raf.seek(valueOffset);
            raf.read(rawValue, 0, valueByteCount);
            raf.close();
            return rawValue;
        } catch (Exception ex) {
            AppLog.logSevere(CanonMakerNote.class, ex);
        }
        return null;
    }

    static short[] getTag1Values(File file, CanonIfd ifd) {
        byte[] raw = getRawValueOfTag(file, 1, ifd);
        if (raw == null) return null;

        return shortValues(raw, ifd.getByteOrder());
    }

    static short[] getTag4Values(File file, CanonIfd ifd) {
        byte[] raw = getRawValueOfTag(file, 4, ifd);
        if (raw == null) return null;

        return shortValues(raw, ifd.getByteOrder());
    }

    private static short[] shortValues(byte[] raw, ByteOrder byteOrder) {
        assert raw.length >= 2;
        if (raw.length < 2) return null;

        byte[] rawValueLen = new byte[2];
        System.arraycopy(raw, 0, rawValueLen, 0, 2);
        short valueCount = (short) (ExifDatatypeUtil.shortFromRawValue(rawValueLen, byteOrder) / 2 - 1);

        if (valueCount <= 0) return null;

        int minLength = 2 + valueCount * 2;
        assert raw.length >= minLength;
        if (raw.length < minLength) return null;

        short[] values   = new short[valueCount];
        byte[]  rawValue = new byte[2];

        for (int i = 0; i < valueCount; i++) {
            System.arraycopy(raw, 2 + 2 * i, rawValue, 0, 2);
            values[i] = ExifDatatypeUtil.shortFromRawValue(rawValue, byteOrder);
        }
        return values;
    }

    private CanonMakerNote() {
    }
}
