package de.elmar_baumann.lib.util.help;

/**
 * A help page of an application's help.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpPage {
    
    private String uri;
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
     * Returns the URI of the help page.
     * 
     * @return URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the URI of the help page.
     * 
     * @param uri  URI
     */
    public void setUri(String uri) {
        this.uri = uri;
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
