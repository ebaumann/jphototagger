package org.jphototagger.program.controller.programs;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ControllerOpenFilesWithOtherApp implements ActionListener {
    private final StartPrograms programStarter = new StartPrograms(null);

    public ControllerOpenFilesWithOtherApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openFiles(PopupMenuThumbnails.INSTANCE.getProgram(evt.getSource()));
    }

    private void openFiles(Program program) {
        programStarter.startProgram(program, GUI.getSelectedImageFiles());
    }
}
