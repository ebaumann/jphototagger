package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.view.dialogs.ImageCollectionsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Füge Bilder einer Bildsammlung hinzu, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerAddToImageCollection implements ActionListener {

    private final PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerAddToImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerAddToImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addSelectedFilesToImageCollection();
    }

    private void addSelectedFilesToImageCollection() {
        String collectionName = selectCollectionName();
        if (collectionName != null) {
            ImageCollectionDatabaseUtils.addImagesToCollection(collectionName,
                    FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        }
    }

    private String selectCollectionName() {
        ImageCollectionsDialog dialog = new ImageCollectionsDialog(null);
        dialog.setVisible(true);
        return dialog.getSelectedCollectionName();
    }
}
