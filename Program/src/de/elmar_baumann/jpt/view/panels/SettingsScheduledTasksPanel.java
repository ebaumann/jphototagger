/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.jpt.event.listener.impl.ListenerProvider;
import de.elmar_baumann.jpt.event.UserSettingsChangeEvent;
import de.elmar_baumann.jpt.model.ListModelAutoscanDirectories;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.dialog.DirectoryChooser.Option;
import de.elmar_baumann.lib.renderer.ListCellRendererFileSystem;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-02
 */
public final class SettingsScheduledTasksPanel extends javax.swing.JPanel
        implements Persistence {

    private static final String                      KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY = "UserSettingsDialog.keyLastSelectedAutoscanDirectory";
    private final        DatabaseAutoscanDirectories db                                   = DatabaseAutoscanDirectories.INSTANCE;
    private final        ListenerProvider            listenerProvider                     = ListenerProvider.INSTANCE;
    private              ListModelAutoscanDirectories modelAutoscanDirectories            = new ListModelAutoscanDirectories();
    private String       lastSelectedAutoscanDirectory                                    = "";

    /** Creates new form SettingsScheduledTasksPanel */
    public SettingsScheduledTasksPanel() {
        initComponents();
        postInitComponents();
    }

    private Set<Option> getDirectoryChooserFilter() {
        return EnumSet.of(Option.MULTI_SELECTION,
                UserSettings.INSTANCE.isAcceptHiddenDirectories()
                ? Option.ACCEPT_HIDDEN_DIRECTORIES
                : Option.REJECT_HIDDEN_DIRECTORIES);
    }

    private void postInitComponents() {
        modelAutoscanDirectories = new ListModelAutoscanDirectories();
        listAutoscanDirectories.setModel(modelAutoscanDirectories);
        setEnabled();
    }

    public void setEnabled() {
        buttonRemoveAutoscanDirectories.setEnabled(
                listAutoscanDirectories.getSelectedIndex() >= 0);
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;
        spinnerMinutesToStartScheduledTasks.setValue(settings.getMinutesToStartScheduledTasks());
        checkBoxIsAutoscanIncludeSubdirectories.setSelected(settings.isAutoscanIncludeSubdirectories());
        lastSelectedAutoscanDirectory = settings.getSettings().getString(KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY);
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().setString(lastSelectedAutoscanDirectory, KEY_LAST_SELECTED_AUTOSCAN_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    private void addAutoscanDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(null, new File(
                lastSelectedAutoscanDirectory), getDirectoryChooserFilter());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> directories = dialog.getSelectedDirectories();
            for (File directory : directories) {
                if (!modelAutoscanDirectories.contains(directory)) {
                    String directoryName = directory.getAbsolutePath();
                    lastSelectedAutoscanDirectory = directoryName;
                    if (!db.existsAutoscanDirectory(directoryName)) {
                        if (db.insertAutoscanDirectory(directoryName)) {
                            modelAutoscanDirectories.addElement(directory);
                        } else {
                            errorMessageInsertAutoscanDirectory(directoryName);
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
            File directory = (File) values[i];
            String directoryName = (directory).getAbsolutePath();
            if (db.existsAutoscanDirectory(directoryName)) {
                if (db.deleteAutoscanDirectory(directoryName)) {
                    modelAutoscanDirectories.removeElement(directory);
                } else {
                    errorMessageDeleteAutoscanDirectory(directoryName);
                }
            }
        }
        setEnabled();
    }

    private void errorMessageInsertAutoscanDirectory(String directoryName) {
        MessageDisplayer.error(this,
                "SettingsTasksPanel.Error.InsertAutoscanDirectory",
                directoryName);
    }

    private void errorMessageDeleteAutoscanDirectory(String directoryName) {
        MessageDisplayer.error(this,
                "SettingsTasksPanel.Error.DeleteAutoscanDirectory",
                directoryName);
    }

    private void handleKeyEventListTasksAutoscanDirectories(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedAutoscanDirectories();
        }
    }

    private void handleStateChangedSpinnerMinutesToStartScheduledTasks() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.MINUTES_TO_START_SCHEDULED_TASKS,
                this);
        evt.setMinutesToStartScheduledTasks(
                (Integer) spinnerMinutesToStartScheduledTasks.getValue());
        notifyChangeListener(evt);
    }

    private void handleActionCheckBoxIsAutoscanIncludeSubdirectories() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.IS_AUTSCAN_INCLUDE_DIRECTORIES,
                this);
        evt.setAutoscanIncludeSubdirectories(
                checkBoxIsAutoscanIncludeSubdirectories.isSelected());
        notifyChangeListener(evt);
    }

    private synchronized void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonRemoveAutoscanDirectories.setEnabled(
                listAutoscanDirectories.getSelectedIndices().length > 0);
    }

    public JButton getButtonScheduledTasks() {
        return buttonStopScheduledTasks;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTasksAutoscan = new javax.swing.JPanel();
        labelAutoscanDirectoriesInfo = new javax.swing.JLabel();
        labelAutoscanDirectoriesPrompt = new javax.swing.JLabel();
        scrollPaneListAutoscanDirectories = new javax.swing.JScrollPane();
        listAutoscanDirectories = new javax.swing.JList();
        checkBoxIsAutoscanIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonRemoveAutoscanDirectories = new javax.swing.JButton();
        buttonAddAutoscanDirectories = new javax.swing.JButton();
        panelStartDelay = new javax.swing.JPanel();
        spinnerMinutesToStartScheduledTasks = new javax.swing.JSpinner();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        buttonStopScheduledTasks = new javax.swing.JButton();

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("SettingsScheduledTasksPanel.panelTasksAutoscan.border.title")));

        labelAutoscanDirectoriesInfo.setText(Bundle.getString("SettingsScheduledTasksPanel.labelAutoscanDirectoriesInfo.text"));
        labelAutoscanDirectoriesInfo.setPreferredSize(new java.awt.Dimension(978, 48));

        labelAutoscanDirectoriesPrompt.setText(Bundle.getString("SettingsScheduledTasksPanel.labelAutoscanDirectoriesPrompt.text"));

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

        checkBoxIsAutoscanIncludeSubdirectories.setText(Bundle.getString("SettingsScheduledTasksPanel.checkBoxIsAutoscanIncludeSubdirectories.text"));
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(evt);
            }
        });

        buttonRemoveAutoscanDirectories.setMnemonic('e');
        buttonRemoveAutoscanDirectories.setText(Bundle.getString("SettingsScheduledTasksPanel.buttonRemoveAutoscanDirectories.text"));
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });

        buttonAddAutoscanDirectories.setMnemonic('h');
        buttonAddAutoscanDirectories.setText(Bundle.getString("SettingsScheduledTasksPanel.buttonAddAutoscanDirectories.text"));
        buttonAddAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAutoscanDirectoriesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTasksAutoscanLayout = new javax.swing.GroupLayout(panelTasksAutoscan);
        panelTasksAutoscan.setLayout(panelTasksAutoscanLayout);
        panelTasksAutoscanLayout.setHorizontalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTasksAutoscanLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTasksAutoscanLayout.createSequentialGroup()
                            .addComponent(buttonRemoveAutoscanDirectories)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonAddAutoscanDirectories)))
                    .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelTasksAutoscanLayout.setVerticalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTasksAutoscanLayout.createSequentialGroup()
                .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddAutoscanDirectories)
                    .addComponent(buttonRemoveAutoscanDirectories))
                .addContainerGap())
        );

        spinnerMinutesToStartScheduledTasks.setModel(new SpinnerNumberModel(5, 1, 6000, 1));
        spinnerMinutesToStartScheduledTasks.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMinutesToStartScheduledTasksStateChanged(evt);
            }
        });

        labelTasksMinutesToStartScheduledTasks.setText(Bundle.getString("SettingsScheduledTasksPanel.labelTasksMinutesToStartScheduledTasks.text"));

        javax.swing.GroupLayout panelStartDelayLayout = new javax.swing.GroupLayout(panelStartDelay);
        panelStartDelay.setLayout(panelStartDelayLayout);
        panelStartDelayLayout.setHorizontalGroup(
            panelStartDelayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStartDelayLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTasksMinutesToStartScheduledTasks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelStartDelayLayout.setVerticalGroup(
            panelStartDelayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStartDelayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(labelTasksMinutesToStartScheduledTasks))
        );

        buttonStopScheduledTasks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_stop_scheduled_tasks_enabled.png")));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle");
        buttonStopScheduledTasks.setToolTipText(bundle.getString("SettingsScheduledTasksPanel.buttonStopScheduledTasks.toolTipText"));
        buttonStopScheduledTasks.setAlignmentY(0.0F);
        buttonStopScheduledTasks.setBorder(null);
        buttonStopScheduledTasks.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/jpt/resource/icons/icon_stop_scheduled_tasks_disabled.png")));
        buttonStopScheduledTasks.setEnabled(false);
        buttonStopScheduledTasks.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        buttonStopScheduledTasks.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(panelStartDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonStopScheduledTasks)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonStopScheduledTasks)
                    .addComponent(panelStartDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JButton buttonStopScheduledTasks;
    private javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private javax.swing.JList listAutoscanDirectories;
    private javax.swing.JPanel panelStartDelay;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
    // End of variables declaration//GEN-END:variables
}
