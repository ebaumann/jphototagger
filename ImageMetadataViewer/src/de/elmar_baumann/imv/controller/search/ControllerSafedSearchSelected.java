package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSearch;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
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
    implements ListSelectionListener, RefreshListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private DatabaseSearch db = DatabaseSearch.getInstance();
    private JList list = appPanel.getListSavedSearches();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerSafedSearchSelected() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        list.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.SafedSearch);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (isControl() && list.getSelectedIndex() >= 0) {
            searchSelectedValue();
            checkEditPanel();
        }
    }

    @Override
    public void refresh() {
        if (isControl() && list.getSelectedIndex() >= 0) {
            searchSelectedValue();
        }
    }

    private void searchSelectedValue() {
        Object selectedValue = list.getSelectedValue();
        if (isControl() && selectedValue != null) {
            searchSelectedValue(selectedValue);
        }
    }

    private void searchSelectedValue(Object selectedValue) {
        if (selectedValue instanceof SavedSearch) {
            SavedSearch data = (SavedSearch) selectedValue;
            ParamStatement stmt = data.getParamStatements().createStatement();
            if (stmt != null) {
                searchParamStatement(stmt);
            }
        }
    }

    private void searchParamStatement(ParamStatement stmt) {
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
