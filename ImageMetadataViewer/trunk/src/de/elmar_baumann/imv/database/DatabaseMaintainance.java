package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.SubstringPosition;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseMaintainance extends Database {

    public static final DatabaseMaintainance INSTANCE =
            new DatabaseMaintainance();

    private DatabaseMaintainance() {
    }

    /**
     * Komprimiert die Datenbank.
     *
     * @return true, wenn die Datenbank erfolgreich komprimiert wurde
     */
    public boolean compressDatabase() {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CHECKPOINT DEFRAG"); // NOI18N
            success = true;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMaintainance.class, ex);
        } finally {
            free(connection);
        }
        return success;
    }

    /**
     * Deletes from the thumbnail's folder all thumbnails without a image file
     * in the database.
     *
     * @param  listener listener: gets the progress and can cancel the
     *         operation through calling {@link ProgressEvent#stop()}.
     *         {@link ProgressEvent#getInfo()} returns the count deleted deleted
     *         thumbnails.
     * @return count of deleted thumbnails
     */
    public int deleteUnusedThumbnails(ProgressListener listener) {
        int delCount = 0;
        Connection connection = null;
        ProgressEvent progressEvent = new ProgressEvent(this, "");
        try {
            try {
                File[] thumbnailFiles = getThumbnailFiles();
                if (thumbnailFiles != null) {
                    connection = getConnection();
                    String sql = "SELECT COUNT(*) FROM files WHERE id = ?"; // NOI18N
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    int fileCount = thumbnailFiles.length;
                    progressEvent =
                            new ProgressEvent(this, 0, fileCount, 0, ""); // NOI18N
                    listener.progressStarted(progressEvent);
                    boolean stop = progressEvent.isStop();
                    int index = 0;
                    while (!stop && index < fileCount) {
                        File thumbnailFile = thumbnailFiles[index];
                        long fileId = getImageIdFromThumbnailFile(thumbnailFile);
                        stmt.setLong(1, fileId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            if (rs.getLong(1) <= 0) {
                                logThumbnailDeleted(thumbnailFile);
                                if (ThumbnailUtil.deleteThumbnail(fileId))
                                    delCount++;
                            }
                        }
                        index++;
                        progressEvent.setValue(index);
                        progressEvent.setInfo(Integer.valueOf(delCount));
                        listener.progressPerformed(progressEvent);
                        stop = progressEvent.isStop();
                    }
                    stmt.close();
                }
            } catch (SQLException ex) {
                AppLog.logSevere(DatabaseMaintainance.class, ex);
            } finally {
                free(connection);
            }
        } catch (Exception ex) {
            AppLog.logWarning(DatabaseMaintainance.class, ex);
        }
        listener.progressEnded(progressEvent);
        return delCount;
    }

    private long getImageIdFromThumbnailFile(File thumbnailFile) {
        try {
            return Long.valueOf(thumbnailFile.getName());
        } catch (Exception ex) {
            AppLog.logWarning(DatabaseMaintainance.class, ex);
        }
        return -1;
    }

    private File[] getThumbnailFiles() {
        File tnDir =
                new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());
        return tnDir.isDirectory()
               ? tnDir.listFiles(new RegexFileFilter(".*[0-9].*", ";")) // NOI18N
               : new File[]{};
    }

    private void logThumbnailDeleted(File thumbnailFile) {
        AppLog.logInfo(DatabaseMaintainance.class,
                Bundle.getString(
                "DatabaseMaintainance.Information.deleteThumbnailsWithoutImageFiles", // NOI18N
                thumbnailFile.getAbsolutePath()));
    }

    /**
     * Replaces in a column all strings or substrings with another string.
     *
     * @param  column      column <em>has to be of the type</em>
     *                     {@link Column#dataType}
     * @param  search      string to replace
     * @param  replacement string that replaces <code>search</code>
     * @param  pos         position of the string to search
     * @return             count of changed strings (affected rows)
     */
    public int replaceString(Column column, String search, String replacement,
            SubstringPosition pos) {
        int affectedRows = 0;
        if (!column.getDataType().equals(Column.DataType.STRING))
            return 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            String quotedSearch = escapeStringForQuotes(search);
            String quotedReplacement = escapeStringForQuotes(replacement);
            String sql = "UPDATE " + tableName + " SET " + columnName + // NOI18N
                    " = REPLACE(" + columnName + ", '" + quotedSearch + "', '" + // NOI18N
                    quotedReplacement + "') WHERE " + columnName + " " + // NOI18N
                    SubstringPosition.getSqlFilterOperator(pos) + " ?"; // NOI18N;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, SubstringPosition.getSqlFilter(pos, search));
            AppLog.logFiner(DatabaseMaintainance.class, stmt.toString());
            affectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return affectedRows;
    }

    private String escapeStringForQuotes(String s) {
        return s.replace("'", "\\'"); // NOI18N
    }
}
