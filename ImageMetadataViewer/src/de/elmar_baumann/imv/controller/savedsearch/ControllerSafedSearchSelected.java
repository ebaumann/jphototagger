package de.elmar_baumann.imv.controller.savedsearch;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerSafedSearchSelected extends Controller
    implements TreeSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private Database db = Database.getInstance();
    private JTree tree = appPanel.getTreeSavedSearches();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();

    public ControllerSafedSearchSelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        tree.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (isStarted() && e.isAddedPath()) {
            search(e.getPath().getLastPathComponent());
        }
    }

    private void search(Object node) {
        if (node instanceof SavedSearch) {
            SavedSearch data = (SavedSearch) node;
            ParamStatement stmt = data.getParamStatements().createStatement();
            if (stmt != null) {
                search(stmt);
            }
        }
    }

    private void search(ParamStatement stmt) {
        List<String> filenames = db.searchFilenames(stmt);
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames));
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }
}
