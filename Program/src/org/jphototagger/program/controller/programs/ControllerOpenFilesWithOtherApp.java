package org.jphototagger.program.controller.programs;

import java.awt.event.MouseEvent;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

/**
 * Kontrolliert die Aktion: Öffne ausgewählte Thumbnails mit einer anderen
 * Anwendung, ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ControllerOpenFilesWithOtherApp extends MouseAdapter implements ActionListener {
    private final StartPrograms programStarter = new StartPrograms(null);

    public ControllerOpenFilesWithOtherApp() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.addActionListenerOpenFilesWithOtherApp(this);
        PopupMenuThumbnails.INSTANCE.getMenuPrograms().addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openFiles(PopupMenuThumbnails.INSTANCE.getProgram(evt.getSource()));
    }

    private void openFiles(Program program) {
        programStarter.startProgram(program, GUI.getSelectedImageFiles());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!ControllerOpenFilesWithStandardApp.isOpenAppDefined(false)) {
            PopupMenuThumbnails.INSTANCE.setVisible(false);
            ControllerOpenFilesWithStandardApp.isOpenAppDefined(true); // Displaying settings dialog to add a program
        }
    }

}
