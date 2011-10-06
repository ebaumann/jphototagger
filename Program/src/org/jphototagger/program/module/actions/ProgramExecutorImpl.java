package org.jphototagger.program.module.actions;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramExecutor;
import org.jphototagger.program.module.programs.StartPrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class ProgramExecutorImpl implements ProgramExecutor {

    private final StartPrograms programStarter;
    private final boolean waitForTermination;

    public ProgramExecutorImpl(boolean waitForTermination) {
        programStarter = new StartPrograms();
        this.waitForTermination = waitForTermination;
    }

    @Override
    public void execute(Program program) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        programStarter.startProgram(program, tnPanel.getSelectedFiles(), waitForTermination);
    }
}
