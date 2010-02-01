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
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.event.FileSystemEvent;
import de.elmar_baumann.jpt.event.listener.FileSystemListener;
import de.elmar_baumann.jpt.io.FileSystemError;
import de.elmar_baumann.jpt.event.listener.impl.FileSystemListenerSupport;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import de.elmar_baumann.jpt.helper.CopyFiles;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.io.FileSystemMove;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public final class MoveToDirectoryDialog extends Dialog implements ProgressListener, FileSystemListener {

    private static final String                    KEY_TARGET_DIRECTORY = "de.elmar_baumann.jpt.view.dialogs.MoveToDirectoryDialog.TargetDirectory";
    private static final long                      serialVersionUID     = 3213926343815394815L;
    private final        List<File>                movedFiles           = new ArrayList<File>();
    private final        ProgressListenerSupport   pListenerSupport     = new ProgressListenerSupport();
    private              FileSystemMove            moveTask;
    private              boolean                   runs                 = false;
    private              boolean                   stop                 = false;
    private              boolean                   errors               = false;
    private              List<File>                sourceFiles;
    private              File                      targetDirectory      = new File("");
    private              boolean                   moveIfVisible        = false;
    private final        FileSystemListenerSupport listenerSupport      = new FileSystemListenerSupport();

    public MoveToDirectoryDialog() {
        super(GUI.INSTANCE.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        setHelpPageUrl(Bundle.getString("Help.Url.MoveToDirectoryDialog"));
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        pListenerSupport.add(listener);
    }

    private void checkClosing() {
        if (runs) {
            MessageDisplayer.error(this, "MoveToDirectoryDialog.Error.AbortBeforeClose");
        } else {
            setVisible(false);
        }
    }

    private void checkErrors() {
        if (errors) {
            MessageDisplayer.error(this, "MoveToDirectoryDialog.Error.CheckLogfile");
        }
    }

    private void addXmpFiles() {
        List<File> xmpFiles = new ArrayList<File>();
        for (File sourceFile : sourceFiles) {
            String xmpFilename = XmpMetadata.getSidecarFilename(sourceFile.getAbsolutePath());
            if (xmpFilename != null) {
                xmpFiles.add(new File(xmpFilename));
            }
        }
        sourceFiles.addAll(xmpFiles);
    }

    private void reset() {
        runs   = false;
        stop   = false;
        errors = false;
        movedFiles.clear();
    }

    private synchronized void start() {
        reset();
        moveTask = new FileSystemMove(sourceFiles, targetDirectory,
                UserSettings.INSTANCE.getCopyMoveFilesOptions().equals(
                CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
        addListenerToMoveTask();
        Thread thread = new Thread(moveTask);
        thread.setName("Moving files to directory " + targetDirectory.getAbsolutePath() + " @ " + getClass().getSimpleName());
        thread.start();
        runs = true;
    }

    public void addFileSystemListener(FileSystemListener listener) {
        listenerSupport.add(listener);
    }

    private synchronized void addListenerToMoveTask() {
        moveTask.addFileSystemListener(this);
        moveTask.addProgressListener(this);
        Set<FileSystemListener> listeners = listenerSupport.get();
        synchronized (listeners) {
            for (FileSystemListener listener : listenerSupport.get()) {
                moveTask.addFileSystemListener(listener);
            }
        }
    }

    private void stop() {
        stop = true;
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(
                                        GUI.INSTANCE.getAppFrame(),
                                        targetDirectory,
                                        UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());
        dialog.addWindowListener(new SizeAndLocationController());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            if (files.size() > 0) {
                targetDirectory = files.get(0);
                if (targetDirectory.canWrite()) {
                    labelDirectoryName.setText(targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    MessageDisplayer.error(
                            this,
                            "MoveToDirectoryDialog.TargetDirNotWritable",
                            targetDirectory);
                }
            }
        } else {
            File dir = new File(labelDirectoryName.getText().trim());
            buttonStart.setEnabled(
                    FileUtil.existsDirectory(dir) && dir.canWrite());
        }
    }

    private void setIconToLabelTargetDirectory() {
        File dir = new File(labelDirectoryName.getText());
        if (dir.isDirectory()) {
            labelDirectoryName.setIcon(
                    FileSystemView.getFileSystemView().getSystemIcon(dir));
        }
    }

    public void setSourceFiles(List<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
        addXmpFiles();
        Collections.sort(sourceFiles);
    }

    /**
     * Sets the target directory. If it exists, move will done after calling
     * {@link #setVisible(boolean)} with <code>true</code> as argument whitout
     * user interaction.
     *
     * @param directory  target directory
     */
    public void setTargetDirectory(File directory) {
        if (directory.exists()) {
            targetDirectory = directory;
            buttonStart.setEnabled(false);
            buttonChooseDirectory.setEnabled(false);
            moveIfVisible = true;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (moveIfVisible) {
                start();
                listenerSupport.add(this);
            } else {
                setTargetDirectory();
                listenerSupport.remove(this);
            }
        } else {
            targetDirectoryToSettings();
        }
        super.setVisible(visible);
    }

    private void setTargetDirectory() {
        targetDirectory = new File(UserSettings.INSTANCE.getSettings().getString(KEY_TARGET_DIRECTORY));
        if (targetDirectory.exists()) {
            labelDirectoryName.setText(targetDirectory.getAbsolutePath());
            setIconToLabelTargetDirectory();
            buttonStart.setEnabled(true);
        }
    }

    private void targetDirectoryToSettings() {
        UserSettings.INSTANCE.getSettings().set(targetDirectory.getAbsolutePath(), KEY_TARGET_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        checkStopEvent(evt);
        pListenerSupport.notifyStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        String filename = ((Pair<File, File>) evt.getInfo()).getFirst().getAbsolutePath();
        labelCurrentFilename.setText(filename);
        checkStopEvent(evt);
        pListenerSupport.notifyPerformed(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        buttonStop.setEnabled(true);
        buttonStart.setEnabled(true);
        runs = false;
        moveTask = null;
        GUI.INSTANCE.getAppPanel().getPanelThumbnails().remove(movedFiles);
        removeMovedFiles();
        pListenerSupport.notifyEnded(evt);
        checkErrors();
        setVisible(false);
    }

    private void removeMovedFiles() {
        for (File movedFile : movedFiles) {
            sourceFiles.remove(movedFile);
        }
        buttonStart.setEnabled(sourceFiles.size() > 0);
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    @Override
    public void actionPerformed(FileSystemEvent event) {

        if (!event.getType().equals(FileSystemEvent.Type.MOVE)) return;
        
        File src = event.getSource();

        if (event.isError()) {
            File            target = event.getTarget();
            FileSystemError error  = event.getError();

            AppLogger.logWarning(MoveToDirectoryDialog.class, "MoveToDirectoryDialog.Error.Logfile", src, target, error.getLocalizedMessage());
            errors = true;
        } else {
            movedFiles.add(src);
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

        labelInfo = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryName = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("MoveToDirectoryDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(Bundle.getString("MoveToDirectoryDialog.labelInfo.text")); // NOI18N

        buttonChooseDirectory.setText(Bundle.getString("MoveToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        labelInfoCurrentFilename.setText(bundle.getString("MoveToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(Bundle.getString("MoveToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        buttonStop.setText(Bundle.getString("MoveToDirectoryDialog.buttonCancelCopy.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setText(Bundle.getString("MoveToDirectoryDialog.buttonStartCopy.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonChooseDirectory)
                    .addComponent(labelInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCurrentFilename)
                    .addComponent(labelInfoCurrentFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop)
                    .addComponent(labelInfoIsThread))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelCurrentFilename, labelInfo});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
    start();
}//GEN-LAST:event_buttonStartActionPerformed

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    stop();
}//GEN-LAST:event_buttonStopActionPerformed

private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
    chooseTargetDirectory();
}//GEN-LAST:event_buttonChooseDirectoryActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    checkClosing();
}//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
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
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
