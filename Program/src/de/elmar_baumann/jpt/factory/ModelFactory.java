/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.controller.directories.ControllerDirectorySelected;
import de.elmar_baumann.jpt.controller.favorites.ControllerFavoriteSelected;
import de.elmar_baumann.jpt.controller.metadata.ControllerMetadataTemplates;
import de.elmar_baumann.jpt.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.jpt.controller.miscmetadata.ControllerMiscMetadataItemSelected;
import de.elmar_baumann.jpt.controller.timeline.ControllerTimelineItemSelected;
import de.elmar_baumann.jpt.model.ComboBoxModelMetadataEditTemplates;
import de.elmar_baumann.jpt.model.ListModelCategories;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.model.ListModelKeywords;
import de.elmar_baumann.jpt.model.ListModelSavedSearches;
import de.elmar_baumann.jpt.model.TableModelExif;
import de.elmar_baumann.jpt.model.TableModelIptc;
import de.elmar_baumann.jpt.model.TableModelXmp;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.model.TreeModelMiscMetadata;
import de.elmar_baumann.jpt.model.TreeModelTimeline;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.thirdparty.SortedListModel;
import java.awt.Cursor;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListModel;
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
        GUI.INSTANCE.getAppPanel().showMessage(
                Bundle.getString("ModelFactory.Init.Start"), 1000);
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
        GUI.INSTANCE.getAppPanel().showMessage(
                Bundle.getString("ModelFactory.Finished.ComboBoxModels"), 1000);
    }

    private void setListModels(final AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelCategories(appPanel);
        setListModelKeywords(appPanel);
        setListModelNoMetadata(appPanel);
    }

    private void setListModelSavedSearches(final AppPanel appPanel) {
        JList list = appPanel.getListSavedSearches();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new ListModelSavedSearches());
        list.setCursor(listCursor);
        GUI.INSTANCE.getAppPanel().showMessage(
                Bundle.getString("ModelFactory.Finished.ListModelSavedSearches"),
                1000);
    }

    private void setListModelImageCollections(final AppPanel appPanel) {
        JList list = appPanel.getListImageCollections();
        Cursor listCursor = setWaitCursor(list);
        list.setModel(new ListModelImageCollections());
        list.setCursor(listCursor);
        GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                "ModelFactory.Finished.ListModelImageCollections"), 1000);
    }

    private void setListModelCategories(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JList listAppPanel = appPanel.getListCategories();
                Cursor listAppPanelCursor = setWaitCursor(listAppPanel);
                ListModel model = new SortedListModel(new ListModelCategories());
                listAppPanel.setModel(model);
                InputHelperDialog.INSTANCE.setModelCategories(model);
                listAppPanel.setCursor(listAppPanelCursor);
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.ListModelCategories"),
                        1000);
            }
        });
        thread.setName("Creating categories model @ " + getClass().getName()); // NOI18N
        thread.start();
    }

    private void setListModelKeywords(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                JList listAppPanel = appPanel.getListKeywords();
                Cursor listAppPanelCursor = setWaitCursor(listAppPanel);
                ListModel model = new SortedListModel(new ListModelKeywords());
                listAppPanel.setModel(model);
                InputHelperDialog.INSTANCE.setModelKeywords(model);
                listAppPanel.setCursor(listAppPanelCursor);
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.ListModelKeywords"), 1000);
            }
        });
        thread.setName("Creating keywords model @ " + getClass().getName()); // NOI18N
        thread.start();
    }

    private void setListModelNoMetadata(AppPanel appPanel) {
        appPanel.getListNoMetadata().setModel(
                new de.elmar_baumann.jpt.model.ListModelNoMetadata());
        GUI.INSTANCE.getAppPanel().showMessage(
                Bundle.getString("ModelFactory.Finished.ListModelNoMetadata"),
                1000);
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
        GUI.INSTANCE.getAppPanel().showMessage(
                Bundle.getString("ModelFactory.Finished.TableModels"), 1000);
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
        GUI.INSTANCE.getAppPanel().getTreeSelHierarchicalKeywords().setModel(m);
        GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().setModel(m);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().setModel(m);
        GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                "ModelFactory.Finished.TreeModelHierarchicalKeywords"), 1000);
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
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.TreeModelMiscMetadata"), 1000);
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
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.TreeModelTimeline"), 1000);
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
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.TreeModelFavorites"), 1000);
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
                GUI.INSTANCE.getAppPanel().showMessage(Bundle.getString(
                        "ModelFactory.Finished.TreeModelDirectories"), 1000);
            }
        });
        thread.setName("Creating model of tree directories" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }
}
