package org.jphototagger.lib.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.jphototagger.lib.util.ObjectUtil;

/**
 * A node is a chapter with help pages and subchapters.
 *
 * @author Elmar Baumann
 */
public final class HelpNode {

    private String title;
    private final List<Object> children = new ArrayList<>();
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

    public void addPage(HelpPage page) {
        if (page == null) {
            throw new NullPointerException("page == null");
        }

        page.setParent(this);
        children.add(page);
    }

    public void addNode(HelpNode chapter) {
        if (chapter == null) {
            throw new NullPointerException("chapter == null");
        }
        HelpNode equalChapter = findEqualChildNode(chapter);
        if (equalChapter == null) {
            chapter.parent = this;
            children.add(chapter);
        } else {
            equalChapter.children.addAll(chapter.children);
        }
    }

    private HelpNode findEqualChildNode(HelpNode helpNode) {
        for (Object child : children) {
            if (child instanceof HelpNode) {
                HelpNode childHelpNode = (HelpNode) child;
                if (childHelpNode.equals(helpNode)) {
                    return childHelpNode;
                }
            }
        }
        return null;
    }

    public int getChildCount() {
        return children.size();
    }

    public Object getChild(int index) {
        int count = children.size();

        if ((index < 0) || (index >= count)) {
            throw new IndexOutOfBoundsException("invalid index" + index + ", size: " + count);
        }

        return children.get(index);
    }

    /**
     * @param  child
     * @return index or -1 if the child does not exist
     */
    public int getIndexOfChild(Object child) {
        if (child == null) {
            throw new NullPointerException("child == null");
        }

        return children.indexOf(child);
    }

    /**
     * @return parent or null if the node is the root node
     */
    public HelpNode getParent() {
        return parent;
    }

    /**
     * @param  url
     * @return path or null if a page with the URL doesn't exist
     */
    public Object[] getPagePath(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        List<Object> found = new ArrayList<>();

        findPath(url, found);

        return (found.size() > 0)
                ? found.toArray()
                : null;
    }

    private void findPath(String url, List<Object> found) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        if (found == null) {
            throw new NullPointerException("found == null");
        }

        int size = children.size();

        for (int i = 0; (found.size() <= 0) && (i < size); i++) {
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
        if (helpPage == null) {
            throw new NullPointerException("helpPage == null");
        }

        Stack<Object> path = new Stack<>();

        path.push(helpPage);

        HelpNode p = helpPage.getParent();

        while (p != null) {
            path.push(p);
            p = p.parent;
        }

        Collections.reverse(path);

        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HelpNode)) {
            return false;
        }
        HelpNode other = (HelpNode) obj;
        return ObjectUtil.equals(this.title, other.title);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }
}
