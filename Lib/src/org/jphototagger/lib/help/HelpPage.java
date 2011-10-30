package org.jphototagger.lib.help;

/**
 * @author Elmar Baumann
 */
public final class HelpPage {

    private String url;
    private String title;
    private HelpNode parent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title == null");
        }

        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        this.url = url;
    }

    public HelpNode getParent() {
        return parent;
    }

    void setParent(HelpNode parent) {
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }

        this.parent = parent;
    }
}
