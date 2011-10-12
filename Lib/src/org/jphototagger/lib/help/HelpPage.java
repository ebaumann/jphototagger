package org.jphototagger.lib.help;

/**
 * A help page of an application's help.
 *
 * @author Elmar Baumann
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
        if (title == null) {
            throw new NullPointerException("title == null");
        }

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
        if (url == null) {
            throw new NullPointerException("url == null");
        }

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
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }

        this.parent = parent;
    }
}