package org.jphototagger.lib.help;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Elmar Baumann
 */
public final class HelpContentsTreeModel implements TreeModel {

    private HelpNode root = new HelpNode();

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((HelpNode) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((HelpNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof HelpPage;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((HelpNode) parent).getIndexOfChild(child);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // ignore
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // ignore
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // ignore
    }

    /**
     * Constructor.
     *
     * @param url  URL of the XML file for the class
     */
    public HelpContentsTreeModel(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        parse(url);
    }

    private void parse(String url) {
        HelpNode rootNode = HelpIndexParser.parse(this.getClass().getResourceAsStream(url));

        if (rootNode != null) {
            root = rootNode;
        }
    }
}
