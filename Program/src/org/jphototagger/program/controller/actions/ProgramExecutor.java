package org.jphototagger.program.controller.actions;

import org.jphototagger.program.data.Program;
import org.jphototagger.program.event.listener.ProgramExecutionListener;
import org.jphototagger.program.helper.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import javax.swing.JProgressBar;

/**
 *
 * @author Elmar Baumann
 */
public final class ProgramExecutor implements ProgramExecutionListener {
    private final StartPrograms programStarter;
    private final boolean waitForTermination;

    public ProgramExecutor(JProgressBar progressBar, boolean waitForTermination) {
        programStarter = new StartPrograms(progressBar);
        this.waitForTermination = waitForTermination;
    }

    @Override
    public void execute(Program program) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        programStarter.startProgram(program, tnPanel.getSelectedFiles(), waitForTermination);
    }
}
