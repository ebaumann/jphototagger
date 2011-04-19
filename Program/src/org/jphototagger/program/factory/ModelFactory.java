package org.jphototagger.program.factory;

import java.io.File;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.app.AppWindowPersistence;
import org.jphototagger.program.model.ComboBoxModelFileFilters;
import org.jphototagger.program.model.ComboBoxModelMetadataTemplates;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.model.ListModelKeywords;
import org.jphototagger.program.model.ListModelMetadataTemplates;
import org.jphototagger.program.model.ListModelNoMetadata;
import org.jphototagger.program.model.ListModelSavedSearches;
import org.jphototagger.program.model.TableModelExif;
import org.jphototagger.program.model.TableModelIptc;
import org.jphototagger.program.model.TableModelXmp;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.model.TreeModelMiscMetadata;
import org.jphototagger.program.model.TreeModelTimeline;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.util.Comparator;

import java.util.List;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.sort.ListSortController;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.panels.SelectRootFilesPanel;
import org.jphototagger.program.view.renderer.KeywordHighlightPredicate;
import org.jphototagger.program.view.renderer.TableCellRendererExif;
import org.jphototagger.program.view.renderer.TableCellRendererIptc;
import org.jphototagger.program.view.renderer.TableCellRendererXmp;

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

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppPanel appPanel = GUI.getAppPanel();

                Support.setStatusbarInfo("ModelFactory.Init.Start");
                setTableModels(appPanel);
                setComboBoxModels(appPanel);
                setListModels(appPanel);
                setTreeModels(appPanel);
                Support.setStatusbarInfo("ModelFactory.Init.Finished");
            }
        });
    }

    private void setComboBoxModels(AppPanel appPanel) {
        setComboBoxModelMetadataTemplates(appPanel);
        setComboBoxModelFileFilters();
    }

    private void setComboBoxModelFileFilters() {
        ComboBoxModelFileFilters model = new ComboBoxModelFileFilters();

        support.add(model);
        GUI.getAppPanel().getComboBoxFileFilters().setModel(model);
    }

    private void setComboBoxModelMetadataTemplates(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.ComboBoxModelMetadataTemplates");

        ComboBoxModelMetadataTemplates model = new ComboBoxModelMetadataTemplates();

        support.add(model);
        appPanel.getPanelEditMetadataActions().getComboBoxMetadataTemplates().setModel(model);
        Support.setStatusbarInfo("ModelFactory.Finished.ComboBoxModelMetadataTemplates");
    }

    private void setListModels(AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelKeywords(appPanel);
        setListModelNoMetadata(appPanel);
    }

    private void setListModelSavedSearches(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.ListModelSavedSearches");

                final JXList list = appPanel.getListSavedSearches();
                final Cursor listCursor = setWaitCursor(list);
                final ListModelSavedSearches model = new ListModelSavedSearches();

                support.add(model);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        list.setModel(model);
                        list.setAutoCreateRowSorter(true);
                        list.setSortOrder(SortOrder.ASCENDING);
                        AppWindowPersistence.readListSavedSearches();
                        list.setCursor(listCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.ListModelSavedSearches");
                    }
                });
            }
        }, "JPhotoTagger: Creating Saved Searches List").start();
    }

    private void setListModelImageCollections(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.ListModelImageCollections");

                final JXList list = appPanel.getListImageCollections();
                final Cursor listCursor = setWaitCursor(list);
                final ListModelImageCollections model = new ListModelImageCollections();

                support.add(model);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        list.setModel(model);
                        ListSortController<ListModelImageCollections> sorter = new ListSortController<ListModelImageCollections>(model);
                        sorter.setComparator(0, model.createAscendingSortComparator());
                        list.setRowSorter(sorter);
                        list.setSortOrder(SortOrder.ASCENDING);
                        AppWindowPersistence.readListImageCollections();
                        list.setCursor(listCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.ListModelImageCollections");
                    }
                });
            }
        }, "JPhotoTagger: Creating Image Collections List").start();
    }

    private void setListModelKeywords(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.ListModelKeywords");

                final JXList listSelectedKeywords = appPanel.getListSelKeywords();
                final KeywordsPanel panelEditKeywords = appPanel.getPanelEditKeywords();
                final Cursor listCursor = setWaitCursor(listSelectedKeywords);
                final ListModelKeywords modelKeywords = new ListModelKeywords();

                support.add(modelKeywords);
                EventQueue.invokeLater(new Runnable() {
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
                        Support.setStatusbarInfo("ModelFactory.Finished.ListModelKeywords");
                    }
                });
            }
        }, "JPhotoTagger: Creating Keywords list").start();
    }

    private void setListModelNoMetadata(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.ListModelNoMetadata");

        ListModelNoMetadata model = new ListModelNoMetadata();

        support.add(model);
        appPanel.getListNoMetadata().setModel(model);
        Support.setStatusbarInfo("ModelFactory.Finished.ListModelNoMetadata");
    }

    private void setTableModels(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TableModels");

        TableModelIptc modelIptc = new TableModelIptc();
        TableModelXmp modelXmp1 = new TableModelXmp();
        TableModelXmp modelXmp2 = new TableModelXmp();
        TableModelXmp modelXmp3 = new TableModelXmp();
        TableModelXmp modelXmp4 = new TableModelXmp();
        TableModelXmp modelXmp5 = new TableModelXmp();
        TableModelXmp modelXmp6 = new TableModelXmp();
        TableModelXmp modelXmp7 = new TableModelXmp();
        TableModelXmp modelXmp8 = new TableModelXmp();
        TableModelExif modelExif = new TableModelExif();

        support.add(modelIptc);
        support.add(modelXmp1);
        support.add(modelXmp2);
        support.add(modelXmp3);
        support.add(modelXmp4);
        support.add(modelXmp5);
        support.add(modelXmp6);
        support.add(modelXmp7);
        support.add(modelXmp8);
        support.add(modelExif);

        appPanel.getTableIptc().setModel(modelIptc);
        appPanel.getTableXmpCameraRawSettings().setModel(modelXmp1);
        appPanel.getTableXmpDc().setModel(modelXmp2);
        appPanel.getTableXmpExif().setModel(modelXmp3);
        appPanel.getTableXmpIptc().setModel(modelXmp4);
        appPanel.getTableXmpLightroom().setModel(modelXmp5);
        appPanel.getTableXmpPhotoshop().setModel(modelXmp6);
        appPanel.getTableXmpTiff().setModel(modelXmp7);
        appPanel.getTableXmpXap().setModel(modelXmp8);
        appPanel.getTableExif().setModel(modelExif);

        setIptcTableComparator(appPanel.getTableIptc());
        setXmpTableComparator(appPanel.getTableXmpCameraRawSettings());
        setXmpTableComparator(appPanel.getTableXmpDc());
        setXmpTableComparator(appPanel.getTableXmpExif());
        setXmpTableComparator(appPanel.getTableXmpIptc());
        setXmpTableComparator(appPanel.getTableXmpLightroom());
        setXmpTableComparator(appPanel.getTableXmpPhotoshop());
        setXmpTableComparator(appPanel.getTableXmpTiff());
        setXmpTableComparator(appPanel.getTableXmpXap());
        setExifTableComparator(appPanel.getTableExif());

        Support.setStatusbarInfo("ModelFactory.Finished.TableModels");
    }

    private void setXmpTableComparator(JTable xmpTable) {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>)xmpTable.getRowSorter();
        Comparator<?> column0Comparator = TableCellRendererXmp.createColumn0Comparator();
        Comparator<?> column1Comparator = TableCellRendererXmp.createColumn1Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
    }

    private void setIptcTableComparator(JTable iptcTabe) {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>)iptcTabe.getRowSorter();
        Comparator<?> column0Comparator = TableCellRendererIptc.createColumn0Comparator();
        Comparator<?> column1Comparator = TableCellRendererIptc.createColumn1Comparator();
        Comparator<?> column2Comparator = TableCellRendererIptc.createColumn2Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
        rowSorter.setComparator(2, column2Comparator);
    }

    private void setExifTableComparator(JTable exif) {
        TableRowSorter<?> rowSorter = (TableRowSorter<?>)exif.getRowSorter();
        Comparator<?> column0Comparator = TableCellRendererExif.createColumn0Comparator();
        Comparator<?> column1Comparator = TableCellRendererExif.createColumn1Comparator();

        rowSorter.setComparator(0, column0Comparator);
        rowSorter.setComparator(1, column1Comparator);
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
                Support.setStatusbarInfo("ModelFactory.Starting.TreeModelKeywords");

                final TreeModel treeModelKeywords = new TreeModelKeywords();
                final ListModelMetadataTemplates listModelTemplates = new ListModelMetadataTemplates();

                support.add(treeModelKeywords);
                support.add(listModelTemplates);
                EventQueue.invokeLater(new Runnable() {
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
                        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelKeywords");
                    }
                });
            }
        }, "JPhotoTagger: Creating Keywords Trees").start();
    }

    private void setTreeModelMiscMetadata(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.TreeModelMiscMetadata");

                final JTree tree = appPanel.getTreeMiscMetadata();
                final Cursor treeCursor = setWaitCursor(tree);
                final TreeModel modelApp = new TreeModelMiscMetadata(false);
                final TreeModel modelInputHelper = new TreeModelMiscMetadata(true);

                support.add(modelApp);
                support.add(modelInputHelper);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        InputHelperDialog.INSTANCE.getPanelMiscXmpMetadata().getTree().setModel(modelInputHelper);
                        tree.setModel(modelApp);
                        AppWindowPersistence.readTreeMiscMetadata();
                        tree.setCursor(treeCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelMiscMetadata");
                    }
                });
            }
        }, "JPhotoTagger: Creating Misc Metadata Tree").start();
    }

    private void setTreeModelTimeline(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.TreeModelTimeline");

                final JTree tree = appPanel.getTreeTimeline();
                final Cursor treeCursor = setWaitCursor(tree);
                final TreeModel model = new TreeModelTimeline();

                support.add(model);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tree.setModel(model);
                        AppWindowPersistence.readTreeTimeline();
                        tree.setCursor(treeCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelTimeline");
                    }
                });
            }
        }, "JPhotoTagger: Creating Timeline Tree").start();
    }

    private void setTreeModelFavorites(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.TreeModelFavorites");

                final JTree tree = appPanel.getTreeFavorites();
                final Cursor treeCursor = setWaitCursor(tree);
                final TreeModelFavorites model = new TreeModelFavorites(tree);

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        support.add(model);
                        tree.setModel(model);
                        model.readFromProperties();
                        tree.setCursor(treeCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelFavorites");
                    }
                });
            }
        }, "JPhotoTagger: Creating Favorites Tree").start();
    }

    private void setTreeModelDirectories(final AppPanel appPanel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Support.setStatusbarInfo("ModelFactory.Starting.TreeModelDirectories");

                final JTree tree = appPanel.getTreeDirectories();
                final Cursor treeCursor = setWaitCursor(tree);
                List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(UserSettings.KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB);
                final TreeModel model = new TreeModelAllSystemDirectories(tree, hideRootFiles, UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles());

                support.add(model);
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        tree.setModel(model);
                        AppWindowPersistence.readTreeDirectories();
                        tree.setCursor(treeCursor);
                        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelDirectories");
                    }
                });
            }
        }, "JPhotoTagger: Creating Directories Tree").start();
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
