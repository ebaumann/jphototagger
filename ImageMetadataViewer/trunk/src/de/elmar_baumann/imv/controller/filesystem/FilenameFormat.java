package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.event.listener.FilenameFormatListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Format of a filename.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public abstract class FilenameFormat {

    private File file;
    private File prevFile;
    private String format;
    private final Set<FilenameFormatListener> listeners =
            new HashSet<FilenameFormatListener>();

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

    /**
     * Adds a filename format listener.
     *
     * @param listener listener
     */
    public void addFilenameFormatListener(FilenameFormatListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a filename format listener.
     *
     * @param listener listener
     */
    public void removeFilenameFormatListener(FilenameFormatListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Sends a request to all {@link FilenameFormatListener}s.
     *
     * @param request request
     */
    protected void requestListeners(FilenameFormatListener.Request request) {
        synchronized (listeners) {
            for (FilenameFormatListener listener : listeners) {
                listener.request(request);
            }
        }
    }
}
