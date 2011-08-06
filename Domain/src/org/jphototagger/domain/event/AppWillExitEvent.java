package org.jphototagger.domain.event;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AppWillExitEvent {

    private final Object source;

    public AppWillExitEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
