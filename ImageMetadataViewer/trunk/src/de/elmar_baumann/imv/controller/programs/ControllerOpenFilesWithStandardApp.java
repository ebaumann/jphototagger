package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller für die Aktion: Dateien ausgewählter THUMBNAILS öffnen,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerOpenFilesWithStandardApp implements ActionListener {

    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerOpenFilesWithStandardApp() {
        listen();
    }

    private void listen() {
        popupMenu.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (checkOpenAppIsDefined()) {
            openFiles();
        }
    }

    private void openFiles() {
        if (thumbnailsPanel.getSelectionCount() < 1) return;
        String allFilenames =
                IoUtil.quoteForCommandLine(thumbnailsPanel.getSelectedFiles());
        IoUtil.execute(
                UserSettings.INSTANCE.getDefaultImageOpenApp(), allFilenames);
    }

    private boolean checkOpenAppIsDefined() {
        if (UserSettings.INSTANCE.getDefaultImageOpenApp().isEmpty()) {
            UserSettingsDialog dialog = UserSettingsDialog.INSTANCE;
            dialog.selectTab(UserSettingsDialog.Tab.PROGRAMS);
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
