package org.jphototagger.program.controller.search;

import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerSavedSearchSelected implements ListSelectionListener, RefreshListener {
    public ControllerSavedSearchSelected() {
        listen();
    }

    private void listen() {
        GUI.getSavedSearchesList().addListSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.SAVED_SEARCH);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            search();
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        search();
    }

    private void search() {
        if (GUI.getSavedSearchesList().getSelectedIndex() >= 0) {
            EventQueue.invokeLater(new ShowThumbnails());
        }
    }

    private class ShowThumbnails implements Runnable {
        @Override
        public void run() {
            Object selectedValue = GUI.getSavedSearchesList().getSelectedValue();

            if (selectedValue != null) {
                WaitDisplay.show();
                searchSelectedValue(selectedValue);
                setMetadataEditable();
                WaitDisplay.hide();
            }
        }

        private void searchSelectedValue(Object selectedValue) {
            if (selectedValue instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) selectedValue;

                if (!savedSearch.isValid()) {
                    assert false : savedSearch;

                    return;
                }

                ParamStatement stmt = savedSearch.createParamStatement();

                SavedSearchesHelper.setSort(savedSearch);
                searchParamStatement(stmt, savedSearch.getName());
            }
        }

        private void searchParamStatement(ParamStatement stmt, String name) {
            List<File> imageFiles = DatabaseFind.INSTANCE.findImageFiles(stmt);

            setTitle(name);
            GUI.getThumbnailsPanel().setFiles(imageFiles, Content.SAVED_SEARCH);
        }

        private void setTitle(String name) {
            GUI.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerSavedSearchSelected.AppFrame.Title.AdvancedSearch.Saved", name));
        }

        private void setMetadataEditable() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!GUI.getThumbnailsPanel().isAFileSelected()) {
                        GUI.getEditPanel().setEditable(false);
                    }
                }
            });
        }
    }
}
