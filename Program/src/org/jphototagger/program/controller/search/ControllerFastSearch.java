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
import org.jphototagger.api.core.Storage;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.repository.ImageFileRepository;
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
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.database.metadata.selections.AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;
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
                autocomplete.decorate(GUI.getSearchTextArea(), isSearchAllDefinedColumns()
                        ? AutoCompleteDataOfColumn.INSTANCE.getFastSearchData().get()
                        : AutoCompleteDataOfColumn.INSTANCE.get(getSearchColumn()).get(), true);
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

            private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

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
                if (isSearchAllDefinedColumns()) {
                    return DatabaseFind.INSTANCE.findImageFilesLikeOr(FastSearchColumns.get(), userInput);
                } else {
                    List<String> searchWords = getSearchWords(userInput);
                    Column searchColumn = getSearchColumn();

                    if (searchColumn == null) {
                        return null;
                    }

                    boolean isKeywordSearch = searchColumn.equals(ColumnXmpDcSubjectsSubject.INSTANCE);

                    if (searchWords.size() == 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(repo.getImageFilesContainingDcSubject(searchWords.get(0), true));
                        } else {
                            return DatabaseFind.INSTANCE.findImageFilesLikeOr(Arrays.asList(searchColumn), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(repo.getImageFilesContainingAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<File>(repo.getImageFilesContainingAllWordsInColumn(searchWords, searchColumn));
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

    private Column getSearchColumn() {
        assert !isSearchAllDefinedColumns() : "More than one search column!";

        if (isSearchAllDefinedColumns()) {
            return null;
        }

        return (Column) getSearchComboBox().getSelectedItem();
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

    private boolean isSearchAllDefinedColumns() {
        Object selItem = getSearchComboBox().getSelectedItem();

        return (selItem != null) && selItem.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS);
    }

    private void addAutocompleteWordsOf(Xmp xmp) {
        if (!getPersistedAutocomplete()) {
            return;
        }

        if (isSearchAllDefinedColumns()) {
            AutocompleteHelper.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteHelper.addAutocompleteData(getSearchColumn(), autocomplete, xmp);
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
