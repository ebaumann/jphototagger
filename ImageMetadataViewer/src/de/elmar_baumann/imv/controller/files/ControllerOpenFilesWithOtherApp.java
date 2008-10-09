package de.elmar_baumann.imv.controller.files;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class ControllerOpenFilesWithOtherApp extends Controller
    implements ActionListener {

    private PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerOpenFilesWithOtherApp() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            openFilesWithApp(popup.getOtherOpenImageApp(e.getActionCommand()));
        }
    }

    private void openFilesWithApp(File otherOpenImageApp) {
        String allFilenames = IoUtil.getArgsAsCommandline(
            popup.getThumbnailsPanel().getSelectedFilenames());
        if (!allFilenames.isEmpty()) {
            IoUtil.startApplication(otherOpenImageApp.getAbsolutePath(), allFilenames);
        }
    }
}
