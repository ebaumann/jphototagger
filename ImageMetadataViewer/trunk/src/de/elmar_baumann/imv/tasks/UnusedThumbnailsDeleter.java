package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Deletes unused thumbnails through
 * {@link DatabaseMaintainance#deleteUnusedThumbnails(de.elmar_baumann.imv.event.listener.ProgressListener)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/25
 */
public final class UnusedThumbnailsDeleter implements Runnable, ProgressListener {

    private final DatabaseMaintainance db = DatabaseMaintainance.INSTANCE;
    private final Set<ProgressListener> progressListeners =
            new HashSet<ProgressListener>();
    private String startMessage;
    private String endMessage;
    private volatile int countDeleted = 0;

    @Override
    public void run() {
        setMessagesFiles();
        logDeleteThumbnails();
        db.deleteUnusedThumbnails(this);
    }

    public int getCountDeleted() {
        return countDeleted;
    }

    /**
     * Adds a progress listener and delegates calls from
     * {@link DatabaseMaintainance#deleteUnusedThumbnails(de.elmar_baumann.imv.event.listener.ProgressListener)}.
     * 
     * @param listener listener
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setInfo(getStartMessage(evt));
        notifyProgressStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        notifyProgressPerformed(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        countDeleted += (Integer) evt.getInfo();
        evt.setInfo(getEndMessage());
        notifyProgressEnded(evt);
    }

    private void notifyProgressEnded(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressEnded(evt);
            }
        }
    }

    private void notifyProgressPerformed(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressPerformed(evt);
            }
        }
    }

    private void notifyProgressStarted(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressStarted(evt);
            }
        }
    }

    private Object getStartMessage(ProgressEvent evt) {
        return new MessageFormat(startMessage).format(new Object[]{evt.
                    getMaximum()});
    }

    private Object getEndMessage() {
        return new MessageFormat(endMessage).format(new Object[]{countDeleted});
    }

    private void logDeleteThumbnails() {
        AppLog.logInfo(UnusedThumbnailsDeleter.class,
                Bundle.getString(
                "UnusedThumbnailsDeleter.InformationMessage.Start")); // NOI18N
    }

    private void setMessagesFiles() {
        startMessage =
                Bundle.getString("UnusedThumbnailsDeleter.Files.StartMessage"); // NOI18N
        endMessage =
                Bundle.getString("UnusedThumbnailsDeleter.Files.EndMessage"); // NOI18N
    }
}
