package de.elmar_baumann.imv.controller.tasks;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.event.TaskListener;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabaseArray;
import de.elmar_baumann.imv.types.DatabaseUpdate;
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
 * {@link de.elmar_baumann.imv.database.Database#getAutoscanDirectories()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ControllerAutoUpdateMetadataTask extends Controller
    implements TaskListener {

    private JProgressBar progressBar;
    private ImageMetadataToDatabaseArray updaterArray;
    private List<String> systemDirectorySubstrings = new ArrayList<String>();
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
        updaterArray.addTaskListener(this);
        super.setControl(false);
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
        updaterArray = new ImageMetadataToDatabaseArray(progressBar);
        updaterArray.setTooltipTextIfProgressEnded(AppSettings.tooltipTextProgressBarScheduledTasks);
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
    public void setControl(boolean control) {
        super.setControl(control);
        if (control) {
            startUpdate();
        } else {
            updaterArray.stop();
        }
    }

    private void startUpdate() {
        updaterArray.start();
        List<String> directories = getDirectoryNames();
        if (!directories.isEmpty()) {
            for (String directory : directories) {
                if (!isSystemDirectory(directory)) {
                    updaterArray.addDirectory(directory, DatabaseUpdate.LastModifiedChanged);
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
