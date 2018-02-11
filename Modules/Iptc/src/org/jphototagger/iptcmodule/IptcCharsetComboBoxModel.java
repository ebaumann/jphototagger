package org.jphototagger.iptcmodule;

import javax.swing.DefaultComboBoxModel;

/**
 * Elements are Strings with the charset name.
 *
 * @author Elmar Baumann
 */
public final class IptcCharsetComboBoxModel extends DefaultComboBoxModel<Object> {

    private static final long serialVersionUID = 1L;

    public IptcCharsetComboBoxModel() {
        super(new String[] { "UTF-8", "ISO-8859-1" });
    }
}
