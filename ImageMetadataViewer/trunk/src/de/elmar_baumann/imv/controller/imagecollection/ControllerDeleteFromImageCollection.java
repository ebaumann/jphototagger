package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
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

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerDeleteFromImageCollection() {
        listen();
    }

    private void listen() {
        popup.addActionListenerDeleteFromImageCollection(this);
        Panels.getInstance().getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getContent().equals(Content.IMAGE_COLLECTION) &&
                thumbnailsPanel.getSelectionCount() > 0) {
            deleteSelectedFilesFromImageCollection();
        }
    }

    private void deleteSelectedFilesFromImageCollection() {
        Object selected = list.getSelectedValue();
        if (selected != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            List<File> selectedFiles = thumbnailsPanel.getSelectedFiles();
            manager.deleteImagesFromCollection(selected.toString(),
                    FileUtil.getAsFilenames(selectedFiles));
            thumbnailsPanel.remove(selectedFiles);
        }
    }
}
