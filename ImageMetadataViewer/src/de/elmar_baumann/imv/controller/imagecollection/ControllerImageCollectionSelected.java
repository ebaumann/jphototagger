package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.util.List;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Kontrolliert die Aktion: Eine Bildsammlung wurde ausgewählt.
 * Ausgelöst wird dies durch Selektieren des Treeitems mit dem
 * Namen der gespeicherten Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerImageCollectionSelected extends Controller
    implements TreeSelectionListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private JTree tree = appPanel.getTreeImageCollections();

    public ControllerImageCollectionSelected() {
        listenToActionSource(tree);
    }

    private void listenToActionSource(JTree tree) {
        tree.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isStarted()) {
            TreePath path = e.getPath();
            if (path != null) {
                Object item = path.getLastPathComponent();
                if (item != null) {
                    showImageCollection(item.toString());
                }
            }
        }
    }

    private void showImageCollection(String collectionName) {
        List<String> filenames = db.getFilenamesOfImageCollection(collectionName);
        thumbnailsPanel.setFilenames(filenames);
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(true);
    }
}
