package org.jphototagger.program.tasks;

import org.jphototagger.api.concurrent.ReplaceableTask;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ReplaceableTask.class)
public final class ReplaceableTaskImpl implements ReplaceableTask {

    @Override
    public void replacePreviousTaskWith(Runnable runnable) {
        AutomaticTask.INSTANCE.setTask(runnable);
    }

    @Override
    public void cancelRunningTask() {
        AutomaticTask.INSTANCE.cancelCurrentTask();
    }
}
