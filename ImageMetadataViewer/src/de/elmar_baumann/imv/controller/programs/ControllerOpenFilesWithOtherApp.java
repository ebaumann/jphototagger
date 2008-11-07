package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.ProgramExecutor;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
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

    private PopupMenuPanelThumbnails popup;
    private ImageFileThumbnailsPanel thumbnailsPanel;
    private ProgramExecutor executor;

    public ControllerOpenFilesWithOtherApp() {
        popup = PopupMenuPanelThumbnails.getInstance();
        popup.addActionListenerOpenFilesWithOtherApp(this);
        thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        executor = new ProgramExecutor(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            openFiles(popup.getProgram(e.getSource()));
        }
    }

    private void openFiles(Program program) {
        executor.execute(program, thumbnailsPanel.getSelectedFiles());
    }
}
