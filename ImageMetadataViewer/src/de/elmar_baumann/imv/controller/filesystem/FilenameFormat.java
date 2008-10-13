package de.elmar_baumann.imv.controller.filesystem;

import java.io.File;

/**
 * Format of a filename.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public abstract class FilenameFormat {
    
    private File file;
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
        this.format = format;
    }

    /**
     * Sets the affected file.
     * 
     * @param file  file
     */
    public void setFile(File file) {
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
     * Tells that the next file will be renamed. This implementation does
     * nothing.
     */
    public void next() {
    }

    /**
     * Returns wheter the format will change.
     * 
     * @return true if changes are possible. This implementation returns true
     */
    public boolean isDynamic() {
        return true;
    }

    /**
     * Returns the formatted filename or part of a filename.
     * 
     * @return filename (-part)
     */
    abstract public String format();
}
