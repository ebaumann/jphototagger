/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.util.help;

/**
 * A help page of an application's help.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-02
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
