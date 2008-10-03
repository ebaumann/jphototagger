package de.elmar_baumann.lib.util.help;

import java.util.Vector;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
 */
public class HelpIndex {

    private Vector<Object> children = new Vector<Object>();

    public HelpIndex() {
    }

    public void addNode(HelpNode node) {
        children.add(node);
    }
    
    public void addPage(HelpPage page) {
        children.add(page);
    }

    public int getChidCount() {
        return children.size();
    }

    public Object getChildAt(int index) {
        return children.get(index);
    }

    public int getIndexOfChild(Object child) {
        return children.indexOf(child);
    }
}
