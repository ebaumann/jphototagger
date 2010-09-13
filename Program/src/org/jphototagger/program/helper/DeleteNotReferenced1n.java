/*
 * @(#)DeleteNotReferenced1n.java    Created on 2010-09-12
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.helper;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseMaintainance;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;

/**
 * Deletes from the database records in 1:n tables not referenced by another
 * record.
 *
 * @author Elmar Baumann
 */
public final class DeleteNotReferenced1n implements Runnable {
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile int                  countDeleted = 0;

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public synchronized int getCountDeleted() {
        return countDeleted;
    }

    @Override
    public void run() {
        notifyProgressStarted();
        countDeleted = DatabaseMaintainance.INSTANCE.deleteNotReferenced1n();
        notifyProgressEnded();
    }

    public void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 0, getStartMessage());

        AppLogger.logInfo(DeleteNotReferenced1n.class,
                          "DeleteNotReferenced1n.Info.Start");
        ls.notifyStarted(evt);
    }

    public void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, 1, 1, getEndMessage());

        ls.notifyEnded(evt);
    }

    private Object getStartMessage() {
        return JptBundle.INSTANCE.getString("DeleteNotReferenced1n.Info.Start");
    }

    private Object getEndMessage() {
        return JptBundle.INSTANCE.getString(
            "DeleteNotReferenced1n.Info.Finished", countDeleted);
    }
}
