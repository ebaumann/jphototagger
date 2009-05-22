package de.elmar_baumann.imv.controller.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Array of {@link FilenameFormat} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class FilenameFormatArray {
    
    private List<FilenameFormat> formats = new ArrayList<FilenameFormat>();
    
    /**
     * Adds a format. {@link #format()} returns the filename built in the
     * same order of the calls to this function.
     * 
     * @param format  format
     */
    public synchronized void addFormat(FilenameFormat format) {
        formats.add(format);
    }
    
    /**
     * Calls to every format {@link FilenameFormat#next()}
     */
    public synchronized void notifyNext() {
        for (FilenameFormat format : formats) {
            format.next();
        }
    }

    /**
     * Removes all Formats.
     */
    public synchronized void clear() {
        formats.clear();
    }
    
    /**
     * Returns the formatted filename: the appended strings of all formats
     * ({@link FilenameFormat#format()}).
     * 
     * @return filename
     */
    public synchronized String format() {
        StringBuffer buffer = new StringBuffer();
        for (FilenameFormat format : formats) {
            buffer.append(format.format());
        }
        return buffer.toString();
    }

}
