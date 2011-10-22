package org.jphototagger.domain.repository;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import org.openide.util.Lookup;

/**
 * Annotate unit test classes requiring the repository with {@code @RunWith(RepositoryConnectedTestClassRunner.class)}.
 * This implementations looks up the SPI for an implementation.
 * @author Elmar Baumann
 */
public class RepositoryConnectedTestClassRunner extends BlockJUnit4ClassRunner {

    private static int startUpRequests;
    private static boolean repoInit;

    public RepositoryConnectedTestClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            initResourceIfNecessary();
            // Unattended Tests: Here: notifier.addListener(new MyFailureListener());
            super.run(notifier);
            disposeResourceIfNecessary();
        } catch (Throwable t) {
            Logger.getLogger(RepositoryConnectedTestClassRunner.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private static void disposeResourceIfNecessary() {
        if (startUpRequests == 0) {
            Repository repository = Lookup.getDefault().lookup(Repository.class);
            repository.shutdown();
        }
    }

    private static void initResourceIfNecessary() throws Exception {
        if (!repoInit) {
            Repository repository = Lookup.getDefault().lookup(Repository.class);
            repository.init();
            repoInit = true;
        }
    }
}
