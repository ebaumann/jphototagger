package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseFileExcludePattern extends Database {

    public static final DatabaseFileExcludePattern INSTANCE =
            new DatabaseFileExcludePattern();

    private DatabaseFileExcludePattern() {
    }

    /**
     * Inserts a file exclude pattern.
     *
     * @param  pattern  pattern
     * @return true if inserted
     * @see    #existsFileExcludePattern(java.lang.String)
     */
    public boolean insertFileExcludePattern(String pattern) {
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO file_exclude_pattern (pattern) VALUES (?)"); // NOI18N
            stmt.setString(1, pattern);
            AppLog.logFiner(DatabaseFileExcludePattern.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            inserted = count > 0;
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Deletes a file exclude pattern from the database.
     *
     * @param  pattern  pattern
     * @return true if deleted
     */
    public boolean deleteFileExcludePattern(String pattern) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM file_exclude_pattern WHERE pattern = ?"); // NOI18N
            stmt.setString(1, pattern);
            AppLog.logFiner(DatabaseFileExcludePattern.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            deleted = count > 0;
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Returns wheter a file exclude pattern exists.
     *
     * @param  pattern pattern
     * @return true if exists
     */
    public boolean existsFileExcludePattern(String pattern) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt =
                    connection.prepareStatement(
                    "SELECT COUNT(*) FROM file_exclude_pattern WHERE pattern = ?"); // NOI18N
            stmt.setString(1, pattern);
            AppLog.logFinest(DatabaseFileExcludePattern.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns all file exclude patterns.
     *
     * @return patterns
     */
    public List<String> getFileExcludePatterns() {
        List<String> patterns = new LinkedList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs =
                    stmt.executeQuery(
                    "SELECT pattern FROM file_exclude_pattern ORDER BY pattern ASC"); // NOI18N
            while (rs.next()) {
                patterns.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return patterns;
    }

    /**
     * Deletes files from the database which matches against some patterns.
     *
     * @param   patterns  patterns
     * @param   listener  progress listener, can cancel the action
     * @return  count of deleted files
     */
    public int deleteFilesWithPattern(List<String> patterns,
            ProgressListener listener) {

        Connection connection = null;
        int count = 0;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            List<String> deletedFiles = new LinkedList<String>();
            Statement queryStmt = connection.createStatement();
            PreparedStatement updateStmt = connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?"); // NOI18N
            ResultSet rs = queryStmt.executeQuery("SELECT filename FROM files"); // NOI18N
            int patternCount = patterns.size();
            int progress = 0;
            ProgressEvent event =
                    new ProgressEvent(this, 0, DatabaseStatistics.INSTANCE.
                    getFileCount() * patternCount, 0, null);
            notifyProgressListenerStart(listener, event);
            boolean stop = event.isStop();
            while (!stop && rs.next()) {
                String filename = rs.getString(1);
                for (int i = 0; !stop && i < patternCount; i++) {
                    progress++;
                    String pattern = patterns.get(i);
                    if (filename.matches(pattern)) {
                        updateStmt.setString(1, filename);
                        deletedFiles.add(filename);
                        AppLog.logFiner(DatabaseFileExcludePattern.class,
                                updateStmt.toString());
                        int affectedRows = updateStmt.executeUpdate();
                        count += affectedRows;
                        if (affectedRows > 0) {
                            ImageFile deletedImageFile = new ImageFile();
                            deletedImageFile.setFilename(filename);
                            notifyDatabaseListener(
                                    DatabaseAction.Type.IMAGEFILE_DELETED,
                                    deletedImageFile);
                        }

                        stop = event.isStop();
                    }
                    event.setInfo(filename);
                    event.setValue(progress);
                    notifyProgressListenerPerformed(listener, event);
                }
            }
            connection.commit();
            queryStmt.close();
            updateStmt.close();
            notifyProgressListenerEnd(listener, event);
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } catch (Exception ex) {
            // regular expression exceptions are possible
            AppLog.logWarning(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }
}
