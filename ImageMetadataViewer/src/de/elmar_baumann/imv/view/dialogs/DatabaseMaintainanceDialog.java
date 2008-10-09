package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.tasks.RecordsWithNotExistingFilesDeleter;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.model.TableModelDatabaseInfo;
import de.elmar_baumann.imv.event.listener.TotalRecordCountListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.TableCellRendererDatabaseInfoColumns;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.persistence.PersistentSettingsHints;
import java.awt.Cursor;
import java.text.MessageFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Modaler Dialog zur Wartung der Thumbnaildatenbank.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public class DatabaseMaintainanceDialog extends javax.swing.JDialog implements
    ProgressListener {

    private Database db = Database.getInstance();
    private TableModelDatabaseInfo modelDatabaseInfo = new TableModelDatabaseInfo();
    private TotalRecordCountListener listenerTotalRecordCount = new TotalRecordCountListener();
    private boolean abortAction = false;
    private boolean closedEnabled = true;
    private final ImageIcon okIcon = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_check_ok_small.png"); // NOI18N
    private final ImageIcon errorIcon = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_check_error_small.png"); // NOI18N
    private static DatabaseMaintainanceDialog instance = new DatabaseMaintainanceDialog();

    private DatabaseMaintainanceDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    public static DatabaseMaintainanceDialog getInstance() {
        return instance;
    }

    @Override
    public void setVisible(boolean visible) {
        modelDatabaseInfo.setListenToDatabase(visible);
        if (visible) {
            PersistentAppSizes.getSizeAndLocation(this);
            PersistentSettings.getInstance().getComponent(this, new PersistentSettingsHints());
            setEnabledButtonStartMaintain();
            modelDatabaseInfo.update();
            setTotalRecordCount();
            listenerTotalRecordCount.addLabel(labelDatabaseTotalRecordCount);
        } else {
            listenerTotalRecordCount.removeLabel(labelDatabaseTotalRecordCount);
        }
        listenerTotalRecordCount.setListenToDatabase(visible);
        super.setVisible(visible);
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
        tableDatabaseInfo.setDefaultRenderer(Object.class, new TableCellRendererDatabaseInfoColumns());
    }

    private void setTotalRecordCount() {
        labelDatabaseTotalRecordCount.setText(Long.toString(db.getTotalRecordCount()));
    }

    private void setEnabledButtonStartMaintain() {
        buttonStartMaintain.setEnabled(//
            checkBoxCompressDatabase.isSelected() ||
            checkBoxDeleteNotExistingFilesInDatabase.isSelected());
    }

    private void setClosedEnabled(boolean enable) {
        closedEnabled = enable;
    }

    private boolean isClosedEnabled() {
        return closedEnabled;
    }

    private void compressDatabase() {
        Cursor oldCursor = getCursor();
        setClosedEnabled(false);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        boolean success = db.compressDatabase();
        setCursor(oldCursor);
        setClosedEnabled(true);
        messageCompressDatabase(success);
    }

    private void deleteNotExistingFilesInDatabase() {
        setClosedEnabled(false);
        RecordsWithNotExistingFilesDeleter deleter = new RecordsWithNotExistingFilesDeleter();
        deleter.addProgressListener(this);
        Thread thread = new Thread(deleter);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
    }

    private void close() {
        if (isClosedEnabled()) {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setComponent(this, new PersistentSettingsHints());
            setVisible(false);
        } else {
            messageWaitBeforeClose();
        }
    }

    private void messageWaitBeforeClose() {
        JOptionPane.showMessageDialog(this,
            Bundle.getString("DatabaseMaintainanceDialog.ErrorMessage.WaitBeforeClose"),
            Bundle.getString("DatabaseMaintainanceDialog.ErrorMessage.WaitBeforeClose.Title"),
            JOptionPane.INFORMATION_MESSAGE,
            AppSettings.getSmallAppIcon());
    }

    private void messageCompressDatabase(boolean success) {
        labelStatusCompressDatabase.setIcon(success ? okIcon : errorIcon);
    }

    private void messageDeleteNotExistingFilesInDatabaseCount(Object[] params) {
        labelStatusDeleteNotExistingFilesInDatabase.setIcon(okIcon);
        MessageFormat message =
            new MessageFormat(Bundle.getString("DatabaseMaintainanceDialog.InformationMessage.CountDeleted"));
        labelCountDeleteNotExistingFilesInDatabase.setText(message.format(params));
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        buttonAbortAction.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        evt.setStop(abortAction);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        buttonAbortAction.setEnabled(false);
        setClosedEnabled(true);
        abortAction = false;
        Object[] params = {(Integer) evt.getInfo()};
        messageDeleteNotExistingFilesInDatabaseCount(params);
    }

    private void startMaintain() {
        setClosedEnabled(false);
        if (checkBoxCompressDatabase.isSelected()) {
            compressDatabase();
        }
        if (checkBoxDeleteNotExistingFilesInDatabase.isSelected()) {
            deleteNotExistingFilesInDatabase();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelInfo = new javax.swing.JPanel();
        scrollPaneTableDatabaseInfo = new javax.swing.JScrollPane();
        tableDatabaseInfo = new javax.swing.JTable();
        labelDatabaseInfoTable = new javax.swing.JLabel();
        labelDatabaseInfoTotalRecordCount = new javax.swing.JLabel();
        labelDatabaseTotalRecordCount = new javax.swing.JLabel();
        panelMaintainance = new javax.swing.JPanel();
        panelMaintainanceTasks = new javax.swing.JPanel();
        checkBoxDeleteNotExistingFilesInDatabase = new javax.swing.JCheckBox();
        labelStatusDeleteNotExistingFilesInDatabase = new javax.swing.JLabel();
        labelCountDeleteNotExistingFilesInDatabase = new javax.swing.JLabel();
        labelStatusCompressDatabase = new javax.swing.JLabel();
        checkBoxCompressDatabase = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        buttonStartMaintain = new javax.swing.JButton();
        buttonAbortAction = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("DatabaseMaintainanceDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tableDatabaseInfo.setModel(modelDatabaseInfo);
        scrollPaneTableDatabaseInfo.setViewportView(tableDatabaseInfo);

        labelDatabaseInfoTable.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDatabaseInfoTable.setText(Bundle.getString("DatabaseMaintainanceDialog.labelDatabaseInfoTable.text")); // NOI18N

        labelDatabaseInfoTotalRecordCount.setFont(new java.awt.Font("Dialog", 0, 12));
        labelDatabaseInfoTotalRecordCount.setText(Bundle.getString("DatabaseMaintainanceDialog.labelDatabaseInfoTotalRecordCount.text")); // NOI18N

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneTableDatabaseInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addComponent(labelDatabaseInfoTable)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(labelDatabaseInfoTotalRecordCount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelDatabaseTotalRecordCount, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDatabaseInfoTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneTableDatabaseInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDatabaseInfoTotalRecordCount)
                    .addComponent(labelDatabaseTotalRecordCount))
                .addContainerGap())
        );

        tabbedPane.addTab(Bundle.getString("DatabaseMaintainanceDialog.panelInfo.TabConstraints.tabTitle"), panelInfo); // NOI18N

        panelMaintainanceTasks.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Bundle.getString("DatabaseMaintainanceDialog.panelMaintainanceTasks.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        checkBoxDeleteNotExistingFilesInDatabase.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        checkBoxDeleteNotExistingFilesInDatabase.setMnemonic('e');
        checkBoxDeleteNotExistingFilesInDatabase.setText(Bundle.getString("DatabaseMaintainanceDialog.checkBoxDeleteNotExistingFilesInDatabase.text")); // NOI18N
        checkBoxDeleteNotExistingFilesInDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteNotExistingFilesInDatabaseActionPerformed(evt);
            }
        });

        labelStatusDeleteNotExistingFilesInDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        labelCountDeleteNotExistingFilesInDatabase.setPreferredSize(new java.awt.Dimension(0, 16));

        labelStatusCompressDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxCompressDatabase.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        checkBoxCompressDatabase.setMnemonic('k');
        checkBoxCompressDatabase.setText(Bundle.getString("DatabaseMaintainanceDialog.checkBoxCompressDatabase.text")); // NOI18N

        javax.swing.GroupLayout panelMaintainanceTasksLayout = new javax.swing.GroupLayout(panelMaintainanceTasks);
        panelMaintainanceTasks.setLayout(panelMaintainanceTasksLayout);
        panelMaintainanceTasksLayout.setHorizontalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                        .addComponent(checkBoxDeleteNotExistingFilesInDatabase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelStatusDeleteNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCountDeleteNotExistingFilesInDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                    .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                        .addComponent(checkBoxCompressDatabase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelStatusCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelMaintainanceTasksLayout.setVerticalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(checkBoxDeleteNotExistingFilesInDatabase)
                    .addComponent(labelStatusDeleteNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCountDeleteNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelStatusCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxCompressDatabase)))
        );

        progressBar.setStringPainted(true);

        buttonStartMaintain.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonStartMaintain.setMnemonic('s');
        buttonStartMaintain.setText(Bundle.getString("DatabaseMaintainanceDialog.buttonStartMaintain.text")); // NOI18N
        buttonStartMaintain.setEnabled(false);
        buttonStartMaintain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartMaintainActionPerformed(evt);
            }
        });

        buttonAbortAction.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonAbortAction.setMnemonic('o');
        buttonAbortAction.setText(Bundle.getString("DatabaseMaintainanceDialog.buttonAbortAction.text")); // NOI18N
        buttonAbortAction.setEnabled(false);
        buttonAbortAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbortActionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMaintainanceLayout = new javax.swing.GroupLayout(panelMaintainance);
        panelMaintainance.setLayout(panelMaintainanceLayout);
        panelMaintainanceLayout.setHorizontalGroup(
            panelMaintainanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMaintainanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMaintainanceTasks, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMaintainanceLayout.createSequentialGroup()
                        .addComponent(buttonAbortAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStartMaintain))
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelMaintainanceLayout.setVerticalGroup(
            panelMaintainanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMaintainanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMaintainanceTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStartMaintain)
                    .addComponent(buttonAbortAction))
                .addGap(33, 33, 33))
        );

        tabbedPane.addTab(Bundle.getString("DatabaseMaintainanceDialog.panelMaintainance.TabConstraints.tabTitle"), panelMaintainance); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    close();
}//GEN-LAST:event_formWindowClosing

private void buttonAbortActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbortActionActionPerformed
    abortAction = true;
}//GEN-LAST:event_buttonAbortActionActionPerformed

private void buttonStartMaintainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartMaintainActionPerformed
    startMaintain();
}//GEN-LAST:event_buttonStartMaintainActionPerformed

private void checkBoxDeleteNotExistingFilesInDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteNotExistingFilesInDatabaseActionPerformed
    setEnabledButtonStartMaintain();
}//GEN-LAST:event_checkBoxDeleteNotExistingFilesInDatabaseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DatabaseMaintainanceDialog dialog = new DatabaseMaintainanceDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
            dialog.setVisible(true);
          }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAbortAction;
    private javax.swing.JButton buttonStartMaintain;
    private javax.swing.JCheckBox checkBoxCompressDatabase;
    private javax.swing.JCheckBox checkBoxDeleteNotExistingFilesInDatabase;
    private javax.swing.JLabel labelCountDeleteNotExistingFilesInDatabase;
    private javax.swing.JLabel labelDatabaseInfoTable;
    private javax.swing.JLabel labelDatabaseInfoTotalRecordCount;
    private javax.swing.JLabel labelDatabaseTotalRecordCount;
    private javax.swing.JLabel labelStatusCompressDatabase;
    private javax.swing.JLabel labelStatusDeleteNotExistingFilesInDatabase;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelMaintainance;
    private javax.swing.JPanel panelMaintainanceTasks;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneTableDatabaseInfo;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableDatabaseInfo;
    // End of variables declaration//GEN-END:variables
}
