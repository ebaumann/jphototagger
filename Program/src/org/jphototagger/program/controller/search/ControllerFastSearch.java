/*
 * @(#)ControllerFastSearch.java    Created on 2008-10-05
 *
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

package org.jphototagger.program.controller.search;

import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections
    .AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;

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
 */
public final class ControllerFastSearch
        implements ActionListener, RefreshListener, DatabaseImageFilesListener {
    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final DatabaseFind  db                     = DatabaseFind.INSTANCE;
    private final AppPanel      appPanel               =
        GUI.INSTANCE.getAppPanel();
    private final JTextArea     textFieldSearch        =
        appPanel.getTextAreaSearch();
    private final JComboBox     comboboxFastSearch     =
        appPanel.getComboBoxFastSearch();
    private final ThumbnailsPanel    tnPanel        =
        appPanel.getPanelThumbnails();
    private final List<JTree>        selectionTrees =
        appPanel.getSelectionTrees();
    private final List<JList>        selectionLists =
        appPanel.getSelectionLists();
    private final EditMetadataPanels editPanels     =
        appPanel.getEditMetadataPanels();
    private final Autocomplete autocomplete;
    private boolean            isAutocomplete;

    public ControllerFastSearch() {
        if (UserSettings.INSTANCE.isAutocomplete()) {
            autocomplete = new Autocomplete();
            autocomplete.setTransferFocusForward(false);
        } else {
            autocomplete   = null;
            isAutocomplete = false;
        }

        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
        textFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });
        appPanel.getButtonSearch().addActionListener(this);
        comboboxFastSearch.addActionListener(this);
        tnPanel.addRefreshListener(this, Content.FAST_SEARCH);
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
        if (isAutocomplete && (evt.getSource() == comboboxFastSearch)
                && (comboboxFastSearch.getSelectedIndex() >= 0)) {
            decorateTextFieldSearch();
        } else if (evt.getSource() == appPanel.getButtonSearch()) {
            search();
        }
    }

    private void decorateTextFieldSearch() {
        if ((autocomplete == null) ||!UserSettings.INSTANCE.isAutocomplete()) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                autocomplete.decorate(textFieldSearch,
                                      isSearchAllDefinedColumns()
                                      ? AutoCompleteDataOfColumn.INSTANCE
                                          .getFastSearchData().get()
                                      : AutoCompleteDataOfColumn.INSTANCE.get(
                                          getSearchColumn()).get(), true);
            }
        }, "JPhotoTagger: Updating autocomplete for search text field").start();
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

                    List<File> imageFiles = searchFiles(userInput);

                    if (imageFiles != null) {
                        setTitle(userInput);
                        GUI.INSTANCE.getAppFrame().selectMenuItemUnsorted();
                        ControllerSortThumbnails.setLastSort();
                        tnPanel.setFiles(imageFiles, Content.SAVED_SEARCH);
                    }
                }
            }
            private void setTitle(String userInput) {
                GUI.INSTANCE.getAppFrame().setTitle(
                    JptBundle.INSTANCE.getString(
                        "ControllerFastSearch.AppFrame.Title.FastSearch",
                        userInput));
            }
            private List<File> searchFiles(String userInput) {
                if (isSearchAllDefinedColumns()) {
                    return db.findImageFilesLikeOr(FastSearchColumns.get(),
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
                            return new ArrayList<File>(DatabaseImageFiles
                                .INSTANCE
                                .getImageFilesOfDcSubject(searchWords
                                    .get(0), DatabaseImageFiles.DcSubjectOption
                                    .INCLUDE_SYNONYMS));
                        } else {
                            return db.findImageFilesLikeOr(
                                Arrays.asList(searchColumn), userInput);
                        }
                    } else if (searchWords.size() > 1) {
                        if (isKeywordSearch) {
                            return new ArrayList<File>(DatabaseImageFiles
                                .INSTANCE
                                .getImageFilesOfAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<File>(
                                DatabaseImageFiles.INSTANCE.getImageFilesOfAll(
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
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (textFieldSearch.isEnabled()) {
            search(textFieldSearch.getText());
        }
    }

    private void setMetadataEditable() {
        if (!tnPanel.isFileSelected()) {
            editPanels.setEditable(false);
        }
    }

    private boolean isSearchAllDefinedColumns() {
        Object selItem = comboboxFastSearch.getSelectedItem();

        return (selItem != null)
               && selItem.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS);
    }

    private void addAutocompleteWordsOf(Xmp xmp) {
        if (!isAutocomplete()) {
            return;
        }

        if (isSearchAllDefinedColumns()) {
            AutocompleteHelper.addFastSearchAutocompleteData(autocomplete, xmp);
        } else {
            AutocompleteHelper.addAutocompleteData(getSearchColumn(),
                    autocomplete, xmp);
        }
    }

    private boolean isAutocomplete() {
        return (autocomplete != null) && UserSettings.INSTANCE.isAutocomplete();
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        addAutocompleteWordsOf(xmp);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        addAutocompleteWordsOf(xmp);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        addAutocompleteWordsOf(updatedXmp);
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }
}
