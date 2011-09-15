package org.jphototagger.program.controller.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.FindRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.openide.util.Lookup;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author Elmar Baumann
 */
public final class ControllerFastSearch implements ActionListener {

    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final Autocomplete autocomplete;
    private boolean isAutocomplete;
    private final FindRepository findRepo = Lookup.getDefault().lookup(FindRepository.class);

    public ControllerFastSearch() {
        if (getPersistedAutocomplete()) {
            autocomplete = new Autocomplete(isAutocompleteFastSearchIgnoreCase());
            autocomplete.setTransferFocusForward(false);
        } else {
            autocomplete = null;
            isAutocomplete = false;
        }

        listen();
    }

    private boolean isAutocompleteFastSearchIgnoreCase() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? storage.getBoolean(Storage.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : false;
    }

    private boolean getPersistedAutocomplete() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(Storage.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private JButton getSearchButton() {
        return GUI.getAppPanel().getButtonSearch();
    }

    private JComboBox getSearchComboBox() {
        return GUI.getAppPanel().getComboBoxFastSearch();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getSearchTextArea().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });
        getSearchButton().addActionListener(this);
        getSearchComboBox().addActionListener(this);
    }

    public void setAutocomplete(boolean ac) {
        if (autocomplete == null) {
            return;
        }

        isAutocomplete = ac;

        if (ac) {
            decorateTextFieldSearch();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        JComboBox cb = getSearchComboBox();

        if (isAutocomplete && (evt.getSource() == cb) && (cb.getSelectedIndex() >= 0)) {
            decorateTextFieldSearch();
        } else if (evt.getSource() == getSearchButton()) {
            search();
        }
    }

    private void decorateTextFieldSearch() {
        if (autocomplete == null || !getPersistedAutocomplete()) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                autocomplete.decorate(GUI.getSearchTextArea(), isSearchAllDefinedMetaDataValues()
                        ? AutoCompleteDataOfMetaDataValue.INSTANCE.getFastSearchData().get()
                        : AutoCompleteDataOfMetaDataValue.INSTANCE.get(getSearchMetaDataValue()).get(), true);
            }
        }, "JPhotoTagger: Updating autocomplete for search text field").start();
    }

    private void clearSelection() {
        TreeUtil.clearSelection(GUI.getAppPanel().getSelectionTrees());
        ListUtil.clearSelection(GUI.getAppPanel().getSelectionLists());
    }

    private void search() {
        search(GUI.getSearchTextArea().getText());
        setMetadataEditable();
    }

    private void search(final String searchText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            private final ImageFilesRepository imageFileRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);

            @Override
            public void run() {
                String userInput = searchText.trim();

                if (!userInput.isEmpty()) {
                    WaitDisplay.show();
                    clearSelection();

                    List<File> imageFiles = searchFiles(userInput);

                    if (imageFiles != null) {
                        setTitle(userInput);
                        GUI.getAppFrame().selectMenuItemUnsorted();
                        ControllerSortThumbnails.setLastSort();
                        GUI.getThumbnailsPanel().setFiles(imageFiles, TypeOfDisplayedImages.SAVED_SEARCH);
                    }

                    WaitDisplay.hide();
                }
            }

            private void setTitle(String userInput) {
                String title = Bundle.getString(ControllerFastSearch.class, "ControllerFastSearch.AppFrame.Title.FastSearch", userInput);
                GUI.getAppFrame().setTitle(title);
            }

            private List<File> searchFiles(String userInput) {
                if (isSearchAllDefinedMetaDataValues()) {
                    return findRepo.findImageFilesLikeOr(FastSearchMetaDataValues.get(), userInput);
                } else {
                    List<String> searchWords = getSearchWords(userInput);
                    MetaDataValue searchValue = getSearchMetaDataValue();

                    if (searchValue == null) {
                        return null;
                    }

                    boolean isKeywordSearch = searchValue.equals(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);

                    if (searchWords.size() == 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(imageFileRepo.findImageFilesContainingDcSubject(searchWords.get(0), true));
                        } else {
                            return findRepo.findImageFilesLikeOr(Arrays.asList(searchValue), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(imageFileRepo.findImageFilesContainingAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<File>(imageFileRepo.findImageFilesContainingAllWordsInMetaDataValue(searchWords, searchValue));
                        }
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    private List<String> getSearchWords(String userInput) {
        List<String> words = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(userInput, DELIMITER_SEARCH_WORDS);

        while (st.hasMoreTokens()) {
            words.add(st.nextToken().trim());
        }

        return words;
    }

    private MetaDataValue getSearchMetaDataValue() {
        assert !isSearchAllDefinedMetaDataValues() : "More than one search value!";

        if (isSearchAllDefinedMetaDataValues()) {
            return null;
        }

        return (MetaDataValue) getSearchComboBox().getSelectedItem();
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getSearchTextArea().isEnabled()) {
            TypeOfDisplayedImages typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (TypeOfDisplayedImages.FAST_SEARCH.equals(typeOfDisplayedImages)) {
                search(GUI.getSearchTextArea().getText());
            }
        }
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isAFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }

    private boolean isSearchAllDefinedMetaDataValues() {
        Object selItem = getSearchComboBox().getSelectedItem();

        return (selItem != null) && selItem.equals(ComboBoxModelFastSearch.ALL_DEFINED_META_DATA_VALUES);
    }

    private void addAutocompleteWordsOf(Xmp xmp) {
        if (!getPersistedAutocomplete()) {
            return;
        }

        if (isSearchAllDefinedMetaDataValues()) {
            AutocompleteHelper.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteHelper.addAutocompleteData(getSearchMetaDataValue(), autocomplete, xmp);
        }
    }

    private boolean isAutocomplete() {
        return autocomplete != null && getPersistedAutocomplete();
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        addAutocompleteWordsOf(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        addAutocompleteWordsOf(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        addAutocompleteWordsOf(evt.getUpdatedXmp());
    }
}
