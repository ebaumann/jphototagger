package org.jphototagger.program.database.metadata.file;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnFilesLastModified extends Column {
    public static final ColumnFilesLastModified INSTANCE = new ColumnFilesLastModified();

    private ColumnFilesLastModified() {
        super("lastmodified", "files", DataType.DATE);
        setDescription(JptBundle.INSTANCE.getString("ColumnFilesLastModified.Description"));
    }
}
