/*
 * @(#)ProgressListenerSupport.java    Created on 2009-12-18
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

package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;

/**
 * Adds, removes and notifies {@link ProgressListener} instances.
 *
 * @author  Elmar Baumann
 */
public final class ProgressListenerSupport
        extends ListenerSupport<ProgressListener> {

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressStarted(org.jphototagger.program.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(ProgressEvent event) {
        synchronized (listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressStarted(event);
            }
        }
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressPerformed(org.jphototagger.program.event.ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@link ProgressEvent#isStop()}
     */
    public boolean notifyPerformed(ProgressEvent event) {
        boolean isStop = false;

        synchronized (listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressPerformed(event);
            }
        }

        return isStop;
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressEnded(org.jphototagger.program.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(ProgressEvent event) {
        synchronized (listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressEnded(event);
            }
        }
    }
}
