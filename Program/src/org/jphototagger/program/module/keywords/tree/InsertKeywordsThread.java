package org.jphototagger.program.module.keywords.tree;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Inserts a list of Strings into the keywords root.
 *
 * @author Elmar Baumann
 */
public final class InsertKeywordsThread extends Thread {

    private final List<String> keywords;
    private final KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);

    public InsertKeywordsThread(List<String> keywords) {
        super("JPhotoTagger: Inserting string list into keywords");

        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        this.keywords = new ArrayList<>(keywords);
    }

    @Override
    public void run() {
        copy();
    }

    private void copy() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                KeywordsTreeModel model = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);
                Object root = model.getRoot();

                if (root instanceof DefaultMutableTreeNode) {
                    insertKeywords((DefaultMutableTreeNode) root, model);
                }
            }
        });
    }

    private void insertKeywords(DefaultMutableTreeNode rootHk, KeywordsTreeModel modelHk) {
        boolean inserted = false;

        for (String keyword : keywords) {
            if (!repo.existsRootKeyword(keyword)) {
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
