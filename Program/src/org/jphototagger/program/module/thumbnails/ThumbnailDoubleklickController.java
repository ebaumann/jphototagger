package org.jphototagger.program.module.thumbnails;

import java.io.File;
import java.util.Arrays;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.ProgramsUtil;
import org.jphototagger.program.module.programs.StartPrograms;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailDoubleklickController {

    private final ThumbnailsPanel panel;
    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

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
            File selectedFile = panel.getFileAtIndex(index);
            Program program = findDefaultImageOpenProgram(selectedFile);
            if (program == null) {
                String message = Bundle.getString(ThumbnailDoubleklickController.class, "ControllerOpenFilesWithStandardApp.Info.DefineOpenApp");
                // Reusing bundle string
                MessageDisplayer.information(null, message);
                ProgramsUtil.openSelectedFilesWidth(ProgramsUtil.addProgram(), false);
            } else {
                StartPrograms startPrograms = new StartPrograms();
                boolean waitForTermination = false;
                startPrograms.startProgram(program, Arrays.asList(selectedFile), waitForTermination);
            }
        }
    }

    private Program findDefaultImageOpenProgram(File file) {
        Program program = repo.findDefaultProgram(FileUtil.getSuffix(file));
        return program == null
                ? repo.findDefaultImageOpenProgram()
                : program;
    }
}
