package org.jphototagger.program.controller.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Array of {@link FilenameFormat} objects.
 *
 * @author Elmar Baumann
 */
public final class FilenameFormatArray {
    private final List<FilenameFormat> formats = new ArrayList<FilenameFormat>();

    /**
     * Adds a format. {@link #format()} returns the filename built in the
     * same order of the calls to this function.
     *
     * @param format  format
     */
    public void addFormat(FilenameFormat format) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }

        synchronized (formats) {
            formats.add(format);
        }
    }

    /**
     * Calls to every format {@link FilenameFormat#next()}
     */
    public void notifyNext() {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.next();
            }
        }
    }

    /**
     * Removes all Formats.
     */
    public void clear() {
        synchronized (formats) {
            formats.clear();
        }
    }

    /**
     * Returns the formatted filename: the appended strings of all formats
     * ({@link FilenameFormat#format()}).
     *
     * @return filename
     */
    public String format() {
        StringBuilder sb = new StringBuilder();

        synchronized (formats) {
            for (FilenameFormat format : formats) {
                sb.append(format.format());
            }
        }

        return sb.toString();
    }

    /**
     * Sets a file to all formats.
     *
     * @param file file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.setFile(file);
            }
        }
    }
}
