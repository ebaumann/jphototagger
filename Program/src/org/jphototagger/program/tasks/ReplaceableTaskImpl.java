package org.jphototagger.program.tasks;

import org.jphototagger.api.concurrent.ReplaceableTask;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ReplaceableTask.class)
public final class ReplaceableTaskImpl implements ReplaceableTask {

    private static final ReplaceableThread DELEGATE = new ReplaceableThread();

    @Override
    public void replacePreviousTaskWith(Runnable runnable) {
        DELEGATE.setTask(runnable);
    }

    @Override
    public void cancelRunningTask() {
        DELEGATE.cancelCurrentThread();
    }
}
