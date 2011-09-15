package org.jphototagger.program.model;

import javax.swing.DefaultComboBoxModel;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;

/**
 * Elements are the metadata values for the fast search - instances of
 * {@link org.jphototagger.program.database.metadata.MetaDataValue} - and a string.
 *
 * The elements retrieved through {@link FastSearchMetaDataValues#get()}. The string is
 * {@link #ALL_DEFINED_META_DATA_VALUES} and means, the fast search shall search
 * in all metadata values, else only in the selected metadata value.
 *
 * @author Elmar Baumann
 */
public final class FastSearchComboBoxModel extends DefaultComboBoxModel {

    public static final String ALL_DEFINED_META_DATA_VALUES = "AllDefined";
    private static final long serialVersionUID = -705435864208734028L;

    public FastSearchComboBoxModel() {
        addElements();
    }

    private void addElements() {
        addElement(ALL_DEFINED_META_DATA_VALUES);

        for (MetaDataValue value : FastSearchMetaDataValues.get()) {
            addElement(value);
        }
    }
}
