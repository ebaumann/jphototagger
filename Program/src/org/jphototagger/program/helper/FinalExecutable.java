package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLifeCycle.FinalTask;
import org.jphototagger.lib.runtime.External;

/**
 * Executable command that can be called before JPhotoTaggers quits.
 * <p>
 * Usage: Create an instance and add it to
 * {@link AppLifeCycle#addFinalTask(org.jphototagger.program.app.AppLifeCycle.FinalTask)}
 *
 * @author Elmar Baumann
 */
public final class FinalExecutable extends FinalTask {
    private String executable;

    public FinalExecutable(String executable) {
        if (executable == null) {
            throw new NullPointerException("exec == null");
        }

        this.executable = executable;
    }

    @Override
    public void execute() {
        External.execute(executable, false);
        notifyFinished();
    }
}
