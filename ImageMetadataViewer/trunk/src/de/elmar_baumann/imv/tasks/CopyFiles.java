package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Kopieren von Dateien in ein Verzeichnis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public final class CopyFiles implements Runnable {

    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final List<File> errorFiles = new ArrayList<File>();
    private final List<Pair<File, File>> files;
    private final boolean forceOverwrite;
    private boolean stop = false;

    /**
     * Konstruktor
     *
     * @param files           Zu kopierende Dateien. Die erste im Paar
     *                        ist die Quelldatei, die zweite die Zieldatei.
     * @param forceOverwrite  true, wenn ohne Rückfrage überschreiben
     */
    public CopyFiles(List<Pair<File, File>> files, boolean forceOverwrite) {
        this.files = files;
        this.forceOverwrite = forceOverwrite;
    }

    /**
     * Beendet das Kopieren.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Fügt einen Aktionsbeobachter hinzu.
     * {@link de.elmar_baumann.imv.event.ProgressListener#progressPerformed(de.elmar_baumann.imv.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imv.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imv.event.ProgressEvent#getInfo()}
     * ein {@link de.elmar_baumann.lib.template.Pair}-Objekt liefert mit der
     * aktuellen Quelldatei als erstes Element und der Zieldatei als zweites.
     * 
     * {@link de.elmar_baumann.imv.event.ProgressListener#progressEnded(de.elmar_baumann.imv.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imv.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imv.event.ProgressEvent#getInfo()}
     * ein {@link java.util.List}-Objekt mit den Dateinamen der Dateien, die nicht
     * kopiert werden konnten.
     * 
     * @param listener  Beobachter
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void run() {
        notifyStart();
        int size = files.size();
        for (int i = 0; !stop && i < size; i++) {
            Pair<File, File> filePair = files.get(i);
            if (checkOverwrite(filePair)) {
                try {
                    File sourceFile = filePair.getFirst();
                    File targetFile = filePair.getSecond();
                    logCopyFile(sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());
                    FileUtil.copyFile(sourceFile, targetFile);
                } catch (IOException ex) {
                    de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
                    errorFiles.add(filePair.getFirst());
                } catch (Exception ex) {
                    de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
                    errorFiles.add(filePair.getFirst());
                }
            }
            notifyPerformed(i, filePair);
        }
        notifyEnded();
    }

    private void logCopyFile(String sourceFilename, String targetFilename) {
        MessageFormat msg = new MessageFormat(Bundle.getString("CopyFiles.InformationMessage.StartCopy"));
        Object[] params = {sourceFilename, targetFilename};
        Log.logInfo(CopyFiles.class, msg.format(params));
    }

    private void notifyStart() {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(), 0, null);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    private void notifyPerformed(int value, Pair<File, File> filePair) {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(), value, filePair);
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, files.size(), files.size(), errorFiles);
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    private boolean checkOverwrite(Pair<File, File> filePair) {
        if (forceOverwrite) {
            return true;
        }
        File target = filePair.getSecond();
        if (target.exists()) {
            MessageFormat msg = new MessageFormat(Bundle.getString("CopyFiles.ConfirmMessage.OverwriteExisting"));
            Object[] params = {filePair.getSecond(), filePair.getFirst()};
            int option = JOptionPane.showConfirmDialog(null,
                msg.format(params),
                Bundle.getString("CopyFiles.ConfirmMessage.OverwriteExisting.Title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppSettings.getMediumAppIcon());
            if (option == JOptionPane.CANCEL_OPTION) {
                stop();
            } else {
                return option == JOptionPane.YES_OPTION;
            }
        }
        return true;
    }
}
