package org.jphototagger.xmp;

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
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MetaDataValueProvider.class)
public final class XmpMetaDataValueProvider implements MetaDataValueProvider {

    private static final List<MetaDataValue> XMP_META_DATA_VALUES = new LinkedList<MetaDataValue>(XmpMetaDataValues.get());

    @Override
    public Collection<MetaDataValue> getProvidedValues() {
        return new ArrayList<MetaDataValue>(XMP_META_DATA_VALUES);
    }

    @Override
    public Collection<MetaDataValueData> getMetaDataForImageFile(File file) {
        try {
            Xmp xmp = XmpMetadata.hasImageASidecarFile(file)
                    ? XmpMetadata.getXmpFromSidecarFileOf(file)
                    : XmpMetadata.getEmbeddedXmp(file);
            if (xmp == null) {
                return Collections.emptyList();
            }
            List<MetaDataValueData> metaDataValueData = new ArrayList<MetaDataValueData>(XMP_META_DATA_VALUES.size());
            for (MetaDataValue metaDataValue : XMP_META_DATA_VALUES) {
                Object value = xmp.getValue(metaDataValue);
                if (value != null) {
                    metaDataValueData.add(new MetaDataValueData(metaDataValue, value));
                }
            }
            return metaDataValueData;
        } catch (Throwable t) {
            Logger.getLogger(XmpMetaDataValueProvider.class.getName()).log(Level.SEVERE, null, t);
            return Collections.emptyList();
        }
    }

    @Override
    public int getPosition() {
        return 300;
    }
}
