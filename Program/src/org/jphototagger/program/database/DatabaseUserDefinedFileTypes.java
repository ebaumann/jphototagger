package org.jphototagger.program.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.UserDefinedFileType;
import org.jphototagger.program.event.listener.DatabaseUserDefinedFileTypesListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseUserDefinedFileTypes extends Database {

    public static final DatabaseUserDefinedFileTypes INSTANCE = new DatabaseUserDefinedFileTypes();
    private final ListenerSupport<DatabaseUserDefinedFileTypesListener> ls = new ListenerSupport<DatabaseUserDefinedFileTypesListener>();

    public int insert(UserDefinedFileType fileType) {
        if (fileType == null) {
            throw new NullPointerException("fileType == null");
        }

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        String suffix = fileType.getSuffix();
        String description = fileType.getDescription();
        boolean externalThumbnailCreator = fileType.isExternalThumbnailCreator();

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                    "INSERT INTO user_defined_file_types (suffix, description, external_thumbnail_creator) VALUES (?, ?, ?)");
            stmt.setString(1, suffix);
            stmt.setString(2, description);
            stmt.setBoolean(3, externalThumbnailCreator);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                fileType.setId(findIdOfSuffix(con, suffix));
                notifyFileTypeInserted(fileType);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public int update(UserDefinedFileType oldFileType, UserDefinedFileType newFileType) {
        if (newFileType == null) {
            throw new NullPointerException("fileType == null");
        }

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        Long id = newFileType.getId();
        String suffix = newFileType.getSuffix();
        String description = newFileType.getDescription();
        boolean externalThumbnailCreator = newFileType.isExternalThumbnailCreator();

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                    "UPDATE user_defined_file_types SET suffix = ?, description = ?, external_thumbnail_creator = ?  WHERE id = ?");
            stmt.setString(1, suffix);
            stmt.setString(2, description);
            stmt.setBoolean(3, externalThumbnailCreator);
            stmt.setLong(4, id);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifyFileTypeUpdated(oldFileType, newFileType);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    /**
     *
     * @param  fileType only the suffix must exist
     * @return
     */
    public int delete(UserDefinedFileType fileType) {
        if (fileType == null) {
            throw new NullPointerException("fileType == null");
        }

        int count = 0;
        String suffix = fileType.getSuffix();
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM user_defined_file_types WHERE suffix = ?");
            stmt.setString(1, suffix);
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count > 0) {
                notifyFileTypeDeleted(fileType);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public List<UserDefinedFileType> getAll() {
        List<UserDefinedFileType> fileTypes = new ArrayList<UserDefinedFileType>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String sql = "SELECT id, suffix, description, external_thumbnail_creator FROM user_defined_file_types ORDER BY suffix";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserDefinedFileType fileType = new UserDefinedFileType();
                long id = rs.getLong(1);
                String suffix = rs.getString(2);
                String description = rs.getString(3);
                boolean externalThumbnailCreator = rs.getBoolean(4);

                fileType.setId(id);
                fileType.setSuffix(suffix);
                fileType.setDescription(description);
                fileType.setExternalThumbnailCreator(externalThumbnailCreator);

                fileTypes.add(fileType);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
            fileTypes.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return fileTypes;
    }

    public static int getMaxLengthSuffix() {
        return 45;
    }

    public boolean existsSuffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }

        long count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM user_defined_file_types WHERE suffix = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, suffix);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }

    public UserDefinedFileType findBySuffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }

        UserDefinedFileType fileType = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String sql = "SELECT id, description, external_thumbnail_creator FROM user_defined_file_types WHERE suffix = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, suffix);
            logFinest(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                fileType = new UserDefinedFileType();
                long id = rs.getLong(1);
                String description = rs.getString(2);
                boolean externalThumbnailCreator = rs.getBoolean(3);

                fileType.setId(id);
                fileType.setSuffix(suffix);
                fileType.setDescription(description);
                fileType.setExternalThumbnailCreator(externalThumbnailCreator);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return fileType;
    }

    private Long findIdOfSuffix(Connection con, String suffix) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long id = null;

        try {
            stmt = con.prepareStatement("SELECT id FROM user_defined_file_types WHERE suffix = ?");
            stmt.setString(1, suffix);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileTypes.class, ex);
        } finally {
            close(rs, stmt);
        }

        return id;
    }

    public void addListener(DatabaseUserDefinedFileTypesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseUserDefinedFileTypesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyFileTypeInserted(UserDefinedFileType fileType) {
        for (DatabaseUserDefinedFileTypesListener listener : ls.get()) {
            listener.fileTypeInserted(fileType);
        }
    }

    private void notifyFileTypeDeleted(UserDefinedFileType fileType) {
        for (DatabaseUserDefinedFileTypesListener listener : ls.get()) {
            listener.fileTypeDeleted(fileType);
        }
    }

    private void notifyFileTypeUpdated(UserDefinedFileType oldFileType, UserDefinedFileType newFileType) {
        for (DatabaseUserDefinedFileTypesListener listener : ls.get()) {
            listener.fileTypeUpdated(oldFileType, newFileType);
        }
    }

    private DatabaseUserDefinedFileTypes() {}
}
