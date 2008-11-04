package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerOpenFilesWithOtherApp() {
        popup.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            openFiles(popup.getProgram(e.getSource()));
        }
    }

    private void openFiles(Program program) {
        String allFilenames = IoUtil.getArgsAsCommandline(
            FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles()));
        String parameters = program.getParameters() == null
            ? ""
            : program.getParameters() + " ";
        String filename = program.getFile().getAbsolutePath();
        if (!allFilenames.isEmpty()) {
            IoUtil.execute(filename, parameters + allFilenames);
        }
    }
}
