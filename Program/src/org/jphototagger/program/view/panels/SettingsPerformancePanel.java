package org.jphototagger.program.view.panels;

import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import java.awt.Container;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsPerformancePanel extends javax.swing.JPanel
        implements Persistence {
    private static final long serialVersionUID = -422417143078270821L;

    public SettingsPerformancePanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setMaximumSecondsToTerminateExternalPrograms() {
        UserSettings.INSTANCE.setMaxSecondsToTerminateExternalPrograms((Integer) spinnerMaximumSecondsToTerminateExternalPrograms.getModel().getValue());
    }

    private void setScanForEmbeddedXmp() {
        UserSettings.INSTANCE.setScanForEmbeddedXmp(checkBoxScanForEmbeddedXmp.isSelected());
    }

    private void setSaveEarly() {
        UserSettings.INSTANCE.setSaveInputEarly(checkBoxSaveInputEarly.isSelected());
    }

    private void setDisplayIptc() {
        UserSettings.INSTANCE.setDisplayIptc(checkBoxDisplayIptc.isSelected());
    }

    private void setEnableAutocomplete() {
        UserSettings.INSTANCE.setEnableAutocomplete(checkBoxEnableAutocomplete.isSelected());
        setEnabledCheckBoxUpdateAutocomplete();
    }

    private void setUpdateAutocomplete() {
        UserSettings.INSTANCE.setUpdateAutocomplete(checkBoxUpdateAutocomplete.isSelected());
    }

    private void setAutocompleteIgnoreCase() {
        UserSettings.INSTANCE.setAutocompleteFastSearchIgnoreCase(checkBoxAutocompleteIgnoreCase.isSelected());
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;

        checkBoxDisplayIptc.setSelected(settings.isDisplayIptc());
        checkBoxScanForEmbeddedXmp.setSelected(settings.isScanForEmbeddedXmp());
        checkBoxSaveInputEarly.setSelected(settings.isSaveInputEarly());
        spinnerMaximumSecondsToTerminateExternalPrograms.getModel().setValue(settings.getMaxSecondsToTerminateExternalPrograms());
        checkBoxEnableAutocomplete.setSelected(settings.isAutocomplete());
        checkBoxUpdateAutocomplete.setSelected(settings.isUpdateAutocomplete());
        checkBoxAutocompleteIgnoreCase.setSelected(settings.isAutocompleteFastSearchIgnoreCase());
        setEnabledCheckBoxUpdateAutocomplete();
    }

    private void setEnabledCheckBoxUpdateAutocomplete() {
        checkBoxUpdateAutocomplete.setEnabled(checkBoxEnableAutocomplete.isSelected());
    }

    @Override
    public void writeProperties() {}

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelMaximumSecondsToTerminateExternalPrograms = new javax.swing.JLabel();
        spinnerMaximumSecondsToTerminateExternalPrograms = new javax.swing.JSpinner();
        checkBoxScanForEmbeddedXmp = new javax.swing.JCheckBox();
        checkBoxDisplayIptc = new javax.swing.JCheckBox();
        checkBoxSaveInputEarly = new javax.swing.JCheckBox();
        panelAutocomplete = new javax.swing.JPanel();
        checkBoxEnableAutocomplete = new javax.swing.JCheckBox();
        checkBoxUpdateAutocomplete = new javax.swing.JCheckBox();
        checkBoxAutocompleteIgnoreCase = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        labelMaximumSecondsToTerminateExternalPrograms.setLabelFor(spinnerMaximumSecondsToTerminateExternalPrograms);
        labelMaximumSecondsToTerminateExternalPrograms.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.labelMaximumSecondsToTerminateExternalPrograms.text")); // NOI18N
        labelMaximumSecondsToTerminateExternalPrograms.setName("labelMaximumSecondsToTerminateExternalPrograms"); // NOI18N

        spinnerMaximumSecondsToTerminateExternalPrograms.setModel(new javax.swing.SpinnerNumberModel(60, 10, 600, 1));
        spinnerMaximumSecondsToTerminateExternalPrograms.setName("spinnerMaximumSecondsToTerminateExternalPrograms"); // NOI18N
        spinnerMaximumSecondsToTerminateExternalPrograms.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(evt);
            }
        });

        checkBoxScanForEmbeddedXmp.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxScanForEmbeddedXmp.text")); // NOI18N
        checkBoxScanForEmbeddedXmp.setName("checkBoxScanForEmbeddedXmp"); // NOI18N
        checkBoxScanForEmbeddedXmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxScanForEmbeddedXmpActionPerformed(evt);
            }
        });

        checkBoxDisplayIptc.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxDisplayIptc.text")); // NOI18N
        checkBoxDisplayIptc.setName("checkBoxDisplayIptc"); // NOI18N
        checkBoxDisplayIptc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayIptcActionPerformed(evt);
            }
        });

        checkBoxSaveInputEarly.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxSaveInputEarly.text")); // NOI18N
        checkBoxSaveInputEarly.setName("checkBoxSaveInputEarly"); // NOI18N
        checkBoxSaveInputEarly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSaveInputEarlyActionPerformed(evt);
            }
        });

        panelAutocomplete.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsPerformancePanel.panelAutocomplete.border.title"))); // NOI18N
        panelAutocomplete.setName("panelAutocomplete"); // NOI18N

        checkBoxEnableAutocomplete.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxEnableAutocomplete.text")); // NOI18N
        checkBoxEnableAutocomplete.setName("checkBoxEnableAutocomplete"); // NOI18N
        checkBoxEnableAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEnableAutocompleteActionPerformed(evt);
            }
        });

        checkBoxUpdateAutocomplete.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxUpdateAutocomplete.text")); // NOI18N
        checkBoxUpdateAutocomplete.setName("checkBoxUpdateAutocomplete"); // NOI18N
        checkBoxUpdateAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUpdateAutocompleteActionPerformed(evt);
            }
        });

        checkBoxAutocompleteIgnoreCase.setText(JptBundle.INSTANCE.getString("SettingsPerformancePanel.checkBoxAutocompleteIgnoreCase.text")); // NOI18N
        checkBoxAutocompleteIgnoreCase.setName("checkBoxAutocompleteIgnoreCase"); // NOI18N
        checkBoxAutocompleteIgnoreCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutocompleteIgnoreCaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAutocompleteLayout = new javax.swing.GroupLayout(panelAutocomplete);
        panelAutocomplete.setLayout(panelAutocompleteLayout);
        panelAutocompleteLayout.setHorizontalGroup(
            panelAutocompleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutocompleteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAutocompleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxEnableAutocomplete)
                    .addComponent(checkBoxUpdateAutocomplete)
                    .addComponent(checkBoxAutocompleteIgnoreCase))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAutocompleteLayout.setVerticalGroup(
            panelAutocompleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAutocompleteLayout.createSequentialGroup()
                .addComponent(checkBoxEnableAutocomplete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxUpdateAutocomplete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAutocompleteIgnoreCase)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelAutocomplete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxSaveInputEarly, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxDisplayIptc, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxScanForEmbeddedXmp, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelMaximumSecondsToTerminateExternalPrograms)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMaximumSecondsToTerminateExternalPrograms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMaximumSecondsToTerminateExternalPrograms, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerMaximumSecondsToTerminateExternalPrograms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxScanForEmbeddedXmp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplayIptc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxSaveInputEarly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAutocomplete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged
        setMaximumSecondsToTerminateExternalPrograms();
    }//GEN-LAST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged

    private void checkBoxScanForEmbeddedXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxScanForEmbeddedXmpActionPerformed
        setScanForEmbeddedXmp();
    }//GEN-LAST:event_checkBoxScanForEmbeddedXmpActionPerformed

    private void checkBoxSaveInputEarlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSaveInputEarlyActionPerformed
        setSaveEarly();
    }//GEN-LAST:event_checkBoxSaveInputEarlyActionPerformed

    private void checkBoxDisplayIptcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayIptcActionPerformed
        setDisplayIptc();
    }//GEN-LAST:event_checkBoxDisplayIptcActionPerformed

    private void checkBoxEnableAutocompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxEnableAutocompleteActionPerformed
        setEnableAutocomplete();
    }//GEN-LAST:event_checkBoxEnableAutocompleteActionPerformed

    private void checkBoxUpdateAutocompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUpdateAutocompleteActionPerformed
        setUpdateAutocomplete();
    }//GEN-LAST:event_checkBoxUpdateAutocompleteActionPerformed

    private void checkBoxAutocompleteIgnoreCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutocompleteIgnoreCaseActionPerformed
        setAutocompleteIgnoreCase();
    }//GEN-LAST:event_checkBoxAutocompleteIgnoreCaseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxAutocompleteIgnoreCase;
    private javax.swing.JCheckBox checkBoxDisplayIptc;
    private javax.swing.JCheckBox checkBoxEnableAutocomplete;
    private javax.swing.JCheckBox checkBoxSaveInputEarly;
    private javax.swing.JCheckBox checkBoxScanForEmbeddedXmp;
    private javax.swing.JCheckBox checkBoxUpdateAutocomplete;
    private javax.swing.JLabel labelMaximumSecondsToTerminateExternalPrograms;
    private javax.swing.JPanel panelAutocomplete;
    private javax.swing.JSpinner spinnerMaximumSecondsToTerminateExternalPrograms;
    // End of variables declaration//GEN-END:variables
}
