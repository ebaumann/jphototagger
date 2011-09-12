package org.jphototagger.program.model;

import javax.swing.DefaultListModel;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.NoMetadataValues;

/**
 * Elements are {@link MetaDataValue}s retrieved through {@link NoMetadataValues#get()}.
 *
 * @author Elmar Baumann
 */
public final class ListModelNoMetadata extends DefaultListModel {

    private static final long serialVersionUID = -1610826692746882410L;

    public ListModelNoMetadata() {
        addMetaDataValues();
    }

    private void addMetaDataValues() {
        for (MetaDataValue value : NoMetadataValues.get()) {
            addElement(value);
        }
    }
}
