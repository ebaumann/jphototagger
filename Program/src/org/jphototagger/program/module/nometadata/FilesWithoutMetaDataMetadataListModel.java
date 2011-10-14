package org.jphototagger.program.module.nometadata;

import javax.swing.DefaultListModel;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.NoMetadataValues;

/**
 * @author Elmar Baumann
 */
public final class FilesWithoutMetaDataMetadataListModel extends DefaultListModel {

    private static final long serialVersionUID = 1L;

    public FilesWithoutMetaDataMetadataListModel() {
        addMetaDataValues();
    }

    private void addMetaDataValues() {
        for (MetaDataValue value : NoMetadataValues.get()) {
            addElement(value);
        }
    }
}
