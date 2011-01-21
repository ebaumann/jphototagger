package org.jphototagger.program.view.panels;

import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.DirectoryChooser;

import java.awt.Container;

import java.io.File;

import java.util.logging.Level;
import javax.swing.Icon;

import javax.swing.filechooser.FileSystemView;
import org.jphototagger.program.controller.misc.ControllerUpdateCheck;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.factory.ControllerFactory;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsMiscPanel extends javax.swing.JPanel
        implements Persistence, UserSettingsListener {
    private static final long   serialVersionUID = 479354601163285718L;

    public SettingsMiscPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    private File chooseDirectory(File startDirectory) {
        File             dir    = null;
        DirectoryChooser dlg =
            new DirectoryChooser(
                GUI.getAppFrame(), startDirectory,
                UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(),
                           "SettingsMiscPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            dir = dlg.getSelectedDirectories().get(0);
        }

        return dir;
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        UserSettings.INSTANCE.setAcceptHiddenDirectories(
            checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void handleActionPerformedChooseDatabaseDirectory(boolean backupDir) {
        File file = chooseDirectory(
                        new File(backupDir
                            ? UserSettings.INSTANCE.getDatabaseBackupDirectoryName()
                            : UserSettings.INSTANCE.getDatabaseDirectoryName()));

        if (file != null) {
            setDatabaseDirectoryName(file.getAbsolutePath(), backupDir);
        }
    }

    private void setDatabaseDirectoryName(String directoryName, boolean backupDir) {
        setIconDatabaseDirectory(backupDir);
        if (backupDir) {
            labelDatabaseBackupDirectory.setText(directoryName);
            UserSettings.INSTANCE.setDatabaseBackupDirectoryName(directoryName);
        } else {
            labelDatabaseDirectory.setText(directoryName);
            UserSettings.INSTANCE.setDatabaseDirectoryName(directoryName);
        }
    }

    private void setIconDatabaseDirectory(boolean backupDir) {
        File dir = new File(labelDatabaseDirectory.getText());

        if (dir.isDirectory()) {
            Icon icon = FileSystemView.getFileSystemView().getSystemIcon(dir);
            if (backupDir) {
                labelDatabaseBackupDirectory.setIcon(icon);
            } else {
                labelDatabaseDirectory.setIcon(icon);
            }
        }
    }

    private void handleActionPerformedSetStandardDatabaseDirectory() {
        setDatabaseDirectoryName(
            UserSettings.INSTANCE.getDefaultDatabaseDirectoryName(), false);
    }

    private void handleActionPerformedCheckBoxDisplaySearchButton() {
        UserSettings.INSTANCE.setDisplaySearchButton(
            checkBoxDisplaySearchButton.isSelected());
    }

    private void handleActionPerformedComboBoxLogLevel() {
        UserSettings.INSTANCE.setLogLevel(
            Level.parse(comboBoxLogLevel.getSelectedItem().toString()));
    }

    private void handleActionPerformedCopyMoveFiles() {
        UserSettings.INSTANCE.setCopyMoveFilesOptions(
            radioButtonCopyMoveFileConfirmOverwrite.isSelected()
            ? CopyFiles.Options.CONFIRM_OVERWRITE
            : radioButtonCopyMoveFileRenameIfExists.isSelected()
              ? CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS
              : CopyFiles.Options.CONFIRM_OVERWRITE);
    }

    private void handleActionPerformedAutoDownload() {
        UserSettings.INSTANCE.setAutoDownloadNewerVersions(
            checkBoxAutoDownloadCheck.isSelected());
    }

    private void handleActionComboBoxIptcCharset() {
        UserSettings.INSTANCE.setIptcCharset(
            comboBoxIptcCharset.getSelectedItem().toString());
    }

    private void handleActionPerformedCheckBoxAddFilenameToGpsLocationExport() {
        UserSettings.INSTANCE.setAddFilenameToGpsLocationExport(
                checkBoxAddFilenameToGpsLocationExport.isSelected());
    }

    private void setExperimentalFileFormats() {
        UserSettings.INSTANCE.setUseExperimentalFileFormats(
                checkBoxExperimentalFileFormats.isSelected());
    }

    private void checkLogLevel() {
        if (comboBoxLogLevel.getSelectedIndex() < 0) {
            comboBoxLogLevel.setSelectedIndex(0);
        }
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        if (evt.getType().equals(UserSettingsEvent.Type.CHECK_FOR_UPDATES)) {
            checkBoxAutoDownloadCheck.setSelected(
                UserSettings.INSTANCE.isAutoDownloadNewerVersions());
        }
    }

    @Override
    public void readProperties() {
        checkLogLevel();

        UserSettings settings = UserSettings.INSTANCE;

        checkBoxAutoDownloadCheck.setSelected(
            settings.isAutoDownloadNewerVersions());
        checkBoxDisplaySearchButton.setSelected(
            UserSettings.INSTANCE.isDisplaySearchButton());
        checkBoxIsAcceptHiddenDirectories.setSelected(
            settings.isAcceptHiddenDirectories());
        checkBoxAddFilenameToGpsLocationExport.setSelected(
                settings.isAddFilenameToGpsLocationExport());
        checkBoxExperimentalFileFormats.setSelected(
                settings.isUseExperimentalFileFormats());
        comboBoxIptcCharset.getModel().setSelectedItem(
            UserSettings.INSTANCE.getIptcCharset());
        comboBoxLogLevel.setSelectedItem(
            settings.getLogLevel().getLocalizedName());
        labelDatabaseDirectory.setText(
            UserSettings.INSTANCE.getDatabaseDirectoryName());
        labelDatabaseBackupDirectory.setText(
            UserSettings.INSTANCE.getDatabaseBackupDirectoryName());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(
            settings.getCopyMoveFilesOptions().equals(
                CopyFiles.Options.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(
            settings.getCopyMoveFilesOptions().equals(
                CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
        setIconDatabaseDirectory(true);
        setIconDatabaseDirectory(false);
    }

    @Override
    public void writeProperties() {
    }

    private void checkDownload() {
        ControllerFactory.INSTANCE.getController(
                ControllerUpdateCheck.class).actionPerformed(null);
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

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxAutoDownloadCheck = new javax.swing.JCheckBox();
        buttonCheckDownload = new javax.swing.JButton();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        checkBoxAddFilenameToGpsLocationExport = new javax.swing.JCheckBox();
        checkBoxExperimentalFileFormats = new javax.swing.JCheckBox();
        panelCopyMoveFiles = new javax.swing.JPanel();
        radioButtonCopyMoveFileConfirmOverwrite = new javax.swing.JRadioButton();
        radioButtonCopyMoveFileRenameIfExists = new javax.swing.JRadioButton();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        comboBoxLogLevel = new javax.swing.JComboBox();
        labelLogLevel = new javax.swing.JLabel();
        panelDatabaseDirectory = new javax.swing.JPanel();
        labelDatabaseDirectory = new javax.swing.JLabel();
        buttonSetStandardDatabaseDirectoryName = new javax.swing.JButton();
        buttonChooseDatabaseDirectory = new javax.swing.JButton();
        labelInfoDatabaseDirectory = new javax.swing.JLabel();
        labelPromptDatabaseBackupDirectory = new javax.swing.JLabel();
        labelDatabaseBackupDirectory = new javax.swing.JLabel();
        buttonChooseDatabaseBackupDirectory = new javax.swing.JButton();

        checkBoxIsAcceptHiddenDirectories.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        checkBoxAutoDownloadCheck.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxAutoDownloadCheck.text")); // NOI18N
        checkBoxAutoDownloadCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoDownloadCheckActionPerformed(evt);
            }
        });

        buttonCheckDownload.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonCheckDownload.text")); // NOI18N
        buttonCheckDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckDownloadActionPerformed(evt);
            }
        });

        checkBoxDisplaySearchButton.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });

        checkBoxAddFilenameToGpsLocationExport.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxAddFilenameToGpsLocationExport.text")); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAddFilenameToGpsLocationExportActionPerformed(evt);
            }
        });

        checkBoxExperimentalFileFormats.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxExperimentalFileFormats.text")); // NOI18N
        checkBoxExperimentalFileFormats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxExperimentalFileFormatsActionPerformed(evt);
            }
        });

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelCopyMoveFiles.border.title"))); // NOI18N

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileRenameIfExistsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCopyMoveFilesLayout = new javax.swing.GroupLayout(panelCopyMoveFiles);
        panelCopyMoveFiles.setLayout(panelCopyMoveFilesLayout);
        panelCopyMoveFilesLayout.setHorizontalGroup(
            panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCopyMoveFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(radioButtonCopyMoveFileRenameIfExists, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                    .addComponent(radioButtonCopyMoveFileConfirmOverwrite, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelCopyMoveFilesLayout.setVerticalGroup(
            panelCopyMoveFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopyMoveFilesLayout.createSequentialGroup()
                .addComponent(radioButtonCopyMoveFileConfirmOverwrite)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCopyMoveFileRenameIfExists)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        labelIptcCharset.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelIptcCharset.text")); // NOI18N

        comboBoxIptcCharset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ISO-8859-1", "UTF-8" }));
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        labelLogLevel.setLabelFor(comboBoxLogLevel);
        labelLogLevel.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelLogLevel.text")); // NOI18N

        panelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelDatabaseDirectory.border.title"))); // NOI18N

        labelDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonSetStandardDatabaseDirectoryName.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonSetStandardDatabaseDirectoryName.text")); // NOI18N
        buttonSetStandardDatabaseDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetStandardDatabaseDirectoryNameActionPerformed(evt);
            }
        });

        buttonChooseDatabaseDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChooseDatabaseDirectory.text")); // NOI18N
        buttonChooseDatabaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseDirectoryActionPerformed(evt);
            }
        });

        labelInfoDatabaseDirectory.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoDatabaseDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelInfoDatabaseDirectory.text")); // NOI18N

        labelPromptDatabaseBackupDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelPromptDatabaseBackupDirectory.text")); // NOI18N

        labelDatabaseBackupDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseBackupDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseDatabaseBackupDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChooseDatabaseBackupDirectory.text")); // NOI18N
        buttonChooseDatabaseBackupDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseBackupDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatabaseDirectoryLayout = new javax.swing.GroupLayout(panelDatabaseDirectory);
        panelDatabaseDirectory.setLayout(panelDatabaseDirectoryLayout);
        panelDatabaseDirectoryLayout.setHorizontalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                    .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(buttonSetStandardDatabaseDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseDirectory))
                    .addComponent(labelPromptDatabaseBackupDirectory)
                    .addComponent(labelDatabaseBackupDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                    .addComponent(buttonChooseDatabaseBackupDirectory, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChooseDatabaseDirectory)
                    .addComponent(buttonSetStandardDatabaseDirectoryName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPromptDatabaseBackupDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDatabaseBackupDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonChooseDatabaseBackupDirectory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxExperimentalFileFormats)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(checkBoxAutoDownloadCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(buttonCheckDownload))
                                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(labelIptcCharset, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                            .addGap(49, 49, 49))
                                        .addComponent(labelLogLevel, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                                    .addGap(14, 14, 14)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(236, 236, 236))
                                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(14, 14, 14))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(checkBoxIsAcceptHiddenDirectories, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(checkBoxAddFilenameToGpsLocationExport, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                            .addContainerGap()))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonCheckDownload)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxIsAcceptHiddenDirectories)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxAutoDownloadCheck)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAddFilenameToGpsLocationExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxExperimentalFileFormats)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLogLevel)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxAutoDownloadCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAutoDownloadCheckActionPerformed
        handleActionPerformedAutoDownload();
    }//GEN-LAST:event_checkBoxAutoDownloadCheckActionPerformed

    private void radioButtonCopyMoveFileRenameIfExistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed
        handleActionPerformedCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileRenameIfExistsActionPerformed

    private void radioButtonCopyMoveFileConfirmOverwriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed
        handleActionPerformedCopyMoveFiles();
    }//GEN-LAST:event_radioButtonCopyMoveFileConfirmOverwriteActionPerformed

    private void checkBoxIsAcceptHiddenDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed
        handleActionPerformedCheckBoxIsAcceptHiddenDirectories();
    }//GEN-LAST:event_checkBoxIsAcceptHiddenDirectoriesActionPerformed

    private void comboBoxLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLogLevelActionPerformed
        handleActionPerformedComboBoxLogLevel();
    }//GEN-LAST:event_comboBoxLogLevelActionPerformed

    private void buttonChooseDatabaseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDatabaseDirectoryActionPerformed
        handleActionPerformedChooseDatabaseDirectory(false);
    }//GEN-LAST:event_buttonChooseDatabaseDirectoryActionPerformed

    private void buttonSetStandardDatabaseDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed
        handleActionPerformedSetStandardDatabaseDirectory();
    }//GEN-LAST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplaySearchButtonActionPerformed
        handleActionPerformedCheckBoxDisplaySearchButton();
    }//GEN-LAST:event_checkBoxDisplaySearchButtonActionPerformed

    private void comboBoxIptcCharsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetActionPerformed
        handleActionComboBoxIptcCharset();
    }//GEN-LAST:event_comboBoxIptcCharsetActionPerformed

    private void buttonChooseDatabaseBackupDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDatabaseBackupDirectoryActionPerformed
        handleActionPerformedChooseDatabaseDirectory(true);
    }//GEN-LAST:event_buttonChooseDatabaseBackupDirectoryActionPerformed

    private void buttonCheckDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCheckDownloadActionPerformed
        checkDownload();
    }//GEN-LAST:event_buttonCheckDownloadActionPerformed

    private void checkBoxAddFilenameToGpsLocationExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxAddFilenameToGpsLocationExportActionPerformed
        handleActionPerformedCheckBoxAddFilenameToGpsLocationExport();
    }//GEN-LAST:event_checkBoxAddFilenameToGpsLocationExportActionPerformed

    private void checkBoxExperimentalFileFormatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxExperimentalFileFormatsActionPerformed
        setExperimentalFileFormats();
    }//GEN-LAST:event_checkBoxExperimentalFileFormatsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCheckDownload;
    private javax.swing.JButton buttonChooseDatabaseBackupDirectory;
    private javax.swing.JButton buttonChooseDatabaseDirectory;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetStandardDatabaseDirectoryName;
    private javax.swing.JCheckBox checkBoxAddFilenameToGpsLocationExport;
    private javax.swing.JCheckBox checkBoxAutoDownloadCheck;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxExperimentalFileFormats;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JComboBox comboBoxLogLevel;
    private javax.swing.JLabel labelDatabaseBackupDirectory;
    private javax.swing.JLabel labelDatabaseDirectory;
    private javax.swing.JLabel labelInfoDatabaseDirectory;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelPromptDatabaseBackupDirectory;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    // End of variables declaration//GEN-END:variables
}
