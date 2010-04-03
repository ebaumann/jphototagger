/*
 * @(#)ProgressBarUpdater.java    Created on 2009-12-18
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

package org.jphototagger.program.view.panels;

import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ProgressBarUpdater implements ProgressListener {
    private final String progressBarString;
    private JProgressBar progressBar;
    private final String progressBarOwner = "ProgressBarUpdater";

    /**
     *
     * @param progressBarString string to paint on the progress bar or null
     */
    public ProgressBarUpdater(String progressBarString) {
        this.progressBarString = progressBarString;
    }

    private synchronized void getProgressBar() {
        if (progressBar != null) {
            return;
        }

        progressBar = ProgressBar.INSTANCE.getResource(progressBarOwner);
    }

    private synchronized void updateProgressBar(final ProgressEvent evt) {
        getProgressBar();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setMinimum(evt.getMinimum());
                    progressBar.setMaximum(evt.getMaximum());
                    progressBar.setValue(evt.getValue());

                    if ((progressBarString != null)
                            &&!progressBar.isStringPainted()) {
                        progressBar.setStringPainted(true);
                    }

                    if ((progressBarString != null)
                            &&!progressBarString.equals(
                                progressBar.getString())) {
                        progressBar.setString(progressBarString);
                    }
                }
            }
        });
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        updateProgressBar(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        updateProgressBar(evt);
    }

    @Override
    public synchronized void progressEnded(final ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    if (progressBar.isStringPainted()) {
                        progressBar.setString("");
                    }

                    progressBar.setValue(0);
                    ProgressBar.INSTANCE.releaseResource(progressBarOwner);
                    progressBar = null;
                }
            }
        });
    }
}
