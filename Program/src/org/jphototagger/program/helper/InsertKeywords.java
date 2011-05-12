package org.jphototagger.program.helper;

import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.GUI;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Inserts a list of Strings into the keywords root.
 *
 * @author Elmar Baumann
 */
public final class InsertKeywords extends Thread {
    private final List<String> keywords;

    public InsertKeywords(List<String> keywords) {
        super("JPhotoTagger: Inserting string list into keywords");

        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        this.keywords = new ArrayList<String>(keywords);
    }

    @Override
    public void run() {
        copy();
    }

    private void copy() {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
                Object root = model.getRoot();

                if (root instanceof DefaultMutableTreeNode) {
                    insertKeywords((DefaultMutableTreeNode) root, model);
                }
            }
        });
    }

    private void insertKeywords(DefaultMutableTreeNode rootHk, TreeModelKeywords modelHk) {
        DatabaseKeywords db = DatabaseKeywords.INSTANCE;
        boolean inserted = false;

        for (String keyword : keywords) {
            if (!db.existsRootKeyword(keyword)) {
                modelHk.insert(rootHk, keyword, true, true);
                inserted = true;
            }
        }

        if (inserted) {
            expandRoot();
        }
    }

    private void expandRoot() {
        JTree tree = GUI.getAppPanel().getTreeSelKeywords();
        Object root = tree.getModel().getRoot();

        tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
    }
}
