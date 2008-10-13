package de.elmar_baumann.imv.controller.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Array of {@link FilenameFormat} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class FilenameFormatArray {
    
    private List<FilenameFormat> patterns = new ArrayList<FilenameFormat>();
    
    /**
     * Adds a pattern. {@link #getFilename()} returns the filename in the
     * same order of the calls to this function.
     * 
     * @param pattern  pattern
     */
    public void addPattern(FilenameFormat pattern) {
        patterns.add(pattern);
    }
    
    /**
     * Returns the formatted filename: the appended strings of all formats
     * ({@link FilenamePattern#format()}).
     * 
     * @return filename
     */
    public String getFilename() {
        StringBuffer buffer = new StringBuffer();
        for (FilenameFormat pattern : patterns) {
            buffer.append(pattern.format());
        }
        return buffer.toString();
    }

}
