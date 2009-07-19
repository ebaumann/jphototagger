package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

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
        progressDialog.setTitle(Bundle.getString("UpdateAllThumbnails.Dialog.Title")); // NOI18N
        progressDialog.setInfoText(Bundle.getString("UpdateAllThumbnails.Dialog.InfoText")); // NOI18N
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
            Bundle.getString("UpdateAllThumbnails.Info.StartUpdate")); // NOI18N
    }

    private synchronized void notifyActionPerformed() {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "Stop")); // NOI18N
        }
    }
}
