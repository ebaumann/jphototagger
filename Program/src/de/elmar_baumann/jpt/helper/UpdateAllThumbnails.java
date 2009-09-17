/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Updates all Thumbnails in the database with the current settings.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-12
 */
public final class UpdateAllThumbnails
        implements Runnable, ProgressListener, ActionListener {

    private ProgressDialog progressDialog;
    private boolean stop = false;
    private final Set<ActionListener> actionListeners =
            new HashSet<ActionListener>();

    /**
     * Adds an action listener. It will be notified when the work is done.
     * 
     * @param listener  action listener
     */
    public synchronized void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    @Override
    public void run() {
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        initProgressDialog();
        logUpdateAllThumbnails();
        db.updateAllThumbnails(this);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(null);
        progressDialog.setTitle(Bundle.getString(
                "UpdateAllThumbnails.Dialog.Title")); // NOI18N
        progressDialog.setInfoText(Bundle.getString(
                "UpdateAllThumbnails.Dialog.InfoText")); // NOI18N
        progressDialog.addActionListener(this);
        progressDialog.setVisible(true);
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        setProgressDialogStarted(evt);
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        setProgressDialogPerformed(evt);
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        setProgressDialogEnded(evt);
        notifyActionPerformed();
    }

    private void setProgressDialogEnded(ProgressEvent evt) {
        progressDialog.setValue(evt.getValue());
        progressDialog.setVisible(false);
        progressDialog.dispose();
    }

    private void setProgressDialogPerformed(ProgressEvent evt) {
        progressDialog.setValue(evt.getValue());
        progressDialog.setCurrentProgressInfoText(evt.getInfo().toString());
    }

    private void setProgressDialogStarted(ProgressEvent evt) {
        progressDialog.setMinimum(evt.getMinimum());
        progressDialog.setMaximum(evt.getMaximum());
        progressDialog.setValue(evt.getValue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stop = true;
    }

    private void logUpdateAllThumbnails() {
        AppLog.logInfo(UpdateAllThumbnails.class,
                "UpdateAllThumbnails.Info.StartUpdate"); // NOI18N
    }

    private synchronized void notifyActionPerformed() {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "Stop")); // NOI18N
        }
    }
}
