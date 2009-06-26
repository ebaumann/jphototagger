package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.model.ComboBoxModelMetadataEditTemplates;
import de.elmar_baumann.imv.model.ListModelCategories;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.model.ListModelKeywords;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.model.TreeModelMiscMetadata;
import de.elmar_baumann.imv.model.TreeModelTimeline;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.thirdparty.SortedListModel;
import javax.swing.tree.TreeModel;

/**
 * Erzeugt die Models und verbindet sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ModelFactory {

    static final ModelFactory INSTANCE = new ModelFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(ModelFactory.class, init);
        if (!init) {
            init = true;
            final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            appPanel.getTableIptc().setModel(new TableModelIptc());
            appPanel.getTableXmpCameraRawSettings().setModel(new TableModelXmp());
            appPanel.getTableXmpDc().setModel(new TableModelXmp());
            appPanel.getTableXmpExif().setModel(new TableModelXmp());
            appPanel.getTableXmpIptc().setModel(new TableModelXmp());
            appPanel.getTableXmpLightroom().setModel(new TableModelXmp());
            appPanel.getTableXmpPhotoshop().setModel(new TableModelXmp());
            appPanel.getTableXmpTiff().setModel(new TableModelXmp());
            appPanel.getTableXmpXap().setModel(new TableModelXmp());
            appPanel.getTableExif().setModel(new TableModelExif());
            appPanel.getListSavedSearches().setModel(
                    new ListModelSavedSearches());
            appPanel.getListImageCollections().setModel(
                    new ListModelImageCollections());
            setTreeModels(appPanel);
            appPanel.getListCategories().setModel(new SortedListModel(
                    new ListModelCategories()));
            appPanel.getListKeywords().setModel(new SortedListModel(
                    new ListModelKeywords()));
            appPanel.getMetadataEditActionsPanel().getComboBoxMetadataTemplates().
                    setModel(new ComboBoxModelMetadataEditTemplates());
        }
    }

    private void setTreeModels(final AppPanel appPanel) {
        setTreeModelTimeline(appPanel);
        setTreeModelMiscMetadata(appPanel);
        setTreeModelFavoriteDirectories(appPanel);
        setTreeModelDirectories(appPanel);
    }

    private void setTreeModelMiscMetadata(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                TreeModel model = new TreeModelMiscMetadata();
                appPanel.getTreeMiscMetadata().setModel(model);
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
                TreeModel model = new TreeModelTimeline();
                appPanel.getTreeTimeline().setModel(model);
            }
        });
        thread.setName("Creating model of tree timeline" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private void setTreeModelFavoriteDirectories(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                TreeModel model = new TreeModelFavoriteDirectories();
                appPanel.getTreeFavoriteDirectories().setModel(model);
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
                TreeModel model =
                        new TreeModelAllSystemDirectories(UserSettings.INSTANCE.
                        getDefaultDirectoryFilterOptions());
                appPanel.getTreeDirectories().setModel(model);
            }
        });
        thread.setName("Creating model of tree directories" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }
}
