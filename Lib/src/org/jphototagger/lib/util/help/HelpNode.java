/*
 * @(#)HelpNode.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.util.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Node in the applications help file tree structure. A node is a chapter
 * with help pages and subchapters.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class HelpNode {
    private String       title;
    private List<Object> children = new ArrayList<Object>();
    private HelpNode     parent;

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
        if (title == null) {
            throw new NullPointerException("title == null");
        }

        this.title = title;
    }

    /**
     * Adds a help page.
     *
     * @param page help page
     */
    public void addPage(HelpPage page) {
        if (page == null) {
            throw new NullPointerException("page == null");
        }

        page.setParent(this);
        children.add(page);
    }

    /**
     * Adds a chapter.
     *
     * @param chapter chapter
     */
    public void addNode(HelpNode chapter) {
        if (chapter == null) {
            throw new NullPointerException("chapter == null");
        }

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
     * @param  index the child's index {@code >= 0}
     *
     * @return child: an object of the type
     *         {@link HelpNode} or {@link HelpPage}
     *         or null if the index is invalid
     * @throws IndexOutOfBoundsException if {@code index < 0}
     */
    public Object getChild(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index < 0: " + index);
        }

        return children.get(index);
    }

    /**
     * Returns the index of a specific child.
     *
     * @param  child  child
     * @return index or -1 if the child does not exist
     */
    public int getIndexOfChild(Object child) {
        if (child == null) {
            throw new NullPointerException("child == null");
        }

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
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        List<Object> found = new ArrayList<Object>();

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
