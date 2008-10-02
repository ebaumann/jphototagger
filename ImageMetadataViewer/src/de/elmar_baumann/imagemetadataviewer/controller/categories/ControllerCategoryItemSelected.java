package de.elmar_baumann.imagemetadataviewer.controller.categories;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Kontrolliert die Aktion: Ein Kategorie-Item wurde selektiert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public class ControllerCategoryItemSelected extends Controller
    implements ListSelectionListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JList listCategories = appPanel.getListCategories();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();

    public ControllerCategoryItemSelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        listCategories.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isStarted()) {
            setFilenamesToThumbnailPanel();
        }
    }

    private void setFilenamesToThumbnailPanel() {
        String category = (String) listCategories.getSelectedValue();
        LinkedHashSet<String> filenames = db.getFilenamesOfCategory(category);
        Vector<String> filenamesArray = new Vector<String>(filenames);
        Collections.sort(filenamesArray);
        thumbnailsPanel.setFilenames(filenamesArray);
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }
}
