package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JProgressBar;

/**
 * Scannt Verzeichnisse nach Bilddateien und aktualisiert die Datenbank mit
 * den Bild-Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ImageMetadataToDatabaseArray implements ProgressListener {

    private final Queue<InsertImageFilesIntoDatabase> updaters = new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private final Map<String, InsertImageFilesIntoDatabase> updaterOfDirectory = new HashMap<String, InsertImageFilesIntoDatabase>();
    private final Map<InsertImageFilesIntoDatabase, String> directoryOfUpdater = new HashMap<InsertImageFilesIntoDatabase, String>();
    private final List<TaskListener> taskListeners = new ArrayList<TaskListener>();
    private final JProgressBar progressBar;
    private boolean wait = false;
    private boolean started = false;
    private String tooltipTextProgressEnded;

    /**
     * Konstruktor.
     * 
     * @param progressBar Progressbar zum Anzeigen des Fortschritts oder null,
     *                    wenn der Fortschritt nicht angezeigt werden soll
     */
    public ImageMetadataToDatabaseArray(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Startet den Scan. Default: false.
     */
    public synchronized void start() {
        setStarted(true);
    }

    /**
     * Stoppt den Scan, unternimmt nichts mehr. Alle Aufgaben werden
     * verworfen. Default: true.
     */
    public synchronized void stop() {
        setStarted(false);
    }

    private synchronized boolean isWait() {
        return wait;
    }

    private void logScanDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.StartScanDirectory"));
        Object[] params = {directoryName};
        Log.logInfo(ImageMetadataToDatabaseArray.class, msg.format(params));
    }

    private synchronized void setWait(boolean wait) {
        this.wait = wait;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * Setzt einen Tooltiptext für die Progressbar, der angezeigt werden soll
     * nachdem ein Verzeichnis vollständig gescannt wurde.
     * 
     * @param text Text. Default: null.
     */
    public void setTooltipTextIfProgressEnded(String text) {
        tooltipTextProgressEnded = text;
    }

    /**
     * Liefert, ob der Scan gestartet ist.
     * 
     * @return true, wenn gestartet
     */
    public synchronized boolean isStarted() {
        return started;
    }

    /**
     * Unterbricht das Scannen eines Verzeichnisses.
     * 
     * @param directoryName Name des Verzeichnisses
     */
    public synchronized void stopUpdateOfDirectory(String directoryName) {
        InsertImageFilesIntoDatabase scanner = updaterOfDirectory.get(directoryName);
        if (scanner != null) {
            scanner.stop();
        }
    }

    /**
     * Fügt einen Task-Beobachter hinzu.
     * 
     * @param listener  Beobachter
     */
    public void addTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    private void checkTasksCompleted() {
        if (updaters.isEmpty()) {
            notifyTaskListenerCompleted();
        }
    }

    private void notifyTaskListenerCompleted() {
        for (TaskListener listener : taskListeners) {
            listener.taskCompleted();
        }
    }

    /**
     * Fügt ein einzuscannendes Verzeichnis hinzu.
     * 
     * @param directoryName    Verzeichnisname
     * @param forceUpdate      Update dieser Metadaten erzwingen
     */
    public synchronized void addDirectory(String directoryName, 
        EnumSet<InsertImageFilesIntoDatabase.ForceUpdate> forceUpdate) {
        updaters.add(createUpdater(directoryName, forceUpdate));
        startUpdateThread();
    }

    private InsertImageFilesIntoDatabase createUpdater(String directoryName,
        EnumSet<InsertImageFilesIntoDatabase.ForceUpdate> forceUpdate) {
        List<String> filenames = FileUtil.getAsFilenames(
            ImageFilteredDirectory.getImageFilesOfDirectory(new File(directoryName)));
        Collections.sort(filenames);
        InsertImageFilesIntoDatabase scanner = new InsertImageFilesIntoDatabase(
            filenames, forceUpdate);
        scanner.addProgressListener(this);
        updaterOfDirectory.put(directoryName, scanner);
        directoryOfUpdater.put(scanner, directoryName);
        return scanner;
    }

    private synchronized void startUpdateThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.remove());
            thread.setPriority(UserSettings.getInstance().getThreadPriority());
            thread.start();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            if (evt.getMaximum() > 0) {
                progressBar.setMaximum(evt.getMaximum());
            }
            progressBar.setValue(evt.getValue());
        }
        logScanDirectory(getDirectoryNameOfUpdater((InsertImageFilesIntoDatabase) evt.getSource()));
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (isStarted()) {
            String filename = evt.getInfo().toString();
            messageUpdateCurrentImage(filename);
            if (progressBar != null) {
                progressBar.setValue(evt.getValue());
                progressBar.setToolTipText(filename);
            }
        } else {
            updaters.clear();
            evt.stop();
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(isStarted() ? evt.getMaximum() : 0);
            if (tooltipTextProgressEnded != null) {
                progressBar.setToolTipText(tooltipTextProgressEnded);
            }
        }
        setWait(false);
        if (updaters.size() > 0) {
            startUpdateThread();
        }
        InsertImageFilesIntoDatabase scanner = (InsertImageFilesIntoDatabase) evt.getSource();
        messageEndUpdateDirectory(scanner);
        removeUpdater(scanner);
        checkTasksCompleted();
    }

    private void removeUpdater(InsertImageFilesIntoDatabase scanner) {
        if (scanner != null) {
            String directoryName = getDirectoryNameOfUpdater(scanner);
            directoryOfUpdater.remove(scanner);
            updaterOfDirectory.remove(directoryName);
        }
    }

    private void messageEndUpdateDirectory(InsertImageFilesIntoDatabase scanner) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.UpdateMetadataFinished"));
        Object[] params = {getDirectoryNameOfUpdater(scanner)};
        Log.logFinest(ImageMetadataToDatabaseArray.class, message.format(params));
    }

    private void messageUpdateCurrentImage(String filename) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.CheckImageForModifications"));
        Object[] params = {filename};
        Log.logFinest(ImageMetadataToDatabaseArray.class, message.format(params));
    }

    private String getDirectoryNameOfUpdater(InsertImageFilesIntoDatabase scanner) {
        String name = null;
        if (scanner != null) {
            name = directoryOfUpdater.get(scanner);
        }
        return name == null ? "" : name; // NOI18N
    }
}
