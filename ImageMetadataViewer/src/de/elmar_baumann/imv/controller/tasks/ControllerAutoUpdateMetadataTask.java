package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabaseArray;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * Kontrolliert: Regelmäßiger Task, der Verzeichnisse nach modifizierten
 * Metadaten scannt und bei Funden die Datenbank aktualisiert. Arbeitet
 * erst durch Aufruf von {@link #start()}.
 * 
 * Ermittelt werden die Verzeichnisse durch
 * {@link de.elmar_baumann.imv.UserSettings#getAutoscanDirectories()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerAutoUpdateMetadataTask extends Controller
    implements TaskListener {

    private JProgressBar progressBar;
    private boolean onlyTextMetadata = false;
    private ImageMetadataToDatabaseArray updaterArray;
    private List<String> systemDirectoryPatterns = new ArrayList<String>();
    private List<TaskListener> taskListeners = new ArrayList<TaskListener>();

    /**
     * Konstruktor.
     * 
     * @param progressBar  Progressbar für Fortschritt oder null, falls
     *                     keiner angezeigt werden soll
     */
    public ControllerAutoUpdateMetadataTask(JProgressBar progressBar) {
        this.progressBar = progressBar;

        init();
        listenToActionSource();
        stop();
    }

    private void listenToActionSource() {
        updaterArray.addTaskListener(this);
    }

    private void init() {
        systemDirectoryPatterns.add("System Volume Information"); // NOI18N
        systemDirectoryPatterns.add("RECYCLER"); // NOI18N
        updaterArray = new ImageMetadataToDatabaseArray(progressBar);
        updaterArray.setTooltipTextIfProgressEnded(AppSettings.tooltipTextProgressBarScheduledTasks);
    }

    private boolean isSystemDirectory(String directoryName) {
        for (String pattern : systemDirectoryPatterns) {
            if (directoryName.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        startScan();
    }

    @Override
    public void stop() {
        updaterArray.stop();
    }

    private void startScan() {
        updaterArray.start();
        List<String> directories = getDirectoryNames();
        if (!directories.isEmpty()) {
            for (String directory : directories) {
                if (!isSystemDirectory(directory)) {
                    updaterArray.addDirectory(directory, onlyTextMetadata, false);
                }
            }
        }
    }

    private List<String> getDirectoryNames() {
        List<String> directories = Database.getInstance().getAutoscanDirectories();
        List<String> subdirectories = new ArrayList<String>();
        if (UserSettings.getInstance().isAutoscanIncludeSubdirectories()) {
            for (String directory : directories) {
                subdirectories.addAll(FileUtil.getAllSubDirectoryNames(directory, UserSettings.getInstance().isAcceptHiddenDirectories()));
            }
            directories.addAll(subdirectories);
            Collections.sort(directories);
            Collections.reverse(directories);
        }
        return directories;
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

    @Override
    public void taskCompleted() {
        for (TaskListener listener : taskListeners) {
            listener.taskCompleted();
        }
    }
}
