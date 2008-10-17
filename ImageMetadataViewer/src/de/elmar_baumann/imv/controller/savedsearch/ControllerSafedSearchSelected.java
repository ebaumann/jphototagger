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
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerSafedSearchSelected extends Controller
    implements ListSelectionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private Database db = Database.getInstance();
    private JList list = appPanel.getListSavedSearches();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();

    public ControllerSafedSearchSelected() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        list.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object selected = list.getSelectedValue();
        if (isStarted() && selected != null) {
            search(selected);
        }
    }
    
    private void search(Object selected) {
        if (selected instanceof SavedSearch) {
            SavedSearch data = (SavedSearch) selected;
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
