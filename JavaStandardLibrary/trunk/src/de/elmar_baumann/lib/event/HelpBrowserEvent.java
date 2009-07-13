package de.elmar_baumann.lib.event;

import java.net.URL;

/**
 * Action of an {@link de.elmar_baumann.lib.dialog.HelpBrowser} instance.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/04
 */
public final class HelpBrowserEvent {

    private final Object source;
    private final Type type;
    private final URL url;

    public HelpBrowserEvent(Object source, Type type, URL url) {
        if (source == null)
            throw new NullPointerException("source == null"); // NOI18N
        if (type == null)
            throw new NullPointerException("type == null"); // NOI18N
        if (url == null)
            throw new NullPointerException("url == null"); // NOI18N

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
     * Returns the url if the action was {@link Type#URL_CHANGED}.
     * 
     * @return URL or null if unaprobriate action
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the source of the action, usually an instance of
     * {@link de.elmar_baumann.lib.dialog.HelpBrowser}
     * 
     * @return instance
     */
    public Object getSource() {
        return source;
    }
}
