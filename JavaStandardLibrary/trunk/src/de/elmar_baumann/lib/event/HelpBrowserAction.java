package de.elmar_baumann.lib.event;

import java.net.URL;

/**
 * Action of an
 *  {@link de.elmar_baumann.lib.dialog.HelpBrowser} instance.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/04
 */
public final class HelpBrowserAction {

    private final Object source;
    private final Type type;
    private URL url;

    public HelpBrowserAction(Object source, Type type) {
        this.source = source;
        this.type = type;
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
     * Sets the URL  if the action was {@link Type#URL_CHANGED}.
     * 
     * @param url  URL
     */
    public void setUrl(URL url) {
        this.url = url;
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
