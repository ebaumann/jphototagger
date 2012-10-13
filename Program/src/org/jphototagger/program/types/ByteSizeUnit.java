package org.jphototagger.program.types;

/**
 * Size units as n multiplied by one byte.
 *
 * @author Elmar Baumann
 */
public enum ByteSizeUnit {

    BYTE(1, "B"),
    KILO_BYTE(1024, "KB"),
    MEGA_BYTE(1024 * 1024, "MB"),
    GIGA_BYTE(1024 * 1024 * 1024, "GB");
    private final long bytes;
    private final String string;

    private ByteSizeUnit(long bytes, String string) {
        this.bytes = bytes;
        this.string = string;
    }

    public long bytes() {
        return bytes;
    }

    public static ByteSizeUnit unit(long size) {
        return (size >= GIGA_BYTE.bytes)
                ? GIGA_BYTE
                : (size >= MEGA_BYTE.bytes)
                ? MEGA_BYTE
                : (size >= KILO_BYTE.bytes)
                ? KILO_BYTE
                : BYTE;
    }

    @Override
    public String toString() {
        return string;
    }
}
