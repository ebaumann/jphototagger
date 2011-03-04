package org.jphototagger.program.controller.filesystem;

import java.io.File;

/**
 * Format of a filename.
 *
 * @author Elmar Baumann
 */
public abstract class FilenameFormat {
    private File file;
    private File prevFile;
    private String format;

    /**
     * Returns a format string.
     *
     * @return string or null if not set
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets a format string.
     *
     * @param format  format
     */
    public void setFormat(String format) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }

        this.format = format;
    }

    /**
     * Sets the affected file.
     *
     * @param file  file
     */
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        this.prevFile = this.file;
        this.file = file;
    }

    /**
     * Returns the affected file.
     *
     * @return file or null if not set
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the previous set file.
     *
     * @return previous file
     */
    public File getPrevFile() {
        return prevFile;
    }

    /**
     * Tells that the next file will be renamed. This implementation does
     * nothing.
     */
    public void next() {

        // ignore
    }

    /**
     * Returns the formatted filename or part of a filename.
     *
     * @return filename (-part)
     */
    abstract public String format();
}
