package org.jphototagger.lib.help;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Elmar Baumann
 */
public final class HelpContentsTreeModel implements TreeModel {

    private HelpNode rootNode = new HelpNode();

    public HelpContentsTreeModel(HelpNode rootNode) {
        if (rootNode == null) {
            throw new NullPointerException("rootNode == null");
        }

        this.rootNode = rootNode;
    }

    @Override
    public Object getRoot() {
        return rootNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        HelpNode parentNode = (HelpNode) parent;
        return parentNode.getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        HelpNode parentNode = (HelpNode) parent;
        return parentNode.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof HelpPage;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        HelpNode parentNode = (HelpNode) parent;
        return parentNode.getIndexOfChild(child);
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
}
