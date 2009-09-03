package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.GUI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Inserts a list of Strings into the hierarchical keywords root.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-03
 */
public final class InsertHierarchicalKeywords extends Thread {

    private final List<String> keywords;

    public InsertHierarchicalKeywords(List<String> keywords) {
        this.keywords = new ArrayList<String>(keywords);
        setName("Inserting string list into hierarchical keywords @ " + // NOI18N
                getClass().getName());
    }

    @Override
    public void run() {
        copy();
    }

    private void copy() {
        TreeModel tm = GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().
                getModel();
        assert tm instanceof TreeModelHierarchicalKeywords :
                "Invalid model: " + tm; // NOI18N
        if (tm instanceof TreeModelHierarchicalKeywords) {
            TreeModelHierarchicalKeywords model =
                    (TreeModelHierarchicalKeywords) tm;
            Object root = model.getRoot();
            assert root instanceof DefaultMutableTreeNode;
            if (root instanceof DefaultMutableTreeNode) {
                insertKeywords((DefaultMutableTreeNode) root, model);
            }
        }
    }

    private void insertKeywords(
            DefaultMutableTreeNode rootHk,
            TreeModelHierarchicalKeywords modelHk) {

        DatabaseHierarchicalKeywords db = DatabaseHierarchicalKeywords.INSTANCE;
        boolean inserted = false;

        for (String keyword : keywords) {
            if (!db.existsRootKeyword(keyword)) {
                modelHk.addKeyword(rootHk, keyword);
                inserted = true;
            }
        }

        if (inserted) {
            expandRoot();
        }
    }

    private void expandRoot() {
        JTree tree = GUI.INSTANCE.getAppPanel().getTreeSelHierarchicalKeywords();
        Object root = tree.getModel().getRoot();
        tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
    }
}
