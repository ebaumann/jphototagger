package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class OpenFilesWithOtherAppController implements ActionListener {

    private final StartPrograms programStarter = new StartPrograms();

    public OpenFilesWithOtherAppController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.addActionListenerOpenFilesWithOtherApp(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openFiles(ThumbnailsPopupMenu.INSTANCE.getProgram(evt.getSource()));
    }

    private void openFiles(Program program) {
        if (program != null) {
            programStarter.startProgram(program, GUI.getSelectedImageFiles(), false);
        }
    }
}
