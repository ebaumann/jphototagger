package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.event.ErrorEvent;
import de.elmar_baumann.imagemetadataviewer.event.listener.ErrorListeners;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Kopieren von Dateien in ein Verzeichnis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/24
 */
public class CopyFiles implements Runnable {

    private Vector<ProgressListener> progressListeners = new Vector<ProgressListener>();
    private Vector<Pair<String, String>> filenames = new Vector<Pair<String, String>>();
    private Vector<String> errorFilenames = new Vector<String>();
    private boolean stop = false;
    private boolean forceOverwrite = false;

    /**
     * Standardkonstruktor. Mit {@link #setFilenames(java.util.Vector)} werden
     * die zu kopierenden Dateien gesetzt.
     */
    public CopyFiles() {
    }

    /**
     * Konstruktor.
     * 
     * @param filenames  Namen der zu kopierenden Dateien. Der erste im Paar
     *                   ist die Quelldatei, der zweite die Zieldatei.
     */
    public CopyFiles(Vector<Pair<String, String>> filenames) {
        this.filenames = filenames;
    }

    /**
     * Beendet das Kopieren.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Setzt die zu kopierenden Dateien.
     * 
     * @param filenames  Namen der zu kopierenden Dateien. Der erste im Paar
     *                   ist die Quelldatei, der zweite die Zieldatei.
     */
    public void setFilenames(Vector<Pair<String, String>> filenames) {
        this.filenames = filenames;
    }

    /**
     * Setzt, ob existierende Zieldateien gleichen Namens ohne Rückfrage
     * überschrieben werden sollen.
     * 
     * @param force  true, wenn ohne Rückfrage überschreiben. Default: false.
     */
    public void setForceOverwrite(boolean force) {
        forceOverwrite = force;
    }

    /**
     * Fügt einen Aktionsbeobachter hinzu.
     * {@link de.elmar_baumann.imagemetadataviewer.event.ProgressListener#progressPerformed(de.elmar_baumann.imagemetadataviewer.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imagemetadataviewer.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imagemetadataviewer.event.ProgressEvent#getInfo()}
     * ein {@link de.elmar_baumann.lib.template.Pair}-Objekt liefert mit der
     * aktuellen Quelldatei als erstes Element und der Zieldatei als zweites.
     * 
     * {@link de.elmar_baumann.imagemetadataviewer.event.ProgressListener#progressEnded(de.elmar_baumann.imagemetadataviewer.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.imagemetadataviewer.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.imagemetadataviewer.event.ProgressEvent#getInfo()}
     * ein {@link java.util.Vector}-Objekt mit den Dateinamen der Dateien, die nicht
     * kopiert werden konnten.
     * 
     * @param listener  Beobachter
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener  Beobachter
     */
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void run() {
        notifyStart();
        int count = filenames.size();
        for (int i = 0; !stop && i < count; i++) {
            Pair<String, String> filePair = filenames.get(i);
            if (checkOverwrite(filePair)) {
                try {
                    FileUtil.copyFile(new File(filePair.getFirst()), new File(filePair.getSecond()));
                } catch (IOException ex) {
                    Logger.getLogger(CopyFiles.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), this));
                    errorFilenames.add(filePair.getFirst());
                } catch (Exception ex) {
                    Logger.getLogger(CopyFiles.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(ex.toString(), this));
                    errorFilenames.add(filePair.getFirst());
                }
            }
            notifyPerformed(i, filePair);
        }
        notifyEnded();
    }

    private void notifyStart() {
        ProgressEvent evt = new ProgressEvent(this, 0, filenames.size(), 0, null);
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    private void notifyPerformed(int value, Pair<String, String> filePair) {
        ProgressEvent evt = new ProgressEvent(this, 0, filenames.size(), value, filePair);
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    private void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, filenames.size(), filenames.size(), errorFilenames);
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    private boolean checkOverwrite(Pair<String, String> filePair) {
        if (forceOverwrite) {
            return true;
        }
        File target = new File(filePair.getSecond());
        if (target.exists()) {
            MessageFormat msg = new MessageFormat(Bundle.getString("CopyFiles.ConfirmMessage.OverwriteExisting"));
            Object[] params = {filePair.getSecond(), filePair.getFirst()};
            int option = JOptionPane.showConfirmDialog(null,
                msg.format(params),
                Bundle.getString("CopyFiles.ConfirmMessage.OverwriteExisting.Title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppSettings.getSmallAppIcon());
            if (option == JOptionPane.CANCEL_OPTION) {
                stop();
            } else {
                return option == JOptionPane.YES_OPTION;
            }
        }
        return true;
    }
}
