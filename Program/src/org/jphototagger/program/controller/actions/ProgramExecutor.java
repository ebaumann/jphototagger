package org.jphototagger.program.controller.actions;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.event.listener.ProgramExecutionListener;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import javax.swing.JProgressBar;
import org.jphototagger.program.resource.GUI;

/**
 * Executes {@link Program}s.
 *
 * @author Elmar Baumann
 */
public final class ProgramExecutor implements ProgramExecutionListener {
    private final StartPrograms programStarter;

    public ProgramExecutor(JProgressBar progressBar) {
        programStarter = new StartPrograms(progressBar);
    }

    @Override
    public void execute(Program program) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        programStarter.startProgram(program, tnPanel.getSelectedFiles());
    }
}
