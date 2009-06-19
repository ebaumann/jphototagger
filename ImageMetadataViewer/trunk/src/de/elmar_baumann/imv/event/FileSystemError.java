package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.resource.Bundle;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSystemError {

    LOCKED(Bundle.getString("FileSystemError.LOCKED")),
    MISSING_PRIVILEGES(Bundle.getString("FileSystemError.MISSING_PRIVILEGES")),
    MOVE_RENAME_EXISTS(Bundle.getString("FileSystemError.MOVE_RENAME_EXISTS")),
    READ_ONLY(Bundle.getString("FileSystemError.READ_ONLY")),
    UNKNOWN(Bundle.getString("FileSystemError.UNKNOWN"));
    private final String message;

    private FileSystemError(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return message;
    }
}
