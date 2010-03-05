/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.helper.CopyFiles;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.filefilter.ExecutableFileChooserFileFilter;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-02
 */
public final class SettingsMiscPanel extends javax.swing.JPanel implements Persistence {

    private static final long                serialVersionUID = 479354601163285718L;
    private static final String              KEY_TABBED_PANE  = "SettingsMiscPanel.TabbedPane";
    private final        Map<Tab, Component> componentOfTab   = new HashMap<Tab, Component>();

    public enum Tab {

        EXTERNAL_APPLICATIONS,
        MISCELLANEOUS,
    }

    private void initComponentOfTab() {
        componentOfTab.put(Tab.EXTERNAL_APPLICATIONS, panelExternalApplications);
        componentOfTab.put(Tab.MISCELLANEOUS, panelMisc);
    }

    public SettingsMiscPanel() {
        initComponents();
        initComponentOfTab();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public void selectTab(Tab tab) {
        Component c = componentOfTab.get(tab);
        if (c != null) {
            tabbedPane.setSelectedComponent(c);
        }
    }

    public Component getTab(Tab tab) {
        return componentOfTab.get(tab);
    }

    private File chooseDirectory(File startDirectory) {
        File dir = null;
        DirectoryChooser dialog = new DirectoryChooser(
                                    GUI.INSTANCE.getAppFrame(),
                                    startDirectory,
                                    UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dialog.addWindowListener(new SizeAndLocationController());
        dialog.setVisible(true);

        if (dialog.accepted()) {
            dir = dialog.getSelectedDirectories().get(0);
        }
        return dir;
    }

    private File chooseFile(FileFilter filter) {
        File file = null;
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setVisible(true);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        return file;
    }

    private void handleActionPerformedCheckBoxIsAcceptHiddenDirectories() {
        UserSettings.INSTANCE.setAcceptHiddenDirectories(checkBoxIsAcceptHiddenDirectories.isSelected());
    }

    private void handleActionPerformedChooseDatabaseDirectory() {
        File file = chooseDirectory(new File(UserSettings.INSTANCE.
                getDatabaseDirectoryName()));
        if (file != null) {
            setDatabaseDirectoryName(file.getAbsolutePath());
        }
    }

    private void setDatabaseDirectoryName(String directoryName) {
        labelDatabaseDirectory.setText(directoryName);
        setIconDatabaseDirectory();
        UserSettings.INSTANCE.setDatabaseDirectoryName(directoryName);
    }

    private void setIconDatabaseDirectory() {
        File dir = new File(labelDatabaseDirectory.getText());
        if (FileUtil.existsDirectory(dir)) {
            labelDatabaseDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
        }
    }

    private void handleActionPerformedSetStandardDatabaseDirectory() {
        setDatabaseDirectoryName(UserSettings.INSTANCE.getDefaultDatabaseDirectoryName());
    }

    private void handleActionPerformedChooseWebBrowser() {
        File programFile = chooseFile(new ExecutableFileChooserFileFilter());
        if (programFile != null) {
            String browserPath = programFile.getAbsolutePath();
            labelWebBrowser.setText(browserPath);
            labelWebBrowser.setIcon(IconUtil.getSystemIcon(programFile));
            UserSettings.INSTANCE.setWebBrowser(browserPath);
        }
    }

    private void handleActionPerformedChoosePdfViewer() {
        File programFile = chooseFile(new ExecutableFileChooserFileFilter());
        if (programFile != null) {
            String viewerPath = programFile.getAbsolutePath();
            labelPdfViewer.setText(viewerPath);
            labelPdfViewer.setIcon(IconUtil.getSystemIcon(programFile));
            UserSettings.INSTANCE.setPdfViewer(viewerPath);
        }
    }

    private void handleActionPerformedCheckBoxDisplaySearchButton() {
        UserSettings.INSTANCE.setDisplaySearchButton(checkBoxDisplaySearchButton.isSelected());
    }

    private void handleActionPerformedComboBoxLogLevel() {
        UserSettings.INSTANCE.setLogLevel(Level.parse(comboBoxLogLevel.getSelectedItem().toString()));
    }

    private void handleActionPerformedCopyMoveFiles() {
        UserSettings.INSTANCE.setCopyMoveFilesOptions(radioButtonCopyMoveFileConfirmOverwrite.isSelected()
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

    private void checkLogLevel() {
        if (comboBoxLogLevel.getSelectedIndex() < 0) {
            comboBoxLogLevel.setSelectedIndex(0);
        }
    }

    @Override
    public void readProperties() {
        checkLogLevel();
        UserSettings settings = UserSettings.INSTANCE;
        readWebBrowserProperties(settings);
        readPdfViewerProperties(settings);
        comboBoxLogLevel.setSelectedItem(settings.getLogLevel().getLocalizedName());
        checkBoxIsAcceptHiddenDirectories.setSelected(settings.isAcceptHiddenDirectories());
        labelDatabaseDirectory.setText(UserSettings.INSTANCE.getDatabaseDirectoryName());
        radioButtonCopyMoveFileConfirmOverwrite.setSelected(
                settings.getCopyMoveFilesOptions().equals(CopyFiles.Options.CONFIRM_OVERWRITE));
        radioButtonCopyMoveFileRenameIfExists.setSelected(
                settings.getCopyMoveFilesOptions().equals(CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
        checkBoxAutoDownloadCheck.setSelected(settings.isAutoDownloadNewerVersions());
        checkBoxDisplaySearchButton.setSelected(UserSettings.INSTANCE.isDisplaySearchButton());
        comboBoxIptcCharset.getModel().setSelectedItem(UserSettings.INSTANCE.getIptcCharset());
        setIconDatabaseDirectory();
        settings.getSettings().applySettings(tabbedPane, KEY_TABBED_PANE, null);
    }

    private void readWebBrowserProperties(UserSettings settings) {
        File webBrowser = new File(settings.getWebBrowser());
        if (webBrowser.exists()) {
            labelWebBrowser.setText(webBrowser.getAbsolutePath());
            labelWebBrowser.setIcon(IconUtil.getSystemIcon(webBrowser));
        }
    }

    private void readPdfViewerProperties(UserSettings settings) {
        File pdfViewer = new File(settings.getPdfViewer());
        if (pdfViewer.exists()) {
            labelPdfViewer.setText(pdfViewer.getAbsolutePath());
            labelPdfViewer.setIcon(IconUtil.getSystemIcon(pdfViewer));
        }
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane, KEY_TABBED_PANE, null);
        UserSettings.INSTANCE.writeToFile();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupCopyMoveFiles = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        panelExternalApplications = new javax.swing.JPanel();
        labelInfoWebBrowser = new javax.swing.JLabel();
        labelWebBrowser = new javax.swing.JLabel();
        buttonChooseWebBrowser = new javax.swing.JButton();
        labelInfoPdfViewer = new javax.swing.JLabel();
        labelPdfViewer = new javax.swing.JLabel();
        buttonChoosePdfViewer = new javax.swing.JButton();
        panelMisc = new javax.swing.JPanel();
        checkBoxIsAcceptHiddenDirectories = new javax.swing.JCheckBox();
        panelCopyMoveFiles = new javax.swing.JPanel();
        radioButtonCopyMoveFileConfirmOverwrite = new javax.swing.JRadioButton();
        radioButtonCopyMoveFileRenameIfExists = new javax.swing.JRadioButton();
        checkBoxAutoDownloadCheck = new javax.swing.JCheckBox();
        checkBoxDisplaySearchButton = new javax.swing.JCheckBox();
        labelIptcCharset = new javax.swing.JLabel();
        comboBoxIptcCharset = new javax.swing.JComboBox();
        labelLogLevel = new javax.swing.JLabel();
        comboBoxLogLevel = new javax.swing.JComboBox();
        panelDatabaseDirectory = new javax.swing.JPanel();
        labelDatabaseDirectory = new javax.swing.JLabel();
        buttonSetStandardDatabaseDirectoryName = new javax.swing.JButton();
        buttonChooseDatabaseDirectory = new javax.swing.JButton();
        labelInfoDatabaseDirectory = new javax.swing.JLabel();

        labelInfoWebBrowser.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelInfoWebBrowser.text")); // NOI18N

        labelWebBrowser.setForeground(new java.awt.Color(0, 0, 255));
        labelWebBrowser.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseWebBrowser.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChooseWebBrowser.text")); // NOI18N
        buttonChooseWebBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseWebBrowserActionPerformed(evt);
            }
        });

        labelInfoPdfViewer.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelInfoPdfViewer.text")); // NOI18N

        labelPdfViewer.setForeground(new java.awt.Color(0, 0, 255));
        labelPdfViewer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChoosePdfViewer.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.buttonChoosePdfViewer.text")); // NOI18N
        buttonChoosePdfViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChoosePdfViewerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelExternalApplicationsLayout = new javax.swing.GroupLayout(panelExternalApplications);
        panelExternalApplications.setLayout(panelExternalApplicationsLayout);
        panelExternalApplicationsLayout.setHorizontalGroup(
            panelExternalApplicationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalApplicationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExternalApplicationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelExternalApplicationsLayout.createSequentialGroup()
                        .addComponent(labelInfoWebBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                        .addGap(16, 16, 16)
                        .addComponent(labelWebBrowser, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonChooseWebBrowser))
                    .addGroup(panelExternalApplicationsLayout.createSequentialGroup()
                        .addComponent(labelInfoPdfViewer, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelPdfViewer, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonChoosePdfViewer)))
                .addContainerGap())
        );
        panelExternalApplicationsLayout.setVerticalGroup(
            panelExternalApplicationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalApplicationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExternalApplicationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelInfoWebBrowser)
                    .addComponent(labelWebBrowser, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonChooseWebBrowser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelExternalApplicationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelInfoPdfViewer)
                    .addComponent(labelPdfViewer, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonChoosePdfViewer))
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelExternalApplications.TabConstraints.tabTitle"), panelExternalApplications); // NOI18N

        checkBoxIsAcceptHiddenDirectories.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.checkBoxIsAcceptHiddenDirectories.text")); // NOI18N
        checkBoxIsAcceptHiddenDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAcceptHiddenDirectoriesActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        panelCopyMoveFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsMiscPanel.panelCopyMoveFiles.border.title"))); // NOI18N

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileConfirmOverwrite);
        radioButtonCopyMoveFileConfirmOverwrite.setText(bundle.getString("SettingsMiscPanel.radioButtonCopyMoveFileConfirmOverwrite.text")); // NOI18N
        radioButtonCopyMoveFileConfirmOverwrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCopyMoveFileConfirmOverwriteActionPerformed(evt);
            }
        });

        buttonGroupCopyMoveFiles.add(radioButtonCopyMoveFileRenameIfExists);
        radioButtonCopyMoveFileRenameIfExists.setText(bundle.getString("SettingsMiscPanel.radioButtonCopyMoveFileRenameIfExists.text")); // NOI18N
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
                    .addComponent(radioButtonCopyMoveFileRenameIfExists, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addComponent(radioButtonCopyMoveFileConfirmOverwrite, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
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

        checkBoxAutoDownloadCheck.setText(bundle.getString("SettingsMiscPanel.checkBoxAutoDownloadCheck.text")); // NOI18N
        checkBoxAutoDownloadCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoDownloadCheckActionPerformed(evt);
            }
        });

        checkBoxDisplaySearchButton.setText(bundle.getString("SettingsMiscPanel.checkBoxDisplaySearchButton.text")); // NOI18N
        checkBoxDisplaySearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplaySearchButtonActionPerformed(evt);
            }
        });

        labelIptcCharset.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelIptcCharset.text")); // NOI18N

        comboBoxIptcCharset.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ISO-8859-1", "UTF-8" }));
        comboBoxIptcCharset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });

        labelLogLevel.setLabelFor(comboBoxLogLevel);
        labelLogLevel.setText(JptBundle.INSTANCE.getString("SettingsMiscPanel.labelLogLevel.text")); // NOI18N

        comboBoxLogLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { java.util.logging.Level.WARNING.getLocalizedName(), java.util.logging.Level.SEVERE.getLocalizedName(), java.util.logging.Level.INFO.getLocalizedName(), java.util.logging.Level.CONFIG.getLocalizedName(), java.util.logging.Level.FINE.getLocalizedName(), java.util.logging.Level.FINER.getLocalizedName(), java.util.logging.Level.FINEST.getLocalizedName() }));
        comboBoxLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLogLevelActionPerformed(evt);
            }
        });

        panelDatabaseDirectory.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsMiscPanel.panelDatabaseDirectory.border.title"))); // NOI18N

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

        javax.swing.GroupLayout panelDatabaseDirectoryLayout = new javax.swing.GroupLayout(panelDatabaseDirectory);
        panelDatabaseDirectory.setLayout(panelDatabaseDirectoryLayout);
        panelDatabaseDirectoryLayout.setHorizontalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatabaseDirectoryLayout.createSequentialGroup()
                        .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetStandardDatabaseDirectoryName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseDatabaseDirectory)))
                .addContainerGap())
        );
        panelDatabaseDirectoryLayout.setVerticalGroup(
            panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatabaseDirectoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatabaseDirectoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChooseDatabaseDirectory)
                    .addComponent(buttonSetStandardDatabaseDirectoryName)
                    .addComponent(labelInfoDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMiscLayout = new javax.swing.GroupLayout(panelMisc);
        panelMisc.setLayout(panelMiscLayout);
        panelMiscLayout.setHorizontalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMiscLayout.createSequentialGroup()
                        .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelIptcCharset, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                            .addComponent(labelLogLevel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(275, 275, 275))
                    .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxAutoDownloadCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                    .addComponent(checkBoxIsAcceptHiddenDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                    .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelMiscLayout.setVerticalGroup(
            panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMiscLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(checkBoxIsAcceptHiddenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAutoDownloadCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplaySearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelCopyMoveFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(comboBoxIptcCharset, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelIptcCharset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(comboBoxLogLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLogLevel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatabaseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("SettingsMiscPanel.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
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
        handleActionPerformedChooseDatabaseDirectory();
}//GEN-LAST:event_buttonChooseDatabaseDirectoryActionPerformed

    private void buttonSetStandardDatabaseDirectoryNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed
        handleActionPerformedSetStandardDatabaseDirectory();
}//GEN-LAST:event_buttonSetStandardDatabaseDirectoryNameActionPerformed

    private void buttonChoosePdfViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChoosePdfViewerActionPerformed
        handleActionPerformedChoosePdfViewer();
}//GEN-LAST:event_buttonChoosePdfViewerActionPerformed

    private void buttonChooseWebBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseWebBrowserActionPerformed
        handleActionPerformedChooseWebBrowser();
}//GEN-LAST:event_buttonChooseWebBrowserActionPerformed

    private void checkBoxDisplaySearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplaySearchButtonActionPerformed
        handleActionPerformedCheckBoxDisplaySearchButton();
}//GEN-LAST:event_checkBoxDisplaySearchButtonActionPerformed

    private void comboBoxIptcCharsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxIptcCharsetActionPerformed
        handleActionComboBoxIptcCharset();
}//GEN-LAST:event_comboBoxIptcCharsetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseDatabaseDirectory;
    private javax.swing.JButton buttonChoosePdfViewer;
    private javax.swing.JButton buttonChooseWebBrowser;
    private javax.swing.ButtonGroup buttonGroupCopyMoveFiles;
    private javax.swing.JButton buttonSetStandardDatabaseDirectoryName;
    private javax.swing.JCheckBox checkBoxAutoDownloadCheck;
    private javax.swing.JCheckBox checkBoxDisplaySearchButton;
    private javax.swing.JCheckBox checkBoxIsAcceptHiddenDirectories;
    private javax.swing.JComboBox comboBoxIptcCharset;
    private javax.swing.JComboBox comboBoxLogLevel;
    private javax.swing.JLabel labelDatabaseDirectory;
    private javax.swing.JLabel labelInfoDatabaseDirectory;
    private javax.swing.JLabel labelInfoPdfViewer;
    private javax.swing.JLabel labelInfoWebBrowser;
    private javax.swing.JLabel labelIptcCharset;
    private javax.swing.JLabel labelLogLevel;
    private javax.swing.JLabel labelPdfViewer;
    private javax.swing.JLabel labelWebBrowser;
    private javax.swing.JPanel panelCopyMoveFiles;
    private javax.swing.JPanel panelDatabaseDirectory;
    private javax.swing.JPanel panelExternalApplications;
    private javax.swing.JPanel panelMisc;
    private javax.swing.JRadioButton radioButtonCopyMoveFileConfirmOverwrite;
    private javax.swing.JRadioButton radioButtonCopyMoveFileRenameIfExists;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
