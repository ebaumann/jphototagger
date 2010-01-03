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
import javax.swing.JPanel;

/**
 * Database maintainance tasks.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-08
 */
public final class DatabaseMaintainancePanel extends JPanel implements ProgressListener {

    private static final Icon                   ICON_FINISHED           = AppLookAndFeel.getIcon("icon_finished.png");
    private static final String                 METHOD_NAME_CANCEL      = "cancel";
    private final        Stack<Runnable>        runnables               = new Stack<Runnable>();
    private final        Map<Class, JLabel>     finishedLabelOfRunnable = new HashMap<Class, JLabel>();
    private final        Set<JCheckBox>         checkBoxes              = new HashSet<JCheckBox>();
    private final        Map<JCheckBox, JLabel> labelOfCheckBox         = new HashMap<JCheckBox, JLabel>();
    private volatile     Runnable               currentRunnable;
    private volatile     boolean                stop;
    private volatile     boolean                canClose                = true;

    public DatabaseMaintainancePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        finishedLabelOfRunnable.put(CompressDatabase.class        , labelFinishedCompressDatabase);
        finishedLabelOfRunnable.put(DatabaseImageFiles.class      , labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);
        finishedLabelOfRunnable.put(DeleteOrphanedThumbnails.class, labelFinishedDeleteOrphanedThumbnails);
        initCheckBoxes();
    }

    private void initCheckBoxes() {
        checkBoxes.add(checkBoxCompressDatabase);
        checkBoxes.add(checkBoxDeleteOrphanedThumbnails);
        checkBoxes.add(checkBoxDeleteRecordsOfNotExistingFilesInDatabase);

        labelOfCheckBox.put(checkBoxCompressDatabase                         , labelFinishedCompressDatabase);
        labelOfCheckBox.put(checkBoxDeleteOrphanedThumbnails                 , labelFinishedDeleteOrphanedThumbnails);
        labelOfCheckBox.put(checkBoxDeleteRecordsOfNotExistingFilesInDatabase, labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);
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
            thread.setName("Database maintainance next task @ " + getClass().getSimpleName());
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
                methodCancel = currentRunnable.getClass().getMethod(METHOD_NAME_CANCEL);
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
                         ? ""
                         : "\n";
        textAreaMessages.append(newline + message);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        appendMessage(evt.getInfo().toString());
        buttonDeleteMessages.setEnabled(false);
        setProgressbarStart(evt);
        buttonAbortAction.setEnabled(!(evt.getSource() instanceof CompressDatabase));
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

        Object source        = evt.getSource(); assert source != null;
        Class  sourceClass   = source.getClass();
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

        checkBoxDeleteRecordsOfNotExistingFilesInDatabase = new javax.swing.JCheckBox();
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase = new javax.swing.JLabel();
        checkBoxCompressDatabase = new javax.swing.JCheckBox();
        labelFinishedCompressDatabase = new javax.swing.JLabel();
        checkBoxDeleteOrphanedThumbnails = new javax.swing.JCheckBox();
        labelFinishedDeleteOrphanedThumbnails = new javax.swing.JLabel();
        scrollPaneMessages = new javax.swing.JScrollPane();
        textAreaMessages = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();
        buttonAbortAction = new javax.swing.JButton();
        buttonStartMaintain = new javax.swing.JButton();
        buttonDeleteMessages = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setMnemonic('e');
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxDeleteRecordsOfNotExistingFilesInDatabase.text")); // NOI18N
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(evt);
            }
        });

        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setPreferredSize(new java.awt.Dimension(18, 18));

        checkBoxCompressDatabase.setMnemonic('k');
        checkBoxCompressDatabase.setText(Bundle.getString("DatabaseMaintainancePanel.checkBoxCompressDatabase.text")); // NOI18N
        checkBoxCompressDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCompressDatabaseActionPerformed(evt);
            }
        });

        labelFinishedCompressDatabase.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedCompressDatabase.setPreferredSize(new java.awt.Dimension(18, 18));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        checkBoxDeleteOrphanedThumbnails.setText(bundle.getString("DatabaseMaintainancePanel.checkBoxDeleteOrphanedThumbnails.text")); // NOI18N
        checkBoxDeleteOrphanedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteOrphanedThumbnailsActionPerformed(evt);
            }
        });

        labelFinishedDeleteOrphanedThumbnails.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteOrphanedThumbnails.setPreferredSize(new java.awt.Dimension(18, 18));

        textAreaMessages.setColumns(20);
        textAreaMessages.setEditable(false);
        textAreaMessages.setLineWrap(true);
        textAreaMessages.setRows(2);
        textAreaMessages.setWrapStyleWord(true);
        scrollPaneMessages.setViewportView(textAreaMessages);

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

        jLabel1.setText(bundle.getString("DatabaseMaintainancePanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneMessages, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonDeleteMessages)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
                        .addComponent(buttonAbortAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStartMaintain))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(checkBoxDeleteOrphanedThumbnails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBoxCompressDatabase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelFinishedDeleteOrphanedThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelFinishedCompressDatabase, labelFinishedDeleteOrphanedThumbnails, labelFinishedDeleteRecordsOfNotExistingFilesInDatabase});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {checkBoxCompressDatabase, checkBoxDeleteOrphanedThumbnails});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkBoxDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxCompressDatabase))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(labelFinishedDeleteRecordsOfNotExistingFilesInDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(labelFinishedCompressDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(checkBoxDeleteOrphanedThumbnails)
                    .addComponent(labelFinishedDeleteOrphanedThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStartMaintain)
                    .addComponent(buttonAbortAction)
                    .addComponent(buttonDeleteMessages))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelFinishedCompressDatabase, labelFinishedDeleteOrphanedThumbnails, labelFinishedDeleteRecordsOfNotExistingFilesInDatabase});

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
    textAreaMessages.setText("");
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelFinishedCompressDatabase;
    private javax.swing.JLabel labelFinishedDeleteOrphanedThumbnails;
    private javax.swing.JLabel labelFinishedDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneMessages;
    private javax.swing.JTextArea textAreaMessages;
    // End of variables declaration//GEN-END:variables
}
