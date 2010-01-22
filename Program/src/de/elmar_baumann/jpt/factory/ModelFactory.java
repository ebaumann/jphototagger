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
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.controller.directories.ControllerDirectorySelected;
import de.elmar_baumann.jpt.controller.favorites.ControllerFavoriteSelected;
import de.elmar_baumann.jpt.controller.metadata.ControllerMetadataTemplates;
import de.elmar_baumann.jpt.controller.metadata.ControllerShowMetadata;
import de.elmar_baumann.jpt.controller.miscmetadata.ControllerMiscMetadataItemSelected;
import de.elmar_baumann.jpt.controller.timeline.ControllerTimelineItemSelected;
import de.elmar_baumann.jpt.model.ComboBoxModelMetadataTemplates;
import de.elmar_baumann.jpt.model.ListModelImageCollections;
import de.elmar_baumann.jpt.model.ListModelKeywords;
import de.elmar_baumann.jpt.model.ListModelMetadataTemplates;
import de.elmar_baumann.jpt.model.ListModelNoMetadata;
import de.elmar_baumann.jpt.model.ListModelSavedSearches;
import de.elmar_baumann.jpt.model.TableModelExif;
import de.elmar_baumann.jpt.model.TableModelIptc;
import de.elmar_baumann.jpt.model.TableModelXmp;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.model.TreeModelMiscMetadata;
import de.elmar_baumann.jpt.model.TreeModelTimeline;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.jpt.view.dialogs.InputHelperDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.lib.componentutil.MessageLabel;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.thirdparty.SortedListModel;
import java.awt.Cursor;
import java.util.List;
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

    public static final ModelFactory INSTANCE = new ModelFactory();
    private final       Support      support  = new Support();
    private volatile    boolean      init;

    void init() {
        synchronized (this) {
            if (!Util.checkInit(getClass(), init)) return;
            init = true;
        }
        AppLogger.logFine(getClass(), "ModelFactory.Init.Start");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Init.Start"), MessageLabel.MessageType.INFO, 1000);

        final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
        setTableModels(appPanel);
        setComboBoxModels(appPanel);
        setListModels(appPanel);
        setTreeModels(appPanel);

        AppLogger.logFine(getClass(), "ModelFactory.Init.Finished");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Init.Finished"), MessageLabel.MessageType.INFO, 2000);
    }

    private void setComboBoxModels(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.ComboBoxModels");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.ComboBoxModels"), MessageLabel.MessageType.INFO, -1);

        ComboBoxModelMetadataTemplates model = new ComboBoxModelMetadataTemplates();

        support.add(model);
        appPanel.getMetadataEditActionsPanel().getComboBoxMetadataTemplates().setModel(model);

        ControllerFactory.INSTANCE.add(new ControllerMetadataTemplates());

        AppLogger.logFine(getClass(), "ModelFactory.Finished.ComboBoxModels");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.ComboBoxModels"), MessageLabel.MessageType.INFO, -1);
    }

    private void setListModels(final AppPanel appPanel) {
        setListModelSavedSearches(appPanel);
        setListModelImageCollections(appPanel);
        setListModelKeywords(appPanel);
        setListModelNoMetadata(appPanel);
    }

    private void setListModelSavedSearches(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.ListModelSavedSearches");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.ListModelSavedSearches"), MessageLabel.MessageType.INFO, -1);

        JList                  list       = appPanel.getListSavedSearches();
        Cursor                 listCursor = setWaitCursor(list);
        ListModelSavedSearches model      = new ListModelSavedSearches();

        support.add(model);
        list.setModel(model);
        list.setCursor(listCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.ListModelSavedSearches");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.ListModelSavedSearches"), MessageLabel.MessageType.INFO, -1);
    }

    private void setListModelImageCollections(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.ListModelImageCollections");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.ListModelImageCollections"), MessageLabel.MessageType.INFO, -1);

        JList                     list       = appPanel.getListImageCollections();
        Cursor                    listCursor = setWaitCursor(list);
        ListModelImageCollections model      = new ListModelImageCollections();

        support.add(model);
        list.setModel(model);
        list.setCursor(listCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.ListModelImageCollections");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.ListModelImageCollections"), MessageLabel.MessageType.INFO, -1);
    }

    private void setListModelKeywords(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.ListModelKeywords");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.ListModelKeywords"), MessageLabel.MessageType.INFO, -1);

        JList             listKeywords  = appPanel.getListSelKeywords();
        Cursor            listCursor    = setWaitCursor(listKeywords);
        ListModelKeywords modelKeywords = new ListModelKeywords();
        ListModel         sortedModel   = new SortedListModel(modelKeywords);

        support.add(modelKeywords);
        listKeywords.setModel(sortedModel);
        appPanel.getListEditKeywords().setModel(sortedModel);
        InputHelperDialog.INSTANCE.setModelKeywords(sortedModel);

        listKeywords.setCursor(listCursor);
        AppLogger.logFine(getClass(), "ModelFactory.Finished.ListModelKeywords");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.ListModelKeywords"), MessageLabel.MessageType.INFO, -1);
    }

    private void setListModelNoMetadata(AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.ListModelNoMetadata");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.ListModelNoMetadata"), MessageLabel.MessageType.INFO, -1);

        ListModelNoMetadata model = new ListModelNoMetadata();

        support.add(model);
        appPanel.getListNoMetadata().setModel(model);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.ListModelNoMetadata");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.ListModelNoMetadata"), MessageLabel.MessageType.INFO, -1);
    }

    private void setTableModels(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TableModels");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.TableModels"), MessageLabel.MessageType.INFO, -1);

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

        ControllerFactory.INSTANCE.add(modelIptc);
        ControllerFactory.INSTANCE.add(modelXmp1);
        ControllerFactory.INSTANCE.add(modelXmp2);
        ControllerFactory.INSTANCE.add(modelXmp3);
        ControllerFactory.INSTANCE.add(modelXmp4);
        ControllerFactory.INSTANCE.add(modelXmp5);
        ControllerFactory.INSTANCE.add(modelXmp6);
        ControllerFactory.INSTANCE.add(modelXmp7);
        ControllerFactory.INSTANCE.add(modelXmp8);
        ControllerFactory.INSTANCE.add(modelExif);

        appPanel.getTableIptc()                .setModel(modelIptc);
        appPanel.getTableXmpCameraRawSettings().setModel(modelXmp1);
        appPanel.getTableXmpDc()               .setModel(modelXmp2);
        appPanel.getTableXmpExif()             .setModel(modelXmp3);
        appPanel.getTableXmpIptc()             .setModel(modelXmp4);
        appPanel.getTableXmpLightroom()        .setModel(modelXmp5);
        appPanel.getTableXmpPhotoshop()        .setModel(modelXmp6);
        appPanel.getTableXmpTiff()             .setModel(modelXmp7);
        appPanel.getTableXmpXap()              .setModel(modelXmp8);
        appPanel.getTableExif()                .setModel(modelExif);

        ControllerFactory.INSTANCE.add(new ControllerShowMetadata());

        AppLogger.logFine(getClass(), "ModelFactory.Finished.TableModels");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.TableModels"), MessageLabel.MessageType.INFO, -1);
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
        setTreeModelKeywords();
        setTreeModelTimeline(appPanel);
        setTreeModelMiscMetadata(appPanel);
        setTreeModelFavorites(appPanel);
        setTreeModelDirectories(appPanel);
    }

    private void setTreeModelKeywords() {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelKeywords");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.TreeModelKeywords"), MessageLabel.MessageType.INFO, -1);

        TreeModel                  treeModelKeywords  = new TreeModelKeywords();
        AppPanel                   appPanel           = GUI.INSTANCE.getAppPanel();
        ListModelMetadataTemplates listModelTemplates = new ListModelMetadataTemplates();

        support.add(treeModelKeywords);
        support.add(listModelTemplates);

        appPanel.getTreeSelKeywords().setModel(treeModelKeywords);
        appPanel.getTreeEditKeywords().setModel(treeModelKeywords);
        InputHelperDialog.INSTANCE.getPanelKeywords().getTree().setModel(treeModelKeywords);
        InputHelperDialog.INSTANCE.getPanelMetaDataTemplates().getList().setModel(listModelTemplates);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.TreeModelKeywords");
        appPanel.setStatusbarText(Bundle.getString("ModelFactory.Finished.TreeModelKeywords"), MessageLabel.MessageType.INFO, -1);
    }

    private void setTreeModelMiscMetadata(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelMiscMetadata");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.TreeModelMiscMetadata"), MessageLabel.MessageType.INFO, -1);

        JTree     tree       = appPanel.getTreeMiscMetadata();
        Cursor    treeCursor = setWaitCursor(tree);
        TreeModel model      = new TreeModelMiscMetadata();

        support.add(model);
        tree.setModel(model);
        ControllerFactory.INSTANCE.add(new ControllerMiscMetadataItemSelected());
        tree.setCursor(treeCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.TreeModelMiscMetadata");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.TreeModelMiscMetadata"), MessageLabel.MessageType.INFO, -1);
    }

    private void setTreeModelTimeline(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelTimeline");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.TreeModelTimeline"), MessageLabel.MessageType.INFO, -1);

        JTree     tree       = appPanel.getTreeTimeline();
        Cursor    treeCursor = setWaitCursor(tree);
        TreeModel model      = new TreeModelTimeline();

        tree.setModel(model);
        ControllerFactory.INSTANCE.add(new ControllerTimelineItemSelected());

        tree.setCursor(treeCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.TreeModelTimeline");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.TreeModelTimeline"), MessageLabel.MessageType.INFO, -1);
    }

    private void setTreeModelFavorites(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelFavorites");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Starting.TreeModelFavorites"), MessageLabel.MessageType.INFO, -1);

        JTree              tree       = appPanel.getTreeFavorites();
        Cursor             treeCursor = setWaitCursor(tree);
        TreeModelFavorites model      = new TreeModelFavorites(tree);

        support.add(model);
        tree.setModel(model);
        ControllerFactory.INSTANCE.add(new ControllerFavoriteSelected());
        model.readFromProperties();
        tree.setCursor(treeCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelDirectories");
        GUI.INSTANCE.getAppPanel().setStatusbarText(Bundle.getString("ModelFactory.Finished.TreeModelFavorites"), MessageLabel.MessageType.INFO, -1);
    }

    private void setTreeModelDirectories(final AppPanel appPanel) {
        AppLogger.logFine(getClass(), "ModelFactory.Starting.TreeModelDirectories");
        appPanel.setStatusbarText(Bundle.getString("ModelFactory.Starting.TreeModelDirectories"), MessageLabel.MessageType.INFO, -1);

        JTree     tree       = appPanel.getTreeDirectories();
        Cursor    treeCursor = setWaitCursor(tree);
        TreeModel model      = new TreeModelAllSystemDirectories(tree, UserSettings.INSTANCE.getDefaultDirectoryFilterOptions());

        support.add(model);
        tree.setModel(model);
        ControllerFactory.INSTANCE.add(new ControllerDirectorySelected());

        if (UserSettings.INSTANCE.isTreeDirectoriesSelectLastDirectory()) {
            ViewUtil.readTreeDirectoriesFromProperties();
        }
        tree.setCursor(treeCursor);

        AppLogger.logFine(getClass(), "ModelFactory.Finished.TreeModelDirectories");
        appPanel.setStatusbarText(Bundle.getString("ModelFactory.Finished.TreeModelDirectories"), MessageLabel.MessageType.INFO, -1);
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
        return support.getFirst(modelClass);
    }
}
