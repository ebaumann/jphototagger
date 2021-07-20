package org.jphototagger.program.module.programs;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramExecutor;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;

/**
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
