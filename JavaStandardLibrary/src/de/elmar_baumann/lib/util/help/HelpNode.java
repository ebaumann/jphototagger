package de.elmar_baumann.lib.util.help;

import java.util.Vector;

/**
 * Node in the applications help file tree structure. A node is a chapter
 * with help pages and subchapters.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpNode {

    private String title;
    private Vector<Object> children = new Vector<Object>();

    /**
     * Rerturns the chapter's title.
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the chapter's title.
     * 
     * @param title tile
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Adds a help page.
     * 
     * @param page help page
     */
    public void addPage(HelpPage page) {
        children.add(page);
    }

    /**
     * Adds a chapter.
     * 
     * @param chapter chapter
     */
    public void addNode(HelpNode chapter) {
        children.add(chapter);
    }

    /**
     * Returns the number of children: help pages and chapters.
     * 
     * @return children count
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns a specific child.
     * @param  index the child's index
     * 
     * @return child: an object of the type
     *         {@link HelpNode} or {@link HelpPage}
     *         or null if the index is invalid
     */
    public Object getChild(int index) {
        return children.get(index);
    }

    /**
     * Returns the index of a specific child.
     * 
     * @param  child  child
     * @return index or -1 if the child does not exist
     */
    public int getIndexOfChild(Object child) {
        return children.indexOf(child);
    }
}
