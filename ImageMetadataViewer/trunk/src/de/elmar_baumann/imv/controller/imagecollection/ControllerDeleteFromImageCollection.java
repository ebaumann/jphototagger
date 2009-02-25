package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JList;

/**
 * Kontrolliert die Aktion: Lösche Bilder aus einer Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerDeleteFromImageCollection implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerDeleteFromImageCollection(this);
        GUI.INSTANCE.getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getContent().equals(Content.IMAGE_COLLECTION)) {
            if (thumbnailsPanel.getSelectionCount() > 0) {
                deleteSelectedFilesFromImageCollection();
            } else {
                AppLog.logWarning(ControllerDeleteFromImageCollection.class, Bundle.getString("ControllerDeleteFromImageCollection.ErrorMessage.NoImagesSelected"));
            }
        }
    }

    private void deleteSelectedFilesFromImageCollection() {
        Object selectedValue = list.getSelectedValue();
        if (selectedValue != null) {
            List<File> selectedFiles = thumbnailsPanel.getSelectedFiles();
            ImageCollectionDatabaseUtils.deleteImagesFromCollection(
                selectedValue.toString(), FileUtil.getAsFilenames(selectedFiles));
            thumbnailsPanel.remove(selectedFiles);
        } else {
            AppLog.logWarning(ControllerDeleteFromImageCollection.class, Bundle.getString("ControllerDeleteFromImageCollection.ErrorMessage.SelectedImageCollectionIsNull"));
        }
    }
}
