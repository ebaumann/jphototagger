package org.jphototagger.program.model;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are Strings with the charset name.
 *
 * @author Elmar Baumann
 */
public final class IptcCharsetComboBoxModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = 1L;

    public IptcCharsetComboBoxModel() {
        super(new String[] { "ISO-8859-1", "UTF-8" });
    }
    
}
