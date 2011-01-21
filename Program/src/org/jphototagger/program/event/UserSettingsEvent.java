package org.jphototagger.program.event;

/**
 * Action: The user has changed the settings.
 *
 * @author Elmar Baumann
 */
public final class UserSettingsEvent {
    private Object source;
    private Type   type;

    public enum Type {
        DISPLAY_IPTC, LOG_LEVEL, MAX_THUMBNAIL_WIDTH, CHECK_FOR_UPDATES
    }

    public UserSettingsEvent(Type type, Object source) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        if (source == null) {
            throw new NullPointerException("source == null");
        }

        this.type   = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }
}
