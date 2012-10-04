package org.jphototagger.domain.metadata.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataValueProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValueProvider.class)
public final class FileMetaDataValueProvider implements MetaDataValueProvider {

    private static final List<MetaDataValue> PROVIDED_META_DATA_VALUES = new LinkedList<MetaDataValue>();

    static {
        PROVIDED_META_DATA_VALUES.add(FilesFilenameMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(FilesLastModifiedMetaDataValue.INSTANCE);
    }

    @Override
    public Collection<MetaDataValue> getProvidedValues() {
        return new ArrayList<MetaDataValue>(PROVIDED_META_DATA_VALUES);
    }

    @Override
    public Collection<MetaDataValueData> getMetaDataForImageFile(File file) {
        List<MetaDataValueData> metaData = new ArrayList<MetaDataValueData>();

        metaData.add(new MetaDataValueData(FilesFilenameMetaDataValue.INSTANCE, file.getAbsolutePath()));
        metaData.add(new MetaDataValueData(FilesLastModifiedMetaDataValue.INSTANCE, new Date(file.lastModified())));

        return metaData;
    }

    @Override
    public int getPosition() {
        return 100;
    }
}
