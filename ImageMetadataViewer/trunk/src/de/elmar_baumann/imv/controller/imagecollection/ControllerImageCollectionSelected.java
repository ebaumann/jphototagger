package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
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
public final class ControllerImageCollectionSelected implements
        ListSelectionListener, RefreshListener {

    private final DatabaseImageCollections db =
            DatabaseImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.
            getEditPanelsArray();
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
        if (list.getSelectedIndex() >= 0) {
            showImageCollection();
        }
    }

    @Override
    public void refresh() {
        if (list.getSelectedIndex() >= 0) {
            showImageCollection();
        }
    }

    private void showImageCollection() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                InfoSetThumbnails info = new InfoSetThumbnails();
                Object selectedValue = list.getSelectedValue();
                if (selectedValue != null) {
                    showImageCollection(selectedValue.toString());
                } else {
                    AppLog.logWarning(ControllerImageCollectionSelected.class,
                            Bundle.getString(
                            "ControllerImageCollectionSelected.ErrorMessage.SelectedValueIsNull"));
                }
                setMetadataEditable();
                info.hide();
            }
        });
        thread.setName("Image collection selected" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private void showImageCollection(String collectionName) {
        List<String> filenames =
                db.getFilenamesOfImageCollection(collectionName);
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
                Content.IMAGE_COLLECTION);
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
