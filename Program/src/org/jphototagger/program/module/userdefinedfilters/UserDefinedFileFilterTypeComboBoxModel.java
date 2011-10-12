package org.jphototagger.program.module.userdefinedfilters;

import javax.swing.DefaultComboBoxModel;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;

/**
 * @author Elmar Baumann
 */
public final class UserDefinedFileFilterTypeComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    public UserDefinedFileFilterTypeComboBoxModel() {
        addElements();
    }

    private void addElements() {
        for (UserDefinedFileFilter.Type type : UserDefinedFileFilter.Type.values()) {
            addElement(type);
        }
    }
}
