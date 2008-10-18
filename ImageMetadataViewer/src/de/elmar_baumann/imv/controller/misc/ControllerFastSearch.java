package de.elmar_baumann.imv.controller.misc;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.AutoCompleteData;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.data.XmpUtil;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.UserSettingsChangeListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JTree;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerFastSearch extends Controller
    implements UserSettingsChangeListener, DatabaseListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private List<Column> fastSearchColumns = UserSettings.getInstance().getFastSearchColumns();
    private List<JTree> selectionTrees = appPanel.getSelectionTrees();
    private boolean isUseAutocomplete = UserSettings.getInstance().isUseAutocomplete();
    private AutoCompleteData searchAutoCompleteData;

    public ControllerFastSearch() {
        textFieldSearch.setEnabled(UserSettings.getInstance().getFastSearchColumns().size() > 0);
        setAutocomplete();
        listenToActionSources();
    }

    private void setAutocomplete() {
        if (UserSettings.getInstance().isUseAutocomplete()) {
            searchAutoCompleteData = new AutoCompleteData();
            AutoCompleteDecorator.decorate(
                textFieldSearch,
                searchAutoCompleteData.getList(),
                false);
        }
    }

    private void listenToActionSources() {
        ListenerProvider.getInstance().addUserSettingsChangeListener(this);

        textFieldSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (isStarted() && e.getKeyCode() == KeyEvent.VK_ENTER) {
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
    }

    private void checkEnabled() {
        if (!textFieldSearch.isEnabled()) {
            UserSettingsDialog settingsDialog = UserSettingsDialog.getInstance();
            settingsDialog.selectTab(UserSettingsDialog.Tab.FastSearch);
            if (settingsDialog.isVisible()) {
                settingsDialog.toFront();
            } else {
                settingsDialog.setVisible(true);
            }
        }
    }

    private void search() {
        search(textFieldSearch.getText());
    }

    private void search(String searchText) {
        TreeUtil.clearSelection(selectionTrees);
        List<String> filenames =
            db.searchFilenamesLikeOr(UserSettings.getInstance().getFastSearchColumns(), searchText);
        thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames),
            ImageFileThumbnailsPanel.Content.Search);
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getType().equals(UserSettingsChangeEvent.Type.FastSearchColumnDefined)) {
            textFieldSearch.setEnabled(true);
        } else if (evt.getType().equals(UserSettingsChangeEvent.Type.NoFastSearchColumns)) {
            textFieldSearch.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (isStarted() && isUseAutocomplete && action.isImageModified()) {
            ImageFile data = action.getImageFileData();
            if (data != null && data.getXmp() != null) {
                addAutoCompleteData(data.getXmp());
            }
        }
    }

    private void addAutoCompleteData(Xmp xmp) {
        for (Column column : fastSearchColumns) {
            XmpUtil.addData(xmp, column, searchAutoCompleteData);
        }
    }
}
