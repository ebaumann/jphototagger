package de.elmar_baumann.imv.types;

/**
 * Size units as n multiplied by one byte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/16
 */
public enum SizeUnit {

    BYTE(1, "B"),
    KILO_BYTE(1024, "KB"),
    MEGA_BYTE(1024 * 1024, "MB"),
    GIGA_BYTE(1024 * 1024 * 1024, "GB");
    private final long bytes;
    private final String string;

    private SizeUnit(long bytes, String string) {
        this.bytes = bytes;
        this.string = string;
    }

    public long bytes() {
        return bytes;
    }

    public static SizeUnit unit(long size) {
        return size >= GIGA_BYTE.bytes
               ? GIGA_BYTE
               : size >= MEGA_BYTE.bytes
                 ? MEGA_BYTE
                 : size >= KILO_BYTE.bytes
                   ? KILO_BYTE
                   : BYTE;
    }

    @Override
    public String toString() {
        return string;
    }
}
