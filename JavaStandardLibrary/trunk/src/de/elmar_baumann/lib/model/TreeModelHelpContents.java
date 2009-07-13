package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.util.help.HelpIndexParser;
import de.elmar_baumann.lib.util.help.HelpPage;
import de.elmar_baumann.lib.util.help.HelpNode;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Contains the help index of an application's help, the root node of an
 * {@link de.elmar_baumann.lib.util.help.HelpNode} object.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/02
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
        // Nothing to bei done
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        // Nothing to bei done
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        // Nothing to bei done
    }

    /**
     * Constructor.
     * 
     * @param url  URL of the XML file for the class
     */
    public TreeModelHelpContents(String url) {
        if (url == null)
            throw new NullPointerException("url == null"); // NOI18N

        parse(url);
    }

    private void parse(String url) {
        assert url != null : url;

        HelpNode rootNode = HelpIndexParser.parse(this.getClass().getResourceAsStream(url));
        if (rootNode != null) {
            root = rootNode;
        }
    }
}
