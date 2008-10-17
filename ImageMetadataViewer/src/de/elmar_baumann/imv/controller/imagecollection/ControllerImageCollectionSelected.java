package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
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
public class ControllerImageCollectionSelected extends Controller
    implements ListSelectionListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private JList list = appPanel.getListImageCollections();

    public ControllerImageCollectionSelected() {
        list.addListSelectionListener(this);
    }

    private void showImageCollection(String collectionName) {
        List<String> filenames = db.getFilenamesOfImageCollection(collectionName);
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
            ImageFileThumbnailsPanel.Content.ImageCollection);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isStarted()) {
            Object selected = list.getSelectedValue();
            if (selected != null) {
                showImageCollection(selected.toString());
            }
        }
    }
}
