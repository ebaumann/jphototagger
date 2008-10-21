package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.tasks.ImageCollectionToDatabase;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

/**
 * Kontrolliert die Aktion: Lösche Bilder aus einer Bildsammlung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public class ControllerDeleteFromImageCollection extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList list = appPanel.getListImageCollections();
    private ListModelImageCollections model = (ListModelImageCollections) list.getModel();
    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerDeleteFromImageCollection() {
        popup.addActionListenerDeleteFromImageCollection(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            deleteSelectedFilesFromImageCollection();
        }
    }

    private void deleteSelectedFilesFromImageCollection() {
        Object selected = list.getSelectedValue();
        if (selected != null) {
            ImageCollectionToDatabase manager = new ImageCollectionToDatabase();
            manager.deleteImagesFromCollection(selected.toString(),
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
            model.removeElement(selected);
        }
    }
}
