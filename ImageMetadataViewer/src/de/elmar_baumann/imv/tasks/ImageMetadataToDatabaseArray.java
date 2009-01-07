package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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
public class ImageMetadataToDatabaseArray implements ProgressListener {

    private Queue<ImageMetadataToDatabase> updaters = new ConcurrentLinkedQueue<ImageMetadataToDatabase>();
    private boolean wait = false;
    private boolean started = false;
    private JProgressBar progressBar;
    private Map<String, ImageMetadataToDatabase> updaterOfDirectory = new HashMap<String, ImageMetadataToDatabase>();
    private Map<ImageMetadataToDatabase, String> directoryOfUpdater = new HashMap<ImageMetadataToDatabase, String>();
    private List<TaskListener> taskListeners = new ArrayList<TaskListener>();
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
    synchronized public void start() {
        setStarted(true);
    }

    /**
     * Stoppt den Scan, unternimmt nichts mehr. Alle Aufgaben werden
     * verworfen. Default: true.
     */
    synchronized public void stop() {
        setStarted(false);
    }

    synchronized private boolean isWait() {
        return wait;
    }

    synchronized private void setWait(boolean wait) {
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
    synchronized public boolean isStarted() {
        return started;
    }

    /**
     * Unterbricht das Scannen eines Verzeichnisses.
     * 
     * @param directoryName Name des Verzeichnisses
     */
    synchronized public void stopUpdateOfDirectory(String directoryName) {
        ImageMetadataToDatabase scanner = updaterOfDirectory.get(directoryName);
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
     * @param update           Update-Eigenschaften
     */
    synchronized public void addDirectory(String directoryName, DatabaseUpdate update) {
        updaters.add(createUpdater(directoryName, update));
        startUpdateThread();
    }

    private ImageMetadataToDatabase createUpdater(String directoryName, DatabaseUpdate update) {
        List<String> filenames = FileUtil.getAsFilenames(
            ImageFilteredDirectory.getImageFilesOfDirectory(new File(directoryName)));
        Collections.sort(filenames);
        ImageMetadataToDatabase scanner = new ImageMetadataToDatabase(filenames, update);
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
            evt.setStop(true);
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
        ImageMetadataToDatabase scanner = (ImageMetadataToDatabase) evt.getSource();
        messageEndUpdateDirectory(scanner);
        removeUpdater(scanner);
        checkTasksCompleted();
    }

    private void removeUpdater(ImageMetadataToDatabase scanner) {
        if (scanner != null) {
            String directoryName = getDirectoryNameOfUpdater(scanner);
            directoryOfUpdater.remove(scanner);
            updaterOfDirectory.remove(directoryName);
        }
    }

    private void messageEndUpdateDirectory(ImageMetadataToDatabase scanner) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.UpdateMetadataFinished")); // NOI18N
        Object[] params = {getDirectoryNameOfUpdater(scanner)};
        Log.logFinest(ImageMetadataToDatabaseArray.class, message.format(params));
    }

    private void messageUpdateCurrentImage(String filename) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.CheckImageForModifications"));
        Object[] params = {filename};
        Log.logFinest(ImageMetadataToDatabaseArray.class, message.format(params));
    }

    private String getDirectoryNameOfUpdater(ImageMetadataToDatabase scanner) {
        String name = null;
        if (scanner != null) {
            name = directoryOfUpdater.get(scanner);
        }
        return name == null ? "" : name; // NOI18N
    }
}
