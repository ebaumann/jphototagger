package de.elmar_baumann.imv.controller.programs;

import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.StartPrograms;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerOpenFilesWithOtherApp implements ActionListener {

    private final PopupMenuThumbnails popupMenu;
    private final ThumbnailsPanel thumbnailsPanel;
    private final StartPrograms executor;

    public ControllerOpenFilesWithOtherApp() {
        popupMenu = PopupMenuThumbnails.INSTANCE;
        listen();
        thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
        executor = new StartPrograms(null);
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
