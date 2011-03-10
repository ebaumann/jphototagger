package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.controller.programs.ControllerOpenFilesWithStandardApp;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.util.Arrays;

/**
 * Kontroller für die Aktion: Doppelklick auf ein Thumbnail ausgelöst von
 * {@link org.jphototagger.program.view.panels.ThumbnailsPanel}.
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailDoubleklick {
    private final ThumbnailsPanel panel;

    public ControllerThumbnailDoubleklick(ThumbnailsPanel panel) {
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
            if (ControllerOpenFilesWithStandardApp.isOpenAppDefined(true)) {
                Program program = DatabasePrograms.INSTANCE.getDefaultImageOpenProgram();

                if (program != null) {
                    new StartPrograms(null).startProgram(program, Arrays.asList(panel.getFile(index)));
                }
            }
        }
    }
}
