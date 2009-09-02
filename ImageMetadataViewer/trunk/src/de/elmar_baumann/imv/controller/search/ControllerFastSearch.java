package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.AutoCompleteDataOfColumn;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.DatabaseSearch;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.model.ComboBoxModelFastSearch;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerFastSearch
        implements
        ActionListener,
        DatabaseListener,
        UserSettingsChangeListener,
        RefreshListener {

    private static final String DELIMITER_SEARCH_WORDS = ";";
    private final DatabaseSearch db = DatabaseSearch.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private final JComboBox comboboxFastSearch =
            appPanel.getComboBoxFastSearch();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
    private final List<Column> fastSearchColumns =
            UserSettings.INSTANCE.getFastSearchColumns();
    private final List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private final List<JList> selectionLists = appPanel.getSelectionLists();
    private final EditMetadataPanelsArray editPanels =
            appPanel.getEditPanelsArray();

    public ControllerFastSearch() {
        setEnabledSearchTextField();
        decorateTextFieldSearch();
        listen();
    }

    private void listen() {
        ListenerProvider.INSTANCE.addUserSettingsChangeListener(this);

        textFieldSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });

        textFieldSearch.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                checkEnabled();
            }
        });

        comboboxFastSearch.addActionListener(this);

        db.addDatabaseListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FAST_SEARCH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comboboxFastSearch) {
            setEnabledSearchTextField();
            decorateTextFieldSearch();
        }
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(
                UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS) ||
                evt.getType().equals(
                UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            setEnabledSearchTextField();
        }
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            ImageFile imageFile = event.getImageFile();
            if (imageFile != null && imageFile.getXmp() != null) {
                addAutoCompleteData(imageFile.getXmp());
            }
        }
    }

    private void addAutoCompleteData(Xmp xmp) {
        for (Column column : fastSearchColumns) {
            AutoCompleteDataOfColumn.INSTANCE.addData(
                    column, xmp.getValue(column));
        }
    }

    private void decorateTextFieldSearch() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                textFieldSearch.setDocument(new PlainDocument()); // else the document seems to "collect" previous auto complete data
                AutoCompleteDecorator.decorate(
                        textFieldSearch,
                        isSearchAllDefinedColumns()
                        ? AutoCompleteDataOfColumn.INSTANCE.getFastSearchData().getData()
                        : AutoCompleteDataOfColumn.INSTANCE.get(getSearchColumn()).getData(),
                        false);
            }
        });
    }

    private void checkEnabled() {
        if (!textFieldSearch.isEnabled()) {
            UserSettingsDialog settingsDialog = UserSettingsDialog.INSTANCE;
            settingsDialog.selectTab(UserSettingsDialog.Tab.FAST_SEARCH);
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
        }
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
                        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
                                Content.SAVED_SEARCH);
                    }
                }
            }

            private List<String> searchFilenames(String userInput) {
                if (isSearchAllDefinedColumns()) {
                    return db.searchFilenamesLikeOr(
                            UserSettings.INSTANCE.getFastSearchColumns(),
                            userInput);
                } else {
                    List<String> searchWords = getSearchWords(userInput);
                    Column searchColumn = getSearchColumn();
                    if (searchColumn == null) return null;
                    if (searchWords.size() == 1) {
                        return db.searchFilenamesLikeOr(
                                Arrays.asList(searchColumn),
                                userInput);
                    } else if (searchWords.size() > 1) {
                        if (searchColumn.equals(
                                ColumnXmpDcSubjectsSubject.INSTANCE)) {
                            return new ArrayList<String>(DatabaseImageFiles.INSTANCE.
                                    getFilenamesOfAllDcSubjects(searchWords));
                        } else {
                            return new ArrayList<String>(DatabaseImageFiles.INSTANCE.
                                    getFilenamesOfAll(
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
        List<String> words = new ArrayList<String>();
        StringTokenizer st =
                new StringTokenizer(userInput, DELIMITER_SEARCH_WORDS);
        while (st.hasMoreTokens()) {
            words.add(st.nextToken().trim());
        }
        return words;
    }

    private Column getSearchColumn() {
        assert !isSearchAllDefinedColumns() : "More than one search column!"; // NOI18N
        if (isSearchAllDefinedColumns()) return null;
        ComboBoxModel model = comboboxFastSearch.getModel();
        assert model instanceof ComboBoxModelFastSearch :
                "Unknown model: " + model;
        if (model instanceof ComboBoxModelFastSearch) {
            return (Column) comboboxFastSearch.getSelectedItem();
        }
        return null;
    }

    @Override
    public void refresh() {
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
        return selItem != null &&
                selItem.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS);
    }

    private void setEnabledSearchTextField() {
        textFieldSearch.setEnabled(
                isSearchAllDefinedColumns()
                ? UserSettings.INSTANCE.getFastSearchColumns().size() > 0
                : true);
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
