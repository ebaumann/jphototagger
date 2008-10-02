package de.elmar_baumann.imagemetadataviewer.controller.files;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class ControllerCopyFilesToDirectory extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel panel = appPanel.getPanelImageFileThumbnails();

    public ControllerCopyFilesToDirectory() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerCopySelectedFilesToDirectory(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            Vector<String> files = panel.getSelectedFilenames();
            if (files.size() > 0) {
                CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
                dialog.setSourceFiles(files);
                dialog.setVisible(true);
            }
        }
    }
}
