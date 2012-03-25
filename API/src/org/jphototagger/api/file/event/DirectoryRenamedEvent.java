package org.jphototagger.api.file.event;

import java.io.File;

import org.jphototagger.api.event.PropertyEvent;

/**
 * @author Elmar Baumann
 */
public final class DirectoryRenamedEvent extends PropertyEvent {

    private final File oldName;
    private final File newName;

    public DirectoryRenamedEvent(Object source, File oldName, File newName) {
        super(source);
        if (oldName == null) {
            throw new NullPointerException("oldName == null");
        }
        if (newName == null) {
            throw new NullPointerException("newName == null");
        }
        this.oldName = oldName;
        this.newName = newName;
    }

    public File getNewName() {
        return newName;
    }

    public File getOldName() {
        return oldName;
    }
}
