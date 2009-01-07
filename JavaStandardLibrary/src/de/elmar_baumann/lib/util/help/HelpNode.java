package de.elmar_baumann.lib.util.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.List;

/**
 * Node in the applications help file tree structure. A node is a chapter
 * with help pages and subchapters.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class HelpNode {

    private String title;
    private List<Object> children = new ArrayList<Object>();
    private HelpNode parent;

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
        page.setParent(this);
        children.add(page);
    }

    /**
     * Adds a chapter.
     * 
     * @param chapter chapter
     */
    public void addNode(HelpNode chapter) {
        chapter.parent = this;
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

    /**
     * Returns the parent node.
     * 
     * @return parent or null if the node is the root node
     */
    public HelpNode getParent() {
        return parent;
    }

    /**
     * Returns the path of a help page with an specific URL.
     * 
     * @param  url URL
     * @return path or null if a page with the URL doesn't exist
     */
    public Object[] getPagePath(String url) {
        List<Object> found = new ArrayList<Object>();
        findPath(url, found);
        return found.size() > 0 ? found.toArray() : null;
    }

    private void findPath(String url, List<Object> found) {
        int size = children.size();
        for (int i = 0; found.size() <= 0 && i < size; i++) {
            Object child = children.get(i);
            if (child instanceof HelpPage) {
                HelpPage helpPage = (HelpPage) child;
                if (helpPage.getUrl().equals(url)) {
                    found.addAll(getPagePath(helpPage));
                }
            } else if (child instanceof HelpNode) {
                ((HelpNode) child).findPath(url, found);
            }
        }
    }

    private Stack<Object> getPagePath(HelpPage helpPage) {
        Stack<Object> path = new Stack<Object>();
        path.push(helpPage);
        HelpNode p = helpPage.getParent();
        while (p != null) {
            path.push(p);
            p = p.parent;
        }
        Collections.reverse(path);
        return path;
    }
}
