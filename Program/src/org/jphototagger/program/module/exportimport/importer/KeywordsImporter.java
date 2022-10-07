package org.jphototagger.program.module.exportimport.importer;

import java.awt.EventQueue;
import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.keywords.tree.KeywordsTreeModel;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public abstract class KeywordsImporter {

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
            ImporterImpl importer = new ImporterImpl(paths);
            importer.importKeywords();
        }
    }

    private static class ImporterImpl implements Cancelable {

        private final Collection<List<KeywordString>> paths;
        private final TreeModel treeModel = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);
        private volatile boolean cancel;
        private final Object source = this;
        private static final String PROGRESSBAR_STRING = Bundle.getString(KeywordsImporter.class, "KeywordImporter.ProgressBar.String");
        private ProgressHandle progressHandle;

        private ImporterImpl(Collection<List<KeywordString>> paths) {
            this.paths = paths;
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        private void importKeywords() {
            if (treeModel instanceof KeywordsTreeModel) {
                boolean autocompletePersisted = isAutocompletePersisted();
                boolean autoComplete = getPersistedAutocomplete();
                persistAutoComplete(false); // If auto complete is enabled, it takes a huge amount of time to insert sorted keywords
                KeywordsTreeModel modelForRepoInsert = new KeywordsTreeModel(); // Copy avoids redrawing the UI (speeds up importing)
                modelForRepoInsert.setSortEnabled(false);
                modelForRepoInsert.setInsertDcSubjects(false);
                progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
                progressHandle.progressStarted(createProgressEventWithValue(0));
                int progressValue = 0;
                int importCount = 0;
                for (List<KeywordString> path : paths) {
                    if (cancel) {
                        break;
                    }
                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) modelForRepoInsert.getRoot();
                    DefaultMutableTreeNode parentNode = rootNode;
                    for (KeywordString keyword : path) {
                        String keywordString = keyword.getKeyword();
                        DefaultMutableTreeNode childNode = modelForRepoInsert.findChildByName(parentNode, keywordString);
                        if (childNode == null) {
                            boolean isRootNode = importCount == 0 && !StringUtil.hasContent(keywordString);
                            if (!isRootNode) {
                                modelForRepoInsert.insert(parentNode, keywordString, keyword.isReal(), false);
                                importCount++;
                            }
                            parentNode = modelForRepoInsert.findChildByName(parentNode, keywordString);
                        } else {
                            parentNode = childNode;
                        }
                    }
                    progressValue++;
                    progressHandle.progressPerformed(createProgressEventWithValue(progressValue));
                }
                if (importCount > 0) {
                    recreateTreeInEdt();
                }
                if (autocompletePersisted) {
                    persistAutoComplete(autoComplete);
                } else {
                    removePersistedAutoComplete();
                }
                progressHandle.progressEnded();
                messageImported(importCount);
                expandRootSelHk();
            }
        }

        private void recreateTreeInEdt() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ((KeywordsTreeModel) treeModel).recreate();
                }
            });
        }

        private void expandRootSelHk() {
            JTree tree = GUI.getAppPanel().getTreeSelKeywords();
            Object root = tree.getModel().getRoot();
            tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
        }

        private ProgressEvent createProgressEventWithValue(final int value) {
            return new ProgressEvent.Builder().source(source).minimum(0).maximum(paths.size()).value(value).stringPainted(true).stringToPaint(PROGRESSBAR_STRING).build();
        }

        private void messageImported(int importCount) {
            String message = Bundle.getString(ImporterImpl.class, "ImportTask.Info.Imported", importCount);
            MainWindowManager messageDisplayer = Lookup.getDefault().lookup(MainWindowManager.class);
            messageDisplayer.setMainWindowStatusbarText(message, MessageType.INFO, 2000);
        }

        private boolean isAutocompletePersisted() {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE);
        }

        private boolean getPersistedAutocomplete() {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                    ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                    : false;
        }

        private void persistAutoComplete(boolean persist) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE, persist);
        }

        private void removePersistedAutoComplete() {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.removeKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE);
        }
    }
}
