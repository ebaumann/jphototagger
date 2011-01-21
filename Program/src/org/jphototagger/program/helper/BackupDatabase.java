package org.jphototagger.program.helper;

import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class BackupDatabase extends AppLifeCycle.FinalTask
        implements Runnable, Cancelable {
    public static final BackupDatabase INSTANCE         = new BackupDatabase();
    private volatile int               currentFileIndex = 0;
    private volatile int               filecount        = 0;
    private volatile boolean           cancel;
    private ProgressBarUpdater         progressBarUpdater;

    private BackupDatabase() {}

    @Override
    public void run() {
        backup();
        notifyFinished();
    }

    @Override
    public void cancel() {
        cancel = true;
    }

    @Override
    public void execute() {
        Thread thread = new Thread(INSTANCE,
                "JPhotoTagger: Backing up database");

        thread.start();
    }

    private void backup() {
        List<File> dbFiles = getDbFiles();
        List<File> tnFiles = getTnFiles();

        if (!dbFiles.isEmpty()) {
            File backupDir = getBackupDir();

            if (backupDir == null) {
                return;
            }

            File tnBackupDir =
                new File(backupDir.getAbsolutePath() + File.separator
                         + UserSettings.getThumbnailDirBasename());
            String pBarString = JptBundle.INSTANCE.getString(
                                    "BackupDatabase.ProgressBar.String");

            progressBarUpdater = new ProgressBarUpdater(this, pBarString);
            filecount          = dbFiles.size() + tnFiles.size();
            notifyProgressStarted();

            if (backup(dbFiles, backupDir) &&!cancel) {
                backup(tnFiles, tnBackupDir);
            }

            notifyProgressEnded();
        } else {
            MessageDisplayer.error(null, "BackupDatabase.Error.FileNotExists");
        }
    }

    private boolean backup(List<File> files, File toDir) {
        for (File file : files) {
            try {
                FileUtil.copyFile(file,
                                  new File(toDir + File.separator
                                           + file.getName()));
                currentFileIndex++;
                notifyProgressPerformed();

                if (cancel) {
                    return true;
                }
            } catch (IOException ex) {
                AppLogger.logSevere(BackupDatabase.class, ex);
                MessageDisplayer.error(null, "BackupDatabase.Error.Copy", file,
                                       toDir);

                return false;
            }
        }

        return true;
    }

    private List<File> getDbFiles() {
        File dbDir =
            new File(UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
        String dbPattern = Pattern.quote(UserSettings.getDatabaseBasename())
                           + ".*";
        File[] dbFileArray = dbDir.listFiles(new RegexFileFilter(dbPattern,
                                 ""));

        if (dbFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(dbFileArray);
    }

    private List<File> getTnFiles() {
        String tnPattern = ".*\\.jpeg";
        File   tnDir =
            new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());
        File[] tnFileArray = tnDir.listFiles(new RegexFileFilter(tnPattern,
                                 ""));

        if (tnFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(tnFileArray);
    }

    private File getBackupDir() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
        String     dirname =
            UserSettings.INSTANCE.getDatabaseBackupDirectoryName()
            + File.separator + df.format(new Date());
        File dir = new File(dirname);
        File tnDir = new File(dirname + File.separator
                              + UserSettings.getThumbnailDirBasename());

        if (dir.mkdir() && tnDir.mkdir()) {
            return dir;
        } else {
            MessageDisplayer.error(null, "BackupDatabase.Error.CreateDir", dir);

            return null;
        }
    }

    private void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount, 0, null);

        progressBarUpdater.progressStarted(evt);
    }

    private void notifyProgressPerformed() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount,
                                currentFileIndex, null);

        progressBarUpdater.progressPerformed(evt);
    }

    private void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount,
                                currentFileIndex, null);

        progressBarUpdater.progressEnded(evt);
    }
}
