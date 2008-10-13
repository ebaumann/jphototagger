package de.elmar_baumann.imv.controller.filesystem;

/**
 * Format of a filename.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface FilenameFormat {

    /**
     * Returns the formatted filename or part of a filename.
     * 
     * @return filename (-part)
     */
    public String format();
}
