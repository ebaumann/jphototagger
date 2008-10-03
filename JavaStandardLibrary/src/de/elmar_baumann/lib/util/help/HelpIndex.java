package de.elmar_baumann.lib.util.help;

import java.util.Vector;

/**
 * Contains all URIs of the application's help files as a tree structure.
 * Nodes are the titles of a chapter and children are page URIs with
 * a title. chapters can be nested .
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpIndex {

    private Vector<Object> children = new Vector<Object>();

    /**
     * Adds a chapter.
     * 
     * @param node chapter
     */
    public void addNode(HelpNode node) {
        children.add(node);
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
     * Returns the number of children: other chapters subordinated to a chapter
     * or help pages of a chapter.
     * 
     * @return number of children
     */
    public int getChidCount() {
        return children.size();
    }

    /**
     * Returns a specific child.
     * 
     * @param  index the child's index
     * @return child or null if the index is invalid
     */
    public Object getChildAt(int index) {
        return children.get(index);
    }

    /**
     * Returns the index of a specific child.
     * 
     * @param  child child
     * @return Index, is -1 if the child does not exist.
     */
    public int getIndexOfChild(Object child) {
        return children.indexOf(child);
    }
}
