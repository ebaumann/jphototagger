/*
 * @(#)DeleteUnusedKeywords.java    Created on 2010-09-12
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

package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Deletes from the database keywords not contained in any image file.
 *
 * @author Elmar Baumann
 */
public final class DeleteUnusedKeywords implements Runnable, Cancelable {
    private volatile boolean              cancel;
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
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        List<String>       keywords =
            new ArrayList<String>(db.getNotReferencedDcSubjects());
        int size = keywords.size();

        notifyProgressStarted(size);

        for (int i = 0; !cancel && (i < size); i++) {
            String keyword = keywords.get(i);

            db.deleteDcSubject(keyword);
            countDeleted++;
            notifyProgressPerformed(i + 1, countDeleted, keyword);
        }

        notifyProgressEnded(size, countDeleted);
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    public void notifyProgressStarted(int count) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, 0,
                                getStartMessage());

        AppLogger.logInfo(DeleteUnusedKeywords.class,
                          "DeleteUnusedKeywords.Info.Start");

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressStarted(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than 1
                // listener
                cancel = true;
            }
        }
    }

    private void notifyProgressPerformed(int count, int countDeleted,
            String keyword) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, countDeleted,
                                keyword);

        // Catching cancellation request
        for (ProgressListener listener : ls.get()) {
            listener.progressPerformed(evt);

            if (evt.isCancel()) {

                // cancel = evt.isCancel() can be wrong when more than
                // 1 listener
                cancel = true;
            }
        }
    }

    public void notifyProgressEnded(int count, int countDeleted) {
        ProgressEvent evt = new ProgressEvent(this, 0, count, countDeleted,
                                getEndMessage(count, countDeleted));

        ls.notifyEnded(evt);
    }

    private Object getEndMessage(int count, int countDeleted) {
        return JptBundle.INSTANCE.getString(
            "DeleteUnusedKeywords.Info.Finished", count, countDeleted);
    }

    private Object getStartMessage() {
        return JptBundle.INSTANCE.getString("DeleteUnusedKeywords.Info.Start");
    }
}
