/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.concurrent;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

// Code from java.util.concurrent.Executor javadoc. Added shutdown()
/**
 * Executes runnables serial: The next runnable will be executed when the
 * previous has finished.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-17
 */
public final class SerialExecutor implements Executor {

    private final        Queue<Exec> runnables                 = new ArrayDeque<Exec>();
    private static final String      ALT_METHOD_NAME_INTERRUPT = "cancel";
    private final        Executor    executor;
    private              Exec        active;

    public SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * Empties the queue and interrupts the current active runnable.
     *
     * To interrupt it, the runnable has to be a {@link Thread} and periodically
     * calling {@link Thread#isInterrupted()}.
     *
     * If the active runnable has a method named <strong>cancel</strong> with
     * no parameters, it will be invoked instead of <strong>interrupt</strong>.
     */
    public synchronized void shutdown() {
        runnables.clear();
        interruptActive(active);
    }

    private synchronized void interruptActive(Exec active) {
        if (active == null) return;
        Method methodCancel = null;
        if (hasCancelMethod(active.r)) {
            try {
                methodCancel = active.r.getClass().getMethod(ALT_METHOD_NAME_INTERRUPT);
                methodCancel.invoke(active.r);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", ex);
            }
        }
        if (methodCancel == null && active.r instanceof Thread) {
            ((Thread) active.r).interrupt();
        }
    }

    private boolean hasCancelMethod(Runnable runnable) {
        Method[] methods = runnable.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(ALT_METHOD_NAME_INTERRUPT) &&
                    method.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the count of runnables.
     *
     * @return count of runnables
     */
    public synchronized int getCount() {
        int activeCount = active == null ? 0 : 1;
        return activeCount + runnables.size();
    }

    @Override
    public synchronized void execute(final Runnable r) {
        runnables.offer(new Exec(r));
        if (active == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((active = runnables.poll()) != null) {
            executor.execute(active);
        }
    }

    private class Exec implements Runnable {

        final Runnable r;

        public Exec(Runnable r) {
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
