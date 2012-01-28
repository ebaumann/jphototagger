package org.jphototagger.domain.metadata.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataValueProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValueProvider.class)
public final class FileMetaDataValueProvider implements MetaDataValueProvider{

    private static final Set<MetaDataValue> PROVIDED_META_DATA_VALUES = new LinkedHashSet<MetaDataValue>();

    static {
        PROVIDED_META_DATA_VALUES.add(FilesFilenameMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(FilesLastModifiedMetaDataValue.INSTANCE);
    }

    @Override
    public Collection<MetaDataValue> getProvidedValues() {
        return Collections.unmodifiableCollection(PROVIDED_META_DATA_VALUES);
    }

    @Override
    public Collection<MetaDataValueData> getMetaDataForImageFile(File file) {
        List<MetaDataValueData> metaData = new ArrayList<MetaDataValueData>();

        metaData.add(new MetaDataValueData(FilesFilenameMetaDataValue.INSTANCE, file.getAbsolutePath()));
        metaData.add(new MetaDataValueData(FilesLastModifiedMetaDataValue.INSTANCE, file.lastModified()));

        return metaData;
    }

    @Override
    public int getPosition() {
        return 5;
    }

}
