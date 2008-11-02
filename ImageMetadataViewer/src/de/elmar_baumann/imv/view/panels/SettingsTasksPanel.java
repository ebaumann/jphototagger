package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.model.ListModelAutoscanDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.renderer.ListCellRendererFileSystem;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/02
 */
public class SettingsTasksPanel extends javax.swing.JPanel
    implements Persistence {

    private DatabaseAutoscanDirectories db = DatabaseAutoscanDirectories.getInstance();
    private ListModelAutoscanDirectories modelAutoscanDirectories = new ListModelAutoscanDirectories();
    private ListenerProvider listenerProvider = ListenerProvider.getInstance();
    private String lastSelectedAutoscanDirectory = ""; // NOI18N
    private final String keyLastSelectedAutoscanDirectory = "UserSettingsDialog.keyLastSelectedAutoscanDirectory"; // NOI18N

    /** Creates new form SettingsTasksPanel */
    public SettingsTasksPanel() {
        initComponents();
        postInitComponents();
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
    public void readPersistent() {
        UserSettings settings = UserSettings.getInstance();
        spinnerMinutesToStartScheduledTasks.setValue(
            settings.getMinutesToStartScheduledTasks());
        checkBoxIsAutoscanIncludeSubdirectories.setSelected(
            settings.isAutoscanIncludeSubdirectories());
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setSelected(
            settings.isTaskRemoveRecordsWithNotExistingFiles());
        lastSelectedAutoscanDirectory = PersistentSettings.getInstance().
            getString(keyLastSelectedAutoscanDirectory);
    }

    @Override
    public void writePersistent() {
        PersistentSettings.getInstance().setString(
            lastSelectedAutoscanDirectory, keyLastSelectedAutoscanDirectory);
    }

    private void addAutoscanDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        ViewUtil.setDirectoryTreeModel(dialog);
        dialog.setStartDirectory(new File(lastSelectedAutoscanDirectory));
        dialog.setMultiSelection(true);
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
                            messageErrorInsertAutoscanDirectory(directoryName);
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
                    messageErrorDeleteAutoscanDirectory(directoryName);
                }
            }
        }
        setEnabled();
    }

    private void messageErrorInsertAutoscanDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ErrorMessage.InsertAutoscanDirectory"));
        Object[] params = {directoryName};
        JOptionPane.showMessageDialog(
            this,
            msg.format(params),
            Bundle.getString("UserSettingsDialog.ErrorMessage.InsertAutoscanDirectory.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void messageErrorDeleteAutoscanDirectory(String directoryName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("UserSettingsDialog.ErrorMessage.DeleteAutoscanDirectory"));
        Object[] params = {directoryName};
        JOptionPane.showMessageDialog(
            this,
            msg.format(params),
            Bundle.getString("UserSettingsDialog.ErrorMessage.DeleteAutoscanDirectory.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getMediumAppIcon());
    }

    private void handleKeyEventListTasksAutoscanDirectories(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedAutoscanDirectories();
        }
    }

    private void handleStateChangedSpinnerMinutesToStartScheduledTasks() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.MinutesToStartScheduledTasks, this);
        evt.setMinutesToStartScheduledTasks(
            (Integer) spinnerMinutesToStartScheduledTasks.getValue());
        notifyChangeListener(evt);
    }

    private void handleActionCheckBoxIsAutoscanIncludeSubdirectories() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsAutoscanIncludeSubdirectories, this);
        evt.setAutoscanIncludeSubdirectories(
            checkBoxIsAutoscanIncludeSubdirectories.isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionCheckBoxIsTaskRemoveRecordsWithNotExistingFiles() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
            UserSettingsChangeEvent.Type.IsTaskRemoveRecordsWithNotExistingFiles, this);
        evt.setTaskRemoveRecordsWithNotExistingFiles(
            checkBoxIsTaskRemoveRecordsWithNotExistingFiles.isSelected());
        notifyChangeListener(evt);
    }

    private void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private void setEnabledButtonRemoveAutoscanDirectory() {
        buttonRemoveAutoscanDirectories.setEnabled(
            listAutoscanDirectories.getSelectedIndices().length > 0);
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
        panelTasksOther = new javax.swing.JPanel();
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles = new javax.swing.JCheckBox();
        labelTasksMinutesToStartScheduledTasks = new javax.swing.JLabel();
        spinnerMinutesToStartScheduledTasks = new javax.swing.JSpinner();

        panelTasksAutoscan.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datenbank aktualisieren", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        labelAutoscanDirectoriesInfo.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAutoscanDirectoriesInfo.setText(Bundle.getString("SettingsTasksPanel.labelAutoscanDirectoriesInfo.text")); // NOI18N

        labelAutoscanDirectoriesPrompt.setFont(new java.awt.Font("Dialog", 0, 12));
        labelAutoscanDirectoriesPrompt.setText(Bundle.getString("SettingsTasksPanel.labelAutoscanDirectoriesPrompt.text")); // NOI18N

        listAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
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

        checkBoxIsAutoscanIncludeSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsAutoscanIncludeSubdirectories.setText(Bundle.getString("SettingsTasksPanel.checkBoxIsAutoscanIncludeSubdirectories.text")); // NOI18N
        checkBoxIsAutoscanIncludeSubdirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsAutoscanIncludeSubdirectoriesActionPerformed(evt);
            }
        });

        buttonRemoveAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonRemoveAutoscanDirectories.setMnemonic('e');
        buttonRemoveAutoscanDirectories.setText(Bundle.getString("SettingsTasksPanel.buttonRemoveAutoscanDirectories.text")); // NOI18N
        buttonRemoveAutoscanDirectories.setEnabled(false);
        buttonRemoveAutoscanDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAutoscanDirectoriesActionPerformed(evt);
            }
        });

        buttonAddAutoscanDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonAddAutoscanDirectories.setMnemonic('h');
        buttonAddAutoscanDirectories.setText(Bundle.getString("SettingsTasksPanel.buttonAddAutoscanDirectories.text")); // NOI18N
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
                    .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTasksAutoscanLayout.createSequentialGroup()
                        .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE))
                            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                                .addComponent(buttonRemoveAutoscanDirectories)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(buttonAddAutoscanDirectories))
                    .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
                    .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        panelTasksAutoscanLayout.setVerticalGroup(
            panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksAutoscanLayout.createSequentialGroup()
                .addComponent(labelAutoscanDirectoriesInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAutoscanDirectoriesPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListAutoscanDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIsAutoscanIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTasksAutoscanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddAutoscanDirectories)
                    .addComponent(buttonRemoveAutoscanDirectories))
                .addContainerGap())
        );

        panelTasksOther.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Weitere Aufgaben", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.setText(Bundle.getString("SettingsTasksPanel.checkBoxIsTaskRemoveRecordsWithNotExistingFiles.text")); // NOI18N
        checkBoxIsTaskRemoveRecordsWithNotExistingFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTasksOtherLayout = new javax.swing.GroupLayout(panelTasksOther);
        panelTasksOther.setLayout(panelTasksOtherLayout);
        panelTasksOtherLayout.setHorizontalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxIsTaskRemoveRecordsWithNotExistingFiles)
                .addContainerGap(123, Short.MAX_VALUE))
        );
        panelTasksOtherLayout.setVerticalGroup(
            panelTasksOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTasksOtherLayout.createSequentialGroup()
                .addComponent(checkBoxIsTaskRemoveRecordsWithNotExistingFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelTasksMinutesToStartScheduledTasks.setFont(new java.awt.Font("Dialog", 0, 12));
        labelTasksMinutesToStartScheduledTasks.setText(Bundle.getString("SettingsTasksPanel.labelTasksMinutesToStartScheduledTasks.text")); // NOI18N

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
                    .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTasksOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTasksMinutesToStartScheduledTasks)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTasksAutoscan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTasksOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTasksMinutesToStartScheduledTasks)
                    .addComponent(spinnerMinutesToStartScheduledTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
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

private void checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed
    handleActionCheckBoxIsTaskRemoveRecordsWithNotExistingFiles();
}//GEN-LAST:event_checkBoxIsTaskRemoveRecordsWithNotExistingFilesActionPerformed

private void spinnerMinutesToStartScheduledTasksStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMinutesToStartScheduledTasksStateChanged
    handleStateChangedSpinnerMinutesToStartScheduledTasks();
}//GEN-LAST:event_spinnerMinutesToStartScheduledTasksStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAutoscanDirectories;
    private javax.swing.JButton buttonRemoveAutoscanDirectories;
    private javax.swing.JCheckBox checkBoxIsAutoscanIncludeSubdirectories;
    private javax.swing.JCheckBox checkBoxIsTaskRemoveRecordsWithNotExistingFiles;
    private javax.swing.JLabel labelAutoscanDirectoriesInfo;
    private javax.swing.JLabel labelAutoscanDirectoriesPrompt;
    private javax.swing.JLabel labelTasksMinutesToStartScheduledTasks;
    private javax.swing.JList listAutoscanDirectories;
    private javax.swing.JPanel panelTasksAutoscan;
    private javax.swing.JPanel panelTasksOther;
    private javax.swing.JScrollPane scrollPaneListAutoscanDirectories;
    private javax.swing.JSpinner spinnerMinutesToStartScheduledTasks;
    // End of variables declaration//GEN-END:variables
}
