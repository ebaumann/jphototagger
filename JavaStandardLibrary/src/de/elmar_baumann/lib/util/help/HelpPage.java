package de.elmar_baumann.lib.util.help;

/**
 * A help page of an application's help.
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
        this.parent = parent;
    }
    
}
