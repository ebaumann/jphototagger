package org.jphototagger.domain.repository.event.wordsets;

import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public final class WordsetRenamedEvent {

    private final Object source;
    private final String oldName;
    private final String newName;

    public WordsetRenamedEvent(Object source, String oldName, String newName) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (!StringUtil.hasContent(oldName)) {
            throw new IllegalArgumentException("Old name must have content: " + oldName);
        }
        if (!StringUtil.hasContent(newName)) {
            throw new IllegalArgumentException("New name must have content: " + newName);
        }
        this.source = source;
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public String getOldName() {
        return oldName;
    }

    public Object getSource() {
        return source;
    }
}
