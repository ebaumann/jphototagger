package org.jphototagger.lib.api;

/**
 * @author Elmar Baumann
 */
public final class LookAndFeelChangedEvent {

    private final Object source;
    private final Object provider;

    public LookAndFeelChangedEvent(Object source, Object provider) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (provider == null) {
            throw new NullPointerException("provider == null");
        }
        this.source = source;
        this.provider = provider;
    }

    public Object getSource() {
        return source;
    }

    public Object getProvider() {
        return provider;
    }
}
