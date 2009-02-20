package de.elmar_baumann.imv.controller.search;

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
public final class ControllerAdvancedSearch implements ActionListener, SearchListener {

    private final DatabaseSearch db = DatabaseSearch.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerAdvancedSearch() {
        listen();
    }

    private void listen() {
        ListenerProvider.getInstance().addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog();
    }

    @Override
    public void actionPerformed(SearchEvent e) {
        if (e.getType().equals(SearchEvent.Type.START)) {
            applySafedSearch(e);
            setMetadataEditable();
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
                Content.SAFED_SEARCH);
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
