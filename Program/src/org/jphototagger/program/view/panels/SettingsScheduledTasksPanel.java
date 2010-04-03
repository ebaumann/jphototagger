/*
 * @(#)SettingsScheduledTasksPanel.java    Created on 2008-11-02
 *
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

package org.jphototagger.program.view.panels;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.model.ListModelAutoscanDirectories;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.model.ComboBoxModelScheduledTaskBackupDatabase;
import org.jphototagger.program.tasks.ScheduledTaskBackupDatabase;
import org.jphototagger.program.tasks.ScheduledTaskBackupDatabase.Interval;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.renderer.ListCellRendererFileSystem;

import java.awt.Container;
import java.awt.event.KeyEvent;

import java.io.File;

import java.util.List;

import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  Elmar Baumann
 */
public final class SettingsScheduledTasksPanel extends javax.swing.JPanel
        implements Persistence {
    private static final String KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY =
        "UserSettingsDialog.keyLastSelectedAutoscanDirectory";
    private static final long                           serialVersionUID =
        -5964543997343669428L;
    private final transient DatabaseAutoscanDirectories db               =
        DatabaseAutoscanDirectories.INSTANCE;
    private ListModelAutoscanDirectories modelAutoscanDirectories =
        new ListModelAutoscanDirectories();
    private String lastSelectedAutoscanDirectory = "";

    public SettingsScheduledTasksPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        modelAutoscanDirectories = new ListModelAutoscanDirectories();
        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        MnemonicUtil.setMnemonics((Container) this);
        setEnabled();
    }

    public void setEnabled() {
        buttonRemoveAutoscanDirectories.setEnabled(
            listAutoscanDirectories.getSelectedIndex() >= 0);
        setEnabledCheckBoxSubdirs();
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;

        spinnerMinutesToStartScheduledTasks.setValue(
            settings.getMinutesToStartScheduledTasks());
        checkBoxIsAutoscanIncludeSubdirectories.setSelected(
            settings.isAutoscanIncludeSubdirectories());
        lastSelectedAutoscanDirectory = settings.getSettings().getString(
            KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY);
        checkBoxScheduledBackupDb.setSelected(UserSettings.INSTANCE.isScheduledBackupDb());

        Interval interval = Interval.fromDays(UserSettings.INSTANCE.getScheduledBackupDbInterval());
        if (interval != null) {
            comboBoxScheduledBackupDb.setSelectedItem(interval);
        }
        comboBoxScheduledBackupDb.setEnabled(checkBoxScheduledBackupDb.isSelected());
        setEnabledCheckBoxSubdirs();

    }

    private void setEnabledCheckBoxSubdirs() {
        boolean hasDirs = listAutoscanDirectories.getModel().getSize() > 0;
        checkBoxIsAutoscanIncludeSubdirectories.setEnabled(hasDirs);
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(lastSelectedAutoscanDirectory,
                KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    private void addAutoscanDirectories() {
        DirectoryChooser dialog =
            new DirectoryChooser(
                GUI.INSTANCE.getAppFrame(),
                new File(lastSelectedAutoscanDirectory),
                UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dialog.setSettings(UserSettings.INSTANCE.getSettings(),
                           "SettingsScheduledTasksPanel.DirChooser");
        dialog.setVisible(true);

        if (dialog.isAccepted()) {
            List<File> directories = dialog.getSelectedDirectories();

            for (File directory : directories) {
                if (!modelAutoscanDirectories.contains(directory)) {
                    lastSelectedAutoscanDirectory = directory.getAbsolutePath();

                    if (!db.exists(directory)) {
                        if (!db.insert(directory)) {
                            errorMessageInsertAutoscanDirectory(directory);
                        }
                    }
                }
            }
        }

        setEnabled();
    }

    private void removeSelectedAutoscanDirectories() {
        Object[] values = listAutoscanDirectories.getSelectedValues();

        for (int i = 0; i < values.length; i++) {
            File   directory     = (File) values[i];

            if (db.exists(directory)) {
                if (!db.delete(directory)) {
                    errorMessageDeleteAutoscanDirectory(directory);
                }
            }
        }

        setEnabled();
    }

    private void errorMessageInsertAutoscanDirectory(File directory) {
        MessageDisplayer.error(
            this, "SettingsScheduledTasksPanel.Error.InsertAutoscanDirectory",
            directory);
    }

    private void errorMessageDeleteAutoscanDirectory(File directory) {
        MessageDisplayer.error(
            this, "SettingsScheduledTasksPanel.Error.DeleteAutoscanDirectory",
            directory);
    }

    private void handleKeyEventListTasksAutoscanDirectories(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedAutoscanDirectories();
        }
    }

    private void handleStateChangedSpinnerMinutesToStartScheduledTasks() {
        UserSettings.INSTANCE.setMinutesToStartScheduledTasks(
            (Integer) spinnerMinutesToStartScheduledTasks.getValue());
    }

    private void handleActionCheckBoxIsAutoscanIncludeSubdirectories() {
        UserSettings.INSTANCE.setAutoscanIncludeSubdirectories(
            checkBoxIsAutoscanIncludeSubdirectories.isSelected());
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonRemoveAutoscanDirectories.setEnabled(
            listAutoscanDirectories.getSelectedIndices().length > 0);
    }

    public JButton getButtonScheduledTasks() {
        return buttonStopScheduledTasks;
    }

    private void handleCheckBoxBackupDbActionPerformed() {
        boolean backup = checkBoxScheduledBackupDb.isSelected();
        UserSettings.INSTANCE.setScheduledBackupDb(backup);
        UserSettings.INSTANCE.writeToFile();
        backupDbIntervalToSettings();
        comboBoxScheduledBackupDb.setEnabled(backup);
        ScheduledTaskBackupDatabase.INSTANCE.setBackup();
    }

    private void backupDbIntervalToSettings() {
        Object selItem = comboBoxScheduledBackupDb.getSelectedItem();
        if (selItem instanceof Interval) {
            Interval interval = (Interval) selItem;
            UserSettings.INSTANCE.setScheduledBackupDbInterval(interval.getDays());
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void handleComboBoxScheduledBackupDbActionPerformed() {
        backupDbIntervalToSettings();
        ScheduledTaskBackupDatabase.INSTANCE.setBackup();
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

        panelTasksAutoscan = new javax.swing.JPanel();
        labelAutoscanDirectoriesInfo = new javax.swing.JLabel();
        labelAutoscanDirectoriesPrompt = new javax.swing.JLabel();
        scrollPaneListAutoscanDirectories = new javax.swing.JScrollPane();
        listAutoscanDirectories = new javax.swing.JList();
        checkBoxIsAutoscanIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonRemoveAutoscanDirectories = new javax.swing.JButton();
        buttonAddAutoscanDirectories = new javax.swing.JButton();
        checkBoxScheduledBackupDb = new javax.swing.JCheckBox();
        comboBoxScheduledBackupDb = new javax.swing.JComboBox();
        buttonStopScheduledTasks = new javax.swing.JButton();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        spinnerMinutesToStartScheduledTasks = new javax.swing.JSpinner();

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.panelTasksAutoscan.border.title"))); // NOI18N
        panelTasksAutoscan.setLayout(new java.awt.GridBagLayout());

        labelAutoscanDirectoriesInfo.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.labelAutoscanDirectoriesInfo.text")); // NOI18N
        labelAutoscanDirectoriesInfo.setPreferredSize(new java.awt.Dimension(978, 48));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelTasksAutoscan.add(labelAutoscanDirectoriesInfo, gridBagConstraints);

        labelAutoscanDirectoriesPrompt.setLabelFor(listAutoscanDirectories);
        labelAutoscanDirectoriesPrompt.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.labelAutoscanDirectoriesPrompt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelTasksAutoscan.add(labelAutoscanDirectoriesPrompt, gridBagConstraints);

        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        listAutoscanDirectories.setCellRenderer(new ListCellRendererFileSystem(true));
        listAutoscanDirectories.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listAutoscanDirectoriesValueChanged(evt);
            }
        });
        listAutoscanDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listAutoscanDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneListAutoscanDirectories.setViewportView(listAutoscanDirectories);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelTasksAutoscan.add(scrollPaneListAutoscanDirectories, gridBagConstraints);

        checkBoxIsAutoscanIncludeSubdirectories.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.checkBoxIsAutoscanIncludeSubdirectories.text")); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelTasksAutoscan.add(checkBoxIsAutoscanIncludeSubdirectories, gridBagConstraints);

        buttonRemoveAutoscanDirectories.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.buttonRemoveAutoscanDirectories.text")); // NOI18N
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelTasksAutoscan.add(buttonRemoveAutoscanDirectories, gridBagConstraints);

        buttonAddAutoscanDirectories.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.buttonAddAutoscanDirectories.text")); // NOI18N
        buttonAddAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAutoscanDirectoriesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panelTasksAutoscan.add(buttonAddAutoscanDirectories, gridBagConstraints);

        checkBoxScheduledBackupDb.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.checkBoxScheduledBackupDb.text")); // NOI18N
        checkBoxScheduledBackupDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxScheduledBackupDbActionPerformed(evt);
            }
        });

        comboBoxScheduledBackupDb.setModel(new ComboBoxModelScheduledTaskBackupDatabase());
        comboBoxScheduledBackupDb.setEnabled(false);
        comboBoxScheduledBackupDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxScheduledBackupDbActionPerformed(evt);
            }
        });

        buttonStopScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_stop_scheduled_tasks_enabled.png"))); // NOI18N
        buttonStopScheduledTasks.setMnemonic('s');
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        buttonStopScheduledTasks.setToolTipText(bundle.getString("SettingsScheduledTasksPanel.buttonStopScheduledTasks.toolTipText")); // NOI18N
        buttonStopScheduledTasks.setAlignmentY(0.0F);
        buttonStopScheduledTasks.setBorder(null);
        buttonStopScheduledTasks.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_stop_scheduled_tasks_disabled.png"))); // NOI18N
        buttonStopScheduledTasks.setEnabled(false);
        buttonStopScheduledTasks.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonStopScheduledTasks.setMargin(new java.awt.Insets(0, 0, 0, 0));

        labelTasksMinutesToStartScheduledTasks.setLabelFor(spinnerMinutesToStartScheduledTasks);
        labelTasksMinutesToStartScheduledTasks.setText(JptBundle.INSTANCE.getString("SettingsScheduledTasksPanel.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N

        spinnerMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));
        spinnerMinutesToStartScheduledTasks.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMinutesToStartScheduledTasksStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxScheduledBackupDb, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxScheduledBackupDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTasksMinutesToStartScheduledTasks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addComponent(buttonStopScheduledTasks)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxScheduledBackupDb)
                    .addComponent(comboBoxScheduledBackupDb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTasksMinutesToStartScheduledTasks)
                    .addComponent(buttonStopScheduledTasks))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listAutoscanDirectoriesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesValueChanged
        setEnabledButtonRemoveAutoscanDirectory();
    }//GEN-LAST:event_listAutoscanDirectoriesValueChanged

    private void listAutoscanDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listAutoscanDirectoriesKeyReleased
        handleKeyEventListTasksAutoscanDirectories(evt);
    }//GEN-LAST:event_listAutoscanDirectoriesKeyReleased

    private void checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed
        handleActionCheckBoxIsAutoscanIncludeSubdirectories();
    }//GEN-LAST:event_checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed

    private void buttonRemoveAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveAutoscanDirectoriesActionPerformed
        removeSelectedAutoscanDirectories();
    }//GEN-LAST:event_buttonRemoveAutoscanDirectoriesActionPerformed

    private void buttonAddAutoscanDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAutoscanDirectoriesActionPerformed
        addAutoscanDirectories();
    }//GEN-LAST:event_buttonAddAutoscanDirectoriesActionPerformed

    private void spinnerMinutesToStartScheduledTasksStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMinutesToStartScheduledTasksStateChanged
        handleStateChangedSpinnerMinutesToStartScheduledTasks();
    }//GEN-LAST:event_spinnerMinutesToStartScheduledTasksStateChanged

    private void checkBoxScheduledBackupDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxScheduledBackupDbActionPerformed
        handleCheckBoxBackupDbActionPerformed();
    }//GEN-LAST:event_checkBoxScheduledBackupDbActionPerformed

    private void comboBoxScheduledBackupDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxScheduledBackupDbActionPerformed
        handleComboBoxScheduledBackupDbActionPerformed();
    }//GEN-LAST:event_comboBoxScheduledBackupDbActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JButton buttonStopScheduledTasks;
    private javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    private javax.swing.JCheckBox checkBoxScheduledBackupDb;
    private javax.swing.JComboBox comboBoxScheduledBackupDb;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private javax.swing.JList listAutoscanDirectories;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
    // End of variables declaration//GEN-END:variables
}
