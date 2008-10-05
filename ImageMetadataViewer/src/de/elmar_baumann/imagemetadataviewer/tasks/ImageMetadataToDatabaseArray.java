package de.elmar_baumann.imagemetadataviewer.tasks;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.event.TaskListener;
import de.elmar_baumann.imagemetadataviewer.io.ImageFilteredDirectory;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

/**
 * Scannt Verzeichnisse nach Bilddateien und aktualisiert die Datenbank mit
 * den Bild-Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/11
 */
public class ImageMetadataToDatabaseArray implements ProgressListener {

    private Stack<ImageMetadataToDatabase> updaters = new Stack<ImageMetadataToDatabase>();
    private boolean wait = false;
    private boolean started = false;
    private JProgressBar progressBar;
    private HashMap<String, ImageMetadataToDatabase> updaterOfDirectory = new HashMap<String, ImageMetadataToDatabase>();
    private HashMap<ImageMetadataToDatabase, String> directoryOfUpdater = new HashMap<ImageMetadataToDatabase, String>();
    private ArrayList<TaskListener> taskListeners = new ArrayList<TaskListener>();
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

    private boolean isWait() {
        return wait;
    }

    private void setWait(boolean wait) {
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

    /**
     * Entfernt einen Task-Beobachter.
     * 
     * @param listener  Beobachter
     */
    public void removeTaskListener(TaskListener listener) {
        taskListeners.remove(listener);
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
     * @param onlyTextMetadata true, wenn nur Textmetadaten aktualisiert werden
     *                         sollen und keine Thumbnails
     * @param force            true, wenn auch aktuelle Bilddateien aktualisiert
     *                         werden sollen, also <em>jede</em> Bilddatei
     */
    synchronized public void addDirectory(String directoryName,
        boolean onlyTextMetadata, boolean force) {
        updaters.push(createUpdater(directoryName, onlyTextMetadata, force));
        startUpdateThread();
    }

    private ImageMetadataToDatabase createUpdater(String directoryName,
        boolean onlyTextMetadata, boolean force) {
        ArrayList<String> filenames = ImageFilteredDirectory.getImageFilenamesOfDirectory(directoryName);
        Collections.sort(filenames);
        int thumbnailLength = UserSettings.getInstance().getMaxThumbnailLength();
        ImageMetadataToDatabase scanner =
            new ImageMetadataToDatabase(filenames, thumbnailLength);
        scanner.setCreateThumbnails(!onlyTextMetadata);
        scanner.setForceUpdate(force);
        scanner.addProgressListener(this);
        updaterOfDirectory.put(directoryName, scanner);
        directoryOfUpdater.put(scanner, directoryName);
        return scanner;
    }

    private synchronized void startUpdateThread() {
        if (!isWait()) {
            setWait(true);
            Thread thread = new Thread(updaters.pop());
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
            updaters.removeAllElements();
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
        Logger.getLogger(ImageMetadataToDatabaseArray.class.getName()).log(Level.FINE, message.format(params));
    }

    private void messageUpdateCurrentImage(String filename) {
        MessageFormat message = new MessageFormat(Bundle.getString("ImageMetadataToDatabaseArray.InformationMessage.CheckImageForModifications"));
        Object[] params = {filename};
        Logger.getLogger(ImageMetadataToDatabaseArray.class.getName()).log(Level.FINEST, message.format(params));
    }

    private String getDirectoryNameOfUpdater(ImageMetadataToDatabase scanner) {
        String name = null;
        if (scanner != null) {
            name = directoryOfUpdater.get(scanner);
        }
        return name == null ? "" : name; // NOI18N
    }
}
