package org.jphototagger.domain.repository.event.fileexcludepattern;

/**
 * @author Elmar Baumann
 */
public final class FileExcludePatternDeletedEvent {

    private final Object source;
    private final String pattern;

    public FileExcludePatternDeletedEvent(Object source, String pattern) {
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
