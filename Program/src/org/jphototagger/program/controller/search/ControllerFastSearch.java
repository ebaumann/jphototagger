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
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.repository.event.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.selections.AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.view.WaitDisplay;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author Elmar Baumann
 */
public final class ControllerFastSearch implements ActionListener, RefreshListener {

    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final Autocomplete autocomplete;
    private boolean isAutocomplete;

    public ControllerFastSearch() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            autocomplete = new Autocomplete(UserSettings.INSTANCE.isAutocompleteFastSearchIgnoreCase());
            autocomplete.setTransferFocusForward(false);
        } else {
            autocomplete = null;
            isAutocomplete = false;
        }

        listen();
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
        GUI.getThumbnailsPanel().addRefreshListener(this, TypeOfDisplayedImages.FAST_SEARCH);
    }

    public void setAutocomplete(boolean ac) {
        assert UserSettings.INSTANCE.isAutocomplete();

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
        if ((autocomplete == null) || !UserSettings.INSTANCE.isAutocomplete()) {
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
                            return new ArrayList<File>(
                                    DatabaseImageFiles.INSTANCE.getImageFilesOfDcSubject(
                                    searchWords.get(0), DatabaseImageFiles.DcSubjectOption.INCLUDE_SYNONYMS));
                        } else {
                            return DatabaseFind.INSTANCE.findImageFilesLikeOr(Arrays.asList(searchColumn), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(
                                    DatabaseImageFiles.INSTANCE.getImageFilesOfAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<File>(DatabaseImageFiles.INSTANCE.getImageFilesOfAll(searchColumn,
                                    searchWords));
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

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getSearchTextArea().isEnabled()) {
            search(GUI.getSearchTextArea().getText());
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
        if (!isAutocomplete()) {
            return;
        }

        if (isSearchAllDefinedColumns()) {
            AutocompleteHelper.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteHelper.addAutocompleteData(getSearchColumn(), autocomplete, xmp);
        }
    }

    private boolean isAutocomplete() {
        return (autocomplete != null) && UserSettings.INSTANCE.isAutocomplete();
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
