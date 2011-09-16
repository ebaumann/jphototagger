package org.jphototagger.program.controller.keywords.tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 * Expands the path of all {@code KeywordsPanel}s trees.
 *
 * @author Elmar Baumann
 */
public final class KeywordsTreePathExpander {

    public static void expand(JTree tree, DefaultMutableTreeNode node) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (node == null) {
            throw new NullPointerException("node == null");
        }

        TreePath path = new TreePath(node.getPath());

        tree.expandPath(path);
    }

    private KeywordsTreePathExpander() {
    }
}
