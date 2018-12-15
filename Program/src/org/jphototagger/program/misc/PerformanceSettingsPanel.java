package org.jphototagger.program.misc;

import java.awt.Container;
import javax.swing.SpinnerModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class PerformanceSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private boolean listen;

    public PerformanceSettingsPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void restore() {
        listen = false;
        checkBoxScanForEmbeddedXmp.setSelected(lookupScanForEmbeddedXmp());
        checkBoxSaveInputEarly.setSelected(lookupSaveInputEarly());
        spinnerMaximumSecondsToTerminateExternalPrograms.getModel().setValue(getMaxSecondsToTerminateExternalPrograms());
        checkBoxEnableAutocomplete.setSelected(lookupEnableAutocomplete());
        checkBoxUpdateAutocomplete.setSelected(lookupUpdateAutocomplete());
        checkBoxUpdateAutocomplete.setEnabled(checkBoxEnableAutocomplete.isSelected());
        checkBoxAutocompleteFastSearchIgnoreCase.setSelected(lookupAutocompleteFastSearchIgnoreCase());
        checkBoxAutoscanDirectories.setSelected(lookupAutoscanDirectories());
        listen = true;
    }

    @Override
    public void persist() {
        // Do nothing, this is done automatically via ActionListeners
    }

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(PerformanceSettingsPanel.class, "PerformanceSettingsPanel.HelpPage");
    }

    private void persistMaximumSecondsToTerminateExternalPrograms() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        SpinnerModel model = spinnerMaximumSecondsToTerminateExternalPrograms.getModel();
        Integer adjustedSeconds = (Integer) model.getValue();
        prefs.setInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS, adjustedSeconds);
    }

    private int getMaxSecondsToTerminateExternalPrograms() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                ? prefs.getInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                : 60;
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP.equals(evt.getKey())) {
            checkBoxScanForEmbeddedXmp.setSelected((boolean) evt.getNewValue());
        }
    }

    private void persistScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean scanForEmbeddedXmpSelected = checkBoxScanForEmbeddedXmp.isSelected();
        prefs.setBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP, scanForEmbeddedXmpSelected);
    }

    private boolean lookupScanForEmbeddedXmp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_SCAN_FOR_EMBEDDED_XMP)
                : false;
    }

    private void persistSaveInputEarly() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean saveInputEarlySelected = checkBoxSaveInputEarly.isSelected();
        prefs.setBoolean(CommonPreferences.KEY_SAVE_INPUT_EARLY, saveInputEarlySelected);
    }

    private boolean lookupSaveInputEarly() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(CommonPreferences.KEY_SAVE_INPUT_EARLY)
                ? prefs.getBoolean(CommonPreferences.KEY_SAVE_INPUT_EARLY)
                : true;
    }

    private void persistUpdateAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean updateAutocompleteSelected = checkBoxUpdateAutocomplete.isSelected();
        prefs.setBoolean(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE, updateAutocompleteSelected);
    }

    private boolean lookupUpdateAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_UPDATE_AUTOCOMPLETE)
                : true;
    }

    private void persistEnableAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean enableAutocompleteSelected = checkBoxEnableAutocomplete.isSelected();
        prefs.setBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE, enableAutocompleteSelected);
        checkBoxUpdateAutocomplete.setEnabled(enableAutocompleteSelected);
    }

    private boolean lookupEnableAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private void persistAutocompleteFastSearchIgnoreCase() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean autocompleteIgnoreCaseSelected = checkBoxAutocompleteFastSearchIgnoreCase.isSelected();
        prefs.setBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE, autocompleteIgnoreCaseSelected);
    }

    private boolean lookupAutocompleteFastSearchIgnoreCase() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : true;
    }

    private void persistAutoscanDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean autoscanDirectoriesSelected = checkBoxAutoscanDirectories.isSelected();
        prefs.setBoolean(PreferencesKeys.KEY_AUTOSCAN_DIRECTORIES, autoscanDirectoriesSelected);
    }

    private boolean lookupAutoscanDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(PreferencesKeys.KEY_AUTOSCAN_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_AUTOSCAN_DIRECTORIES)
                : false;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelMaximumSecondsToTerminateExternalPrograms = org.jphototagger.resources.UiFactory.panel();
        labelMaximumSecondsToTerminateExternalPrograms = org.jphototagger.resources.UiFactory.label();
        spinnerMaximumSecondsToTerminateExternalPrograms = org.jphototagger.resources.UiFactory.spinner();
        checkBoxScanForEmbeddedXmp = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxAutoscanDirectories = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxSaveInputEarly = org.jphototagger.resources.UiFactory.checkBox();
        panelAutocomplete = org.jphototagger.resources.UiFactory.panel();
        checkBoxEnableAutocomplete = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxUpdateAutocomplete = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxAutocompleteFastSearchIgnoreCase = org.jphototagger.resources.UiFactory.checkBox();

        setLayout(new java.awt.GridBagLayout());

        panelMaximumSecondsToTerminateExternalPrograms.setLayout(new java.awt.GridBagLayout());

        labelMaximumSecondsToTerminateExternalPrograms.setLabelFor(spinnerMaximumSecondsToTerminateExternalPrograms);
        labelMaximumSecondsToTerminateExternalPrograms.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.labelMaximumSecondsToTerminateExternalPrograms.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        panelMaximumSecondsToTerminateExternalPrograms.add(labelMaximumSecondsToTerminateExternalPrograms, gridBagConstraints);

        spinnerMaximumSecondsToTerminateExternalPrograms.setModel(new javax.swing.SpinnerNumberModel(60, 10, 600, 1));
        spinnerMaximumSecondsToTerminateExternalPrograms.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 2, 0, 10);
        panelMaximumSecondsToTerminateExternalPrograms.add(spinnerMaximumSecondsToTerminateExternalPrograms, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(panelMaximumSecondsToTerminateExternalPrograms, gridBagConstraints);

        checkBoxScanForEmbeddedXmp.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxScanForEmbeddedXmp.text")); // NOI18N
        checkBoxScanForEmbeddedXmp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxScanForEmbeddedXmpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 0, 10);
        add(checkBoxScanForEmbeddedXmp, gridBagConstraints);

        checkBoxAutoscanDirectories.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxAutoscanDirectories.text")); // NOI18N
        checkBoxAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        add(checkBoxAutoscanDirectories, gridBagConstraints);

        checkBoxSaveInputEarly.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxSaveInputEarly.text")); // NOI18N
        checkBoxSaveInputEarly.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxSaveInputEarlyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        add(checkBoxSaveInputEarly, gridBagConstraints);

        panelAutocomplete.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "PerformanceSettingsPanel.panelAutocomplete.border.title"))); // NOI18N
        panelAutocomplete.setLayout(new java.awt.GridBagLayout());

        checkBoxEnableAutocomplete.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxEnableAutocomplete.text")); // NOI18N
        checkBoxEnableAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxEnableAutocompleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelAutocomplete.add(checkBoxEnableAutocomplete, gridBagConstraints);

        checkBoxUpdateAutocomplete.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxUpdateAutocomplete.text")); // NOI18N
        checkBoxUpdateAutocomplete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxUpdateAutocompleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 5);
        panelAutocomplete.add(checkBoxUpdateAutocomplete, gridBagConstraints);

        checkBoxAutocompleteFastSearchIgnoreCase.setText(Bundle.getString(getClass(), "PerformanceSettingsPanel.checkBoxAutocompleteFastSearchIgnoreCase.text")); // NOI18N
        checkBoxAutocompleteFastSearchIgnoreCase.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutocompleteFastSearchIgnoreCaseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 5, 5);
        panelAutocomplete.add(checkBoxAutocompleteFastSearchIgnoreCase, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 0, 10);
        add(panelAutocomplete, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void spinnerMaximumSecondsToTerminateExternalProgramsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged
        if (listen) {
            persistMaximumSecondsToTerminateExternalPrograms();
        }
    }//GEN-LAST:event_spinnerMaximumSecondsToTerminateExternalProgramsStateChanged

    private void checkBoxScanForEmbeddedXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxScanForEmbeddedXmpActionPerformed
        if (listen) {
            persistScanForEmbeddedXmp();
        }
    }//GEN-LAST:event_checkBoxScanForEmbeddedXmpActionPerformed

    private void checkBoxSaveInputEarlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxSaveInputEarlyActionPerformed
        if (listen) {
            persistSaveInputEarly();
        }
    }//GEN-LAST:event_checkBoxSaveInputEarlyActionPerformed

    private void checkBoxEnableAutocompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxEnableAutocompleteActionPerformed
        if (listen) {
            persistEnableAutocomplete();
        }
    }//GEN-LAST:event_checkBoxEnableAutocompleteActionPerformed

    private void checkBoxUpdateAutocompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxUpdateAutocompleteActionPerformed
        if (listen) {
            persistUpdateAutocomplete();
        }
    }//GEN-LAST:event_checkBoxUpdateAutocompleteActionPerformed

    private void checkBoxAutocompleteFastSearchIgnoreCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutocompleteFastSearchIgnoreCaseActionPerformed
        if (listen) {
            persistAutocompleteFastSearchIgnoreCase();
        }
    }//GEN-LAST:event_checkBoxAutocompleteFastSearchIgnoreCaseActionPerformed

    private void checkBoxAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutoscanDirectoriesActionPerformed
        if (listen) {
            persistAutoscanDirectories();
        }
    }//GEN-LAST:event_checkBoxAutoscanDirectoriesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBoxAutocompleteFastSearchIgnoreCase;
    private javax.swing.JCheckBox checkBoxAutoscanDirectories;
    private javax.swing.JCheckBox checkBoxEnableAutocomplete;
    private javax.swing.JCheckBox checkBoxSaveInputEarly;
    private javax.swing.JCheckBox checkBoxScanForEmbeddedXmp;
    private javax.swing.JCheckBox checkBoxUpdateAutocomplete;
    private javax.swing.JLabel labelMaximumSecondsToTerminateExternalPrograms;
    private javax.swing.JPanel panelAutocomplete;
    private javax.swing.JPanel panelMaximumSecondsToTerminateExternalPrograms;
    private javax.swing.JSpinner spinnerMaximumSecondsToTerminateExternalPrograms;
    // End of variables declaration//GEN-END:variables
}
