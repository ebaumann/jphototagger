/*
 * @(#)AutomaticTask.java    Created on 2009-07-16
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

package org.jphototagger.program.tasks;


import org.jphototagger.lib.concurrent.Cancelable;

/**
 * An automatic task is a background task running as long as the next task
 * shall start.
 *
 * @author  Elmar Baumann
 */
public final class AutomaticTask {
    public static final AutomaticTask INSTANCE = new AutomaticTask();
    private Runnable                  runnable;

    /**
     * Sets a new automatic task and calls {@link #cancelCurrentTask()} to the
     * currently running task.
     *
     * @param runnable runnable
     */
    public synchronized void setTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }

        cancelCurrentTask();
        this.runnable = runnable;
        startTask(runnable);
    }

    /**
     * Cancels the current task.
     * <p>
     * If the active runnable implements {@link Cancelable}, its method
     * {@link Cancelable#cancel()} will be called. If it does not implement
     * that interface and it is an instance of {@link Thread},
     * {@link Thread#interrupt()} will be called.
     */
    public void cancelCurrentTask() {
        if (runnable != null) {
            cancel(runnable);
        }
    }

    private synchronized void cancel(Runnable r) {
        if (r == null) {
            return;
        }

        if (r instanceof Cancelable) {
            ((Cancelable) r).cancel();
        } else if (r instanceof Thread) {
            ((Thread) r).interrupt();
        }
    }

    private void startTask(final Runnable runnable) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });

        t.setName(getName(runnable));
        t.start();
    }

    private String getName(Runnable runnable) {
        if (runnable instanceof Thread) {
            return ((Thread) runnable).getName();
        }

        return "Automatic task @ " + getClass().getName();
    }

    private AutomaticTask() {}
}
