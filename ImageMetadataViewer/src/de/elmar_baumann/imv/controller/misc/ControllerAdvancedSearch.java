package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.data.SavedSearchParamStatement;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.SearchListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
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

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private List<JTree> selectionTrees = appPanel.getSelectionTrees();

    public ControllerAdvancedSearch() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        appPanel.getButtonAdvanedSearch().addActionListener(this);
        ListenerProvider.getInstance().addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            showAdvancedSearchDialog();
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

        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
            ImageFileThumbnailsPanel.Content.Search);
    }
}
