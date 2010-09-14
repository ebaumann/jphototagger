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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import java.awt.Component;

import java.awt.EventQueue;

/**
 * Adds, removes and notifies {@link ProgressListener} instances.
 *
 * @author  Elmar Baumann
 */
public final class ProgressListenerSupport
        extends ListenerSupport<ProgressListener> {

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressStarted(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyStarted(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.progressStarted(event);
                    }
                });
            } else {
                listener.progressStarted(event);
            }
        }
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressPerformed(ProgressEvent)}.
     *
     * @param  event progress event
     * @return       true if one of the of the events returns
     *               {@link ProgressEvent#isCancel()}
     */
    public boolean notifyPerformed(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        boolean cancel = false;

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.progressPerformed(event);
                    }
                });
            } else {
                listener.progressPerformed(event);
            }
        }

        return cancel;
    }

    /**
     * Calls on every added progress listener
     * {@link ProgressListener#progressEnded(ProgressEvent)}.
     *
     * @param event progress event
     */
    public void notifyEnded(final ProgressEvent event) {
        if (event == null) {
            throw new NullPointerException("event == null");
        }

        for (final ProgressListener listener : listeners) {
            if (listener instanceof Component) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.progressEnded(event);
                    }
                });
            } else {
                listener.progressEnded(event);
            }
        }
    }
}
