package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCopyFilesToDirectory extends Controller
    implements ActionListener {

    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ImageFileThumbnailsPanel panel = appPanel.getPanelThumbnails();

    public ControllerCopyFilesToDirectory() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerCopySelectedFilesToDirectory(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            copySelectedFiles();
        }
    }

    private void copySelectedFiles() {
        List<File> files = panel.getSelectedFiles();
        if (files.size() > 0) {
            CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.setVisible(true);
        }
    }
}
