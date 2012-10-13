package org.jphototagger.program.module.search;

import javax.swing.DefaultComboBoxModel;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.FastSearchMetaDataValues;

/**
 * @author Elmar Baumann
 */
public final class FastSearchComboBoxModel extends DefaultComboBoxModel<Object> {

    public static final String ALL_DEFINED_META_DATA_VALUES = "AllDefined";
    private static final long serialVersionUID = 1L;

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
