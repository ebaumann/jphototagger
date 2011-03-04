package org.jphototagger.program.model;

import org.jphototagger.program.resource.JptBundle;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Contains exactly one tree node below the root with a "wait" text and is a
 * substitute as * long as a large tree model will be created.
 *
 * @author Elmar Baumann
 */
public final class TreeModelWait extends DefaultTreeModel {
    private static final long serialVersionUID = -6456827464935791978L;
    private static final String ITEM_TEXT = JptBundle.INSTANCE.getString("TreeModelWait.ItemText");
    public static final TreeModelWait INSTANCE = new TreeModelWait();

    public TreeModelWait() {
        super(new DefaultMutableTreeNode(ITEM_TEXT));
        ((DefaultMutableTreeNode) getRoot()).add(new DefaultMutableTreeNode(ITEM_TEXT));
    }
}
