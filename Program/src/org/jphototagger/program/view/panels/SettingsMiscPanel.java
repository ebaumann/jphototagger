package org.jphototagger.program.view.panels;

import java.util.List;
import org.jphototagger.lib.dialog.DirectoryChooser.Option;
import org.jphototagger.program.event.UserSettingsEvent;
import org.jphototagger.program.event.UserSettingsEvent.Type;
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
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.controller.misc.ControllerUpdateCheck;
import org.jphototagger.program.event.listener.UserSettingsListener;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.model.IptcCharsetComboBoxModel;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsMiscPanel extends javax.swing.JPanel implements Persistence, UserSettingsListener {
    private static final long serialVersionUID = 479354601163285718L;

    public SettingsMiscPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        UserSettings.INSTANCE.addUserSettingsListener(this);
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        Option showHiddenDirs = UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), startDirectory, showHiddenDirs);

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), "SettingsMiscPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            dir = dlg.getSelectedDirectories().get(0);
        }

        return dir;
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        UserSettings.INSTANCE.setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void handleActionPerformedChooseDatabaseDirectory(boolean backupDir) {
        File file = chooseDirectory(new File(
                            backupDir
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

    private void displayExperimentalFileFormats() {
        ComponentUtil.show(experimentalFileFormatsDialog);
    }

    private String getExperimentalFileFormatsAsText() {
        StringBuilder sb = new StringBuilder();
        List<String> experimentalFileFormatDescriptions = AppFileFilters.getExperimentalFileFormatDescriptions();
        String lineSeparator = SystemProperties.getLineSeparator();
        boolean isFirstLine = true;

        for (String description : experimentalFileFormatDescriptions) {
            sb.append(isFirstLine ? "" : lineSeparator);
            sb.append(description);
            isFirstLine = false;
        }

        return sb.toString();
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
        UserSettings.INSTANCE.setAutoDownloadNewerVersions(checkBoxAutoDownloadCheck.isSelected());
    }

    private void handleActionComboBoxIptcCharset() {
        UserSettings.INSTANCE.setIptcCharset(comboBoxIptcCharset.getSelectedItem().toString());
    }

    private void handleActionPerformedCheckBoxAddFilenameToGpsLocationExport() {
        UserSettings.INSTANCE.setAddFilenameToGpsLocationExport(checkBoxAddFilenameToGpsLocationExport.isSelected());
    }

    private void setExperimentalFileFormats() {
        UserSettings.INSTANCE.setUseExperimentalFileFormats(checkBoxExperimentalFileFormats.isSelected());
    }

    private void checkLogLevel() {
        if (comboBoxLogLevel.getSelectedIndex() < 0) {
            comboBoxLogLevel.setSelectedIndex(0);
        }
    }

    @Override
    public void applySettings(UserSettingsEvent evt) {
        Type eventType = evt.getType();

        if (eventType.equals(UserSettingsEvent.Type.CHECK_FOR_UPDATES)) {
            checkBoxAutoDownloadCheck.setSelected(UserSettings.INSTANCE.isAutoDownloadNewerVersions());
        } else if (eventType.equals(UserSettingsEvent.Type.IPTC_CHARSET)) {
            setIptcCharsetFromUserSettings();
        }
    }

    private void setIptcCharsetFromUserSettings() {
        comboBoxIptcCharset.getModel().setSelectedItem(UserSettings.INSTANCE.getIptcCharset());
    }

    @Override
    public void readProperties() {
        checkLogLevel();

        UserSettings settings = UserSettings.INSTANCE;

        checkBoxAutoDownloadCheck.setSelected(settings.isAutoDownloadNewerVersions());
        checkBoxDisplaySearchButton.setSelected(UserSettings.INSTANCE.isDisplaySearchButton());
        checkBoxIsAcceptHiddenDirectories.setSelected(settings.isAcceptHiddenDirectories());
        checkBoxAddFilenameToGpsLocationExport.setSelected(settings.isAddFilenameToGpsLocationExport());
        checkBoxExperimentalFileFormats.setSelected(settings.isUseExperimentalFileFormats());
        setIptcCharsetFromUserSettings();
        comboBoxLogLevel.setSelectedItem(settings.getLogLevel().getLocalizedName());
        labelDatabaseDirectory.setText(UserSettings.INSTANCE.getDatabaseDirectoryName());
        labelDatabaseBackupDirectory.setText(UserSettings.INSTANCE.getDatabaseBackupDirectoryName());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(settings.getCopyMoveFilesOptions().equals(CopyFiles.Options.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(settings.getCopyMoveFilesOptions().equals(CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
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

    private void initComponents() {//GEN-BEGIN:initComponents

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        experimentalFileFormatsDialog = new javax.swing.JDialog();
        labelExperimentalFileFormatsPrompt = new javax.swing.JLabel();
        scrollPaneExperimentalFileFormats = new javax.swing.JScrollPane();
        textAreaExperimentalFileFormats = new javax.swing.JTextArea();
        labelExperimentalFileFormatsInfo = new org.jdesktop.swingx.JXLabel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        checkBoxAutoDownloadCheck = new javax.swing.JCheckBox();
        buttonCheckDownload = new javax.swing.JButton();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        checkBoxAddFilenameToGpsLocationExport = new javax.swing.JCheckBox();
        checkBoxExperimentalFileFormats = new javax.swing.JCheckBox();
        buttonDisplayExperimentalFileFormats = new javax.swing.JButton();
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        experimentalFileFormatsDialog.setTitle(bundle.getString("SettingsMiscPanel.experimentalFileFormatsDialog.title")); // NOI18N
        experimentalFileFormatsDialog.setModal(true);
        experimentalFileFormatsDialog.setName("experimentalFileFormatsDialog"); // NOI18N

        labelExperimentalFileFormatsPrompt.setText(bundle.getString("SettingsMiscPanel.labelExperimentalFileFormatsPrompt.text")); // NOI18N
        labelExperimentalFileFormatsPrompt.setName("labelExperimentalFileFormatsPrompt"); // NOI18N

        scrollPaneExperimentalFileFormats.setName("scrollPaneExperimentalFileFormats"); // NOI18N

        textAreaExperimentalFileFormats.setColumns(20);
        textAreaExperimentalFileFormats.setEditable(false);
        textAreaExperimentalFileFormats.setRows(5);
        textAreaExperimentalFileFormats.setText(getExperimentalFileFormatsAsText());
        textAreaExperimentalFileFormats.setName("textAreaExperimentalFileFormats"); // NOI18N
        scrollPaneExperimentalFileFormats.setViewportView(textAreaExperimentalFileFormats);

        labelExperimentalFileFormatsInfo.setLineWrap(true);
        labelExperimentalFileFormatsInfo.setText(bundle.getString("SettingsMiscPanel.labelExperimentalFileFormatsInfo.text")); // NOI18N
        labelExperimentalFileFormatsInfo.setName("labelExperimentalFileFormatsInfo"); // NOI18N

        javax.swing.GroupLayout experimentalFileFormatsDialogLayout = new javax.swing.GroupLayout(experimentalFileFormatsDialog.getContentPane());
        experimentalFileFormatsDialog.getContentPane().setLayout(experimentalFileFormatsDialogLayout);
        experimentalFileFormatsDialogLayout.setHorizontalGroup(
            experimentalFileFormatsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentalFileFormatsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentalFileFormatsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelExperimentalFileFormatsPrompt)
                    .addComponent(scrollPaneExperimentalFileFormats, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(labelExperimentalFileFormatsInfo, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        experimentalFileFormatsDialogLayout.setVerticalGroup(
            experimentalFileFormatsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentalFileFormatsDialogLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(labelExperimentalFileFormatsPrompt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneExperimentalFileFormats, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelExperimentalFileFormatsInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addContainerGap())
        );

        experimentalFileFormatsDialog.pack();
        experimentalFileFormatsDialog.setLocationRelativeTo(this);

        setName("Form"); // NOI18N

        checkBoxIsAcceptHiddenDirectories.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.setName("checkBoxIsAcceptHiddenDirectories"); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        checkBoxAutoDownloadCheck.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxAutoDownloadCheck.text")); // NOI18N
        checkBoxAutoDownloadCheck.setName("checkBoxAutoDownloadCheck"); // NOI18N
        checkBoxAutoDownloadCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoDownloadCheckActionPerformed(evt);
            }
        });

        buttonCheckDownload.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonCheckDownload.text")); // NOI18N
        buttonCheckDownload.setName("buttonCheckDownload"); // NOI18N
        buttonCheckDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckDownloadActionPerformed(evt);
            }
        });

        checkBoxDisplaySearchButton.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.setName("checkBoxDisplaySearchButton"); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });

        checkBoxAddFilenameToGpsLocationExport.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxAddFilenameToGpsLocationExport.text")); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.setName("checkBoxAddFilenameToGpsLocationExport"); // NOI18N
        checkBoxAddFilenameToGpsLocationExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAddFilenameToGpsLocationExportActionPerformed(evt);
            }
        });

        checkBoxExperimentalFileFormats.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxExperimentalFileFormats.text")); // NOI18N
        checkBoxExperimentalFileFormats.setName("checkBoxExperimentalFileFormats"); // NOI18N
        checkBoxExperimentalFileFormats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxExperimentalFileFormatsActionPerformed(evt);
            }
        });

        buttonDisplayExperimentalFileFormats.setText(bundle.getString("SettingsMiscPanel.buttonDisplayExperimentalFileFormats.text")); // NOI18N
        buttonDisplayExperimentalFileFormats.setToolTipText(bundle.getString("SettingsMiscPanel.buttonDisplayExperimentalFileFormats.toolTipText")); // NOI18N
        buttonDisplayExperimentalFileFormats.setName("buttonDisplayExperimentalFileFormats"); // NOI18N
        buttonDisplayExperimentalFileFormats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDisplayExperimentalFileFormatsActionPerformed(evt);
            }
        });

        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelCopyMoveFiles.border.title"))); // NOI18N
        panelCopyMoveFiles.setName("panelCopyMoveFiles"); // NOI18N

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.setName("radioButtonCopyMoveFileConfirmOverwrite"); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
        radioButtonCopyMoveFileRenameIfExists.setName("radioButtonCopyMoveFileRenameIfExists"); // NOI18N
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
                    .addComponent(radioButtonCopyMoveFileRenameIfExists, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radioButtonCopyMoveFileConfirmOverwrite, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
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

        labelIptcCharset.setLabelFor(comboBoxIptcCharset);
        labelIptcCharset.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelIptcCharset.text")); // NOI18N
        labelIptcCharset.setName("labelIptcCharset"); // NOI18N

        comboBoxIptcCharset.setModel(new IptcCharsetComboBoxModel());
        comboBoxIptcCharset.setName("comboBoxIptcCharset"); // NOI18N
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.setName("comboBoxLogLevel"); // NOI18N
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        labelLogLevel.setLabelFor(comboBoxLogLevel);
        labelLogLevel.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelLogLevel.text")); // NOI18N
        labelLogLevel.setName("labelLogLevel"); // NOI18N

        panelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelDatabaseDirectory.border.title"))); // NOI18N
        panelDatabaseDirectory.setName("panelDatabaseDirectory"); // NOI18N

        labelDatabaseDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDatabaseDirectory.setName("labelDatabaseDirectory"); // NOI18N

        buttonSetStandardDatabaseDirectoryName.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonSetStandardDatabaseDirectoryName.text")); // NOI18N
        buttonSetStandardDatabaseDirectoryName.setName("buttonSetStandardDatabaseDirectoryName"); // NOI18N
        buttonSetStandardDatabaseDirectoryName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetStandardDatabaseDirectoryNameActionPerformed(evt);
            }
        });

        buttonChooseDatabaseDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChooseDatabaseDirectory.text")); // NOI18N
        buttonChooseDatabaseDirectory.setName("buttonChooseDatabaseDirectory"); // NOI18N
        buttonChooseDatabaseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDatabaseDirectoryActionPerformed(evt);
            }
        });

        labelInfoDatabaseDirectory.setForeground(new java.awt.Color(255, 0, 0));
        labelInfoDatabaseDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelInfoDatabaseDirectory.text")); // NOI18N
        labelInfoDatabaseDirectory.setName("labelInfoDatabaseDirectory"); // NOI18N

        labelPromptDatabaseBackupDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelPromptDatabaseBackupDirectory.text")); // NOI18N
        labelPromptDatabaseBackupDirectory.setName("labelPromptDatabaseBackupDirectory"); // NOI18N

        labelDatabaseBackupDirectory.setForeground(new java.awt.Color(0, 0, 255));
        labelDatabaseBackupDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelDatabaseBackupDirectory.setName("labelDatabaseBackupDirectory"); // NOI18N

        buttonChooseDatabaseBackupDirectory.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChooseDatabaseBackupDirectory.text")); // NOI18N
        buttonChooseDatabaseBackupDirectory.setName("buttonChooseDatabaseBackupDirectory"); // NOI18N
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
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetStandardDatabaseDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseDirectory))
                    .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelPromptDatabaseBackupDirectory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseBackupDirectory))
                    .addComponent(labelDatabaseBackupDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSetStandardDatabaseDirectoryName)
                    .addComponent(buttonChooseDatabaseDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPromptDatabaseBackupDirectory)
                    .addComponent(buttonChooseDatabaseBackupDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDatabaseBackupDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxAutoDownloadCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonCheckDownload))
                    .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelLogLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelIptcCharset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxExperimentalFileFormats)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonDisplayExperimentalFileFormats))
                    .addComponent(checkBoxAddFilenameToGpsLocationExport)
                    .addComponent(checkBoxIsAcceptHiddenDirectories))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxAutoDownloadCheck)
                    .addComponent(buttonCheckDownload))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAddFilenameToGpsLocationExport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxExperimentalFileFormats)
                    .addComponent(buttonDisplayExperimentalFileFormats))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelIptcCharset)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLogLevel)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }//GEN-END:initComponents

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

    private void buttonDisplayExperimentalFileFormatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDisplayExperimentalFileFormatsActionPerformed
        displayExperimentalFileFormats();
    }//GEN-LAST:event_buttonDisplayExperimentalFileFormatsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCheckDownload;
    private javax.swing.JButton buttonChooseDatabaseBackupDirectory;
    private javax.swing.JButton buttonChooseDatabaseDirectory;
    private javax.swing.JButton buttonDisplayExperimentalFileFormats;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetStandardDatabaseDirectoryName;
    private javax.swing.JCheckBox checkBoxAddFilenameToGpsLocationExport;
    private javax.swing.JCheckBox checkBoxAutoDownloadCheck;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxExperimentalFileFormats;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JComboBox comboBoxLogLevel;
    private javax.swing.JDialog experimentalFileFormatsDialog;
    private javax.swing.JLabel labelDatabaseBackupDirectory;
    private javax.swing.JLabel labelDatabaseDirectory;
    private org.jdesktop.swingx.JXLabel labelExperimentalFileFormatsInfo;
    private javax.swing.JLabel labelExperimentalFileFormatsPrompt;
    private javax.swing.JLabel labelInfoDatabaseDirectory;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelPromptDatabaseBackupDirectory;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    private javax.swing.JScrollPane scrollPaneExperimentalFileFormats;
    private javax.swing.JTextArea textAreaExperimentalFileFormats;
    // End of variables declaration//GEN-END:variables
}
