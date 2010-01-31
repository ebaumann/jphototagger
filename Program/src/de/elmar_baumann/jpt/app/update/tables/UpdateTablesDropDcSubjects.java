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
package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.resource.Bundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-31
 */
final class UpdateTablesDropDcSubjects {

    void update(Connection connection) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "xmp_dc_subjects"))
            return;
        UpdateTablesMessages.INSTANCE.message(Bundle.getString("UpdateTablesDropDcSubjects.Info"));
        moveDcSubjects(connection);
    }

    private void moveDcSubjects(Connection connection) throws SQLException {
        String sql = "SELECT DISTINCT files.id, xmp_dc_subjects.subject" +
                " FROM xmp_dc_subjects" +
                " INNER JOIN xmp ON xmp_dc_subjects.id_xmp = xmp.id" +
                " INNER JOIN files ON xmp.id_files = files.id";
        Statement stmt = connection.createStatement();
        ResultSet rs   = stmt.executeQuery(sql);

        while (rs.next()) {
            long   idFile      = rs.getLong(1);
            String dcSubject   = rs.getString(2);
            long   idHrSubject = DatabaseKeywords.INSTANCE.getIdOfRootKeyword(dcSubject);

            if (idHrSubject >= 0) {
                insert(connection, idFile, idHrSubject);
            } else {
                Keyword kw = new Keyword(null, null, dcSubject, Boolean.TRUE);
                DatabaseKeywords.INSTANCE.insert(kw);
                if (kw.getId() < 0) throw new SQLException("Got no ID for keyword: " + dcSubject);
                insert(connection, idFile, kw.getId());
            }
        }

        stmt.close();
        stmt = connection.createStatement();
        stmt.execute("DROP TABLE xmp_dc_subjects");
        stmt.close();
    }

    private void insert(Connection connection, long idFile, long idSubject) throws SQLException  {
        String            sql = "INSERT INTO hierarchical_subjects_files" +
                                " (id_file, id_subject) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setLong(1, idFile);
        stmt.setLong(2, idSubject);
        stmt.executeUpdate();

        stmt.close();
    }


}
