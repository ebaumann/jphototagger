package org.jphototagger.maintainance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.ProgressDialog;
import org.jphototagger.lib.swing.SizeAndLocationController;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class UpdateAllThumbnails implements Runnable, ProgressListener, ActionListener {

    private ProgressDialog progressDialog;
    private boolean cancel;
    private final Set<ActionListener> actionListeners = new HashSet<>();
    private static final Logger LOGGER = Logger.getLogger(UpdateAllThumbnails.class.getName());
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

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

        initProgressDialog();
        logUpdateAllThumbnails();
        repo.updateAllThumbnails(this);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(ComponentUtil.findFrameWithIcon());
        progressDialog.setTitle(Bundle.getString(UpdateAllThumbnails.class, "UpdateAllThumbnails.Dialog.Title"));
        progressDialog.setInfoText(Bundle.getString(UpdateAllThumbnails.class, "UpdateAllThumbnails.Dialog.InfoText"));
        progressDialog.addActionListener(this);
        progressDialog.addWindowListener(new SizeAndLocationController());
        progressDialog.setVisible(true);
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.setCancel(true);
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                setProgressDialogStarted(evt);
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressDialog.setValue(evt.getValue());
                progressDialog.setVisible(false);
                progressDialog.dispose();
            }
        });
    }

    private void setProgressDialogPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressDialog.setValue(evt.getValue());
                progressDialog.setCurrentProgressInfoText(evt.getInfo().toString());
            }
        });
    }

    private void setProgressDialogStarted(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

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
        LOGGER.log(Level.INFO, "Updating all known thumbnails");
    }

    private synchronized void notifyActionPerformed() {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, 0, "Cancel"));
        }
    }
}
