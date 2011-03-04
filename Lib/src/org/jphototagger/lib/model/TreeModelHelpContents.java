package org.jphototagger.lib.model;

import org.jphototagger.lib.util.help.HelpIndexParser;
import org.jphototagger.lib.util.help.HelpNode;
import org.jphototagger.lib.util.help.HelpPage;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Contains the help index of an application's help, the root node of an
 * {@link org.jphototagger.lib.util.help.HelpNode} object.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class TreeModelHelpContents implements TreeModel {
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
    public TreeModelHelpContents(String url) {
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
