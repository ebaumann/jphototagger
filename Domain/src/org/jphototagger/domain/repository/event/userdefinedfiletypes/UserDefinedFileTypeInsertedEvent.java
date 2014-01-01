package org.jphototagger.domain.repository.event.userdefinedfiletypes;

import org.jphototagger.domain.filetypes.UserDefinedFileType;

/**
 * @author  Elmar Baumann
 */
public final class UserDefinedFileTypeInsertedEvent {

    private final Object source;
    private final UserDefinedFileType fileType;

    public UserDefinedFileTypeInsertedEvent(Object source, UserDefinedFileType fileType) {
        if (fileType == null) {
            throw new NullPointerException("fileType == null");
        }

        this.source = source;
        this.fileType = fileType;
    }

    public UserDefinedFileType getFileType() {
        return fileType;
    }

    public Object getSource() {
        return source;
    }
}
