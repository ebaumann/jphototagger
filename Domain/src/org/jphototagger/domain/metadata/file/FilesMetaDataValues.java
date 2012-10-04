package org.jphototagger.domain.metadata.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class FilesMetaDataValues {

    private static final Set<MetaDataValue> VALUES = new LinkedHashSet<MetaDataValue>();

    static {
        VALUES.add(FilesFilenameMetaDataValue.INSTANCE);
        VALUES.add(FilesLastModifiedMetaDataValue.INSTANCE);
    }

    public static Collection<MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(VALUES);
    }

    private FilesMetaDataValues() {
    }
}
