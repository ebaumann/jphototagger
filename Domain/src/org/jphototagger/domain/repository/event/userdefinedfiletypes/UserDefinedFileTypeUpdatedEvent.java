package org.jphototagger.domain.repository.event.userdefinedfiletypes;

import org.jphototagger.domain.filetypes.UserDefinedFileType;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class UserDefinedFileTypeUpdatedEvent {

    private final Object source;
    private final UserDefinedFileType oldFileType;
    private final UserDefinedFileType newFileType;

    public UserDefinedFileTypeUpdatedEvent(Object source, UserDefinedFileType oldFileType, UserDefinedFileType newFileType) {
        if (oldFileType == null) {
            throw new NullPointerException("oldFileType == null");
        }
        
        if (newFileType == null) {
            throw new NullPointerException("newFileType == null");
        }
        
        this.source = source;
        this.oldFileType = oldFileType;
        this.newFileType = newFileType;
    }

    public UserDefinedFileType getNewFileType() {
        return newFileType;
    }

    public UserDefinedFileType getOldFileType() {
        return oldFileType;
    }

    public Object getSource() {
        return source;
    }
}
