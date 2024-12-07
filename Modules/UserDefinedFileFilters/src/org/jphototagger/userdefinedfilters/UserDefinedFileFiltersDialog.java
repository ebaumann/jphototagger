package org.jphototagger.userdefinedfilters;

import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class UserDefinedFileFiltersDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public UserDefinedFileFiltersDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(UserDefinedFileFiltersDialog.class, "UserDefinedFileFiltersDialog.HelpPage"));
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel = new org.jphototagger.userdefinedfilters.UserDefinedFileFiltersPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "UserDefinedFileFiltersDialog.title")); // NOI18N
        
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel.setName("panel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panel, gridBagConstraints);

        pack();
    }

    private org.jphototagger.userdefinedfilters.UserDefinedFileFiltersPanel panel;
}
