package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.view.panels.ProgressBarScheduledTasks;
import de.elmar_baumann.lib.concurrent.SerialExecutor;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Kontrolliert: Regelmäßiger Task, der Verzeichnisse nach modifizierten
 * Metadaten scannt und bei Funden die Datenbank aktualisiert. Arbeitet
 * erst durch Aufruf von {@link #start()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ScheduledTasks {

    public static final ScheduledTasks INSTANCE =
            new ScheduledTasks();
    private final List<String> systemDirectorySubstrings =
            new ArrayList<String>();
    private final SerialExecutor executor =
            new SerialExecutor(Executors.newCachedThreadPool());
    private static final long WAIT_BEFORE_PERFORM_MILLISECONDS =
            UserSettings.INSTANCE.getMinutesToStartScheduledTasks() * 1000 * 60;

    private ScheduledTasks() {
        init();
    }

    /**
     * Runs the tasks.
     */
    public void run() {
        if (WAIT_BEFORE_PERFORM_MILLISECONDS <= 0) return;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_BEFORE_PERFORM_MILLISECONDS);
                    startUpdate();
                } catch (Exception ex) {
                    AppLog.logSevere(getClass(), ex);
                }
            }
        });
        thread.setName(
                "Scheduled tasks waiting for start @ " + getClass().getName());
        thread.start();
    }

    /**
     * Returns the count of scheduled tasks.
     *
     * @return count of scheduled tasks
     */
    public int getCount() {
        return executor.getCount();
    }

    /**
     * Removes all added user tasks and calls {@link Thread#interrupt()} of the
     * currently running runnable if it's an instance of
     * <code>java.lang.Thread</code>.
     *
     * Thus means: The currently running task stops only when it is a thread
     * that will periodically check {@link Thread#isInterrupted()}.
     */
    public void shutdown() {
        executor.shutdown();
    }

    private void startUpdate() {
        List<File> directories = getDirectories();
        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    executor.execute(new InsertImageFilesIntoDatabase(
                            getImageFilenamesOfDirectory(directory),
                            EnumSet.of(
                            InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE),
                            ProgressBarScheduledTasks.INSTANCE));
                }
            }
        }
    }

    private List<String> getImageFilenamesOfDirectory(File directory) {
        return FileUtil.getAsFilenames(
                ImageFilteredDirectory.getImageFilesOfDirectory(directory));
    }

    private List<File> getDirectories() {
        List<String> directoryNames =
                DatabaseAutoscanDirectories.INSTANCE.getAutoscanDirectories();
        addSubdirectoryNames(directoryNames);
        Collections.sort(directoryNames);
        Collections.reverse(directoryNames);
        return FileUtil.getAsFiles(directoryNames);
    }

    private void addSubdirectoryNames(List<String> directoryNames) {
        List<String> subdirectoryNames = new ArrayList<String>();
        if (UserSettings.INSTANCE.isAutoscanIncludeSubdirectories()) {
            for (String directoryName : directoryNames) {
                subdirectoryNames.addAll(
                        FileUtil.getAllSubDirectoryNames(directoryName,
                        UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
            }
            directoryNames.addAll(subdirectoryNames);
        }
    }

    private void addSystemDirectorySubstrings() {
        systemDirectorySubstrings.add("System Volume Information"); // NOI18N
        systemDirectorySubstrings.add("RECYCLER"); // NOI18N
    }

    private boolean isSystemDirectory(String directoryName) {
        for (String substring : systemDirectorySubstrings) {
            if (directoryName.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    private void init() {
        addSystemDirectorySubstrings();
    }
}
