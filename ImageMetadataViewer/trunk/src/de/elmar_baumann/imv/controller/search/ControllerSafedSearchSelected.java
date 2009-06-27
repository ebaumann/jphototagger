package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.database.DatabaseSearch;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.InfoSetThumbnails;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerSafedSearchSelected
        implements ListSelectionListener, RefreshListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final DatabaseSearch db = DatabaseSearch.INSTANCE;
    private final JList list = appPanel.getListSavedSearches();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.
            getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels = appPanel.
            getEditPanelsArray();

    public ControllerSafedSearchSelected() {
        listen();
    }

    private void listen() {
        list.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.SAFED_SEARCH);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            search();
        }
    }

    @Override
    public void refresh() {
        search();
    }

    private void search() {
        if (list.getSelectedIndex() >= 0) {
            SwingUtilities.invokeLater(new ShowThumbnails());
        }
    }

    private class ShowThumbnails implements Runnable {

        @Override
        public void run() {
            Object selectedValue = list.getSelectedValue();
            if (selectedValue != null) {
                InfoSetThumbnails info = new InfoSetThumbnails();
                searchSelectedValue(selectedValue);
                info.hide();
                setMetadataEditable();
            }
        }

        private void searchSelectedValue(Object selectedValue) {
            if (selectedValue instanceof SavedSearch) {
                SavedSearch data = (SavedSearch) selectedValue;
                ParamStatement stmt =
                        data.getParamStatements().createStatement();
                if (stmt != null) {
                    searchParamStatement(stmt);
                }
            }
        }

        private void searchParamStatement(ParamStatement stmt) {
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
}
