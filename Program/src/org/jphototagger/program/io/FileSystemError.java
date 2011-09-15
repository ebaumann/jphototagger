package org.jphototagger.program.io;

import org.jphototagger.lib.util.Bundle;

/**
 *
 * @author Elmar Baumann
 */
public enum FileSystemError {

    LOCKED(Bundle.getString(FileSystemError.class, "FileSystemError.LOCKED")),
    MISSING_PRIVILEGES(Bundle.getString(FileSystemError.class, "FileSystemError.MISSING_PRIVILEGES")),
    MOVE_RENAME_EXISTS(Bundle.getString(FileSystemError.class, "FileSystemError.MOVE_RENAME_EXISTS")),
    READ_ONLY(Bundle.getString(FileSystemError.class, "FileSystemError.READ_ONLY")),
    UNKNOWN(Bundle.getString(FileSystemError.class, "FileSystemError.UNKNOWN"));

    private final String message;

    private FileSystemError(String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        this.message = message;
    }

    public String getLocalizedMessage() {
        return message;
    }
}
