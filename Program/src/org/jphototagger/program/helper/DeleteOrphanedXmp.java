package org.jphototagger.program.helper;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.lib.event.ProgressEvent;
import org.jphototagger.lib.event.listener.ProgressListener;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseImageFiles;

/**
 * Löscht in der Datenbank Datensätze mit Dateien, die nicht mehr existieren.
 *
 * @author Elmar Baumann
 * @see     DatabaseImageFiles#deleteOrphanedXmp(ProgressListener)
 */
public final class DeleteOrphanedXmp implements Runnable, ProgressListener {
    private final ProgressListenerSupport ls = new ProgressListenerSupport();
    private volatile boolean notifyProgressEnded;
    private volatile boolean cancel;
    private volatile int countDeleted = 0;
    private String startMessage;
    private String endMessage;
    private static final Logger LOGGER = Logger.getLogger(DeleteOrphanedXmp.class.getName());

    @Override
    public void run() {
        setMessagesFiles();
        logDeleteRecords();

        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

        db.deleteNotExistingImageFiles(this);

        if (!cancel) {
            setMessagesXmp();
            notifyProgressEnded = true;    // called before last action
            db.deleteOrphanedXmp(this);
        }
    }

    public int getCountDeleted() {
        return countDeleted;
    }

    /**
     * Fügt einen Fortschrittsbeobachter hinzu. Delegiert an diesen Aufrufe
     * von Database.deleteNotExistingImageFiles().
     *
     * @param listener Fortschrittsbeobachter
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        evt.setInfo(getStartMessage(evt));

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

    @Override
    public void progressPerformed(ProgressEvent evt) {

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

    @Override
    public void progressEnded(ProgressEvent evt) {
        countDeleted += (Integer) evt.getInfo();
        evt.setInfo(getEndMessage());

        if (cancel || notifyProgressEnded) {
            ls.notifyEnded(evt);
        }
    }

    private Object getStartMessage(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return new MessageFormat(startMessage).format(new Object[] { evt.getMaximum() });
    }

    private Object getEndMessage() {
        return new MessageFormat(endMessage).format(new Object[] { countDeleted });
    }

    private void logDeleteRecords() {
        LOGGER.log(Level.INFO, "Delete from database records with not existing files");
    }

    private void setMessagesFiles() {
        startMessage = Bundle.getString(DeleteOrphanedXmp.class, "DeleteOrphanedXmp.Files.Start");
        endMessage = Bundle.getString(DeleteOrphanedXmp.class, "DeleteOrphanedXmp.Files.End");
    }

    private void setMessagesXmp() {
        startMessage = Bundle.getString(DeleteOrphanedXmp.class, "DeleteOrphanedXmp.Xmp.Start");
        endMessage = Bundle.getString(DeleteOrphanedXmp.class, "DeleteOrphanedXmp.Xmp.End");
    }
}
