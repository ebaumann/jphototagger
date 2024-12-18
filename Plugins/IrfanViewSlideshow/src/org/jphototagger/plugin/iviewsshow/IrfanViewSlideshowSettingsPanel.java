package org.jphototagger.plugin.iviewsshow;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author  Elmar Baumann
 */
public class IrfanViewSlideshowSettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public IrfanViewSlideshowSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isReloadOnLoop = prefs.getBoolean(IrfanViewSlideshowUserPreferencesKeys.KEY_RELOAD_ON_LOOP);

        checkBoxReloadOnLoop.setSelected(isReloadOnLoop);
    }

    private void setReloadOnLoop() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(IrfanViewSlideshowUserPreferencesKeys.KEY_RELOAD_ON_LOOP, checkBoxReloadOnLoop.isSelected());
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        checkBoxReloadOnLoop = UiFactory.checkBox();
        panelFill = UiFactory.panel();
        labelVersion = UiFactory.label();

        
        setLayout(new java.awt.GridBagLayout());

        checkBoxReloadOnLoop.setText(Bundle.getString(getClass(), "IrfanViewSlideshowSettingsPanel.checkBoxReloadOnLoop.text")); // NOI18N
        checkBoxReloadOnLoop.setName("checkBoxReloadOnLoop"); // NOI18N
        checkBoxReloadOnLoop.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxReloadOnLoopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(checkBoxReloadOnLoop, gridBagConstraints);

        panelFill.setName("panelFill"); // NOI18N
        panelFill.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelFill, gridBagConstraints);

        labelVersion.setText(Bundle.getString(getClass(), "IrfanViewSlideshowSettingsPanel.labelVersion.text")); // NOI18N
        labelVersion.setName("labelVersion"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(labelVersion, gridBagConstraints);
    }

    private void checkBoxReloadOnLoopActionPerformed(java.awt.event.ActionEvent evt) {
        setReloadOnLoop();
    }

    private javax.swing.JCheckBox checkBoxReloadOnLoop;
    private javax.swing.JLabel labelVersion;
    private javax.swing.JPanel panelFill;
}
