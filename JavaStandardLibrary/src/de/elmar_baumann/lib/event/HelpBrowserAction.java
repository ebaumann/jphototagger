package de.elmar_baumann.lib.event;

import java.net.URL;

/**
 * Action of an
 *  {@link de.elmar_baumann.lib.dialog.HelpBrowser} instance.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/04
 */
public class HelpBrowserAction {

    private Object source;
    private Type type;
    private URL url;

    public HelpBrowserAction(Object source, Type type) {
        this.source = source;
        this.type = type;
    }

    public enum Type {

        /**
         * The URL displayed changed
         */
        UrlChanged
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
     * Sets the action type.
     * 
     * @param type  action type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the url if the action was {@link Type#UrlChanged}.
     * 
     * @return URL or null if unaprobriate action
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets the URL  if the action was {@link Type#UrlChanged}.
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

    /**
     * Sets the source of the action.
     * 
     * @param source  Source
     */
    public void setSource(Object source) {
        this.source = source;
    }
}
