/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.controller.search;

import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.database.DatabaseFind;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections
    .AutoCompleteDataOfColumn;
import de.elmar_baumann.jpt.database.metadata.selections.FastSearchColumns;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.event.RefreshEvent;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.helper.AutocompleteHelper;
import de.elmar_baumann.jpt.model.ComboBoxModelFastSearch;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanels;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.Autocomplete;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class ControllerFastSearch
        implements ActionListener, UserSettingsListener, RefreshListener,
                   DatabaseImageFilesListener {
    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final DatabaseFind  db                     = DatabaseFind.INSTANCE;
    private final AppPanel      appPanel               =
        GUI.INSTANCE.getAppPanel();
    private final JTextArea     textFieldSearch        =
        appPanel.getTextAreaSearch();
    private final JComboBox     comboboxFastSearch     =
        appPanel.getComboBoxFastSearch();
    private final ThumbnailsPanel thumbnailsPanel =
        appPanel.getPanelThumbnails();
    private final List<JTree>        selectionTrees =
        appPanel.getSelectionTrees();
    private final List<JList>        selectionLists =
        appPanel.getSelectionLists();
    private final EditMetadataPanels editPanels     =
        appPanel.getEditMetadataPanels();
    private boolean            isAutocomplete;
    private final Autocomplete autocomplete = new Autocomplete();

    public ControllerFastSearch() {
        autocomplete.setTransferFocusForward(false);
        listen();
    }

    private void listen() {
        UserSettings.INSTANCE.addUserSettingsListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
        textFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });
        appPanel.getButtonSearch().addActionListener(this);
        comboboxFastSearch.addActionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FAST_SEARCH);
    }

    public void setAutocomplete(boolean autocomplete) {
        isAutocomplete = autocomplete;

        if (autocomplete) {
            decorateTextFieldSearch();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isAutocomplete && (e.getSource() == comboboxFastSearch)
                && (comboboxFastSearch.getSelectedIndex() >= 0)) {
            decorateTextFieldSearch();
        } else if (e.getSource() == appPanel.getButtonSearch()) {
            search();
        }
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.FAST_SEARCH_COLUMNS)
                || evt.getType().equals(
                    UserSettingsEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            if (isSearchAllDefinedColumns()) {
                textFieldSearch.setEnabled(
                    evt.getType().equals(
                        UserSettingsEvent.Type.FAST_SEARCH_COLUMNS));
            }
        }
    }

    private void decorateTextFieldSearch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                autocomplete.decorate(textFieldSearch,
                                      isSearchAllDefinedColumns()
                                      ? AutoCompleteDataOfColumn.INSTANCE
                                          .getFastSearchData().get()
                                      : AutoCompleteDataOfColumn.INSTANCE.get(
                                          getSearchColumn()).get());
            }
        }).start();
    }

    private void clearSelection() {
        TreeUtil.clearSelection(selectionTrees);
        ListUtil.clearSelection(selectionLists);
    }

    private void search() {
        search(textFieldSearch.getText());
        setMetadataEditable();
    }

    private void search(final String searchText) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String userInput = searchText.trim();

                if (!userInput.isEmpty()) {
                    clearSelection();

                    List<String> filenames = searchFilenames(userInput);

                    if (filenames != null) {
                        setTitle(userInput);
                        GUI.INSTANCE.getAppFrame().selectMenuItemUnsorted();
                        ControllerSortThumbnails.setLastSort();
                        thumbnailsPanel.setFiles(
                            FileUtil.getAsFiles(filenames),
                            Content.SAVED_SEARCH);
                    }
                }
            }
            private void setTitle(String userInput) {
                GUI.INSTANCE.getAppFrame().setTitle(
                    JptBundle.INSTANCE.getString(
                        "ControllerFastSearch.AppFrame.Title.FastSearch",
                        userInput));
            }
            private List<String> searchFilenames(String userInput) {
                if (isSearchAllDefinedColumns()) {
                    return db.findFilenamesLikeOr(FastSearchColumns.get(),
                                                  userInput);
                } else {
                    List<String> searchWords  = getSearchWords(userInput);
                    Column       searchColumn = getSearchColumn();

                    if (searchColumn == null) {
                        return null;
                    }

                    boolean isKeywordSearch =
                        searchColumn.equals(
                            ColumnXmpDcSubjectsSubject.INSTANCE);

                    if (searchWords.size() == 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<String>(DatabaseImageFiles
                                .INSTANCE
                                .getFilenamesOfDcSubject(searchWords
                                    .get(0), DatabaseImageFiles.DcSubjectOption
                                    .INCLUDE_SYNONYMS));
                        } else {
                            return db.findFilenamesLikeOr(
                                Arrays.asList(searchColumn), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<String>(DatabaseImageFiles
                                .INSTANCE
                                .getFilenamesOfAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<String>(
                                DatabaseImageFiles.INSTANCE.getFilenamesOfAll(
                                    searchColumn, searchWords));
                        }
                    } else {
                        return null;
                    }
                }
            }
        });
    }

    private List<String> getSearchWords(String userInput) {
        List<String>    words = new ArrayList<String>();
        StringTokenizer st    = new StringTokenizer(userInput,
                                    DELIMITER_SEARCH_WORDS);

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

        return (Column) comboboxFastSearch.getSelectedItem();
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (textFieldSearch.isEnabled()) {
            search(textFieldSearch.getText());
        }
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }

    private boolean isSearchAllDefinedColumns() {
        Object selItem = comboboxFastSearch.getSelectedItem();

        return (selItem != null)
               && selItem.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS);
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (event.getType().equals(
                DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED)) {
            return;
        }

        ImageFile imageFile = event.getImageFile();

        if ((imageFile == null) || (imageFile.getXmp() == null)) {
            return;
        }

        if (isSearchAllDefinedColumns()) {
            AutocompleteHelper.addFastSearchAutocompleteData(autocomplete,
                    imageFile.getXmp());
        } else {
            AutocompleteHelper.addAutocompleteData(getSearchColumn(),
                    autocomplete, imageFile.getXmp());
        }
    }
}
