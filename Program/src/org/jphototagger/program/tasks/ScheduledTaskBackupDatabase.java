package org.jphototagger.program.tasks;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLifeCycle.FinalTaskListener;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.helper.BackupDatabase;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ScheduledTaskBackupDatabase implements FinalTaskListener {
    public static final String                      CHARSET_INFO_FILE = "ASCII";
    private static final String                     DATE_FORMAT = "yyyy-MM-dd";
    private static final String                     FILENAME_LAST_BACKUP =
        "LastBackup";
    private static final long                       MILLISECONDS_PER_DAY =
        86400000;
    public static final ScheduledTaskBackupDatabase INSTANCE =
        new ScheduledTaskBackupDatabase();

    private ScheduledTaskBackupDatabase() {}

    public enum Interval {
        PER_SESSION(
            0, JptBundle.INSTANCE.getString(
                "ScheduledTaskBackupDatabase.Interval.Session")),
        PER_DAY(
            1, JptBundle.INSTANCE.getString(
                "ScheduledTaskBackupDatabase.Interval.Day")),
        PER_WEEK(
            7, JptBundle.INSTANCE.getString(
                "ScheduledTaskBackupDatabase.Interval.Week")), PER_MONTH(
                    30,
                    JptBundle.INSTANCE.getString(
                        "ScheduledTaskBackupDatabase.Interval.Month")),
        ;

        private final int    days;
        private final String displayName;

        private Interval(int days, String displayName) {
            this.days        = days;
            this.displayName = displayName;
        }

        public int getDays() {
            return days;
        }

        public static Interval fromDays(int days) {
            for (Interval interval : values()) {
                if (interval.getDays() == days) {
                    return interval;
                }
            }

            return null;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    @Override
    public void finished() {
        createInfoFile();
    }

    public void setBackup() {
        if (isUpdate()) {
            BackupDatabase.INSTANCE.addListener(this);
            AppLifeCycle.INSTANCE.addFinalTask(BackupDatabase.INSTANCE);
        } else {
            BackupDatabase.INSTANCE.removeListener(this);
            AppLifeCycle.INSTANCE.removeFinalTask(BackupDatabase.INSTANCE);
        }
    }

    private void createInfoFile() {
        String           infoFileName = getInfoFileName();
        DateFormat       df           = new SimpleDateFormat(DATE_FORMAT);
        String           dateString   = df.format(new Date());
        FileOutputStream fos          = null;

        try {
            fos = new FileOutputStream(infoFileName);
            fos.write(dateString.getBytes(CHARSET_INFO_FILE));
            fos.flush();
        } catch (Exception ex) {
            AppLogger.logSevere(ScheduledTaskBackupDatabase.class, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    AppLogger.logSevere(ScheduledTaskBackupDatabase.class, ex);
                }
            }
        }
    }

    private static boolean isUpdate() {
        if (!UserSettings.INSTANCE.isScheduledBackupDb()) {
            return false;
        }

        String infoFileName = getInfoFileName();

        if (FileUtil.existsFile(infoFileName)) {
            return getDaysEllapsed(new File(infoFileName))
                   >= UserSettings.INSTANCE.getScheduledBackupDbInterval();
        } else {
            return true;
        }
    }

    private static String getInfoFileName() {
        return UserSettings.INSTANCE.getDatabaseDirectoryName()
               + File.separator + FILENAME_LAST_BACKUP;
    }

    // Whole days ellapsed
    private static long getDaysEllapsed(File infoFile) {
        String dateString = null;

        try {
            dateString = FileUtil.getContentAsString(infoFile,
                    CHARSET_INFO_FILE);
        } catch (IOException ex) {
            Logger.getLogger(ScheduledTaskBackupDatabase.class.getName()).log(
                Level.SEVERE, null, ex);

            return -1;
        }

        if (dateString == null) {
            return -1;
        }

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);

        try {
            Date lastBackup = df.parse(dateString);
            Date today      = new Date();
            long diff       = today.getTime() - lastBackup.getTime();

            return diff / MILLISECONDS_PER_DAY;
        } catch (Exception ex) {
            AppLogger.logSevere(ScheduledTaskBackupDatabase.class, ex);
        }

        return -1;
    }
}
