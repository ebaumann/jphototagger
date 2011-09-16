package org.jphototagger.domain.metadata.file;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>lastmodified</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class FilesLastModifiedMetaDataValue extends MetaDataValue {

    public static final FilesLastModifiedMetaDataValue INSTANCE = new FilesLastModifiedMetaDataValue();

    private FilesLastModifiedMetaDataValue() {
        super("lastmodified", "files", ValueType.DATE);
        setDescription(Bundle.getString(FilesLastModifiedMetaDataValue.class, "FilesLastModifiedMetaDataValue.Description"));
    }
}
