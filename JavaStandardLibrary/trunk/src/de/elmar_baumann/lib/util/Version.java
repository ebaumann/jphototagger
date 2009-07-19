package de.elmar_baumann.lib.util;

import java.text.MessageFormat;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-04-30
 */
public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public int compareTo(Version o) {
        return major > o.major ? 1
                : major < o.major ? -1
                : minor > o.minor ? 1
                : minor < o.minor ? -1
                : 0;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}.{1}", new Object[]{major, minor}); // NOI18N
    }
}
