package org.jphototagger.program.module.search;

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
import javax.swing.JTextArea;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.MainWindowManager;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.FindRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.Autocomplete;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.misc.AutocompleteUtil;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author Elmar Baumann
 */
public final class FastSearchController implements ActionListener {

    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final Autocomplete autocomplete;
    private boolean isAutocomplete;
    private final FindRepository findRepo = Lookup.getDefault().lookup(FindRepository.class);

    public FastSearchController() {
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
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : false;
    }

    private boolean getPersistedAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
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
        JTextArea searchTextArea = GUI.getSearchTextArea();
        searchTextArea.addKeyListener(new KeyAdapter() {

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
    }

    private void search(final String searchText) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            private final ImageFilesRepository imageFileRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);

            @Override
            public void run() {
                String userInput = searchText.trim();

                if (!userInput.isEmpty()) {
                    WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                    waitDisplayer.show();
                    clearSelection();

                    List<File> imageFiles = searchFiles(userInput);

                    if (imageFiles != null) {
                        setTitle(userInput);
                        GUI.getThumbnailsPanel().setFiles(imageFiles, OriginOfDisplayedThumbnails.FILES_FOUND_BY_FAST_SEARCH);
                    }
                    waitDisplayer.hide();
                }
            }

            private void setTitle(String userInput) {
                String title = Bundle.getString(FastSearchController.class, "FastSearchController.AppFrame.Title.FastSearch", userInput);
                MainWindowManager mainWindowManager = Lookup.getDefault().lookup(MainWindowManager.class);
                mainWindowManager.setMainWindowTitle(title);
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
        JTextArea searchTextArea = GUI.getSearchTextArea();
        if (searchTextArea.isEnabled()) {
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_FOUND_BY_FAST_SEARCH.equals(typeOfDisplayedImages)) {
                search(GUI.getSearchTextArea().getText());
            }
        }
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(ThumbnailsChangedEvent evt) {
        OriginOfDisplayedThumbnails originOfDisplayedThumbnails = evt.getOriginOfDisplayedThumbnails();
        if (!OriginOfDisplayedThumbnails.FILES_FOUND_BY_FAST_SEARCH.equals(originOfDisplayedThumbnails)) {
            JTextArea searchTextArea = GUI.getSearchTextArea();
            searchTextArea.setText("");
        }
    }

    private boolean isSearchAllDefinedMetaDataValues() {
        Object selItem = getSearchComboBox().getSelectedItem();

        return (selItem != null) && selItem.equals(FastSearchComboBoxModel.ALL_DEFINED_META_DATA_VALUES);
    }

    private void addAutocompleteWordsOf(Xmp xmp) {
        if (!getPersistedAutocomplete()) {
            return;
        }

        if (isSearchAllDefinedMetaDataValues()) {
            AutocompleteUtil.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteUtil.addAutocompleteData(getSearchMetaDataValue(), autocomplete, xmp);
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
