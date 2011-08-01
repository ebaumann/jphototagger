package org.jphototagger.domain.database.file;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class ColumnFilesLastModified extends Column {
    public static final ColumnFilesLastModified INSTANCE = new ColumnFilesLastModified();

    private ColumnFilesLastModified() {
        super("lastmodified", "files", DataType.DATE);
        setDescription(Bundle.getString(ColumnFilesLastModified.class, "ColumnFilesLastModified.Description"));
    }
}
