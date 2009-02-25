package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.app.AppLog;
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
public final class InsertImageFilesIntoDatabaseArray implements ProgressListener {

    private final Queue<InsertImageFilesIntoDatabase> inserters = new ConcurrentLinkedQueue<InsertImageFilesIntoDatabase>();
    private final Map<String, InsertImageFilesIntoDatabase> inserterOfDirectory = new HashMap<String, InsertImageFilesIntoDatabase>();
    private final Map<InsertImageFilesIntoDatabase, String> directoryOfInserter = new HashMap<InsertImageFilesIntoDatabase, String>();
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
    public InsertImageFilesIntoDatabaseArray(JProgressBar progressBar) {
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

    private void logUpdateDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.StartScanDirectory"));
        Object[] params = {directoryName};
        AppLog.logInfo(InsertImageFilesIntoDatabaseArray.class, msg.format(params));
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
        InsertImageFilesIntoDatabase inserter = inserterOfDirectory.get(directoryName);
        if (inserter != null) {
            inserter.stop();
        }
    }

    /**
     * Fügt einen Task-Beobachter hinzu.
     * 
     * @param listener  Beobachter
     */
    public synchronized void addTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    private void checkTasksCompleted() {
        if (inserters.isEmpty()) {
            notifyTaskListenerCompleted();
        }
    }

    private synchronized void notifyTaskListenerCompleted() {
        for (TaskListener listener : taskListeners) {
            listener.taskCompleted();
        }
    }

    /**
     * Fügt ein einzuscannendes Verzeichnis hinzu.
     * 
     * @param directoryName Verzeichnisname
     * @param what          Einzufügende Metadaten
     */
    public synchronized void addDirectory(String directoryName, 
        EnumSet<InsertImageFilesIntoDatabase.Insert> what) {
        inserters.add(createInserter(directoryName, what));
        startUpdateThread();
    }

    private InsertImageFilesIntoDatabase createInserter(String directoryName,
        EnumSet<InsertImageFilesIntoDatabase.Insert> what) {

        List<String> filenames = FileUtil.getAsFilenames(
            ImageFilteredDirectory.getImageFilesOfDirectory(new File(directoryName)));

        Collections.sort(filenames);

        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(filenames, what);

        inserter.addProgressListener(this);
        updateDirectoryMapsInserterCreated(directoryName, inserter);

        return inserter;
    }

    private synchronized void startUpdateThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(inserters.remove());
            thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
            thread.start();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        logUpdateDirectory(getDirectoryNameOfInserter((InsertImageFilesIntoDatabase) evt.getSource()));
        setProgressBarStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (isStarted()) {
            String filename = evt.getInfo().toString();
            informationMessageUpdateCurrentImage(filename);
            setProgressBarPerformed(evt, filename);
        } else {
            inserters.clear();
            evt.stop();
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        setProgressBarEnded(evt);
        informationMessageEndUpdateDirectory((InsertImageFilesIntoDatabase) evt.getSource());
        updateDirectoryMapsProgressEnded((InsertImageFilesIntoDatabase) evt.getSource());
        checkTasksCompleted();
        setWait(false);
        if (inserters.size() > 0) {
            startUpdateThread();
        }
    }

    private void setProgressBarStarted(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setMinimum(evt.getMinimum());
            if (evt.getMaximum() > 0) {
                progressBar.setMaximum(evt.getMaximum());
            }
            progressBar.setValue(evt.getValue());
        }
    }

    private void setProgressBarPerformed(ProgressEvent evt, String filename) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
            progressBar.setToolTipText(filename);
        }
    }

    private void setProgressBarEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(isStarted() ? evt.getMaximum() : 0);
            if (tooltipTextProgressEnded != null) {
                progressBar.setToolTipText(tooltipTextProgressEnded);
            }
        }
    }

    private void updateDirectoryMapsInserterCreated(String directoryName, InsertImageFilesIntoDatabase inserter) {
        inserterOfDirectory.put(directoryName, inserter);
        directoryOfInserter.put(inserter, directoryName);
    }

    private void updateDirectoryMapsProgressEnded(InsertImageFilesIntoDatabase inserter) {
        if (inserter != null) {
            String directoryName = getDirectoryNameOfInserter(inserter);
            directoryOfInserter.remove(inserter);
            inserterOfDirectory.remove(directoryName);
        }
    }

    private void informationMessageEndUpdateDirectory(InsertImageFilesIntoDatabase scanner) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.UpdateMetadataFinished"));
        Object[] params = {getDirectoryNameOfInserter(scanner)};
        AppLog.logFinest(InsertImageFilesIntoDatabaseArray.class, message.format(params));
    }

    private void informationMessageUpdateCurrentImage(String filename) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.CheckImageForModifications"));
        Object[] params = {filename};
        AppLog.logFinest(InsertImageFilesIntoDatabaseArray.class, message.format(params));
    }

    private String getDirectoryNameOfInserter(InsertImageFilesIntoDatabase scanner) {
        String name = null;
        if (scanner != null) {
            name = directoryOfInserter.get(scanner);
        }
        return name == null ? "" : name; // NOI18N
    }
}
