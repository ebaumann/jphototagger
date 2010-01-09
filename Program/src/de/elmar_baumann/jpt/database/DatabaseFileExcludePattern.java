/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
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
 * @version 2008-10-21
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
     * @see    #existsValueIn(java.lang.String)
     */
    public boolean insert(String pattern) {
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO file_exclude_pattern (pattern) VALUES (?)");
            stmt.setString(1, pattern);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            inserted = count > 0;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
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
    public boolean delete(String pattern) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM file_exclude_pattern WHERE pattern = ?");
            stmt.setString(1, pattern);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            deleted = count > 0;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Returns wheter a file exclude pattern existsValueIn.
     *
     * @param  pattern pattern
     * @return true if existsValueIn
     */
    public boolean exists(String pattern) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM file_exclude_pattern WHERE pattern = ?");
            stmt.setString(1, pattern);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
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
    public List<String> getAll() {
        List<String> patterns = new LinkedList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT pattern" +
                    " FROM file_exclude_pattern" +
                    " ORDER BY pattern ASC";
            logFinest(sql);
            ResultSet rs =
                    stmt.executeQuery(sql);
            while (rs.next()) {
                patterns.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
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
    public int deleteMatchingFiles(List<String> patterns, ProgressListener listener) {

        Connection connection = null;
        int count = 0;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            List<String>      deletedFiles = new LinkedList<String>();
            Statement         stmtQuery    = connection.createStatement();
            String            sqlUpdate    = "DELETE FROM files WHERE filename = ?";
            PreparedStatement stmtUpdate   = connection.prepareStatement(sqlUpdate);
            String            sqlQuery     = "SELECT filename FROM files";

            logFinest(sqlQuery);
            ResultSet rs = stmtQuery.executeQuery(sqlQuery);

            int           patternCount = patterns.size();
            int           progress     = 0;
            ProgressEvent event        = new ProgressEvent(this, 0, DatabaseStatistics.INSTANCE.getFileCount() * patternCount, 0, null);

            notifyProgressListenerStart(listener, event);

            boolean stop = event.isStop();
            while (!stop && rs.next()) {
                String filename = rs.getString(1);
                for (int i = 0; !stop && i < patternCount; i++) {
                    progress++;
                    String pattern = patterns.get(i);
                    if (filename.matches(pattern)) {

                        stmtUpdate.setString(1, filename);
                        deletedFiles.add(filename);
                        logFiner(stmtUpdate);
                        int affectedRows = stmtUpdate.executeUpdate();
                        count += affectedRows;
                        if (affectedRows > 0) {

                            PersistentThumbnails.getThumbnailFileOfImageFile(filename).delete();
                            ImageFile deletedImageFile = new ImageFile();
                            deletedImageFile.setFilename(filename);
                            notifyDatabaseListener(DatabaseImageEvent.Type.IMAGEFILE_DELETED, deletedImageFile);
                        }

                        stop = event.isStop();
                    }
                    event.setInfo(filename);
                    event.setValue(progress);
                    notifyProgressListenerPerformed(listener, event);
                }
            }
            connection.commit();
            stmtQuery.close();
            stmtUpdate.close();
            notifyProgressListenerEnd(listener, event);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
        } catch (Exception ex) {
            // regular expression exceptions are possible
            AppLog.logSevere(DatabaseFileExcludePattern.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }
}
