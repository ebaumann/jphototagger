package org.jphototagger.exif;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataValueProvider;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifDateTimeOriginalMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValueProvider.class)
public final class ExifMetaDataValueProvider implements MetaDataValueProvider {

    private static final List<MetaDataValue> PROVIDED_META_DATA_VALUES = new LinkedList<MetaDataValue>();

    static {
        PROVIDED_META_DATA_VALUES.add(ExifDateTimeOriginalMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(ExifFocalLengthMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(ExifLensMetaDataValue.INSTANCE);
        PROVIDED_META_DATA_VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
    }

    @Override
    public Collection<MetaDataValue> getProvidedValues() {
        return new ArrayList<MetaDataValue>(PROVIDED_META_DATA_VALUES);
    }

    @Override
    public Collection<MetaDataValueData> getMetaDataForImageFile(File file) {
        try {
            Exif exif = ExifMetadata.getExifPreferCached(file);
            if (exif == null) {
                return Collections.emptyList();
            }
            List<MetaDataValueData> metaDataValueData = new ArrayList<MetaDataValueData>(PROVIDED_META_DATA_VALUES.size());
            addMetaDataValueDataIfNotNull(ExifDateTimeOriginalMetaDataValue.INSTANCE, exif.getDateTimeOriginal(), metaDataValueData);
            addMetaDataValueDataIfNotNull(ExifFocalLengthMetaDataValue.INSTANCE, exif.getFocalLengthGreaterZeroOrNull(), metaDataValueData);
            addMetaDataValueDataIfNotNull(ExifIsoSpeedRatingsMetaDataValue.INSTANCE, exif.getIsoSpeedRatings(), metaDataValueData);
            addMetaDataValueDataIfNotNull(ExifLensMetaDataValue.INSTANCE, exif.getLens(), metaDataValueData);
            addMetaDataValueDataIfNotNull(ExifRecordingEquipmentMetaDataValue.INSTANCE, exif.getRecordingEquipment(), metaDataValueData);
            return metaDataValueData;
        } catch (Throwable t) {
            Logger.getLogger(ExifMetaDataValueProvider.class.getName()).log(Level.SEVERE, null, t);
            return Collections.emptyList();
        }
    }

    private void addMetaDataValueDataIfNotNull(MetaDataValue metaDataValue, Object value, Collection<MetaDataValueData> values) {
        if (value != null) {
            values.add(new MetaDataValueData(metaDataValue, value));
        }
    }

    @Override
    public int getPosition() {
        return 20;
    }
}
