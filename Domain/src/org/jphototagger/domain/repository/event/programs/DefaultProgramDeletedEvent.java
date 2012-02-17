package org.jphototagger.domain.repository.event.programs;

/**
 * @author Elmar Baumann
 */
public final class DefaultProgramDeletedEvent {

    private final Object source;
    private final String filenameSuffix;

    public DefaultProgramDeletedEvent(Object source, String filenameSuffix) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (filenameSuffix == null) {
            throw new NullPointerException("filenameSuffix == null");
        }
        this.source = source;
        this.filenameSuffix = filenameSuffix;
    }

    public Object getSource() {
        return source;
    }

    public String getFilenameSuffix() {
        return filenameSuffix;
    }
}
