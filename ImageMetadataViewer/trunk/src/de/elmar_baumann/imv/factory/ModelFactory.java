package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.directories.ControllerDirectorySelected;
import de.elmar_baumann.imv.controller.favorites.ControllerFavoriteSelected;
import de.elmar_baumann.imv.controller.metadata.ControllerMetadataTemplates;
import de.elmar_baumann.imv.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.imv.controller.miscmetadata.ControllerMiscMetadataItemSelected;
import de.elmar_baumann.imv.controller.timeline.ControllerTimelineItemSelected;
import de.elmar_baumann.imv.model.ComboBoxModelMetadataEditTemplates;
import de.elmar_baumann.imv.model.ListModelCategories;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.model.ListModelKeywords;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.imv.model.TreeModelTimeline;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.thirdparty.SortedListModel;
import java.awt.Cursor;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

/**
 * Erzeugt die Models und verbindet sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ModelFactory {

    static final ModelFactory INSTANCE = new ModelFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(ModelFactory.class, init);
        if (!init) {
            init = true;
            final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            setTableModels(appPanel);
            setComboBoxModels(appPanel);
            setListModels(appPanel);
            setTreeModels(appPanel);
        }
    }

    private void setComboBoxModels(final AppPanel appPanel) {
        appPanel.getMetadataEditActionsPanel().getComboBoxMetadataTemplates().
                setModel(new ComboBoxModelMetadataEditTemplates());
        new ControllerMetadataTemplates();
    }

    private void setListModels(final AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelCategories(appPanel);
        setListModelKeywords(appPanel);
    }

    private void setListModelSavedSearches(final AppPanel appPanel) {
        JList list = appPanel.getListSavedSearches();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new ListModelSavedSearches());
        list.setCursor(listCursor);
    }

    private void setListModelImageCollections(final AppPanel appPanel) {
        JList list = appPanel.getListImageCollections();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new ListModelImageCollections());
        list.setCursor(listCursor);
    }

    private void setListModelCategories(final AppPanel appPanel) {
        JList list = appPanel.getListCategories();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new SortedListModel(new ListModelCategories()));
        list.setCursor(listCursor);
    }

    private void setListModelKeywords(final AppPanel appPanel) {
        JList list = appPanel.getListKeywords();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new SortedListModel(new ListModelKeywords()));
        list.setCursor(listCursor);
    }

    private void setTableModels(final AppPanel appPanel) {
        appPanel.getTableIptc().
                setModel(new TableModelIptc());
        appPanel.getTableXmpCameraRawSettings().
                setModel(new TableModelXmp());
        appPanel.getTableXmpDc().
                setModel(new TableModelXmp());
        appPanel.getTableXmpExif().
                setModel(new TableModelXmp());
        appPanel.getTableXmpIptc().
                setModel(new TableModelXmp());
        appPanel.getTableXmpLightroom().
                setModel(new TableModelXmp());
        appPanel.getTableXmpPhotoshop().
                setModel(new TableModelXmp());
        appPanel.getTableXmpTiff().
                setModel(new TableModelXmp());
        appPanel.getTableXmpXap().
                setModel(new TableModelXmp());
        appPanel.getTableExif().
                setModel(new TableModelExif());
        new ControllerShowMetadata();
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

    private void setTreeModels(final AppPanel appPanel) {
        setTreeModelHierarchicalKeywords();
        setTreeModelTimeline(appPanel);
        setTreeModelMiscMetadata(appPanel);
        setTreeModelFavorites(appPanel);
        setTreeModelDirectories(appPanel);
    }

    private void setTreeModelHierarchicalKeywords() {
        TreeModel m = new TreeModelHierarchicalKeywords();
        HierarchicalKeywordsDialog.INSTANCE.getPanel().getTree().setModel(m);
        GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().setModel(m);
    }

    private void setTreeModelMiscMetadata(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JTree tree = appPanel.getTreeMiscMetadata();
                Cursor treeCursor = setWaitCursor(tree);
                TreeModel model = new TreeModelMiscMetadata();
                tree.setModel(model);
                new ControllerMiscMetadataItemSelected();
                tree.setCursor(treeCursor);
            }
        });
        thread.setName("Creating model of tree misc metadata" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private void setTreeModelTimeline(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JTree tree = appPanel.getTreeTimeline();
                Cursor treeCursor = setWaitCursor(tree);
                TreeModel model = new TreeModelTimeline();
                tree.setModel(model);
                new ControllerTimelineItemSelected();
                tree.setCursor(treeCursor);
            }
        });
        thread.setName("Creating model of tree timeline" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private void setTreeModelFavorites(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JTree tree = appPanel.getTreeFavorites();
                Cursor treeCursor = setWaitCursor(tree);
                TreeModelFavorites model = new TreeModelFavorites(tree);
                tree.setModel(model);
                new ControllerFavoriteSelected();
                model.readFromProperties();
                tree.setCursor(treeCursor);
            }
        });
        thread.setName("Creating model of tree favorite directories" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private void setTreeModelDirectories(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JTree tree = appPanel.getTreeDirectories();
                Cursor treeCursor = setWaitCursor(tree);
                TreeModel model =
                        new TreeModelAllSystemDirectories(tree,
                        UserSettings.INSTANCE.getDefaultDirectoryFilterOptions());
                tree.setModel(model);
                new ControllerDirectorySelected();
                if (UserSettings.INSTANCE.isTreeDirectoriesSelectLastDirectory()) {
                    ViewUtil.readTreeDirectoriesFromProperties();
                }
                tree.setCursor(treeCursor);
            }
        });
        thread.setName("Creating model of tree directories" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }
}
