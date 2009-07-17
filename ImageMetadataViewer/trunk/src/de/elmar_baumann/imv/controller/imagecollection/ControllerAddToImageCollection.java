package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.ModifyImageCollections;
import de.elmar_baumann.imv.view.dialogs.ImageCollectionsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerAddToImageCollection implements ActionListener {

    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerAddToImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemAddToImageCollection().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addSelectedFilesToImageCollection();
    }

    private void addSelectedFilesToImageCollection() {
        String collectionName = selectCollectionName();
        if (collectionName != null) {
            ModifyImageCollections.addImagesToCollection(collectionName,
                    FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dialog = new ImageCollectionsDialog(
                GUI.INSTANCE.getAppFrame());
        dialog.setVisible(true);
        return dialog.getSelectedCollectionName();
    }
}
