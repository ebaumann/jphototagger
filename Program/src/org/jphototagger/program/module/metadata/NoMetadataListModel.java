package org.jphototagger.program.module.metadata;

import javax.swing.DefaultListModel;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.NoMetadataValues;

/**
 * Elements are {@code MetaDataValue}s retrieved through {@code NoMetadataValues#get()}.
 *
 * @author Elmar Baumann
 */
public final class NoMetadataListModel extends DefaultListModel {

    private static final long serialVersionUID = 1L;

    public NoMetadataListModel() {
        addMetaDataValues();
    }

    private void addMetaDataValues() {
        for (MetaDataValue value : NoMetadataValues.get()) {
            addElement(value);
        }
    }
}