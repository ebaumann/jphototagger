package org.jphototagger.program.module.programs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class OpenFilesWithStandardAppController implements ActionListener {

    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    public OpenFilesWithStandardAppController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        openSelectedImages();
    }

    private void openSelectedImages() {
        Program program = repo.findDefaultImageOpenProgram();

        if (program == null) {
            String message = Bundle.getString(OpenFilesWithOtherAppController.class, "OpenFilesWithStandardAppController.Info.DefineOpenApp");
            MessageDisplayer.information(null, message);
            ProgramsUtil.openSelectedFilesWidth(ProgramsUtil.addProgram(), false);
        } else {
            StartPrograms startPrograms = new StartPrograms();
            List<File> selectedImageFiles = GUI.getSelectedImageFiles();
            boolean waitForTermination = false;

            startPrograms.startProgram(program, selectedImageFiles, waitForTermination);
        }
    }
}
