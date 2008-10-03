package de.elmar_baumann.imagemetadataviewer.controller.misc;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.UserSettingsChangeEvent;
import de.elmar_baumann.imagemetadataviewer.event.UserSettingsChangeListener;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * Kontrolliert die Aktion: Schnellsuche durchführen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/11
 */
public class ControllerFastSearch extends Controller implements UserSettingsChangeListener {

    private Database db = Database.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JTextField textFieldSearch = appPanel.getTextFieldSearch();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelImageFileThumbnails();
    private Vector<JTree> selectionTrees = appPanel.getSelectionTrees();

    public ControllerFastSearch() {
        textFieldSearch.setEnabled(UserSettings.getInstance().getFastSearchColumns().size() > 0);
        listenToActionSources();
    }

    private void listenToActionSources() {
        UserSettingsDialog.getInstance().addChangeListener(this);

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
        Vector<String> filenames =
            db.searchFilenamesLikeOr(UserSettings.getInstance().getFastSearchColumns(), searchText);
        thumbnailsPanel.setFilenames(filenames);
        PopupMenuPanelThumbnails.getInstance().setIsImageCollection(false);
    }

    @Override
    public void applySettings(UserSettingsChangeEvent evt) {
        if (evt.getChanged().equals(UserSettingsChangeEvent.Changed.fastSearchColumnDefined)) {
            textFieldSearch.setEnabled(true);
        } else if (evt.getChanged().equals(UserSettingsChangeEvent.Changed.noFastSearchColumns)) {
            textFieldSearch.setEnabled(false);
        }
    }
}
