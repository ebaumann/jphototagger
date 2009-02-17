package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert Aktion: Erzeuge eine Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateImageCollection implements ActionListener {

    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ListModelImageCollections model = (ListModelImageCollections) appPanel.getListImageCollections().getModel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerCreateImageCollection() {
        listen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createCollectionOfSelectedFiles();
    }

    private void createCollectionOfSelectedFiles() {
        ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
        String collectionName = manager.addImageCollection(
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        if (collectionName != null) {
            ListUtil.insertSorted(model, collectionName, new ComparatorStringAscending(true));
        }
    }

    private void listen() {
        popup.addActionListenerCreateImageCollection(this);
    }
}
