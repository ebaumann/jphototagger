package de.elmar_baumann.lib.util.help;

/**
 * A help page of an application's help.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public final class HelpPage {

    private String url;
    private String title;
    private HelpNode parent;

    /**
     * Returns the title of the help page.
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the help page.
     * 
     * @param title  title
     */
    public void setTitle(String title) {
        if (title == null)
            throw new NullPointerException("title == null");

        this.title = title;
    }

    /**
     * Returns the URL of the help page.
     * 
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the help page.
     * 
     * @param url  URL
     */
    public void setUrl(String url) {
        if (url == null)
            throw new NullPointerException("url == null");

        this.url = url;
    }

    /**
     * Returns the parent node.
     * 
     * @return parent node
     */
    public HelpNode getParent() {
        return parent;
    }

    /**
     * Sets the parent node.
     * 
     * @param parent  parent
     */
    void setParent(HelpNode parent) {
        if (parent == null)
            throw new NullPointerException("parent == null");

        this.parent = parent;
    }
}
