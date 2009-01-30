package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller für die Aktion: Dateien ausgewählter Thumbnails öffnen,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerOpenFilesWithStandardApp extends Controller
    implements ActionListener {

    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerOpenFilesWithStandardApp() {
        popup.addActionListenerOpenFilesWithStandardApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            if (checkOpenAppIsDefined()) {
                openFiles();
            }
        }
    }

    private void openFiles() {
        String allFilenames = IoUtil.getQuotedForCommandline(
            thumbnailsPanel.getSelectedFiles(), "\"");
        if (!allFilenames.isEmpty()) {
            IoUtil.execute(
                UserSettings.getInstance().getDefaultImageOpenApp(), allFilenames);
        }
    }

    private boolean checkOpenAppIsDefined() {
        if (UserSettings.getInstance().getDefaultImageOpenApp().isEmpty()) {
            UserSettingsDialog dialog = UserSettingsDialog.getInstance();
            dialog.selectTab(UserSettingsDialog.Tab.Programs);
            if (dialog.isVisible()) {
                dialog.toFront();
            } else {
                dialog.setVisible(true);
            }
            return false;
        }
        return true;
    }
}
