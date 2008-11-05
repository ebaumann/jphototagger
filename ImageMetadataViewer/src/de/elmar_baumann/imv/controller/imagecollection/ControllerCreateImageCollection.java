package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.controller.Controller;
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
public class ControllerCreateImageCollection extends Controller
    implements ActionListener {

    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelImageCollections model = (ListModelImageCollections) appPanel.getListImageCollections().getModel();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerCreateImageCollection() {
        popup.addActionListenerCreateImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            createCollectionOfSelectedFiles();
        }
    }

    private void createCollectionOfSelectedFiles() {
        ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
        String collectionName = manager.addImageCollection(
            FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        if (collectionName != null) {
            ListUtil.insertSorted(model, collectionName, new ComparatorStringAscending(true));
        }
    }
}
