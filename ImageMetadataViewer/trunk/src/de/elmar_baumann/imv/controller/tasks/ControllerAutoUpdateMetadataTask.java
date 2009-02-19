package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabaseArray;
import de.elmar_baumann.imv.tasks.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.tasks.Task;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * Kontrolliert: Regelmäßiger Task, der Verzeichnisse nach modifizierten
 * Metadaten scannt und bei Funden die Datenbank aktualisiert. Arbeitet
 * erst durch Aufruf von {@link #start()}.
 * 
 * Ermittelt werden die Verzeichnisse durch
 * {@link de.elmar_baumann.imv.database.Database#getAutoscanDirectories()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerAutoUpdateMetadataTask
        implements TaskListener, Task {

    private final JProgressBar progressBar;
    private InsertImageFilesIntoDatabaseArray updaterArray;
    private final List<String> systemDirectorySubstrings = new ArrayList<String>();
    private final List<TaskListener> taskListeners = new ArrayList<TaskListener>();

    /**
     * Konstruktor.
     * 
     * @param progressBar  Progressbar für Fortschritt oder null, falls
     *                     keiner angezeigt werden soll
     */
    public ControllerAutoUpdateMetadataTask(JProgressBar progressBar) {
        this.progressBar = progressBar;

        init();
        listen();
    }

    private void init() {
        addSystemDirectorySubstrings();
        createUpdaterArray();
    }

    private void addSystemDirectorySubstrings() {
        systemDirectorySubstrings.add("System Volume Information"); // NOI18N
        systemDirectorySubstrings.add("RECYCLER"); // NOI18N
    }

    private void createUpdaterArray() {
        updaterArray = new InsertImageFilesIntoDatabaseArray(progressBar);
        updaterArray.setTooltipTextIfProgressEnded(AppTexts.tooltipTextProgressBarScheduledTasks);
    }

    private boolean isSystemDirectory(String directoryName) {
        for (String substring : systemDirectorySubstrings) {
            if (directoryName.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stop() {
        updaterArray.stop();
    }

    @Override
    public void start() {
        startUpdate();
    }

    private void listen() {
        updaterArray.addTaskListener(this);
    }

    private void startUpdate() {
        updaterArray.start();
        List<String> directories = getDirectoryNames();
        if (!directories.isEmpty()) {
            for (String directory : directories) {
                if (!isSystemDirectory(directory)) {
                    updaterArray.addDirectory(directory, EnumSet.of(
                        InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE));
                }
            }
        }
    }

    private List<String> getDirectoryNames() {
        List<String> directoryNames = DatabaseAutoscanDirectories.getInstance().getAutoscanDirectories();
        addSubdirectoryNames(directoryNames);
        Collections.sort(directoryNames);
        Collections.reverse(directoryNames);
        return directoryNames;
    }

    private void addSubdirectoryNames(List<String> directoryNames) {
        List<String> subdirectoryNames = new ArrayList<String>();
        if (UserSettings.getInstance().isAutoscanIncludeSubdirectories()) {
            for (String directoryName : directoryNames) {
                subdirectoryNames.addAll(
                        FileUtil.getAllSubDirectoryNames(directoryName,
                        UserSettings.getInstance().isAcceptHiddenDirectories()));
            }
            directoryNames.addAll(subdirectoryNames);
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

    @Override
    public void taskCompleted() {
        for (TaskListener listener : taskListeners) {
            listener.taskCompleted();
        }
    }
}
