package org.jphototagger.lib.help;

import org.jphototagger.lib.util.ObjectUtil;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HelpPage)) {
            return false;
        }
        HelpPage other = (HelpPage) obj;
        return ObjectUtil.equals(url, other.url);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return title;
    }
}
