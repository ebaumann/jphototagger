package org.jphototagger.program.helper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.ProgressEvent;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class BackupDatabase extends AppLifeCycle.FinalTask implements Runnable, Cancelable {

    public static final BackupDatabase INSTANCE = new BackupDatabase();
    private volatile int currentFileIndex = 0;
    private volatile int filecount = 0;
    private volatile boolean cancel;
    private ProgressBarUpdater progressBarUpdater;

    private BackupDatabase() {
    }

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
        Thread thread = new Thread(INSTANCE, "JPhotoTagger: Backing up database");

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

            UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
            File tnBackupDir = new File(backupDir.getAbsolutePath() + File.separator + provider.getThumbnailsDirectoryBasename());
            String pBarString = Bundle.getString(BackupDatabase.class, "BackupDatabase.ProgressBar.String");

            progressBarUpdater = new ProgressBarUpdater(this, pBarString);
            filecount = dbFiles.size() + tnFiles.size();
            notifyProgressStarted();

            if (backup(dbFiles, backupDir) && !cancel) {
                backup(tnFiles, tnBackupDir);
            }

            notifyProgressEnded();
        } else {
            String message = Bundle.getString(BackupDatabase.class, "BackupDatabase.Error.FileNotExists");
            MessageDisplayer.error(null, message);
        }
    }

    private boolean backup(List<File> files, File toDir) {
        for (File file : files) {
            try {
                FileUtil.copyFile(file, new File(toDir + File.separator + file.getName()));
                currentFileIndex++;
                notifyProgressPerformed();

                if (cancel) {
                    return true;
                }
            } catch (IOException ex) {
                Logger.getLogger(BackupDatabase.class.getName()).log(Level.SEVERE, null, ex);
                String message = Bundle.getString(BackupDatabase.class, "BackupDatabase.Error.Copy", file, toDir);
                MessageDisplayer.error(null, message);

                return false;
            }
        }

        return true;
    }

    private List<File> getDbFiles() {
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File dbDir = provider.getDefaultDatabaseDirectory();
        String dbPattern = Pattern.quote(provider.getDatabaseBasename()) + ".*";
        File[] dbFileArray = dbDir.listFiles(new RegexFileFilter(dbPattern, ""));

        if (dbFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(dbFileArray);
    }

    private List<File> getTnFiles() {
        String tnPattern = ".*\\.jpeg";
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        File tnDir = provider.getThumbnailsDirectory();
        File[] tnFileArray = tnDir.listFiles(new RegexFileFilter(tnPattern, ""));

        if (tnFileArray == null) {
            return new ArrayList<File>();
        }

        return Arrays.asList(tnFileArray);
    }

    private File getBackupDir() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        String backupDirname = provider.getDatabaseBackupDirectory().getAbsolutePath();
        String dirname = backupDirname + File.separator + df.format(new Date());
        File dir = new File(dirname);
        File tnDir = new File(dirname + File.separator + provider.getThumbnailsDirectoryBasename());

        if (dir.mkdir() && tnDir.mkdir()) {
            return dir;
        } else {
            String message = Bundle.getString(BackupDatabase.class, "BackupDatabase.Error.CreateDir", dir);
            MessageDisplayer.error(null, message);

            return null;
        }
    }

    private void notifyProgressStarted() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount, 0, null);

        progressBarUpdater.progressStarted(evt);
    }

    private void notifyProgressPerformed() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount, currentFileIndex, null);

        progressBarUpdater.progressPerformed(evt);
    }

    private void notifyProgressEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0, filecount, currentFileIndex, null);

        progressBarUpdater.progressEnded(evt);
    }
}
