package org.jphototagger.program.importer;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.MessageLabel.MessageType;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ProgressBar;

/**
 *
 * @author Elmar Baumann
 */
public abstract class KeywordsImporter {

    private static final String PROGRESSBAR_STRING = Bundle.getString(KeywordsImporter.class, "KeywordImporter.ProgressBar.String");

    /**
     * Returns all keyword paths to the leaf nodes.
     * <p>
     * Every path is a list. The first string in a path - the first list element
     * - is the root keyword (no parent) and the following keywords - string
     * elements in the list - are children where a string following a string is
     * a child of the previous string.
     * <p>
     * E.g. the tree
     *
     * {@code
     * Landscape
     *     Tree
     *         Beech
     *         Birch
     * Building
     * }
     * <p>
     * has these lists, elements delimited by a comma:
     * {@code
     * Landscape, Tree
     * Landscape, Tree, Beech
     * Landscape, Tree, Birch
     * Building
     * }
     *
     * @param  file file with keywords to import
     * @return      keyword paths, null on errors.
     */
    public abstract Collection<List<KeywordString>> getPaths(File file);

    protected void importKeywordsFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        Collection<List<KeywordString>> paths = getPaths(file);

        if (paths != null) {
            new ImportTask(paths).start();
        }
    }

    private static class ImportTask extends Thread implements Cancelable {

        private final Collection<List<KeywordString>> paths;
        private final TreeModel treeModel = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
        private JProgressBar progressBar;
        private volatile boolean cancel;
        private final Object pBarOwner = this;

        ImportTask(Collection<List<KeywordString>> paths) {
            super("JPhotoTagger: Importing keywords");

            if (paths == null) {
                throw new NullPointerException("paths == null");
            }

            this.paths = paths;
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        private void getProgressBar() {
            if (progressBar != null) {
                return;
            }

            progressBar = ProgressBar.INSTANCE.getResource(pBarOwner);
        }

        @Override
        public void run() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    importKeywords();
                }
            });
        }

        private void importKeywords() {
            if (treeModel instanceof TreeModelKeywords) {
                TreeModelKeywords model = (TreeModelKeywords) treeModel;

                updateProgressBar(0);

                int progressValue = 0;
                int importCount = 0;

                for (List<KeywordString> path : paths) {
                    if (cancel || isInterrupted()) {
                        break;
                    }

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();

                    for (KeywordString keyword : path) {
                        DefaultMutableTreeNode existingNode = model.findChildByName(node, keyword.getKeyword());

                        if (existingNode == null) {
                            model.insert(node, keyword.getKeyword(), keyword.isReal(), false);
                            node = model.findChildByName(node, keyword.getKeyword());
                            importCount++;
                        } else {
                            node = existingNode;
                        }
                    }

                    updateProgressBar(++progressValue);
                }

                releaseProgressBar();
                messageImported(importCount);
                expandRootSelHk();
            }
        }

        private void expandRootSelHk() {
            JTree tree = GUI.getAppPanel().getTreeSelKeywords();
            Object root = tree.getModel().getRoot();

            tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
        }

        private void updateProgressBar(final int value) {
            getProgressBar();
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    if (progressBar != null) {
                        progressBar.setMinimum(0);
                        progressBar.setMaximum(paths.size());
                        progressBar.setValue(value);

                        if (!progressBar.isStringPainted()) {
                            progressBar.setStringPainted(true);
                        }

                        if (!PROGRESSBAR_STRING.equals(progressBar.getString())) {
                            progressBar.setString(PROGRESSBAR_STRING);
                        }
                    }
                }
            });
        }

        private void releaseProgressBar() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    if (progressBar != null) {
                        if (progressBar.isStringPainted()) {
                            progressBar.setString("");
                        }

                        progressBar.setValue(0);
                    }

                    ProgressBar.INSTANCE.releaseResource(pBarOwner);
                    progressBar = null;
                }
            });
        }

        private void messageImported(int importCount) {
            String message = Bundle.getString(ImportTask.class, "ImportTask.Info.Imported", importCount);

            GUI.getAppPanel().setStatusbarText(message, MessageType.INFO, 2000);
        }
    }
}
