/*
 * @(#)UserTasks.java    Created on 2009-07-16
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

import org.jphototagger.lib.concurrent.SerialExecutor;

import java.util.concurrent.Executors;

/**
 * Queues user tasks and starts them as thread after the previous user task has
 * finished.
 *
 * @author  Elmar Baumann
 */
public final class UserTasks {
    public static final UserTasks INSTANCE = new UserTasks();
    private final SerialExecutor  executor =
        new SerialExecutor(Executors.newCachedThreadPool());

    /**
     * Adds a new user task.
     *
     * @param runnable runnable
     */
    public void add(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("runnable == null");
        }

        executor.execute(runnable);
    }

    /**
     * Returns the count of user tasks.
     *
     * @return count of user tasks
     */
    public int getCount() {
        return executor.getCount();
    }

    /**
     * Removes all added user tasks.
     * <p>
     * If the active runnable implements {@link Cancelable}, its method
     * {@link Cancelable#cancel()} will be called. If it does not implement
     * that interface and it is an instance of {@link Thread},
     * {@link Thread#interrupt()} will be called.
     */
    public void cancelCurrentTasks() {
        executor.cancel();
    }

    private UserTasks() {}
}
