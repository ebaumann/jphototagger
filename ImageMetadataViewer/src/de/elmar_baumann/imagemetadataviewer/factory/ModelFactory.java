package de.elmar_baumann.imagemetadataviewer.factory;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.model.ListModelCategories;
import de.elmar_baumann.imagemetadataviewer.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imagemetadataviewer.model.TableModelExif;
import de.elmar_baumann.imagemetadataviewer.model.TableModelIptc;
import de.elmar_baumann.imagemetadataviewer.model.TableModelXmp;
import de.elmar_baumann.imagemetadataviewer.model.TreeModelImageCollections;
import de.elmar_baumann.imagemetadataviewer.model.TreeModelSavedSearches;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.lib.model.TreeModelDirectories;

/**
 * Erzeugt die Models und verbindet sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class ModelFactory {

    private static ModelFactory instance = new ModelFactory();

    static ModelFactory getInstance() {
        return instance;
    }

    private ModelFactory() {
        createModels();
    }

    private void createModels() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
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
        appPanel.getTreeSavedSearches().setModel(new TreeModelSavedSearches());
        appPanel.getTreeImageCollections().setModel(new TreeModelImageCollections());
        appPanel.getTreeDirectories().setModel(new TreeModelDirectories(UserSettings.getInstance().isAcceptHiddenDirectories()));
        appPanel.getListFavoriteDirectories().setModel(new ListModelFavoriteDirectories());
        appPanel.getListCategories().setModel(new ListModelCategories());
    }
}
