package org.jphototagger.program.misc;

import java.awt.Container;

import javax.swing.SpinnerModel;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.settings.AppPreferencesKeys;

/**
 * @author Elmar Baumann
 */
public final class PerformanceSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;

    public PerformanceSettingsPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setMaximumSecondsToTerminateExternalPrograms() {
        SpinnerModel model = spinnerMaximumSecondsToTerminateExternalPrograms.getModel();
        Integer seconds = (Integer) model.getValue();

        setMaxSecondsToTerminateExternalPrograms(seconds);
    }

    private void setMaxSecondsToTerminateExternalPrograms(Integer seconds) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS, seconds);
    }

    private void setScanForEmbeddedXmp() {
        setScanForEmbeddedXmp(checkBoxScanForEmbeddedXmp.isSelected());
    }

    private void setScanForEmbeddedXmp(boolean scan) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP, scan);
    }

    private void setSaveEarly() {
        setSaveInputEarly(checkBoxSaveInputEarly.isSelected());
    }

    private void setSaveInputEarly(boolean early) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY, early);
    }

    private void setEnableAutocomplete() {
        setEnableAutocomplete(checkBoxEnableAutocomplete.isSelected());
        setEnabledCheckBoxUpdateAutocomplete();
    }

    private void setEnableAutocomplete(boolean enable) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE, enable);
    }

    private void setUpdateAutocomplete() {
        setUpdateAutocomplete(checkBoxUpdateAutocomplete.isSelected());
    }

    private void setUpdateAutocomplete(boolean update) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE, update);
    }

    private void setAutocompleteIgnoreCase() {
        setAutocompleteFastSearchIgnoreCase(checkBoxAutocompleteIgnoreCase.isSelected());
    }

    private void setAutocompleteFastSearchIgnoreCase(boolean ignore) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE, ignore);
    }

    @Override
    public void restore() {
        checkBoxScanForEmbeddedXmp.setSelected(isScanForEmbeddedXmp());
        checkBoxSaveInputEarly.setSelected(isSaveInputEarly());
        spinnerMaximumSecondsToTerminateExternalPrograms.getModel().setValue(getMaxSecondsToTerminateExternalPrograms());
        checkBoxEnableAutocomplete.setSelected(isAutocomplete());
        checkBoxUpdateAutocomplete.setSelected(isUpdateAutocomplete());
        checkBoxAutocompleteIgnoreCase.setSelected(isAutocompleteFastSearchIgnoreCase());
        setEnabledCheckBoxUpdateAutocomplete();
    }

    private boolean isAutocompleteFastSearchIgnoreCase() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : false;
    }

    private boolean isUpdateAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    private boolean isAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private int getMaxSecondsToTerminateExternalPrograms() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                ? prefs.getInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                : 60;
    }

    private boolean isSaveInputEarly() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                : true;
    }

    private boolean isScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    private void setEnabledCheckBoxUpdateAutocomplete() {
        checkBoxUpdateAutocomplete.setEnabled(checkBoxEnableAutocomplete.isSelected());
    }

    @Override
    public void persist() {}

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(PerformanceSettingsPanel.class, "PerformanceSettingsPanel.HelpPage");
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelMaximumSecondsToTerminateExternalPrograms = new javax.swing.JLabel();
        spinnerMaximumSecondsToTerminateExternalPrograms = new javax.swing.JSpinner();
        checkBoxScanForEmbeddedXmp = new javax.swing.JCheckBox();
        checkBoxSaveInputEarly = new javax.swing.JCheckBox();
        panelAutocomplete = new javax.swing.JPanel();
        checkBoxEnableAutocomplete = new javax.swing.JCheckBox();
        checkBoxUpdateAutocomplete = new javax.swing.JCheckBox();
        checkBoxAutocompleteIgnoreCase = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        labelMaximumSecondsToTerminateExternalPrograms.setLabelFor(spinnerMaximumSecondsToTerminateExternalPrograms);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/misc/Bundle"); // NOI18N
        labelMaximumSecondsToTerminateExternalPrograms.setText(bundle.getString("PerformanceSettingsPanel.labelMaximumSecondsToTerminateExternalPrograms.text")); // NOI18N
        labelMaximumSecondsToTerminateExternalPrograms.setName("labelMaximumSecondsToTerminateExternalPrograms"); // NOI18N

        spinnerMaximumSecondsToTerminateExternalPrograms.setModel(new javax.swing.SpinnerNumberModel(60, 10, 600, 1));
        spinnerMaximumSecondsToTerminateExternalPrograms.setName("spinnerMaximumSecondsToTerminateExternalPrograms"); // NOI18N
        spinnerMaximumSecondsToTerminateExternalPrograms.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(evt);
            }
        });

        checkBoxScanForEmbeddedXmp.setText(bundle.getString("PerformanceSettingsPanel.checkBoxScanForEmbeddedXmp.text")); // NOI18N
        checkBoxScanForEmbeddedXmp.setName("checkBoxScanForEmbeddedXmp"); // NOI18N
        checkBoxScanForEmbeddedXmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxScanForEmbeddedXmpActionPerformed(evt);
            }
        });

        checkBoxSaveInputEarly.setText(bundle.getString("PerformanceSettingsPanel.checkBoxSaveInputEarly.text")); // NOI18N
        checkBoxSaveInputEarly.setName("checkBoxSaveInputEarly"); // NOI18N
        checkBoxSaveInputEarly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSaveInputEarlyActionPerformed(evt);
            }
        });

        panelAutocomplete.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PerformanceSettingsPanel.panelAutocomplete.border.title"))); // NOI18N
        panelAutocomplete.setName("panelAutocomplete"); // NOI18N

        checkBoxEnableAutocomplete.setText(bundle.getString("PerformanceSettingsPanel.checkBoxEnableAutocomplete.text")); // NOI18N
        checkBoxEnableAutocomplete.setName("checkBoxEnableAutocomplete"); // NOI18N
        checkBoxEnableAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEnableAutocompleteActionPerformed(evt);
            }
        });

        checkBoxUpdateAutocomplete.setText(bundle.getString("PerformanceSettingsPanel.checkBoxUpdateAutocomplete.text")); // NOI18N
        checkBoxUpdateAutocomplete.setName("checkBoxUpdateAutocomplete"); // NOI18N
        checkBoxUpdateAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUpdateAutocompleteActionPerformed(evt);
            }
        });

        checkBoxAutocompleteIgnoreCase.setText(bundle.getString("PerformanceSettingsPanel.checkBoxAutocompleteIgnoreCase.text")); // NOI18N
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
                .addGap(23, 23, 23)
                .addComponent(checkBoxSaveInputEarly)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelAutocomplete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents

    private void spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged
        setMaximumSecondsToTerminateExternalPrograms();
    }//GEN-LAST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged

    private void checkBoxScanForEmbeddedXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxScanForEmbeddedXmpActionPerformed
        setScanForEmbeddedXmp();
    }//GEN-LAST:event_checkBoxScanForEmbeddedXmpActionPerformed

    private void checkBoxSaveInputEarlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSaveInputEarlyActionPerformed
        setSaveEarly();
    }//GEN-LAST:event_checkBoxSaveInputEarlyActionPerformed

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
    private javax.swing.JCheckBox checkBoxEnableAutocomplete;
    private javax.swing.JCheckBox checkBoxSaveInputEarly;
    private javax.swing.JCheckBox checkBoxScanForEmbeddedXmp;
    private javax.swing.JCheckBox checkBoxUpdateAutocomplete;
    private javax.swing.JLabel labelMaximumSecondsToTerminateExternalPrograms;
    private javax.swing.JPanel panelAutocomplete;
    private javax.swing.JSpinner spinnerMaximumSecondsToTerminateExternalPrograms;
    // End of variables declaration//GEN-END:variables
}
