package org.jphototagger.plugin.htmlreports;

import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public class HtmlReportsSettingsDialog extends DialogExt {

    private static final long serialVersionUID = 1L;

    public HtmlReportsSettingsDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel = new org.jphototagger.plugin.htmlreports.HtmlReportsSettingsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "HtmlReportsSettingsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel.setName("panel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panel, gridBagConstraints);

        pack();
    }

    private org.jphototagger.plugin.htmlreports.HtmlReportsSettingsPanel panel;
}
