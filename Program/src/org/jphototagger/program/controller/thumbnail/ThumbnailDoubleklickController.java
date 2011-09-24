package org.jphototagger.program.controller.thumbnail;

import java.io.File;
import java.util.Arrays;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.ProgramsHelper;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 * Kontroller für die Aktion: Doppelklick auf ein Thumbnail ausgelöst von
 * {@code org.jphototagger.program.view.panels.ThumbnailsPanel}.
 *
 * @author Elmar Baumann
 */
public final class ThumbnailDoubleklickController {

    private final ThumbnailsPanel panel;

    public ThumbnailDoubleklickController(ThumbnailsPanel panel) {
        if (panel == null) {
            throw new NullPointerException("panel == null");
        }

        this.panel = panel;
    }

    public void doubleClickAtIndex(int index) {
        openImageAtIndex(index);
    }

    private void openImageAtIndex(int index) {
        if (panel.isIndex(index)) {
            ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);
            Program program = repo.findDefaultImageOpenProgram();

            if (program == null) {
                String message = Bundle.getString(ThumbnailDoubleklickController.class, "ControllerOpenFilesWithStandardApp.Info.DefineOpenApp");

                // Reusing bundle string
                MessageDisplayer.information(null, message);
                ProgramsHelper.openSelectedFilesWidth(ProgramsHelper.addProgram(), false);
            } else {
                StartPrograms startPrograms = new StartPrograms();
                File selectedFile = panel.getFileAtIndex(index);
                boolean waitForTermination = false;

                startPrograms.startProgram(program, Arrays.asList(selectedFile), waitForTermination);
            }
        }
    }
}
