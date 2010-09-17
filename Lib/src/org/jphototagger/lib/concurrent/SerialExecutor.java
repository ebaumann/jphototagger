/*
 * @(#)SerialExecutor.java    Created on 2009-07-17
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.concurrent;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.Queue;

//Code from java.util.concurrent.Executor javadoc. Added cancel()

/**
 * Executes runnables serial: The next runnable will be executed when the
 * previous has finished.
 *
 * @author Elmar Baumann
 */
public final class SerialExecutor implements Executor {
    private final Queue<Exec> runnables = new ArrayDeque<Exec>();
    private final Executor    executor;
    private Exec              active;

    public SerialExecutor(Executor executor) {
        if (executor == null) {
            throw new NullPointerException("executor == null");
        }

        this.executor = executor;
    }

    /**
     * Empties the queue and interrupts the current active runnable.
     *
     * If the active runnable implements {@link Cancelable}, its method
     * {@link Cancelable#cancel()} will be called. If it does not implement
     * that interface and it is an instance of {@link Thread},
     * {@link Thread#interrupt()} will be called.
     */
    public synchronized void cancel() {
        runnables.clear();
        cancel(active);
    }

    private synchronized void cancel(Exec active) {
        if (active == null) {
            return;
        }

        if (active.r instanceof Cancelable) {
            ((Cancelable) active.r).cancel();
        } else if (active.r instanceof Thread) {
            ((Thread) active.r).interrupt();
        }
    }

    /**
     * Returns the count of runnables.
     *
     * @return count of runnables
     */
    public synchronized int getCount() {
        int activeCount = (active == null)
                          ? 0
                          : 1;

        return activeCount + runnables.size();
    }

    @Override
    public synchronized void execute(final Runnable r) {
        runnables.offer(new Exec(r));

        if (active == null) {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext() {
        if ((active = runnables.poll()) != null) {
            executor.execute(active);
        }
    }

    private class Exec implements Runnable {
        final Runnable r;

        Exec(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } finally {
                scheduleNext();
            }
        }
    }
}
