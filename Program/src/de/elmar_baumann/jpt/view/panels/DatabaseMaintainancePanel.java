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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.helper.CompressDatabase;
import de.elmar_baumann.jpt.helper.DeleteOrphanedXmp;
import de.elmar_baumann.jpt.helper.DeleteOrphanedThumbnails;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * Database maintainance tasks.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-08
 */
public final class DatabaseMaintainancePanel extends javax.swing.JPanel
        implements ProgressListener {

    private static final Icon ICON_FINISHED =
            AppLookAndFeel.getIcon("icon_finished.png"); // NOI18N
    private static final String METHOD_NAME_CANCEL = "cancel";
    private final Stack<Runnable> runnables = new Stack<Runnable>();
    private final Map<Class, JLabel> finishedLabelOfRunnable =
            new HashMap<Class, JLabel>();
    private final Set<JCheckBox> checkBoxes = new HashSet<JCheckBox>();
    private final Map<JCheckBox, JLabel> labelOfCheckBox =
            new HashMap<JCheckBox, JLabel>();
    private volatile Runnable currentRunnable;
    private volatile boolean stop = false;
    private volatile boolean canClose = true;

    /** Creates new form DatabaseMaintainancePanel */
    public DatabaseMaintainancePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        // When two values are equal, this does not work!
        finishedLabelOfRunnable.put(CompressDatabase.class,
                labelFinishedCompressDatabase);
        finishedLabelOfRunnable.put(DatabaseImageFiles.class,
                labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);
        finishedLabelOfRunnable.put(DeleteOrphanedThumbnails.class,
                labelFinishedDeleteOrphanedThumbnails);
        initCheckBoxes();
    }

    private void initCheckBoxes() {
        checkBoxes.add(checkBoxCompressDatabase);
        checkBoxes.add(checkBoxDeleteOrphanedThumbnails);
        checkBoxes.add(checkBoxDeleteRecordsOfNotExistingFilesInDatabase);

        labelOfCheckBox.put(checkBoxCompressDatabase,
                labelFinishedCompressDatabase);
        labelOfCheckBox.put(checkBoxDeleteOrphanedThumbnails,
                labelFinishedDeleteOrphanedThumbnails);
        labelOfCheckBox.put(checkBoxDeleteRecordsOfNotExistingFilesInDatabase,
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
            checkCheckboxes();
        }
    }

    private void resetIcons() {
        labelFinishedCompressDatabase.setIcon(null);
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setIcon(null);
        labelFinishedDeleteOrphanedThumbnails.setIcon(null);
        progressBar.setValue(0);
    }

    private void checkCheckboxes() {
        buttonStartMaintain.setEnabled(isACheckBoxSelected());
        removeFinishedIcons();
    }
    
    private boolean isACheckBoxSelected() {
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void removeFinishedIcons() {
        for (JCheckBox checkBox : checkBoxes) {
            if (!checkBox.isSelected()) {
                labelOfCheckBox.get(checkBox).setIcon(null);
            }
        }
    }

    private void setCanClose(boolean can) {
        canClose = can;
    }

    public boolean canClose() {
        return canClose;
    }

    private synchronized void startNextThread() {
        if (runnables.size() > 0) {
            currentRunnable = runnables.pop();
            Thread thread = new Thread(currentRunnable);
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

    private void stopCurrentRunnable() {
        if (currentRunnable == null) return;
        Method methodCancel = null;
        if (hasCancelMethod(currentRunnable)) {
            try {
                methodCancel = currentRunnable.getClass().getMethod(
                        METHOD_NAME_CANCEL);
                methodCancel.invoke(currentRunnable);
            } catch (Exception ex) {
                AppLog.logSevere(getClass(), ex);
            }
        }
        if (methodCancel == null && currentRunnable instanceof Thread) {
            ((Thread) currentRunnable).interrupt();
        }
    }

    private boolean hasCancelMethod(Runnable runnable) {
        Method[] methods = runnable.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(METHOD_NAME_CANCEL) &&
                    method.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }

    private void startMaintain() {
        resetIcons();
        addRunnables();
        setRunnablesAreRunning(true);
        stop = false;
        startNextThread();
    }

    private synchronized void addRunnables() {
        runnables.clear();

        // reverse order of checkboxes because the runnables are in a stack

        if (checkBoxDeleteOrphanedThumbnails.isSelected()) {
            DeleteOrphanedThumbnails runnable = new DeleteOrphanedThumbnails();
            runnable.addProgressListener(this);
            runnables.push(runnable);
        }
        if (checkBoxCompressDatabase.isSelected()) {
            CompressDatabase runnable = new CompressDatabase();
            runnable.addProgressListener(this);
            runnables.push(runnable);
        }
        if (checkBoxDeleteRecordsOfNotExistingFilesInDatabase.isSelected()) {
            DeleteOrphanedXmp runnable = new DeleteOrphanedXmp();
            runnable.addProgressListener(this);
            runnables.push(runnable);
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
                !(evt.getSource() instanceof CompressDatabase));
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
            currentRunnable = null;
            setRunnablesAreRunning(false);
        }
    }

    private void handleButtonAbortActionPerformed() {
        stop = true;
        synchronized (runnables) {
            stopCurrentRunnable();
            runnables.clear();
            setRunnablesAreRunning(false);
            currentRunnable = null;
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
        checkBoxCompressDatabase = new javax.swing.JCheckBox();
        labelFinishedCompressDatabase = new javax.swing.JLabel();
        checkBoxDeleteOrphanedThumbnails = new javax.swing.JCheckBox();
        labelFinishedDeleteOrphanedThumbnails = new javax.swing.JLabel();
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

        checkBoxCompressDatabase.setMnemonic('k');
        checkBoxCompressDatabase.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxCompressDatabase.text")); // NOI18N
        checkBoxCompressDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCompressDatabaseActionPerformed(evt);
            }
        });

        labelFinishedCompressDatabase.setPreferredSize(new java.awt.Dimension(16, 16));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        checkBoxDeleteOrphanedThumbnails.setText(bundle.getString("DatabaseMaintainancePanel.checkBoxDeleteOrphanedThumbnails.text")); // NOI18N
        checkBoxDeleteOrphanedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteOrphanedThumbnailsActionPerformed(evt);
            }
        });

        labelFinishedDeleteOrphanedThumbnails.setPreferredSize(new java.awt.Dimension(16, 16));

        javax.swing.GroupLayout panelMaintainanceTasksLayout = new javax.swing.GroupLayout(panelMaintainanceTasks);
        panelMaintainanceTasks.setLayout(panelMaintainanceTasksLayout);
        panelMaintainanceTasksLayout.setHorizontalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                        .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMaintainanceTasksLayout.createSequentialGroup()
                                .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                                .addGap(52, 52, 52))
                            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                                .addComponent(checkBoxCompressDatabase)
                                .addGap(275, 275, 275)))
                        .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                        .addComponent(checkBoxDeleteOrphanedThumbnails)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 225, Short.MAX_VALUE)
                        .addComponent(labelFinishedDeleteOrphanedThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        panelMaintainanceTasksLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelFinishedCompressDatabase, labelFinishedDeleteOrphanedThumbnails, labelFinishedDeleteRecordsOfNotExistingFilesInDatabase});

        panelMaintainanceTasksLayout.setVerticalGroup(
            panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainanceTasksLayout.createSequentialGroup()
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxCompressDatabase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMaintainanceTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkBoxDeleteOrphanedThumbnails)
                    .addComponent(labelFinishedDeleteOrphanedThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelMaintainanceTasksLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelFinishedCompressDatabase, labelFinishedDeleteOrphanedThumbnails, labelFinishedDeleteRecordsOfNotExistingFilesInDatabase});

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
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMaintainMessagesLayout.setVerticalGroup(
            panelMaintainMessagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMaintainMessagesLayout.createSequentialGroup()
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
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
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonDeleteMessages)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
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
                .addGap(17, 17, 17))
        );
    }// </editor-fold>//GEN-END:initComponents

private void checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed
    checkCheckboxes();
}//GEN-LAST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed

private void checkBoxCompressDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCompressDatabaseActionPerformed
    checkCheckboxes();
}//GEN-LAST:event_checkBoxCompressDatabaseActionPerformed

private void buttonStartMaintainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartMaintainActionPerformed
    startMaintain();
}//GEN-LAST:event_buttonStartMaintainActionPerformed

private void buttonAbortActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbortActionActionPerformed
    handleButtonAbortActionPerformed();
}//GEN-LAST:event_buttonAbortActionActionPerformed

private void buttonDeleteMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteMessagesActionPerformed
    textAreaMessages.setText(""); // NOI18N
}//GEN-LAST:event_buttonDeleteMessagesActionPerformed

private void checkBoxDeleteOrphanedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteOrphanedThumbnailsActionPerformed
    checkCheckboxes();
}//GEN-LAST:event_checkBoxDeleteOrphanedThumbnailsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAbortAction;
    private javax.swing.JButton buttonDeleteMessages;
    private javax.swing.JButton buttonStartMaintain;
    private javax.swing.JCheckBox checkBoxCompressDatabase;
    private javax.swing.JCheckBox checkBoxDeleteOrphanedThumbnails;
    private javax.swing.JCheckBox checkBoxDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JLabel labelFinishedCompressDatabase;
    private javax.swing.JLabel labelFinishedDeleteOrphanedThumbnails;
    private javax.swing.JLabel labelFinishedDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JPanel panelMaintainMessages;
    private javax.swing.JPanel panelMaintainanceTasks;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneMessages;
    private javax.swing.JTextArea textAreaMessages;
    // End of variables declaration//GEN-END:variables
}
