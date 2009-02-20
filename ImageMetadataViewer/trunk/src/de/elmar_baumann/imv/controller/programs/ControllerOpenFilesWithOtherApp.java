package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.tasks.ProgramStarter;
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
public final class ControllerOpenFilesWithOtherApp implements ActionListener {

    private final PopupMenuPanelThumbnails popupMenu;
    private final ImageFileThumbnailsPanel thumbnailsPanel;
    private final ProgramStarter executor;

    public ControllerOpenFilesWithOtherApp() {
        popupMenu = PopupMenuPanelThumbnails.getInstance();
        listen();
        thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        executor = new ProgramStarter(null);
    }

    private void listen() {
        popupMenu.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        openFiles(popupMenu.getProgram(e.getSource()));
    }

    private void openFiles(Program program) {
        executor.startProgram(program, thumbnailsPanel.getSelectedFiles());
    }
}
