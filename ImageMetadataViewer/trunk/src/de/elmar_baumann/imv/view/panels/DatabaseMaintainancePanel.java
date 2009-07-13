package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.DatabaseCompress;
import de.elmar_baumann.imv.tasks.RecordsWithNotExistingFilesDeleter;
import de.elmar_baumann.imv.tasks.UnusedThumbnailsDeleter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * Database maintainance tasks.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/08
 */
public final class DatabaseMaintainancePanel extends javax.swing.JPanel
        implements ProgressListener {

    private static final Icon ICON_FINISHED =
            AppIcons.getIcon("icon_finished.png"); // NOI18N
    private final Stack<Runnable> runnables = new Stack<Runnable>();
    private final Map<Class, JLabel> finishedLabelOfRunnable =
            new HashMap<Class, JLabel>();
    private volatile boolean stop = false;
    private volatile boolean canClose = true;

    /** Creates new form DatabaseMaintainancePanel */
    public DatabaseMaintainancePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        // When two values are equal, this does not work!
        finishedLabelOfRunnable.put(DatabaseCompress.class,
                labelFinishedCompressDatabase);
        finishedLabelOfRunnable.put(DatabaseMaintainance.class,
                labelFinishedDeleteUnusedThumbnails);
        finishedLabelOfRunnable.put(DatabaseImageFiles.class,
                labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);
    }

    private void setProgressbarStart(ProgressEvent evt) {
        if (evt.isIndeterminate()) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setMinimum(evt.getMinimum());
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(evt.getValue());
        }
    }

    private void setProgressbarEnd(ProgressEvent evt) {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        } else {
            progressBar.setValue(evt.getValue());
        }
    }

    public void getsVisible(boolean visible) {
        if (visible) {
            resetIcons();
            setEnabledButtonStartMaintain();
        }
    }

    private void resetIcons() {
        labelFinishedCompressDatabase.setIcon(null);
        labelFinishedDeleteUnusedThumbnails.setIcon(null);
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setIcon(null);
        progressBar.setValue(0);
    }

    private void setEnabledButtonStartMaintain() {
        buttonStartMaintain.setEnabled(
                checkBoxCompressDatabase.isSelected() ||
                checkBoxDeleteRecordsOfNotExistingFilesInDatabase.isSelected() ||
                checkBoxDeleteUnusedThumbnails.isSelected());
    }

    private void setCanClose(boolean can) {
        canClose = can;
    }

    public boolean canClose() {
        return canClose;
    }

    private synchronized void startNextThread() {
        if (runnables.size() > 0) {
            Thread thread = new Thread(runnables.pop());
            thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
            thread.setName("Database maintainance next task" + " @ " + // NOI18N
                    getClass().getName());
            thread.start();
        }
    }

    private void setRunnablesAreRunning(boolean running) {
        buttonAbortAction.setEnabled(running);
        buttonStartMaintain.setEnabled(!running);
        buttonDeleteMessages.setEnabled(!running);
        setCanClose(!running);
    }

    private void startMaintain() {
        addRunnables();
        setRunnablesAreRunning(true);
        stop = false;
        startNextThread();
    }

    private synchronized void addRunnables() {
        runnables.clear();
        // reverse order of checkboxes because the runnables are in a stack
        if (checkBoxCompressDatabase.isSelected()) {
            DatabaseCompress databaseCompress = new DatabaseCompress();
            databaseCompress.addProgressListener(this);
            runnables.push(databaseCompress);
        }
        if (checkBoxDeleteUnusedThumbnails.isSelected()) {
            UnusedThumbnailsDeleter deleter = new UnusedThumbnailsDeleter();
            deleter.addProgressListener(this);
            runnables.push(deleter);
        }
        if (checkBoxDeleteRecordsOfNotExistingFilesInDatabase.isSelected()) {
            RecordsWithNotExistingFilesDeleter deleter =
                    new RecordsWithNotExistingFilesDeleter();
            deleter.addProgressListener(this);
            runnables.push(deleter);
        }
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    private void appendMessage(String message) {
        String newline = textAreaMessages.getText().trim().isEmpty()
                         ? "" // NOI18N
                         : "\n"; // NOI18N
        textAreaMessages.append(newline + message);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        appendMessage(evt.getInfo().toString());
        buttonDeleteMessages.setEnabled(false);
        setProgressbarStart(evt);
        buttonAbortAction.setEnabled(
                !(evt.getSource() instanceof DatabaseCompress));
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        setProgressbarEnd(evt);
        appendMessage(evt.getInfo().toString());
        Object source = evt.getSource();
        assert source != null;
        Class sourceClass = source.getClass();
        JLabel labelFinished = finishedLabelOfRunnable.get(sourceClass);
        if (labelFinished != null) {
            labelFinished.setIcon(ICON_FINISHED);
        }
        if (runnables.size() > 0) {
            startNextThread();
        } else {
            setRunnablesAreRunning(false);
        }
    }

    private void handleButtonAbortActionPerformed() {
        stop = true;
        synchronized (runnables) {
            runnables.clear();
            setRunnablesAreRunning(false);
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

        panelMaintainanceTasks = new javax.swing.JPanel();
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase = new javax.swing.JCheckBox();
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase = new javax.swing.JLabel();
        checkBoxDeleteUnusedThumbnails = new javax.swing.JCheckBox();
        labelFinishedDeleteUnusedThumbnails = new javax.swing.JLabel();
        checkBoxCompressDatabase = new javax.swing.JCheckBox();
        labelFinishedCompressDatabase = new javax.swing.JLabel();
        panelMaintainMessages = new javax.swing.JPanel();
        scrollPaneMessages = new javax.swing.JScrollPane();
        textAreaMessages = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        buttonAbortAction = new javax.swing.JButton();
        buttonStartMaintain = new javax.swing.JButton();
        buttonDeleteMessages = new javax.swing.JButton();

        panelMaintainanceTasks.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("DatabaseMaintainancePanel.panelMaintainanceTasks.border.title"))); // NOI18N

        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setMnemonic('e');
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxDeleteRecordsOfNotExistingFilesInDatabase.text")); // NOI18N
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(evt);
            }
        });

        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxDeleteUnusedThumbnails.setMnemonic('l');
        checkBoxDeleteUnusedThumbnails.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxDeleteUnusedThumbnails.text")); // NOI18N
        checkBoxDeleteUnusedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteUnusedThumbnailsActionPerformed(evt);
            }
        });

        labelFinishedDeleteUnusedThumbnails.setPreferredSize(new java.awt.Dimension(16, 16));

        checkBoxCompressDatabase.setMnemonic('k');
        checkBoxCompressDatabase.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxCompressDatabase.text")); // NOI18N
        checkBoxCompressDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCompressDatabaseActionPerformed(evt);
            }
        });

        labelFinishedCompressDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout panelMaintainanceTasksLayout = new javax.swing.GroupLayout(panelMaintainanceTasks);
        panelMaintainanceTasks.setLayout(panelMaintainanceTasksLayout);
        panelMaintainanceTasksLayout.setHorizontalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxCompressDatabase)
                    .addComponent(checkBoxDeleteUnusedThumbnails)
                    .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE))
                .addGap(52, 52, 52)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFinishedDeleteUnusedThumbnails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelMaintainanceTasksLayout.setVerticalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelFinishedDeleteUnusedThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxDeleteUnusedThumbnails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxCompressDatabase))
                .addContainerGap())
        );

        panelMaintainMessages.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("DatabaseMaintainancePanel.panelMaintainMessages.border.title"))); // NOI18N

        textAreaMessages.setColumns(20);
        textAreaMessages.setEditable(false);
        textAreaMessages.setLineWrap(true);
        textAreaMessages.setRows(2);
        textAreaMessages.setWrapStyleWord(true);
        scrollPaneMessages.setViewportView(textAreaMessages);

        javax.swing.GroupLayout panelMaintainMessagesLayout = new javax.swing.GroupLayout(panelMaintainMessages);
        panelMaintainMessages.setLayout(panelMaintainMessagesLayout);
        panelMaintainMessagesLayout.setHorizontalGroup(
            panelMaintainMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainMessagesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMaintainMessagesLayout.setVerticalGroup(
            panelMaintainMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainMessagesLayout.createSequentialGroup()
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonAbortAction.setMnemonic('o');
        buttonAbortAction.setText(Bundle.getString("DatabaseMaintainancePanel.buttonAbortAction.text")); // NOI18N
        buttonAbortAction.setEnabled(false);
        buttonAbortAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbortActionActionPerformed(evt);
            }
        });

        buttonStartMaintain.setMnemonic('s');
        buttonStartMaintain.setText(Bundle.getString("DatabaseMaintainancePanel.buttonStartMaintain.text")); // NOI18N
        buttonStartMaintain.setEnabled(false);
        buttonStartMaintain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartMaintainActionPerformed(evt);
            }
        });

        buttonDeleteMessages.setText(Bundle.getString("DatabaseMaintainancePanel.buttonDeleteMessages.text")); // NOI18N
        buttonDeleteMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteMessagesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMaintainMessages, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelMaintainanceTasks, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonDeleteMessages)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                        .addComponent(buttonAbortAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStartMaintain)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMaintainanceTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMaintainMessages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStartMaintain)
                    .addComponent(buttonAbortAction)
                    .addComponent(buttonDeleteMessages))
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

private void checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed
    setEnabledButtonStartMaintain();
}//GEN-LAST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed

private void checkBoxCompressDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCompressDatabaseActionPerformed
    setEnabledButtonStartMaintain();
}//GEN-LAST:event_checkBoxCompressDatabaseActionPerformed

private void buttonStartMaintainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartMaintainActionPerformed
    startMaintain();
}//GEN-LAST:event_buttonStartMaintainActionPerformed

private void buttonAbortActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbortActionActionPerformed
    handleButtonAbortActionPerformed();
}//GEN-LAST:event_buttonAbortActionActionPerformed

private void checkBoxDeleteUnusedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteUnusedThumbnailsActionPerformed
    setEnabledButtonStartMaintain();
}//GEN-LAST:event_checkBoxDeleteUnusedThumbnailsActionPerformed

private void buttonDeleteMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteMessagesActionPerformed
    textAreaMessages.setText(""); // NOI18N
}//GEN-LAST:event_buttonDeleteMessagesActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAbortAction;
    private javax.swing.JButton buttonDeleteMessages;
    private javax.swing.JButton buttonStartMaintain;
    private javax.swing.JCheckBox checkBoxCompressDatabase;
    private javax.swing.JCheckBox checkBoxDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JCheckBox checkBoxDeleteUnusedThumbnails;
    private javax.swing.JLabel labelFinishedCompressDatabase;
    private javax.swing.JLabel labelFinishedDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JLabel labelFinishedDeleteUnusedThumbnails;
    private javax.swing.JPanel panelMaintainMessages;
    private javax.swing.JPanel panelMaintainanceTasks;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneMessages;
    private javax.swing.JTextArea textAreaMessages;
    // End of variables declaration//GEN-END:variables
}
