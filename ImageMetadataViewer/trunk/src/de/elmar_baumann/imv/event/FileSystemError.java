package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.resource.Bundle;

/**
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSystemError {

    LOCKED(Bundle.getString("FileSystemError.LOCKED")), // NOI18N
    MISSING_PRIVILEGES(Bundle.getString("FileSystemError.MISSING_PRIVILEGES")), // NOI18N
    MOVE_RENAME_EXISTS(Bundle.getString("FileSystemError.MOVE_RENAME_EXISTS")), // NOI18N
    READ_ONLY(Bundle.getString("FileSystemError.READ_ONLY")), // NOI18N
    UNKNOWN(Bundle.getString("FileSystemError.UNKNOWN")); // NOI18N
    private final String message;

    private FileSystemError(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return message;
    }
}
