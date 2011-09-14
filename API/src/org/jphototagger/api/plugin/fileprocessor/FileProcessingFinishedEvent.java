package org.jphototagger.api.plugin.fileprocessor;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileProcessingFinishedEvent {

    private final Object source;
    private final boolean success;

    public FileProcessingFinishedEvent(Object source, boolean success) {
        this.source = source;
        this.success = success;
    }

    public Object getSource() {
        return source;
    }

    public boolean getSuccess() {
        return success;
    }
}
