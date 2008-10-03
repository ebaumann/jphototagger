package de.elmar_baumann.lib.util.help;

import java.util.Vector;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpSection {

    private String uri;
    private String title;
    private Vector<Object> children = new Vector<Object>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void addPage(HelpPage page) {
        children.add(page);
    }

    public void addSection(HelpSection section) {
        children.add(section);
    }

    public int getChildCount() {
        return children.size();
    }

    public Object getChild(int index) {
        return children.get(index);
    }

    public int getIndexOfChild(Object child) {
        return children.indexOf(child);
    }
}
