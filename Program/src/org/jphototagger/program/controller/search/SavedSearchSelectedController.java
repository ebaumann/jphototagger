package org.jphototagger.program.controller.search;

import java.io.File;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.search.ParamStatement;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.domain.repository.FindRepository;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class SavedSearchSelectedController implements ListSelectionListener {

    private final FindRepository repo = Lookup.getDefault().lookup(FindRepository.class);

    public SavedSearchSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getSavedSearchesList().addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            search();
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        TypeOfDisplayedImages typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

        if (TypeOfDisplayedImages.SAVED_SEARCH.equals(typeOfDisplayedImages)) {
            search();
        }
    }

    private void search() {
        if (GUI.getSavedSearchesList().getSelectedIndex() >= 0) {
            EventQueueUtil.invokeInDispatchThread(new ShowThumbnails());
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
            List<File> imageFiles = repo.findImageFiles(stmt);

            setTitle(name);
            GUI.getThumbnailsPanel().setFiles(imageFiles, TypeOfDisplayedImages.SAVED_SEARCH);
        }

        private void setTitle(String name) {
            GUI.getAppFrame().setTitle(
                    Bundle.getString(ShowThumbnails.class, "SavedSearchSelectedController.AppFrame.Title.AdvancedSearch.Saved", name));
        }

        private void setMetadataEditable() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
