package org.jphototagger.program.database.metadata.file;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Tabellenspalte <code>filename</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnFilesFilename extends Column {
    public static final ColumnFilesFilename INSTANCE =
        new ColumnFilesFilename();

    private ColumnFilesFilename() {
        super("filename", "files", DataType.STRING);
        setLength(512);
        setDescription(
            JptBundle.INSTANCE.getString("ColumnFilesFilename.Description"));
    }
}
