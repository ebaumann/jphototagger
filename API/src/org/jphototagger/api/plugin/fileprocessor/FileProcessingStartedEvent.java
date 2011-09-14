package org.jphototagger.api.plugin.fileprocessor;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileProcessingStartedEvent {

    private final Object source;

    public FileProcessingStartedEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
