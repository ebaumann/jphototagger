package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.AutoCompleteData;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.data.AutoCompleteUtil;
import de.elmar_baumann.imv.database.DatabaseSearch;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.RefreshListener;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTree;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerFastSearch
        implements UserSettingsChangeListener, DatabaseListener, RefreshListener {

    private final DatabaseSearch db = DatabaseSearch.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private final ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private final List<Column> fastSearchColumns = UserSettings.getInstance().getFastSearchColumns();
    private final List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private final List<JList> selectionLists = appPanel.getSelectionLists();
    private boolean isUseAutocomplete = UserSettings.getInstance().isUseAutocomplete();
    private AutoCompleteData searchAutoCompleteData;
    private final EditMetadataPanelsArray editPanels = appPanel.getEditPanelsArray();

    public ControllerFastSearch() {
        textFieldSearch.setEnabled(UserSettings.getInstance().getFastSearchColumns().size() > 0);
        decorateTextFieldSearch();
        listenToActionSources();
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.FAST_SEARCH_COLUMNS)) {
            textFieldSearch.setEnabled(true);
        } else if (evt.getType().equals(UserSettingsChangeEvent.Type.NO_FAST_SEARCH_COLUMNS)) {
            textFieldSearch.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (isUseAutocomplete && action.isImageModified()) {
            ImageFile data = action.getImageFileData();
            if (data != null && data.getXmp() != null) {
                addAutoCompleteData(data.getXmp());
            }
        }
    }

    private void addAutoCompleteData(Xmp xmp) {
        for (Column column : fastSearchColumns) {
            AutoCompleteUtil.addData(xmp, column, searchAutoCompleteData);
        }
    }

    private void decorateTextFieldSearch() {
        if (UserSettings.getInstance().isUseAutocomplete()) {
            searchAutoCompleteData = new AutoCompleteData();
            AutoCompleteDecorator.decorate(
                textFieldSearch,
                searchAutoCompleteData.getList(),
                false);
        }
    }

    private void checkEnabled() {
        if (!textFieldSearch.isEnabled()) {
            UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
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
        checkEditPanel();
    }

    private void search(String searchText) {
        if (!searchText.trim().isEmpty()) {
            clearSelection();
            List<String> filenames =
                db.searchFilenamesLikeOr(UserSettings.getInstance().getFastSearchColumns(), searchText.trim());
            thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
                Content.SAFED_SEARCH);
        }
    }

    private void listenToActionSources() {
        ListenerProvider.getInstance().addUserSettingsChangeListener(this);

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

        db.addDatabaseListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.FAST_SEARCH);
    }

    @Override
    public void refresh() {
        if (textFieldSearch.isEnabled()) {
            search(textFieldSearch.getText());
        }
    }

    private void checkEditPanel() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
