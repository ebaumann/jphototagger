/*
 * @(#)DatabaseMaintainancePanel.java    Created on 2008-11-08
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

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.CompressDatabase;
import org.jphototagger.program.helper.DeleteOrphanedThumbnails;
import org.jphototagger.program.helper.DeleteOrphanedXmp;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.util.Settings;

import java.awt.Container;

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
 * @author  Elmar Baumann
 */
public final class DatabaseMaintainancePanel extends JPanel
        implements ProgressListener {
    private static final Icon ICON_FINISHED =
        AppLookAndFeel.getIcon("icon_finished.png");
    private static final String METHOD_NAME_CANCEL              = "cancel";
    private static final String KEY_DEL_RECORDS_OF_NOT_EX_FILES =
        "DatabaseMaintainancePanel.CheckBox.DeleteNotExistingFilesFromDb";
    private static final String KEY_COMPRESS_DB =
        "DatabaseMaintainancePanel.CheckBox.CompressDb";
    private static final String KEY_DEL_ORPHANED_THUMBS =
        "DatabaseMaintainancePanel.CheckBox.DeleteOrphanedThumbnails";
    private static final long           serialVersionUID        =
        -4557401822534070313L;
    private final Stack<Runnable>       runnables               =
        new Stack<Runnable>();
    private final Map<Class<?>, JLabel> finishedLabelOfRunnable =
        new HashMap<Class<?>, JLabel>();
    private final Set<JCheckBox>         checkBoxes      =
        new HashSet<JCheckBox>();
    private final Map<JCheckBox, JLabel> labelOfCheckBox =
        new HashMap<JCheckBox, JLabel>();
    private volatile Runnable currentRunnable;
    private volatile boolean  stop;
    private volatile boolean  canClose = true;

    public DatabaseMaintainancePanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        finishedLabelOfRunnable.put(CompressDatabase.class,
                                    labelFinishedCompressDatabase);
        finishedLabelOfRunnable.put(
            DatabaseImageFiles.class,
            labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);
        finishedLabelOfRunnable.put(DeleteOrphanedThumbnails.class,
                                    labelFinishedDeleteOrphanedThumbnails);
        initCheckBoxes();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initCheckBoxes() {
        checkBoxes.add(checkBoxCompressDatabase);
        checkBoxes.add(checkBoxDeleteOrphanedThumbnails);
        checkBoxes.add(checkBoxDeleteRecordsOfNotExistingFilesInDatabase);
        labelOfCheckBox.put(checkBoxCompressDatabase,
                            labelFinishedCompressDatabase);
        labelOfCheckBox.put(checkBoxDeleteOrphanedThumbnails,
                            labelFinishedDeleteOrphanedThumbnails);
        labelOfCheckBox.put(
            checkBoxDeleteRecordsOfNotExistingFilesInDatabase,
            labelFinishedDeleteRecordsOfNotExistingFilesInDatabase);

        Settings settings = UserSettings.INSTANCE.getSettings();

        checkBoxCompressDatabase.setSelected(
            settings.getBoolean(KEY_COMPRESS_DB));
        checkBoxDeleteOrphanedThumbnails.setSelected(
            settings.getBoolean(KEY_DEL_ORPHANED_THUMBS));
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setSelected(
            settings.getBoolean(KEY_DEL_RECORDS_OF_NOT_EX_FILES));
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

            thread.setName("Database maintainance next task @ "
                           + getClass().getSimpleName());
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
        if (currentRunnable == null) {
            return;
        }

        Method methodCancel = null;

        if (hasCancelMethod(currentRunnable)) {
            try {
                methodCancel =
                    currentRunnable.getClass().getMethod(METHOD_NAME_CANCEL);
                methodCancel.invoke(currentRunnable);
            } catch (Exception ex) {
                AppLogger.logSevere(getClass(), ex);
            }
        }

        if ((methodCancel == null) && (currentRunnable instanceof Thread)) {
            ((Thread) currentRunnable).interrupt();
        }
    }

    private boolean hasCancelMethod(Runnable runnable) {
        Method[] methods = runnable.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals(METHOD_NAME_CANCEL)
                    && (method.getParameterTypes().length == 0)) {
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
        buttonDeleteMessages.setEnabled(true);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        appendMessage(evt.getInfo().toString());
        buttonDeleteMessages.setEnabled(false);
        setProgressbarStart(evt);
        buttonAbortAction.setEnabled(!(evt.getSource()
                                       instanceof CompressDatabase));
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        progressBar.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        setProgressbarEnd(evt);
        appendMessage(evt.getInfo().toString());

        Object source = evt.getSource();

        assert source != null;

        Class<?> sourceClass   = source.getClass();
        JLabel   labelFinished = finishedLabelOfRunnable.get(sourceClass);

        progressBar.setValue(0);

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

    private void deleteMessages() {
        textAreaMessages.setText("");
        buttonDeleteMessages.setEnabled(false);
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
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase =
            new javax.swing.JCheckBox();
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase =
            new javax.swing.JLabel();
        checkBoxCompressDatabase              = new javax.swing.JCheckBox();
        labelFinishedCompressDatabase         = new javax.swing.JLabel();
        checkBoxDeleteOrphanedThumbnails      = new javax.swing.JCheckBox();
        labelFinishedDeleteOrphanedThumbnails = new javax.swing.JLabel();
        labelMessages                         = new javax.swing.JLabel();
        scrollPaneMessages                    = new javax.swing.JScrollPane();
        textAreaMessages                      = new javax.swing.JTextArea();
        progressBar                           = new javax.swing.JProgressBar();
        buttonDeleteMessages                  = new javax.swing.JButton();
        buttonAbortAction                     = new javax.swing.JButton();
        buttonStartMaintain                   = new javax.swing.JButton();
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.setText(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainancePanel.checkBoxDeleteRecordsOfNotExistingFilesInDatabase.text"));    // NOI18N
        checkBoxDeleteRecordsOfNotExistingFilesInDatabase.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(
                    evt);
            }
        });
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setBorder(
            javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setIconTextGap(
            0);
        labelFinishedDeleteRecordsOfNotExistingFilesInDatabase.setPreferredSize(
            new java.awt.Dimension(22, 22));
        checkBoxCompressDatabase.setText(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainancePanel.checkBoxCompressDatabase.text"));    // NOI18N
        checkBoxCompressDatabase.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxCompressDatabaseActionPerformed(evt);
            }
        });
        labelFinishedCompressDatabase.setBorder(
            javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedCompressDatabase.setIconTextGap(0);
        labelFinishedCompressDatabase.setPreferredSize(
            new java.awt.Dimension(22, 22));

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "org/jphototagger/program/resource/properties/Bundle");    // NOI18N

        checkBoxDeleteOrphanedThumbnails.setText(
            bundle.getString(
                "DatabaseMaintainancePanel.checkBoxDeleteOrphanedThumbnails.text"));    // NOI18N
        checkBoxDeleteOrphanedThumbnails.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteOrphanedThumbnailsActionPerformed(evt);
            }
        });
        labelFinishedDeleteOrphanedThumbnails.setBorder(
            javax.swing.BorderFactory.createEtchedBorder());
        labelFinishedDeleteOrphanedThumbnails.setIconTextGap(0);
        labelFinishedDeleteOrphanedThumbnails.setPreferredSize(
            new java.awt.Dimension(22, 22));
        labelMessages.setForeground(new java.awt.Color(0, 0, 255));
        labelMessages.setText(
            bundle.getString("DatabaseMaintainancePanel.labelMessages.text"));    // NOI18N
        textAreaMessages.setColumns(20);
        textAreaMessages.setEditable(false);
        textAreaMessages.setLineWrap(true);
        textAreaMessages.setRows(2);
        textAreaMessages.setWrapStyleWord(true);
        scrollPaneMessages.setViewportView(textAreaMessages);
        buttonDeleteMessages.setForeground(new java.awt.Color(0, 0, 255));
        buttonDeleteMessages.setText(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainancePanel.buttonDeleteMessages.text"));    // NOI18N
        buttonDeleteMessages.setEnabled(false);
        buttonDeleteMessages.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteMessagesActionPerformed(evt);
            }
        });
        buttonAbortAction.setText(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainancePanel.buttonAbortAction.text"));    // NOI18N
        buttonAbortAction.setEnabled(false);
        buttonAbortAction.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAbortActionActionPerformed(evt);
            }
        });
        buttonStartMaintain.setText(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainancePanel.buttonStartMaintain.text"));    // NOI18N
        buttonStartMaintain.setEnabled(false);
        buttonStartMaintain.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartMaintainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(
                    buttonDeleteMessages).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137,
                    Short.MAX_VALUE).addComponent(
                        buttonAbortAction).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        buttonStartMaintain).addGap(12, 12, 12)).addGroup(
                            javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup().addContainerGap().addComponent(
                                progressBar,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 462,
                                Short.MAX_VALUE).addContainerGap()).addGroup(
                                    javax.swing.GroupLayout.Alignment.TRAILING,
                                    layout.createSequentialGroup().addGap(
                                        12, 12, 12).addGroup(
                                        layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                checkBoxDeleteOrphanedThumbnails,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        432,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                            checkBoxCompressDatabase,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                    432,
                                                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                                        checkBoxDeleteRecordsOfNotExistingFilesInDatabase,
                                                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                    432,
                                                                                    Short.MAX_VALUE)).addPreferredGap(
                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                                                            layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                                                                    labelFinishedDeleteRecordsOfNotExistingFilesInDatabase,
                                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                                                                                        labelFinishedCompressDatabase,
                                                                                                                            javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                                                                                                                            labelFinishedDeleteOrphanedThumbnails,
                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(labelMessages).addContainerGap(398, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(scrollPaneMessages, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE).addContainerGap()));
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                        new java.awt.Component[] {
                            labelFinishedCompressDatabase,
                            labelFinishedDeleteOrphanedThumbnails,
                            labelFinishedDeleteRecordsOfNotExistingFilesInDatabase });
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                        new java.awt.Component[] { checkBoxCompressDatabase,
                checkBoxDeleteOrphanedThumbnails });
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                        checkBoxDeleteRecordsOfNotExistingFilesInDatabase,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                            labelFinishedDeleteRecordsOfNotExistingFilesInDatabase,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                layout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                    checkBoxCompressDatabase).addComponent(
                                    labelFinishedCompressDatabase,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                        layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                                checkBoxDeleteOrphanedThumbnails).addComponent(
                                                    labelFinishedDeleteOrphanedThumbnails,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                        labelMessages).addPreferredGap(
                                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                                scrollPaneMessages,
                                                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        109,
                                                                                        Short.MAX_VALUE).addPreferredGap(
                                                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                                                progressBar,
                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                        23,
                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                                                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                                                                                                                layout.createParallelGroup(
                                                                                                                    javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                                                                                        buttonStartMaintain).addComponent(
                                                                                                                            buttonAbortAction).addComponent(
                                                                                                                                buttonDeleteMessages)).addGap(
                                                                                                                                    12,
                                                                                                                                    12,
                                                                                                                                    12)));
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] {
                            labelFinishedCompressDatabase,
                            labelFinishedDeleteOrphanedThumbnails,
                            labelFinishedDeleteRecordsOfNotExistingFilesInDatabase });
    }    // </editor-fold>//GEN-END:initComponents

    private void checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed
        UserSettings.INSTANCE.getSettings().set(
            checkBoxDeleteRecordsOfNotExistingFilesInDatabase.isSelected(),
            KEY_DEL_RECORDS_OF_NOT_EX_FILES);
        UserSettings.INSTANCE.writeToFile();
        checkCheckboxes();
    }//GEN-LAST:event_checkBoxDeleteRecordsOfNotExistingFilesInDatabaseActionPerformed

    private void checkBoxCompressDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCompressDatabaseActionPerformed
        UserSettings.INSTANCE.getSettings().set(
            checkBoxCompressDatabase.isSelected(), KEY_COMPRESS_DB);
        UserSettings.INSTANCE.writeToFile();
        checkCheckboxes();
    }//GEN-LAST:event_checkBoxCompressDatabaseActionPerformed

    private void buttonStartMaintainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartMaintainActionPerformed
        startMaintain();
    }//GEN-LAST:event_buttonStartMaintainActionPerformed

    private void buttonAbortActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAbortActionActionPerformed
        handleButtonAbortActionPerformed();
    }//GEN-LAST:event_buttonAbortActionActionPerformed

    private void buttonDeleteMessagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteMessagesActionPerformed
        deleteMessages();
    }//GEN-LAST:event_buttonDeleteMessagesActionPerformed

    private void checkBoxDeleteOrphanedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteOrphanedThumbnailsActionPerformed
        UserSettings.INSTANCE.getSettings().set(
            checkBoxDeleteOrphanedThumbnails.isSelected(),
            KEY_DEL_ORPHANED_THUMBS);
        UserSettings.INSTANCE.writeToFile();
        checkCheckboxes();
    }//GEN-LAST:event_checkBoxDeleteOrphanedThumbnailsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton   buttonAbortAction;
    private javax.swing.JButton   buttonDeleteMessages;
    private javax.swing.JButton   buttonStartMaintain;
    private javax.swing.JCheckBox checkBoxCompressDatabase;
    private javax.swing.JCheckBox checkBoxDeleteOrphanedThumbnails;
    private javax.swing.JCheckBox checkBoxDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JLabel labelFinishedCompressDatabase;
    private javax.swing.JLabel labelFinishedDeleteOrphanedThumbnails;
    private javax.swing.JLabel labelFinishedDeleteRecordsOfNotExistingFilesInDatabase;
    private javax.swing.JLabel       labelMessages;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane  scrollPaneMessages;
    private javax.swing.JTextArea    textAreaMessages;

    // End of variables declaration//GEN-END:variables
}
