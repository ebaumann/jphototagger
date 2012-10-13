package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.applifecycle.AppUpdater;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressHandle;
import org.jphototagger.api.progress.ProgressHandleFactory;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.Version;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;
import org.jphototagger.repository.hsqldb.update.tables.DatabaseUpdateTask;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = AppUpdater.class),
    @ServiceProvider(service = DatabaseUpdateTask.class)
})
public final class DatabaseUpdateTask02 extends Database implements DatabaseUpdateTask, AppUpdater {

    private static final String KEY_UPDATED = "DatabaseUpdateTask02.Updated";
    private static final Logger LOGGER = Logger.getLogger(DatabaseUpdateTask02.class.getName());

    @Override
    public Version getUpdatesToDatabaseVersion() {
        return new Version(0, 9999, 0);
    }

    @Override
    public boolean canUpdateDatabaseVersion(Version version) {
        return true;
    }

    @Override
    public void preCreateTables() {
        // Do nothing
    }

    @Override
    public void postCreateTables() {
        try {
            update();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void update() throws SQLException {
        Connection con = null;
        try {
            con = getConnection();
            dropThumbnailColumn(con);
            addSizeInBytesColumn(con);
        } finally {
            free(con);
        }
    }

    private void dropThumbnailColumn(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "files", "thumbnail")) {
            return;
        }
        LOGGER.log(Level.INFO, "Deleting column 'thumbnail' from table 'files'");
        Database.execute(con, "ALTER TABLE files DROP COLUMN thumbnail");
    }

    private void addSizeInBytesColumn(Connection con) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, "files", "size_in_bytes")) {
            return;
        }
        LOGGER.log(Level.INFO, "Adding column 'size_in_bytes' to table 'files'");
        Database.execute(con, "ALTER TABLE files ADD COLUMN size_in_bytes BIGINT");
    }

    @Override
    public void updateToVersion(int major, int minor1, int minor2) {
        ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        if (repo.existsKey(KEY_UPDATED)) {
            return;
        }
        Logger.getLogger(DatabaseUpdateTask02.class.getName()).log(Level.INFO,
                "Updating database for all known images with the file size in bytes");
        new SizeInBytesUpdater().start();
    }

    private class SizeInBytesUpdater extends Thread {

        private SizeInBytesUpdater() {
            super("JPhotoTagger: Updating DB with file sizes");
        }

        @Override
        public void run() {
            Updater updater = new Updater();
            updater.init();
            ImageFilesRepository imageFilesRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);
            imageFilesRepo.eachImage(updater);
            updater.exit();
            if (updater.allUpdated()) {
                setUpdated();
            }
        }

        private void setUpdated() {
            ApplicationPropertiesRepository repo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
            repo.setBoolean(KEY_UPDATED, true);
        }

        private class Updater implements Functor<File>, Cancelable {

            private final ProgressHandle progressHandle = Lookup.getDefault().lookup(ProgressHandleFactory.class).createProgressHandle(this);
            private volatile boolean cancel;
            private int countUpdated = 0;
            private long fileCount;
            private ProgressEvent progressEvent;
            private Connection con;
            private PreparedStatement stmt;

            private void init() {
                ImageFilesRepository imageFilesRepo = Lookup.getDefault().lookup(ImageFilesRepository.class);
                fileCount = imageFilesRepo.getFileCount();
                progressEvent = createStartProgressEvent((int) fileCount);
                progressHandle.progressStarted(progressEvent);
                try {
                    con = getConnection();
                    String sql = "UPDATE files set size_in_bytes = ? WHERE filename = ?";
                    stmt = con.prepareStatement(sql);
                } catch (Throwable t) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, t);
                }
            }

            private void exit() {
                progressHandle.progressEnded();
                close(stmt);
                free(con);
            }

            @Override
            public void execute(File file) {
                if (!cancel && stmt != null) {
                    try {
                        stmt.setLong(1, file.length());
                        stmt.setString(2, file.getAbsolutePath());
                        LOGGER.log(Level.FINER, stmt.toString());
                        stmt.executeUpdate();
                        countUpdated++;
                        progressEvent.setValue(countUpdated);
                        progressHandle.progressPerformed(progressEvent);
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, null, t);
                    }
                }
            }

            @Override
            public void cancel() {
                cancel = true;
            }

            private boolean allUpdated() {
                return countUpdated == fileCount;
            }
        }

        private ProgressEvent createStartProgressEvent(int fileCount) {
            return new ProgressEvent.Builder()
                    .minimum(0)
                    .maximum(fileCount)
                    .value(0)
                    .stringPainted(true)
                    .stringToPaint(Bundle.getString(SizeInBytesUpdater.class, "SizeInBytesUpdater.ProgressString", fileCount))
                    .build();
        }
    }
}
