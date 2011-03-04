package org.jphototagger.program.io;

import org.jphototagger.program.resource.JptBundle;

/**
 *
 * @author Elmar Baumann
 */
public enum FileSystemError {
    LOCKED(JptBundle.INSTANCE.getString("FileSystemError.LOCKED")),
    MISSING_PRIVILEGES(JptBundle.INSTANCE.getString("FileSystemError.MISSING_PRIVILEGES")),
    MOVE_RENAME_EXISTS(JptBundle.INSTANCE.getString("FileSystemError.MOVE_RENAME_EXISTS")),
    READ_ONLY(JptBundle.INSTANCE.getString("FileSystemError.READ_ONLY")),
    UNKNOWN(JptBundle.INSTANCE.getString("FileSystemError.UNKNOWN"));

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
