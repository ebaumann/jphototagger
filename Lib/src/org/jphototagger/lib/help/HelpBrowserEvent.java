package org.jphototagger.lib.help;

import java.net.URL;

/**
 * @author Elmar Baumann
 */
public final class HelpBrowserEvent {

    private final Object source;
    private final Type type;
    private final URL url;

    public HelpBrowserEvent(Object source, Type type, URL url) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (type == null) {
            throw new NullPointerException("type == null");
        }

        if (url == null) {
            throw new NullPointerException("url == null");
        }

        this.source = source;
        this.type = type;
        this.url = url;
    }

    public enum Type {

        /**
         * The URL displayed changed
         */
        URL_CHANGED
    }

    /**
     * Returns the action type.
     *
     * @return action type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the url if the action was {@code Type#URL_CHANGED}.
     *
     * @return URL or null if unaprobriate action
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the source of the action, usually an instance of
     * {@code org.jphototagger.lib.dialog.HelpBrowser}
     *
     * @return instance
     */
    public Object getSource() {
        return source;
    }
}
