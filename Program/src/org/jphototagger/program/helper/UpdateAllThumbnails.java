/*
 * @(#)UpdateAllThumbnails.java    Created on 2008-10-12
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

import org.jphototagger.lib.dialog.ProgressDialog;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.controller.misc.SizeAndLocationController;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import java.util.HashSet;
import java.util.Set;

/**
 * Updates all Thumbnails in the database with the current settings.
 *
 * @author  Elmar Baumann
 */
public final class UpdateAllThumbnails
        implements Runnable, ProgressListener, ActionListener {
    private ProgressDialog            progressDialog;
    private boolean                   cancel;
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
        progressDialog = new ProgressDialog(GUI.INSTANCE.getAppFrame());
        progressDialog.setTitle(
            JptBundle.INSTANCE.getString("UpdateAllThumbnails.Dialog.Title"));
        progressDialog.setInfoText(
            JptBundle.INSTANCE.getString(
                "UpdateAllThumbnails.Dialog.InfoText"));
        progressDialog.addActionListener(this);
        progressDialog.addWindowListener(new SizeAndLocationController());
        progressDialog.setVisible(true);
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.cancel();
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
        setProgressDialogStarted(evt);
        checkCancel(evt);
    }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
        setProgressDialogPerformed(evt);
        checkCancel(evt);
    }
        });
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        setProgressDialogEnded(evt);
        notifyActionPerformed();
    }

    private void setProgressDialogEnded(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
        progressDialog.setValue(evt.getValue());
        progressDialog.setVisible(false);
        progressDialog.dispose();
    }
        });
    }

    private void setProgressDialogPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
        progressDialog.setValue(evt.getValue());
                progressDialog.setCurrentProgressInfoText(
                    evt.getInfo().toString());
    }
        });
    }

    private void setProgressDialogStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
        progressDialog.setMinimum(evt.getMinimum());
        progressDialog.setMaximum(evt.getMaximum());
        progressDialog.setValue(evt.getValue());
    }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        cancel = true;
    }

    private void logUpdateAllThumbnails() {
        AppLogger.logInfo(UpdateAllThumbnails.class,
                          "UpdateAllThumbnails.Info.StartUpdate");
    }

    private synchronized void notifyActionPerformed() {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "Cancel"));
        }
    }
}
