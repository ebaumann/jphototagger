package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerImageCollectionSelected implements ListSelectionListener, RefreshListener {

    private final DatabaseImageCollections db = DatabaseImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();
    private final JList list = appPanel.getListImageCollections();

    public ControllerImageCollectionSelected() {
        listen();
    }

    private void listen() {
        list.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.IMAGE_COLLECTION);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        showImageCollection();
    }

    @Override
    public void refresh() {
        showImageCollection();
    }

    private void showImageCollection() {
        if (list.getSelectedIndex() >= 0) {
            Object selectedValue = list.getSelectedValue();
            if (selectedValue != null) {
                showImageCollection(selectedValue.toString());
            }
            setMetadataEditable();
        }
    }

    private void showImageCollection(String collectionName) {
        List<String> filenames = db.getFilenamesOfImageCollection(collectionName);
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
                Content.IMAGE_COLLECTION);
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
