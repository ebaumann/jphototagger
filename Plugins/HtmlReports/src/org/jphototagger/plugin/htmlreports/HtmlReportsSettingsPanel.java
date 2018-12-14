package org.jphototagger.plugin.htmlreports;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class HtmlReportsSettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public HtmlReportsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        setPersistedValues();
    }

    private void setPersistedValues() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            checkBoxInputFilename.setSelected(prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_INPUT_FILENAME_BEFORE_CREATING));
            checkBoxOpenReport.setSelected(prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_OPEN_REPORT_AFTER_CREATING));
            checkBoxShowSettings.setSelected(prefs.getBoolean(HtmlReportsPreferencesKeys.KEY_SHOW_SETTINGS_BEFORE_CREATING));
            setReportsDirectory();
        }
    }

    private void setReportsDirectory() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String dirPathname = prefs.getString(HtmlReportsPreferencesKeys.KEY_DIRECTORY);
        File dir;
        if (StringUtil.hasContent(dirPathname)) {
            dir = new File(dirPathname);
        } else {
            dir = new File(HtmlReports.getDefaultReportsDirectoryName());
            try {
                FileUtil.ensureDirectoryExists(dir);
            } catch (IOException ex) {
                Logger.getLogger(HtmlReportsSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setReportsDirectory(dir);
    }

    private void setReportsDirectory(File dir) {
        labelDirectory.setText(dir.getAbsolutePath());
        if (dir.isDirectory()) {
            labelDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
            labelDirectory.setToolTipText(dir.getAbsolutePath());
        } else {
            labelDirectory.setIcon(org.jphototagger.resources.Icons.getIcon("icon_error.png"));
            labelDirectory.setToolTipText(Bundle.getString(HtmlReportsSettingsPanel.class, "HtmlReportsSettingsPanel.Error.DirectoryDoesNotExist"));
        }
    }

    private void chooseDirectory() {
        File startDir = new File(labelDirectory.getText());
        Frame parent = ComponentUtil.findFrameWithIcon();
        DirectoryChooser dirChooser = new DirectoryChooser(parent, startDir, Option.NO_OPTION);
        dirChooser.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
        if (dirChooser.isAccepted()) {
            List<File> selectedDirectories = dirChooser.getSelectedDirectories();
            if (!selectedDirectories.isEmpty()) {
                File selectedDir = CollectionUtil.getFirstElement(selectedDirectories);
                setReportsDirectory(selectedDir);
                Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
                prefs.setString(HtmlReportsPreferencesKeys.KEY_DIRECTORY, selectedDir.getAbsolutePath());
            }
        }
    }

    private void setBooleanPreferences(String key, boolean value) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(key, value);
    }

    private void showHelp() {
        HelpUtil.showHelp("/org/jphototagger/plugin/htmlreports/help/index.html");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelDefaultValues = org.jphototagger.resources.UiFactory.label();
        panelColumns = new org.jphototagger.plugin.htmlreports.MetaDataValueSelectionPanels();
        panelDirectory = new javax.swing.JPanel();
        labelDirectoryPrompt = org.jphototagger.resources.UiFactory.label();
        labelDirectory = org.jphototagger.resources.UiFactory.label();
        buttonChooseDirectory = org.jphototagger.resources.UiFactory.button();
        checkBoxShowSettings = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxInputFilename = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxOpenReport = org.jphototagger.resources.UiFactory.checkBox();
        panelVersion = new javax.swing.JPanel();
        labelVersion = org.jphototagger.resources.UiFactory.label();
        buttonHelp = org.jphototagger.resources.UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelDefaultValues.setLabelFor(panelColumns);
        labelDefaultValues.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.labelDefaultValues.text")); // NOI18N
        labelDefaultValues.setName("labelDefaultValues"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(labelDefaultValues, gridBagConstraints);

        panelColumns.setName("panelColumns"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 0, 10);
        add(panelColumns, gridBagConstraints);

        panelDirectory.setName("panelDirectory"); // NOI18N
        panelDirectory.setLayout(new java.awt.GridBagLayout());

        labelDirectoryPrompt.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.labelDirectoryPrompt.text")); // NOI18N
        labelDirectoryPrompt.setName("labelDirectoryPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelDirectory.add(labelDirectoryPrompt, gridBagConstraints);

        labelDirectory.setText(" "); // NOI18N
        labelDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDirectory.setName("labelDirectory"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelDirectory.add(labelDirectory, gridBagConstraints);

        buttonChooseDirectory.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 5, 0, 0);
        panelDirectory.add(buttonChooseDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 0, 10);
        add(panelDirectory, gridBagConstraints);

        checkBoxShowSettings.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.checkBoxShowSettings.text")); // NOI18N
        checkBoxShowSettings.setName("checkBoxShowSettings"); // NOI18N
        checkBoxShowSettings.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxShowSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 10, 0, 10);
        add(checkBoxShowSettings, gridBagConstraints);

        checkBoxInputFilename.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.checkBoxInputFilename.text")); // NOI18N
        checkBoxInputFilename.setName("checkBoxInputFilename"); // NOI18N
        checkBoxInputFilename.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxInputFilenameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        add(checkBoxInputFilename, gridBagConstraints);

        checkBoxOpenReport.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.checkBoxOpenReport.text")); // NOI18N
        checkBoxOpenReport.setName("checkBoxOpenReport"); // NOI18N
        checkBoxOpenReport.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxOpenReportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        add(checkBoxOpenReport, gridBagConstraints);

        panelVersion.setName("panelVersion"); // NOI18N
        panelVersion.setLayout(new java.awt.GridBagLayout());

        labelVersion.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.labelVersion.text")); // NOI18N
        labelVersion.setName("labelVersion"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelVersion.add(labelVersion, gridBagConstraints);

        buttonHelp.setText(Bundle.getString(getClass(), "HtmlReportsSettingsPanel.buttonHelp.text")); // NOI18N
        buttonHelp.setName("buttonHelp"); // NOI18N
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 0);
        panelVersion.add(buttonHelp, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        add(panelVersion, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
        chooseDirectory();
    }//GEN-LAST:event_buttonChooseDirectoryActionPerformed

    private void checkBoxInputFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxInputFilenameActionPerformed
        setBooleanPreferences(HtmlReportsPreferencesKeys.KEY_INPUT_FILENAME_BEFORE_CREATING, checkBoxInputFilename.isSelected());
    }//GEN-LAST:event_checkBoxInputFilenameActionPerformed

    private void checkBoxOpenReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxOpenReportActionPerformed
        setBooleanPreferences(HtmlReportsPreferencesKeys.KEY_OPEN_REPORT_AFTER_CREATING, checkBoxOpenReport.isSelected());
    }//GEN-LAST:event_checkBoxOpenReportActionPerformed

    private void checkBoxShowSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxShowSettingsActionPerformed
        setBooleanPreferences(HtmlReportsPreferencesKeys.KEY_SHOW_SETTINGS_BEFORE_CREATING, checkBoxShowSettings.isSelected());
    }//GEN-LAST:event_checkBoxShowSettingsActionPerformed

    private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHelpActionPerformed
        showHelp();
    }//GEN-LAST:event_buttonHelpActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonHelp;
    private javax.swing.JCheckBox checkBoxInputFilename;
    private javax.swing.JCheckBox checkBoxOpenReport;
    private javax.swing.JCheckBox checkBoxShowSettings;
    private javax.swing.JLabel labelDefaultValues;
    private javax.swing.JLabel labelDirectory;
    private javax.swing.JLabel labelDirectoryPrompt;
    private javax.swing.JLabel labelVersion;
    private org.jphototagger.plugin.htmlreports.MetaDataValueSelectionPanels panelColumns;
    private javax.swing.JPanel panelDirectory;
    private javax.swing.JPanel panelVersion;
    // End of variables declaration//GEN-END:variables
}
