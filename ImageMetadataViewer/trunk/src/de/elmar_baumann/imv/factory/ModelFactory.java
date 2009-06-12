package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.model.ComboBoxModelMetadataEditTemplates;
import de.elmar_baumann.imv.model.ListModelCategories;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.model.ListModelKeywords;
import de.elmar_baumann.imv.model.ListModelSavedSearches;
import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.model.TreeModelDirectories;
import de.elmar_baumann.lib.thirdparty.SortedListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

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
            setTimelineModel(appPanel);
            appPanel.getListSavedSearches().setModel(
                    new ListModelSavedSearches());
            appPanel.getListImageCollections().setModel(
                    new ListModelImageCollections());
            createTreeModelDirectories(appPanel);
            appPanel.getListFavoriteDirectories().setModel(
                    new ListModelFavoriteDirectories());
            appPanel.getListCategories().setModel(new SortedListModel(
                    new ListModelCategories()));
            appPanel.getListKeywords().setModel(new SortedListModel(
                    new ListModelKeywords()));
            appPanel.getMetadataEditActionsPanel().getComboBoxMetadataTemplates().
                    setModel(new ComboBoxModelMetadataEditTemplates());
        }
    }

    private void createTreeModelDirectories(final AppPanel appPanel) {
        JTree treeDirectories = appPanel.getTreeDirectories();
        treeDirectories.setModel(
                new TreeModelDirectories(UserSettings.INSTANCE.
                getDefaultDirectoryFilterOptions()));
    }

    private void setTimelineModel(final AppPanel appPanel) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                appPanel.getTreeTimeline().
                        setModel(
                        new DefaultTreeModel(DatabaseImageFiles.INSTANCE.
                        getTimeline().
                        getRoot()));
            }
        });
        thread.setName("Timeline-Tree");
        thread.start();
    }
}
