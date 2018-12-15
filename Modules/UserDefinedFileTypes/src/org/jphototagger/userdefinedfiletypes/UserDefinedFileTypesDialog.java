package org.jphototagger.userdefinedfiletypes;

import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class UserDefinedFileTypesDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public UserDefinedFileTypesDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(UserDefinedFileTypesDialog.class, "UserDefinedFileTypesDialog.HelpPage"));
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelUserDefinedFileTypes = new org.jphototagger.userdefinedfiletypes.UserDefinedFileTypesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "UserDefinedFileTypesDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelUserDefinedFileTypes, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private org.jphototagger.userdefinedfiletypes.UserDefinedFileTypesPanel panelUserDefinedFileTypes;
}
