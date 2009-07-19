package de.elmar_baumann.imv.event;

/**
 * Event in a file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public enum FileSystemEvent {

    /**
     * A file was copied
     */
    COPY,
    /**
     * A file was deleted
     */
    DELETE,
    /**
     * A file was moved
     */
    MOVE,
    /**
     * A file was renamed
     */
    RENAME,
}
