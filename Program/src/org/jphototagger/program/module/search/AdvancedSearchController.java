package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.domain.metadata.search.ParamStatement;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.FindRepository;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.ui.WaitDisplay;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktionen: Erweiterter Suchdialog soll angezeigt werden sowie
 * eine Suche soll durchgeführt werden, ausgelöst vom Suchdialog.
 *
 * @author Elmar Baumann
 */
public final class AdvancedSearchController implements ActionListener {

    private final FindRepository repo = Lookup.getDefault().lookup(FindRepository.class);

    public AdvancedSearchController() {
        listen();
    }

    private void listen() {
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
                WaitDisplay.INSTANCE.show();

                ParamStatement stmt = savedSearch.createParamStatement();

                TreeUtil.clearSelection(GUI.getAppPanel().getSelectionTrees());

                List<File> imageFiles = repo.findImageFiles(stmt);

                setTitle(savedSearch.getName());
                GUI.getThumbnailsPanel().setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_FOUND_BY_SAVED_SEARCH);
                WaitDisplay.INSTANCE.hide();
            }

            private void setTitle(String name) {
                String titleAdvancedSearch = Bundle.getString(AdvancedSearchController.class, "AdvancedSearchController.AppFrame.Title.AdvancedSearch");
                String titleSavedSearch = Bundle.getString(AdvancedSearchController.class, "AdvancedSearchController.AppFrame.Title.AdvancedSearch.Saved", name);
                String title = name == null ? titleAdvancedSearch : titleSavedSearch;
                MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
                mainWindowManager.setMainWindowTitle(title);
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }

    /**
     * Takes a search via {@code AdvancedSearchPanel#createSavedSearch()} and
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
