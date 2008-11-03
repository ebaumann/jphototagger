package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.data.SavedSearchParamStatement;
import de.elmar_baumann.imv.database.DatabaseSearch;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.SearchListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;
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

    private DatabaseSearch db = DatabaseSearch.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerAdvancedSearch() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        ListenerProvider.getInstance().addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            showAdvancedSearchDialog();
        }
    }

    @Override
    public void actionPerformed(SearchEvent e) {
        if (isControl() && e.getType().equals(SearchEvent.Type.Start)) {
            applySafedSearch(e);
            checkEditPanel();
        }
    }

    private void showAdvancedSearchDialog() {
        AdvancedSearchDialog dialogAdvancedSearch = AdvancedSearchDialog.getInstance();
        if (dialogAdvancedSearch.isVisible()) {
            dialogAdvancedSearch.toFront();
        } else {
            dialogAdvancedSearch.setVisible(true);
        }
    }

    private void applySafedSearch(SearchEvent e) {
        SavedSearch data = e.getSafedSearch();
        if (data != null) {
            SavedSearchParamStatement pData = data.getParamStatements();
            if (pData != null) {
                search(pData.createStatement());
            }
        }
    }

    private void search(ParamStatement stmt) {
        TreeUtil.clearSelection(selectionTrees);
        List<String> filenames = db.searchFilenames(stmt);

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
            Content.SafedSearch);
    }

    private void checkEditPanel() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
