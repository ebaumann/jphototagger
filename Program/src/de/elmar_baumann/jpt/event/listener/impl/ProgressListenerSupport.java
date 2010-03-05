/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;

/**
 * Adds, removes and notifies {@link ProgressListener} instances.
 *
 * @author  Elmar Baumann
 * @version 2009-12-18
 */
public final class ProgressListenerSupport extends ListenerSupport<ProgressListener> {

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressStarted(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(ProgressEvent event) {
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressStarted(event);
            }
        }
    }
    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressPerformed(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@link ProgressEvent#isStop()}
     */
    public boolean notifyPerformed(ProgressEvent event) {
        boolean isStop = false;
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressPerformed(event);
            }
        }
        return isStop;
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressEnded(de.elmar_baumann.jpt.event.ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(ProgressEvent event) {
        synchronized(listeners) {
            for (ProgressListener listener : listeners) {
                listener.progressEnded(event);
            }
        }
    }
}
