package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.DatabaseMetadataUtil;
import de.elmar_baumann.imv.database.metadata.Join;
import de.elmar_baumann.imv.database.metadata.ParamStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public class DatabaseSearch extends Database {
    
    private static final DatabaseSearch instance = new DatabaseSearch();
    
    public static DatabaseSearch getInstance() {
        return instance;
    }
    
    private DatabaseSearch() {
    }

    /**
     * Liefert Dateinamen anhand eines Statements.
     *
     * @param paramStatement Korrekt ausgefülltes Statement
     * @return Dateiname
     */
    public List<String> searchFilenames(ParamStatement paramStatement) {
        List<String> filenames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(paramStatement.getSql());
            if (paramStatement.getValues() != null) {
                for (int i = 0; i < paramStatement.getValues().length; i++) {
                    preparedStatement.setObject(i + 1, paramStatement.getValues()[i]);
                }
            }
            logStatement(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                filenames.add(resultSet.getString(1));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            filenames.clear();
        } finally {
            free(connection);
        }
        return filenames;
    }

    /**
     * Liefert alle Dateinamen, der Metadaten bestimmte Suchbegriffe enthalten.
     * Gesucht wird in allen Spalten mit TabelleA.SpalteB LIKE '%Suchbegriff%'
     * OR TabelleC.SpalteD LIKE '%Suchbegriff%' ...
     *
     * @param searchColumns Spalten, in denen der Suchbegriff vorkommen soll
     * @param searchString  Suchteilzeichenkette
     * @return              Alle gefundenen Dateinamen
     */
    public List<String> searchFilenamesLikeOr(List<Column> searchColumns, String searchString) {
        List<String> filenames = new ArrayList<String>();
        addFilenamesSearchFilenamesLikeOr(DatabaseMetadataUtil.getTableColumnsOfTableCategory(
            searchColumns, "xmp"), searchString, filenames, "xmp"); // NOI18N
        addFilenamesSearchFilenamesLikeOr(DatabaseMetadataUtil.getTableColumnsOfTableCategory(
            searchColumns, "exif"), searchString, filenames, "exif"); // NOI18N
        return filenames;
    }

    private void addFilenamesSearchFilenamesLikeOr(List<Column> searchColumns,
        String searchString, List<String> filenames, String tablename) {
        if (searchColumns.size() > 0) {
            Connection connection = null;
            try {
                connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                    getSqlSearchFilenamesLikeOr(searchColumns, tablename));
                for (int i = 0; i < searchColumns.size(); i++) {
                    preparedStatement.setString(i + 1, "%" + searchString + "%");
                }
                logStatement(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();
                String string;
                while (resultSet.next()) {
                    string = resultSet.getString(1);
                    if (!filenames.contains(string)) {
                        filenames.add(string);
                    }
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
                filenames.clear();
            } finally {
                free(connection);
            }
        }
    }

    private String getSqlSearchFilenamesLikeOr(List<Column> searchColumns, String tablename) {
        StringBuffer sql = new StringBuffer("SELECT DISTINCT files.filename FROM ");
        List<String> tablenames = DatabaseMetadataUtil.getUniqueTableNamesOfColumnArray(searchColumns);

        sql.append((tablename.equals("xmp")  // NOI18N
            ? Join.getSqlFilesXmpJoin(tablenames) // NOI18N
            : Join.getSqlFilesExifJoin(tablenames)) + // NOI18N
            " WHERE "); // NOI18N
        boolean isFirstColumn = true;
        for (Column tableColumn : searchColumns) {
            sql.append((!isFirstColumn ? " OR " : "") + // NOI18N
                tableColumn.getTable().getName() + "." + tableColumn.getName() + 
                " LIKE ?"); // NOI18N
            isFirstColumn = false;
        }
        sql.append(" ORDER BY files.filename ASC"); // NOI18N
        return sql.toString();
    }

}
