package org.jphototagger.lib.concurrent;

import java.awt.EventQueue;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Executes runnables in the Swing event queue.
 * <p>
 * Modified http://www.javaconcurrencyinpractice.com/listings/GuiExecutor.java
 *
 * @author Elmar Baumann
 */
public final class SwingExecutor extends AbstractExecutorService {
    public static final SwingExecutor INSTANCE = new SwingExecutor();

    @Override
    public void execute(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    private SwingExecutor() {}
}
