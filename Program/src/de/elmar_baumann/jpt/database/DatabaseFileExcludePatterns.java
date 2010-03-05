/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.event.DatabaseFileExcludePatternsEvent;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseFileExcludePatternsListener;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseFileExcludePatterns extends Database {

    public static final DatabaseFileExcludePatterns                          INSTANCE        = new DatabaseFileExcludePatterns();
    private final       ListenerSupport<DatabaseFileExcludePatternsListener> listenerSupport = new ListenerSupport<DatabaseFileExcludePatternsListener>();

    private DatabaseFileExcludePatterns() {
    }

    /**
     * Inserts a file exclude pattern.
     *
     * @param  pattern  pattern
     * @return true if inserted
     * @see    #exists(java.lang.String)
     */
    public boolean insert(String pattern) {
        boolean inserted = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                        "INSERT INTO file_exclude_pattern (pattern) VALUES (?)");
            stmt.setString(1, pattern);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count > 0;
            if (inserted) {
                notifyListeners(DatabaseFileExcludePatternsEvent.Type.PATTERN_INSERTED, pattern);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFileExcludePatterns.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                         "DELETE FROM file_exclude_pattern WHERE pattern = ?");
            stmt.setString(1, pattern);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            if (deleted) {
                notifyListeners(DatabaseFileExcludePatternsEvent.Type.PATTERN_DELETED, pattern);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFileExcludePatterns.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM file_exclude_pattern WHERE pattern = ?");
            stmt.setString(1, pattern);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFileExcludePatterns.class, ex);
        } finally {
            close(rs, stmt);
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
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            String sql = "SELECT pattern" +
                         " FROM file_exclude_pattern" +
                         " ORDER BY pattern ASC";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                patterns.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFileExcludePatterns.class, ex);
        } finally {
            close(rs, stmt);
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
        int count = 0;
        Connection        connection = null;
        PreparedStatement stmtUpdate = null;
        Statement         stmtQuery  = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            List<String> deletedFiles = new LinkedList<String>();
            String       sqlUpdate    = "DELETE FROM files WHERE filename = ?";
            String       sqlQuery     = "SELECT filename FROM files";

            stmtQuery  = connection.createStatement();
            stmtUpdate = connection.prepareStatement(sqlUpdate);
            logFinest(sqlQuery);
            rs = stmtQuery.executeQuery(sqlQuery);

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

                            deleteThumbnail(filename);
                            ImageFile deletedImageFile = new ImageFile();
                            deletedImageFile.setFilename(filename);
                            DatabaseImageFiles.INSTANCE.notifyListeners(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED, deletedImageFile);
                        }

                        stop = event.isStop();
                    }
                    event.setInfo(filename);
                    event.setValue(progress);
                    notifyProgressListenerPerformed(listener, event);
                }
            }
            connection.commit();
            notifyProgressListenerEnd(listener, event);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFileExcludePatterns.class, ex);
            rollback(connection);
        } finally {
            close(rs, stmtQuery);
            close(stmtUpdate);
            free(connection);
        }
        return count;
    }

    private void deleteThumbnail(String filename) {
        File tnFile = PersistentThumbnails.getThumbnailFileOfImageFile(filename);
        if (tnFile == null) return;

        if (!tnFile.delete()) {
            AppLogger.logWarning(getClass(), "DatabaseFileExcludePattern.Error.DeleteThumbnail", tnFile, filename);
        }
    }

    public void addListener(DatabaseFileExcludePatternsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseFileExcludePatternsListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(DatabaseFileExcludePatternsEvent.Type type, String pattern) {
        DatabaseFileExcludePatternsEvent         evt       = new DatabaseFileExcludePatternsEvent(type, pattern);
        Set<DatabaseFileExcludePatternsListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (DatabaseFileExcludePatternsListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
