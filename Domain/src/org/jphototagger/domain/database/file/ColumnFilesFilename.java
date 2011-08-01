package org.jphototagger.domain.database.file;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

/**
 * Tabellenspalte <code>filename</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnFilesFilename extends Column {
    public static final ColumnFilesFilename INSTANCE = new ColumnFilesFilename();

    private ColumnFilesFilename() {
        super("filename", "files", DataType.STRING);
        setLength(512);
        setDescription(Bundle.getString(ColumnFilesFilename.class, "ColumnFilesFilename.Description"));
    }
}
