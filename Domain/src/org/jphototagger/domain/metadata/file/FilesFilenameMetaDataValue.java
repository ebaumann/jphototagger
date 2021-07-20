package org.jphototagger.domain.metadata.file;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>filename</code> der Tabelle <code>files</code>.
 *
 * @author Elmar Baumann
 */
public final class FilesFilenameMetaDataValue extends MetaDataValue {

    public static final FilesFilenameMetaDataValue INSTANCE = new FilesFilenameMetaDataValue();

    private FilesFilenameMetaDataValue() {
        super("filename", "files", ValueType.STRING);
        setValueLength(512);
        setDescription(Bundle.getString(FilesFilenameMetaDataValue.class, "FilesFilenameMetaDataValue.Description"));
    }
}
