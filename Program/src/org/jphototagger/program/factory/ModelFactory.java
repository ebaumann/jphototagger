/*
 * @(#)ModelFactory.java    Created on 2008-09-29
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.factory;

import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.lib.thirdparty.SortedListModel;
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

import java.util.List;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.tree.TreeModel;

/**
 * Erzeugt die Models und verbindet sie mit den GUI-Elementen.
 *
 * @author Elmar Baumann
 */
public final class ModelFactory {
    public static final ModelFactory INSTANCE = new ModelFactory();
    private final Support            support  = new Support();
    private volatile boolean         init;

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
        Support.setStatusbarInfo(
            "ModelFactory.Starting.ComboBoxModelMetadataTemplates");

        ComboBoxModelMetadataTemplates model =
            new ComboBoxModelMetadataTemplates();

        support.add(model);
        appPanel.getPanelEditMetadataActions().getComboBoxMetadataTemplates()
            .setModel(model);
        Support.setStatusbarInfo(
            "ModelFactory.Finished.ComboBoxModelMetadataTemplates");
    }

    private void setListModels(AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelKeywords(appPanel);
        setListModelNoMetadata(appPanel);
    }

    private void setListModelSavedSearches(AppPanel appPanel) {
        Support.setStatusbarInfo(
            "ModelFactory.Starting.ListModelSavedSearches");

        JList                  list       = appPanel.getListSavedSearches();
        Cursor                 listCursor = setWaitCursor(list);
        ListModelSavedSearches model      = new ListModelSavedSearches();

        support.add(model);
        list.setModel(model);
        list.setCursor(listCursor);
        Support.setStatusbarInfo(
            "ModelFactory.Finished.ListModelSavedSearches");
    }

    private void setListModelImageCollections(AppPanel appPanel) {
        Support.setStatusbarInfo(
            "ModelFactory.Starting.ListModelImageCollections");

        JList                     list = appPanel.getListImageCollections();
        Cursor                    listCursor = setWaitCursor(list);
        ListModelImageCollections model      = new ListModelImageCollections();

        support.add(model);
        list.setModel(model);
        list.setCursor(listCursor);
        Support.setStatusbarInfo(
            "ModelFactory.Finished.ListModelImageCollections");
    }

    private void setListModelKeywords(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.ListModelKeywords");

        JList             listKeywords  = appPanel.getListSelKeywords();
        Cursor            listCursor    = setWaitCursor(listKeywords);
        ListModelKeywords modelKeywords = new ListModelKeywords();
        ListModel         sortedModel   = new SortedListModel(modelKeywords);

        support.add(modelKeywords);
        listKeywords.setModel(sortedModel);
        appPanel.getListEditKeywords().setModel(sortedModel);
        InputHelperDialog.INSTANCE.setModelKeywords(sortedModel);
        listKeywords.setCursor(listCursor);
        Support.setStatusbarInfo("ModelFactory.Finished.ListModelKeywords");
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
        TableModelXmp  modelXmp1 = new TableModelXmp();
        TableModelXmp  modelXmp2 = new TableModelXmp();
        TableModelXmp  modelXmp3 = new TableModelXmp();
        TableModelXmp  modelXmp4 = new TableModelXmp();
        TableModelXmp  modelXmp5 = new TableModelXmp();
        TableModelXmp  modelXmp6 = new TableModelXmp();
        TableModelXmp  modelXmp7 = new TableModelXmp();
        TableModelXmp  modelXmp8 = new TableModelXmp();
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
        Support.setStatusbarInfo("ModelFactory.Finished.TableModels");
    }

    private void setTreeModels(AppPanel appPanel) {
        setTreeModelKeywords(appPanel);
        setTreeModelTimeline(appPanel);
        setTreeModelMiscMetadata(appPanel);
        setTreeModelFavorites(appPanel);
        setTreeModelDirectories(appPanel);
    }

    private void setTreeModelKeywords(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TreeModelKeywords");

        TreeModel                  treeModelKeywords = new TreeModelKeywords();
        ListModelMetadataTemplates listModelTemplates =
            new ListModelMetadataTemplates();

        support.add(treeModelKeywords);
        support.add(listModelTemplates);
        appPanel.getTreeSelKeywords().setModel(treeModelKeywords);
        appPanel.getTreeEditKeywords().setModel(treeModelKeywords);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().setModel(
            treeModelKeywords);
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList()
            .setModel(listModelTemplates);
        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelKeywords");
    }

    private void setTreeModelMiscMetadata(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TreeModelMiscMetadata");

        JTree     tree             = appPanel.getTreeMiscMetadata();
        Cursor    treeCursor       = setWaitCursor(tree);
        TreeModel modelApp         = new TreeModelMiscMetadata(false);
        TreeModel modelInputHelper = new TreeModelMiscMetadata(true);

        support.add(modelApp);
        support.add(modelInputHelper);
        InputHelperDialog.INSTANCE.getPanelMiscXmpMetadata().getTree().setModel(
            modelInputHelper);
        tree.setModel(modelApp);
        tree.setCursor(treeCursor);
        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelMiscMetadata");
    }

    private void setTreeModelTimeline(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TreeModelTimeline");

        JTree     tree       = appPanel.getTreeTimeline();
        Cursor    treeCursor = setWaitCursor(tree);
        TreeModel model      = new TreeModelTimeline();

        support.add(model);
        tree.setModel(model);
        tree.setCursor(treeCursor);
        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelTimeline");
    }

    private void setTreeModelFavorites(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TreeModelFavorites");

        JTree              tree       = appPanel.getTreeFavorites();
        Cursor             treeCursor = setWaitCursor(tree);
        TreeModelFavorites model      = new TreeModelFavorites(tree);

        support.add(model);
        tree.setModel(model);
        model.readFromProperties();
        tree.setCursor(treeCursor);
        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelFavorites");
    }

    private void setTreeModelDirectories(AppPanel appPanel) {
        Support.setStatusbarInfo("ModelFactory.Starting.TreeModelDirectories");

        JTree     tree       = appPanel.getTreeDirectories();
        Cursor    treeCursor = setWaitCursor(tree);
        TreeModel model =
            new TreeModelAllSystemDirectories(tree,
                UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles());

        support.add(model);
        tree.setModel(model);
        tree.setCursor(treeCursor);
        Support.setStatusbarInfo("ModelFactory.Finished.TreeModelDirectories");
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
