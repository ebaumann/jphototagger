package org.jphototagger.program.model;

import javax.swing.DefaultComboBoxModel;
import org.jphototagger.program.data.UserDefinedFileFilter;


/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelUserDefinedFileFilterType extends DefaultComboBoxModel
         {
    private static final long serialVersionUID = 6723254193291648654L;

    public ComboBoxModelUserDefinedFileFilterType() {
        addElements();
    }

    private void addElements() {
        for (UserDefinedFileFilter.Type type : UserDefinedFileFilter.Type.values()) {
            addElement(type);
        }
    }
}
