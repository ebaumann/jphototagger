package org.jphototagger.program.controller.search;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.panels.AdvancedSearchPanel;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;

import javax.swing.JButton;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Kontrolliert die Aktionen: Erweiterter Suchdialog soll angezeigt werden sowie
 * eine Suche soll durchgeführt werden, ausgelöst vom Suchdialog.
 *
 * @author Elmar Baumann
 */
public final class ControllerAdvancedSearch implements ActionListener {
    public ControllerAdvancedSearch() {
        getSearchButton().addActionListener(this);
    }

    private AdvancedSearchPanel getSearchPanel() {
        return AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel();
    }

    private JButton getSearchButton() {
        return getSearchPanel().getButtonSearch();
    }

    private void applySavedSearch(final SavedSearch savedSearch) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                assert savedSearch.isValid() : savedSearch;
                WaitDisplay.show();

                ParamStatement stmt = savedSearch.createParamStatement();

                TreeUtil.clearSelection(GUI.getAppPanel().getSelectionTrees());

                List<File> imageFiles = DatabaseFind.INSTANCE.findImageFiles(stmt);

                setTitle(savedSearch.getName());
                SavedSearchesHelper.setSort(savedSearch);
                GUI.getThumbnailsPanel().setFiles(imageFiles, Content.SAVED_SEARCH);
                WaitDisplay.hide();
            }
            private void setTitle(String name) {
                GUI.getAppFrame().setTitle((name == null)
                                           ? JptBundle.INSTANCE.getString(
                                               "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch")
                                           : JptBundle.INSTANCE.getString(
                                               "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch.Saved", name));
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }

    /**
     * Takes a search via {@link AdvancedSearchPanel#createSavedSearch()} and
     * performs it (searches).
     *
     * @param evt can be null
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        SavedSearch savedSearch = getSearchPanel().createSavedSearch();

        if (savedSearch.isValid()) {
            applySavedSearch(savedSearch);
            setMetadataEditable();
        }
    }
}
