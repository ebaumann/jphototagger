package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.RepositoryStatistics;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternDeletedEvent;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternInsertedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class FileExcludePatternsDatabase extends Database {

    static final FileExcludePatternsDatabase INSTANCE = new FileExcludePatternsDatabase();
    private static final Logger LOGGER = Logger.getLogger(FileExcludePatternsDatabase.class.getName());

    private FileExcludePatternsDatabase() {
    }

    /**
     * Inserts a file exclude pattern.
     *
     * @param  pattern  pattern
     * @return true if inserted
     */
    boolean insertFileExcludePattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
        boolean inserted = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO file_exclude_patterns (pattern) VALUES (?)");
            stmt.setString(1, pattern);
            LOGGER.log(Level.FINER, stmt.toString());
            int count = stmt.executeUpdate();
            con.commit();
            inserted = count > 0;
            if (inserted) {
                notifyInserted(pattern);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return inserted;
    }

    /**
     * Deletes a file exclude pattern from the database.
     *
     * @param  pattern  pattern
     * @return true if deleted
     */
    boolean deleteFileExcludePattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM file_exclude_patterns WHERE pattern = ?");
            stmt.setString(1, pattern);
            LOGGER.log(Level.FINER, stmt.toString());
            int count = stmt.executeUpdate();
            con.commit();
            deleted = count > 0;
            if (deleted) {
                notifyDeleted(pattern);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return deleted;
    }

    /**
     * Returns wheter a file exclude pattern existsValueInMetaDataValues.
     *
     * @param  pattern pattern
     * @return true if existsValueInMetaDataValues
     */
    boolean existsFileExcludePattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM file_exclude_patterns WHERE pattern = ?");
            stmt.setString(1, pattern);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    /**
     * Returns all file exclude patterns.
     *
     * @return patterns
     */
    List<String> getAllFileExcludePatterns() {
        List<String> patterns = new LinkedList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = "SELECT pattern FROM file_exclude_patterns ORDER BY pattern ASC";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patterns.add(rs.getString(1));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return patterns;
    }

    /**
     * Deletes files from the database which matches against some patterns.
     *
     * @param   patterns  patterns
     * @param   listener  progress listener or null, can cancel the action
     * @return  count of deleted files
     */
    int deleteMatchingFiles(List<String> patterns, ProgressListener listener) {
        if (patterns == null) {
            throw new NullPointerException("patterns == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmtUpdate = null;
        Statement stmtQuery = null;
        ResultSet rs = null;
        try {
            int patternCount = patterns.size();
            int progress = 0;
            RepositoryStatistics repoStatistics = Lookup.getDefault().lookup(RepositoryStatistics.class);
            ProgressEvent event = new ProgressEvent.Builder()
                    .source(this)
                    .minimum(0)
                    .maximum(repoStatistics.getFileCount() * patternCount)
                    .value(0)
                    .build();
            con = getConnection();
            con.setAutoCommit(false);
            String sqlUpdate = "DELETE FROM files WHERE filename = ?";
            String sqlQuery = "SELECT filename FROM files";
            stmtQuery = con.createStatement();
            stmtUpdate = con.prepareStatement(sqlUpdate);
            LOGGER.log(Level.FINEST, sqlQuery);
            rs = stmtQuery.executeQuery(sqlQuery);
            ThumbnailsRepository tnRepo = Lookup.getDefault().lookup(ThumbnailsRepository.class); // NO AUTOCOMMIT: ThumbnailsRepository should be a different database
            notifyProgressListenerStart(listener, event);
            boolean cancel = event.isCancel();
            while (!cancel && rs.next()) {
                String filepath = rs.getString(1);
                for (int i = 0; !cancel && (i < patternCount); i++) {
                    progress++;
                    String pattern = patterns.get(i);
                    if (filepath.matches(pattern)) {
                        stmtUpdate.setString(1, filepath);
                        LOGGER.log(Level.FINER, stmtUpdate.toString());
                        int affectedRows = stmtUpdate.executeUpdate();
                        count += affectedRows;
                        if (affectedRows > 0) {
                            File imageFile = new File(filepath);
                            tnRepo.deleteThumbnail(imageFile);
                            EventBus.publish(new ImageFileDeletedEvent(this, imageFile));
                        }
                        cancel = event.isCancel();
                    }
                    event.setInfo(filepath);
                    event.setValue(progress);
                    notifyProgressListenerPerformed(listener, event);
                }
            }
            con.commit();
            notifyProgressListenerEnd(listener, event);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(rs, stmtQuery);
            close(stmtUpdate);
            free(con);
        }
        return count;
    }

    private void notifyInserted(String pattern) {
        EventBus.publish(new FileExcludePatternInsertedEvent(this, pattern));
    }

    private void notifyDeleted(String pattern) {
        EventBus.publish(new FileExcludePatternDeletedEvent(this, pattern));
    }
}
