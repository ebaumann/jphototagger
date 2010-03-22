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

import org.jphototagger.program.app.AppLogger;

import java.lang.reflect.Method;

/**
 * An automatic task is a background task running as long as the next task
 * shall start.
 *
 * @author  Elmar Baumann
 */
public final class AutomaticTask {
    public static final AutomaticTask INSTANCE                  =
        new AutomaticTask();
    private static final String       ALT_METHOD_NAME_INTERRUPT = "cancel";
    private Runnable                  runnable;

    /**
     * Sets a new automatic task and calls {@link Thread#interrupt()} to the
     * currently running task if it's an instance of
     * <code>java.lang.Thread</code>.
     *
     * This means: The currently running task stops only when it is a thread
     * that will periodically check {@link Thread#isInterrupted()}.
     *
     * If the active has a method named <strong>cancel</strong> with no
     * parameters, it will be invoked instead of <strong>interrupt</strong>.
     *
     * @param runnable runnable
     */
    public synchronized void setTask(Runnable runnable) {
        stopCurrentTask();
        this.runnable = runnable;
        startTask(runnable);
    }

    /**
     * Interrupts the currently running tasks. For limitations see remarks:
     * {@link #setTask(java.lang.Runnable)}.
     */
    public void stopCurrentTask() {
        if (runnable != null) {
            interrupt(runnable);
        }
    }

    private synchronized void interrupt(Runnable r) {
        if (r == null) {
            return;
        }

        Method methodCancel = null;

        if (hasCancelMethod(r)) {
            try {
                methodCancel =
                    r.getClass().getMethod(ALT_METHOD_NAME_INTERRUPT);
                methodCancel.invoke(r);
            } catch (Exception ex) {
                AppLogger.logSevere(AutomaticTask.class, ex);
            }
        }

        if ((methodCancel == null) && (r instanceof Thread)) {
            ((Thread) r).interrupt();
        }
    }

    private boolean hasCancelMethod(Runnable runnable) {
        Method[] methods = runnable.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(ALT_METHOD_NAME_INTERRUPT)
                    && (method.getParameterTypes().length == 0)) {
                return true;
            }
        }

        return false;
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
