package org.jphototagger.program.module.keywords.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;

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

        this.keywords = new ArrayList<String>(keywords);
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

//    private void copyKeywordsToKeywordsTree() {
//        List<String> keywords = ListUtil.toStringList(ModelFactory.INSTANCE.getModel(KeywordsListModel.class));
//
//        if (keywords.size() > 0) {
//            new InsertKeywordsThread(keywords).run();    // Has to run in this thread!
//            String message = Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.Info.CopyKeywordsToTree");
//            MessageDisplayer.information(this, message);
//        }
//    }
//
//    private void deleteAllKeywordsFromKeywordsTree() {
//        String message = Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.Confirm.DeleteAllKeywordsFromKeywordsTree");
//
//        if (MessageDisplayer.confirmYesNo(this, message)) {
//            KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);
//
//            int count = repo.deleteAllKeywords();
//
//            if (count > 0) {
//                 Collection<KeywordsTreeModel> models =
//                       ModelFactory.INSTANCE.getModels(KeywordsTreeModel.class);
//
//                 if (models != null) {
//                     for (final KeywordsTreeModel model : models) {
//                        EventQueueUtil.invokeInDispatchThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                model.removeAllKeywords();
//                            }
//                        });
//                     }
//                 }
//
//                 message = Bundle.getString(RepositoryUpdatePanel.class, "RepositoryUpdatePanel.Info.DeletedKeywords", count);
//                 MessageDisplayer.information(this, message);
//             }
//
//        }
//    }
}
