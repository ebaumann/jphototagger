package org.jphototagger.program.factory;

import java.awt.Cursor;
import java.io.File;
import java.util.List;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.SortOrder;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.sort.ListSortController;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.imagecollections.ImageCollectionSortAscendingComparator;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.swing.AllSystemDirectoriesTreeModel;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.app.ui.AppWindowPersistence;
import org.jphototagger.program.misc.InputHelperDialog;
import org.jphototagger.program.module.favorites.FavoritesTreeModel;
import org.jphototagger.program.module.imagecollections.ImageCollectionsListModel;
import org.jphototagger.program.module.keywords.KeywordHighlightPredicate;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.jphototagger.program.module.keywords.list.KeywordsListModel;
import org.jphototagger.program.module.keywords.tree.KeywordsTreeModel;
import org.jphototagger.program.module.metadatatemplates.MetadataTemplatesListModel;
import org.jphototagger.program.module.miscmetadata.MiscMetadataTreeModel;
import org.jphototagger.program.module.search.SavedSearchesListModel;
import org.jphototagger.program.module.thumbnails.FileFiltersComboBoxModel;
import org.jphototagger.program.module.timeline.TimelineTreeModel;
import org.jphototagger.program.resource.GUI;

/**
 * Erzeugt die Models und verbindet sie mit den GUI-Elementen.
 *
 * @author Elmar Baumann
 */
public final class ModelFactory {

    public static final ModelFactory INSTANCE = new ModelFactory();
    private final Support support = new Support();
    private volatile boolean init;

    void init() {
        synchronized (this) {
            if (!Support.checkInit(getClass(), init)) {
                return;
            }

            init = true;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Init.Start");
                Support.setStatusbarInfo(message);
                setComboBoxModels(appPanel);
                setListModels(appPanel);
                setTreeModels(appPanel);
                message = Bundle.getString(ModelFactory.class, "ModelFactory.Init.Finished");
                Support.setStatusbarInfo(message);
            }
        });
    }

    private void setComboBoxModels(AppPanel appPanel) {
        setComboBoxModelFileFilters();
    }

    private void setComboBoxModelFileFilters() {
        FileFiltersComboBoxModel model = new FileFiltersComboBoxModel();

        support.add(model);
        GUI.getAppPanel().getComboBoxFileFilters().setModel(model);
    }

    private void setListModels(AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelKeywords(appPanel);
    }

    private void setListModelSavedSearches(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.ListModelSavedSearches");
                Support.setStatusbarInfo(message);

                final JXList list = appPanel.getListSavedSearches();
                final Cursor listCursor = setWaitCursor(list);
                final SavedSearchesListModel model = new SavedSearchesListModel();

                support.add(model);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        list.setModel(model);
                        list.setAutoCreateRowSorter(true);
                        list.setSortOrder(SortOrder.ASCENDING);
                        AppWindowPersistence.readListSavedSearches();
                        list.setCursor(listCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.ListModelSavedSearches");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Saved Searches List").start();
    }

    private void setListModelImageCollections(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.ListModelImageCollections");
                Support.setStatusbarInfo(message);

                final JXList list = appPanel.getListImageCollections();
                final Cursor listCursor = setWaitCursor(list);
                final ImageCollectionsListModel model = new ImageCollectionsListModel();

                support.add(model);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        list.setModel(model);
                        ListSortController<ImageCollectionsListModel> sorter = new ListSortController<ImageCollectionsListModel>(model);
                        sorter.setComparator(0, new ImageCollectionSortAscendingComparator());
                        list.setRowSorter(sorter);
                        list.setSortOrder(SortOrder.ASCENDING);
                        AppWindowPersistence.readListImageCollections();
                        list.setCursor(listCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.ListModelImageCollections");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Image Collections List").start();
    }

    private void setListModelKeywords(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.ListModelKeywords");
                Support.setStatusbarInfo(message);

                final JXList listSelectedKeywords = appPanel.getListSelKeywords();
                final KeywordsPanel panelEditKeywords = appPanel.getPanelEditKeywords();
                final Cursor listCursor = setWaitCursor(listSelectedKeywords);
                final KeywordsListModel modelKeywords = new KeywordsListModel();

                support.add(modelKeywords);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        listSelectedKeywords.setModel(modelKeywords);
                        listSelectedKeywords.setAutoCreateRowSorter(true);
                        listSelectedKeywords.setSortOrder(SortOrder.ASCENDING);
                        listSelectedKeywords.addHighlighter(KeywordHighlightPredicate.getHighlighter());
                        panelEditKeywords.setListModel(modelKeywords);
                        InputHelperDialog.INSTANCE.setModelKeywords(modelKeywords);
                        AppWindowPersistence.readListSelKeywords();
                        listSelectedKeywords.setCursor(listCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.ListModelKeywords");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Keywords list").start();
    }

    private void setTreeModels(AppPanel appPanel) {
        setTreeModelKeywords(appPanel);
        setTreeModelTimeline(appPanel);
        setTreeModelMiscMetadata(appPanel);
        setTreeModelFavorites(appPanel);
        setTreeModelDirectories(appPanel);
    }

    private void setTreeModelKeywords(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.TreeModelKeywords");
                Support.setStatusbarInfo(message);

                final TreeModel treeModelKeywords = new KeywordsTreeModel();
                final MetadataTemplatesListModel listModelTemplates = new MetadataTemplatesListModel();

                support.add(treeModelKeywords);
                support.add(listModelTemplates);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        JTree treeSelKeywords = appPanel.getTreeSelKeywords();

                        treeSelKeywords.setModel(treeModelKeywords);
                        AppWindowPersistence.readTreeSelKeywords();

                        JTree treeEditKeywords = appPanel.getTreeEditKeywords();

                        treeEditKeywords.setModel(treeModelKeywords);
                        AppWindowPersistence.readTreeEditKeywords();
                        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().setModel(treeModelKeywords);
                        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().setModel(listModelTemplates);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.TreeModelKeywords");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Keywords Trees").start();
    }

    private void setTreeModelMiscMetadata(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.TreeModelMiscMetadata");
                Support.setStatusbarInfo(message);

                final JTree tree = appPanel.getTreeMiscMetadata();
                final Cursor treeCursor = setWaitCursor(tree);
                final TreeModel modelApp = new MiscMetadataTreeModel(false);
                final TreeModel modelInputHelper = new MiscMetadataTreeModel(true);

                support.add(modelApp);
                support.add(modelInputHelper);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        InputHelperDialog.INSTANCE.getPanelMiscXmpMetadata().getTree().setModel(modelInputHelper);
                        tree.setModel(modelApp);
                        AppWindowPersistence.readTreeMiscMetadata();
                        tree.setCursor(treeCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.TreeModelMiscMetadata");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Misc Metadata Tree").start();
    }

    private void setTreeModelTimeline(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.TreeModelTimeline");
                Support.setStatusbarInfo(message);

                final JTree tree = appPanel.getTreeTimeline();
                final Cursor treeCursor = setWaitCursor(tree);
                final TreeModel model = new TimelineTreeModel();

                support.add(model);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        tree.setModel(model);
                        AppWindowPersistence.readTreeTimeline();
                        tree.setCursor(treeCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.TreeModelTimeline");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Timeline Tree").start();
    }

    private void setTreeModelFavorites(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.TreeModelFavorites");
                Support.setStatusbarInfo(message);

                final JTree tree = appPanel.getTreeFavorites();
                final Cursor treeCursor = setWaitCursor(tree);
                final FavoritesTreeModel model = new FavoritesTreeModel(tree);

                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        support.add(model);
                        tree.setModel(model);
                        model.readFromProperties();
                        tree.setCursor(treeCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.TreeModelFavorites");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Favorites Tree").start();
    }

    private void setTreeModelDirectories(final AppPanel appPanel) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String message = Bundle.getString(ModelFactory.class, "ModelFactory.Starting.TreeModelDirectories");
                Support.setStatusbarInfo(message);

                final JTree tree = appPanel.getTreeDirectories();
                final Cursor treeCursor = setWaitCursor(tree);
                List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
                final TreeModel model = new AllSystemDirectoriesTreeModel(tree, hideRootFiles, getDirFilterOptionShowHiddenFiles());

                support.add(model);
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        tree.setModel(model);
                        AppWindowPersistence.readTreeDirectories();
                        tree.setCursor(treeCursor);
                        String message = Bundle.getString(ModelFactory.class, "ModelFactory.Finished.TreeModelDirectories");
                        Support.setStatusbarInfo(message);
                    }
                });
            }
        }, "JPhotoTagger: Creating Directories Tree").start();
    }

    private DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    /**
     * Returns all instances of a specific model.
     *
     * @param  <T>        type of model class
     * @param  modelClass model class (key)
     * @return            model instances or null if no model of that class was
     *                    instanciated
     */
    public <T> List<T> getModels(Class<T> modelClass) {
        if (modelClass == null) {
            throw new NullPointerException("modelClass == null");
        }

        return support.getAll(modelClass);
    }

    /**
     * Returns the first added instance of a specific model.
     *
     * @param  <T>        type of model class
     * @param  modelClass model class (key)
     * @return            model instance or null if no model of that class was
     *                    instanciated
     */
    public <T> T getModel(Class<T> modelClass) {
        if (modelClass == null) {
            throw new NullPointerException("modelClass == null");
        }

        return support.getFirst(modelClass);
    }

    private Cursor setWaitCursor(JList list) {
        Cursor listCursor = list.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

        list.setCursor(waitCursor);

        return listCursor;
    }

    private synchronized Cursor setWaitCursor(JTree tree) {
        Cursor treeCursor = tree.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

        tree.setCursor(waitCursor);

        return treeCursor;
    }
}
