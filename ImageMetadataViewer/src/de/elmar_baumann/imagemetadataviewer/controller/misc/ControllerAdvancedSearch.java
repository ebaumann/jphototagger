package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearchParamStatement;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement;
import de.elmar_baumann.imagemetadataviewer.event.SearchEvent;
import de.elmar_baumann.imagemetadataviewer.event.SearchListener;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JTree;

/**
 * Kontrolliert die Aktionen: Erweiterter Suchdialog soll angezeigt werden sowie
 * eine Suche soll durchgeführt werden, ausgelöst vom Suchdialog.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerAdvancedSearch extends Controller
    implements ActionListener, SearchListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private AdvancedSearchDialog dialogAdvancedSearch = AdvancedSearchDialog.getInstance();

    public ControllerAdvancedSearch() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        appPanel.getButtonAdvanedSearch().addActionListener(this);
        dialogAdvancedSearch.addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showAdvancedSearchDialog();
        }
    }

    private void showAdvancedSearchDialog() {
        if (dialogAdvancedSearch.isVisible()) {
            dialogAdvancedSearch.toFront();
        } else {
            dialogAdvancedSearch.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(SearchEvent e) {
        if (isStarted() && e.getType().equals(SearchEvent.Type.Start)) {
            SavedSearch data = e.getSafedSearch();
            if (data != null) {
                SavedSearchParamStatement pData = data.getParamStatements();
                if (pData != null) {
                    search(pData.createStatement());
                }
            }
        }
    }

    private void search(ParamStatement stmt) {
        TreeUtil.clearSelection(selectionTrees);
        List<String> filenames = db.searchFilenames(stmt);

        thumbnailsPanel.setFilenames(filenames);
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }
}
