package de.elmar_baumann.imv.helper;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Kopieren von Dateien.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-24
 */
public final class CopyFiles implements Runnable {

    private final List<ProgressListener> progressListeners =
            new ArrayList<ProgressListener>();
    private final List<File> errorFiles = new ArrayList<File>();
    private final List<Pair<File, File>> files;
    private final Options options;
    private boolean stop = false;

    /**
     * Copy options.
     */
    public enum Options {

        /** Overwrite existing files only if confirmed */
        CONFIRM_OVERWRITE,
        /** Overwrite existing files without confirm */
        FORCE_OVERWRITE
    }

    /**
     * Konstruktor
     *
     * @param files    Zu kopierende Dateien. Die erste im Paar
     *                 ist die Quelldatei, die zweite die Zieldatei.
     * @param options  Optionen
     */
    public CopyFiles(List<Pair<File, File>> files, Options options) {
        this.files = files;
        this.options = options;
    }

    /**
     * Beendet das Kopieren.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Fügt einen Aktionsbeobachter hinzu.
     * {@link de.elmar_baumann.imv.event.listener.ProgressListener#progressPerformed(de.elmar_baumann.imv.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imv.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imv.event.ProgressEvent#getInfo()}
     * ein {@link de.elmar_baumann.lib.generics.Pair}-Objekt liefert mit der
     * aktuellen Quelldatei als erstes Element und der Zieldatei als zweites.
     * 
     * {@link de.elmar_baumann.imv.event.listener.ProgressListener#progressEnded(de.elmar_baumann.imv.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imv.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imv.event.ProgressEvent#getInfo()}
     * ein {@link java.util.List}-Objekt mit den Dateinamen der Dateien, die nicht
     * kopiert werden konnten.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void run() {
        notifyStart();
        int size = files.size();
        for (int i = 0; !stop && i < size; i++) {
            Pair<File, File> filePair = files.get(i);
            if (checkDifferent(filePair) && checkOverwrite(filePair)) {
                try {
                    File sourceFile = filePair.getFirst();
                    File targetFile = filePair.getSecond();
                    logCopyFile(sourceFile.getAbsolutePath(), targetFile.
                            getAbsolutePath());
                    FileUtil.copyFile(sourceFile, targetFile);
                } catch (Exception ex) {
                    AppLog.logSevere(CopyFiles.class, ex);
                    errorFiles.add(filePair.getFirst());
                }
            }
            notifyPerformed(i, filePair);
        }
        notifyEnded();
    }

    private void logCopyFile(String sourceFilename, String targetFilename) {
        AppLog.logInfo(CopyFiles.class, Bundle.getString(
                "CopyFiles.Info.StartCopy", // NOI18N
                sourceFilename, targetFilename));
    }

    private synchronized void notifyStart() {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(), 0, null);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    private synchronized void notifyPerformed(int value,
            Pair<File, File> filePair) {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(), value,
                filePair);
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(),
                files.size(), errorFiles);
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    private boolean checkOverwrite(Pair<File, File> filePair) {
        if (options.equals(Options.FORCE_OVERWRITE)) {
            return true;
        }
        File target = filePair.getSecond();
        if (target.exists()) {
            MessageDisplayer.ConfirmAction action = MessageDisplayer.confirm(
                    "CopyFiles.Confirm.OverwriteExisting", // NOI18N
                    MessageDisplayer.CancelButton.SHOW,
                    filePair.getSecond(), filePair.getFirst());
            if (action.equals(MessageDisplayer.ConfirmAction.CANCEL)) {
                stop();
            } else {
                return action.equals(MessageDisplayer.ConfirmAction.YES);
            }
        }
        return true;
    }

    private boolean checkDifferent(Pair<File, File> filePair) {
        if (filePair.getFirst().equals(filePair.getSecond())) {
            MessageDisplayer.error("CopyFiles.Error.FilesAreEquals", // NOI18N
                    filePair.getFirst());
            return false;
        }
        return true;
    }
}
