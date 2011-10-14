package org.jphototagger.api.lifecycle;

/**
 * @author Elmar Baumann
 */
public final class AppWillInitEvent {

    private final Object source;

    public AppWillInitEvent(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
