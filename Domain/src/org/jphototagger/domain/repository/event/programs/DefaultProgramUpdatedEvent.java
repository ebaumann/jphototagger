package org.jphototagger.domain.repository.event.programs;

/**
 * @author Elmar Baumann
 */
public final class DefaultProgramUpdatedEvent {

    private final Object source;
    private final String filenameSuffix;
    private final long idProgram;

    public DefaultProgramUpdatedEvent(Object source, String filenameSuffix, long idProgram) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (filenameSuffix == null) {
            throw new NullPointerException("filenameSuffix == null");
        }
        this.source = source;
        this.idProgram = idProgram;
        this.filenameSuffix = filenameSuffix;
    }

    public Object getSource() {
        return source;
    }

    public String getFilenameSuffix() {
        return filenameSuffix;
    }

    public long getIdProgram() {
        return idProgram;
    }
}
