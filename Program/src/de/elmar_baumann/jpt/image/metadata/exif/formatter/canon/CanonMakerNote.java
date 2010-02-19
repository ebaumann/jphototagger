package de.elmar_baumann.jpt.image.metadata.exif.formatter.canon;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.jpt.types.FileType;
import de.elmar_baumann.lib.lang.Util;
import de.elmar_baumann.lib.thirdparty.KMPMatch;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-13
 */
public final class CanonMakerNote {

    private static final byte[]    JPEG_MAGIC_BYTES      = { (byte) 0xFF, (byte) 0xD8 };
    private static final byte[]    JPEG_EXIF_MAGIC_BYTES = { 0x45, 0x78, 0x69, 0x66, 0x00, 0x00 }; // "Exif" + 0x00, 0x00
    private static final byte[]    JPEG_TIFF_MAGIC_BYTES = { 0x49, 0x49, 0x2A, 0x00, 0x08, 0x00, 0x00, 0x00 };
    private static final byte[]    JPEG_APP1_MARKER      = { (byte) 0xFF, (byte) 0xE1 };
    private static final byte[]    JPEG_SOS_MARKER       = { (byte) 0xFF, (byte) 0xDA }; // Start Of (image) Stream
    private static final ByteOrder JPEG_BYTE_ORDER       = ByteOrder.BIG_ENDIAN;


    static byte[] getRawValueOfTag(File file, int tag, CanonIfd ifd) {

        CanonIfd.Entry entry = ifd.getEntryOfTag(tag);
        if (entry == null) return null;

        if (FileType.isJpegFile(file.getName())) {
            return rawValueFromJpeg(file, entry);
        } else {
            return rawValueFromTiff(file, entry);
        }
    }


    // JPEG files: EXIF offsets are relative to the start
    // of the TIFF header at the beginning of the EXIF segment)

    // Reading JPEG-EXIF: http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html

    private static byte[] rawValueFromJpeg(File file, CanonIfd.Entry entry) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");

            if (!isJpegFile(raf)) return null;
            if (!seekToJpegApp1Marker(raf)) return null;

            final int app1DataSize = getJpegMarkerDataSize(raf);
            if (app1DataSize - 2 <= 0) return null;

            byte[] app1Data = new byte[app1DataSize - 2];

            final long app1DataPos = raf.getFilePointer();
            raf.seek(app1DataPos + 2); // +2: Data size info
            int read = raf.read(app1Data, 0, app1DataSize - 2);
            if (read != app1DataSize - 2) return null;

            if (!hasExif(app1Data)) return null;

            int tiffStart = KMPMatch.indexOf(app1Data, JPEG_TIFF_MAGIC_BYTES);
            if (tiffStart <= 0) return null;

            long valueOffset    = app1DataPos + 2 + tiffStart + entry.getValueOffset();
            int  valueByteCount = entry.getValueByteCount();

            return rawValue(file, valueOffset, valueByteCount);

        } catch (Exception ex) {
            AppLogger.logSevere(CanonMakerNote.class, ex);
        } finally {
            close(raf);
        }
        return null;
    }

    private static boolean hasExif(byte[] raw) {
        return KMPMatch.indexOf(raw, JPEG_EXIF_MAGIC_BYTES) >= 0;
    }

    private static boolean seekToJpegApp1Marker(RandomAccessFile raf) throws Exception {
        boolean found        = false;
        long    markerOffset = 2;
        byte[]  marker       = new byte[2];

        do {
            if (!isSeekOk(raf, markerOffset)) return false;
            raf.seek(markerOffset);

            if (!isReadLengthOk(raf, 2)) return false;
            int read = raf.read(marker, 0, 2);
            if (read != 2) return false;

            found = Util.compareTo(marker, JPEG_APP1_MARKER) == 0;
            boolean isSos = Util.compareTo(marker, JPEG_SOS_MARKER) == 0;

            if (isSos) return false;

            if (!found) {
                markerOffset = raf.getFilePointer() + getJpegMarkerDataSize(raf);
            }
        } while (!found);
        return found;
    }
    
    private static boolean isSeekOk(RandomAccessFile raf, long offset) throws Exception {
        long    pos        = raf.getFilePointer();
        long    fileLength = raf.length();
        boolean ok         = offset >= 0 && pos + offset < fileLength;
        
        assert ok : "Invalid seek! Current pos: " + pos + ", will seek to offset: " + offset + ", file length: " + fileLength;
        
        return ok;
    }
    
    private static boolean isReadLengthOk(RandomAccessFile raf, long length) throws Exception {
        long    pos        = raf.getFilePointer();
        long    fileLength = raf.length();
        boolean ok         = length > 0 && pos + length < fileLength;
        
        assert ok : "Invalid read! Current pos: " + pos + ", will read: " + length + " bytes, file length: " + fileLength;
        
        return ok;
    }

    private static short getJpegMarkerDataSize(RandomAccessFile raf) throws Exception {
        if (!isReadLengthOk(raf, 2)) return -1;

        byte[] size = new byte[2];
        long   pos  = raf.getFilePointer();

        int read = raf.read(size, 0, 2);
        if (read != 2) throw new IOException("Size read != 2");
        raf.seek(pos);

        return ExifDatatypeUtil.shortFromRawValue(size, JPEG_BYTE_ORDER);
    }
    
    private static boolean isJpegFile(RandomAccessFile raf) throws Exception {
        byte[] start = new byte[2];

        raf.seek(0);
        int read = raf.read(start, 0, 2);
        if (read != 2) return false;

        return Util.compareTo(start, JPEG_MAGIC_BYTES) == 0;
    }

    private static byte[] rawValueFromTiff(File file, CanonIfd.Entry entry) {
        int  valueOffset    = entry.getValueOffset();
        int  valueByteCount = entry.getValueByteCount();

        return rawValue(file, valueOffset, valueByteCount);
    }
    
    private static byte[] rawValue(File file, long valueOffset, int valueByteCount) {
        long minFileLength = valueOffset + valueByteCount + 1;
        long fileLength    = file.length();

        assert fileLength >= minFileLength;
        if (fileLength < minFileLength) return null;

        RandomAccessFile raf = null;
        try {
            byte[] rawValue = new byte[valueByteCount];
                   raf      = new RandomAccessFile(file, "r");

            raf.seek(valueOffset);
            raf.read(rawValue, 0, valueByteCount);

            return rawValue;
        } catch (Exception ex) {
            AppLogger.logSevere(CanonMakerNote.class, ex);
        } finally {
            close(raf);
        }
        return null;
    }

    private static void close(RandomAccessFile raf) {
        if (raf != null) {
            try {
                raf.close();
            } catch (Exception ex) {
                AppLogger.logSevere(CanonMakerNote.class, ex);
            }
        }
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
