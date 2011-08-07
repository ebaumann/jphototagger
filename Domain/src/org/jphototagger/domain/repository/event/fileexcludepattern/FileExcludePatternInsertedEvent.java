package org.jphototagger.domain.repository.event.fileexcludepattern;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileExcludePatternInsertedEvent {

    private final Object source;
    private final String pattern;

    public FileExcludePatternInsertedEvent(Object source, String pattern) {
        this.source = source;
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public Object getSource() {
        return source;
    }
}
