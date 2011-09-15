package org.jphototagger.program.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jphototagger.lib.util.Bundle;

/**
 * Contains exactly one tree node below the root with a "wait" text and is a
 * substitute as * long as a large tree model will be created.
 *
 * @author Elmar Baumann
 */
public final class WaitTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = -6456827464935791978L;
    private static final String ITEM_TEXT = Bundle.getString(WaitTreeModel.class, "WaitTreeModel.ItemText");
    public static final WaitTreeModel INSTANCE = new WaitTreeModel();

    public WaitTreeModel() {
        super(new DefaultMutableTreeNode(ITEM_TEXT));
        ((DefaultMutableTreeNode) getRoot()).add(new DefaultMutableTreeNode(ITEM_TEXT));
    }
}
