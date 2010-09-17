/*
 * @(#)MoveToDirectoryDialog.java    Created on 2008-10-20
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.event.listener.impl.FileSystemListenerSupport;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.io.FileSystemMove;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.io.FileUtil;

import java.awt.Container;
import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Elmar Baumann
 */
public final class MoveToDirectoryDialog extends Dialog
        implements ProgressListener, FileSystemListener {
    private static final String KEY_TARGET_DIRECTORY =
        "org.jphototagger.program.view.dialogs.MoveToDirectoryDialog.TargetDirectory";
    private static final long                       serialVersionUID =
        3213926343815394815L;
    private final List<File>                        movedFiles       =
        new ArrayList<File>();
    private final transient ProgressListenerSupport pListenerSupport =
        new ProgressListenerSupport();
    private transient FileSystemMove                  moveTask;
    private boolean                                   runs   = false;
    private boolean                                   cancel = false;
    private boolean                                   errors = false;
    private List<File>                                sourceFiles;
    private File                                      targetDirectory =
        new File("");
    private boolean                                   moveIfVisible   = false;
    private final transient FileSystemListenerSupport ls =
        new FileSystemListenerSupport();

    public MoveToDirectoryDialog() {
        super(GUI.getAppFrame(), false,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString("Help.Url.MoveToDirectoryDialog"));
    }

    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pListenerSupport.add(listener);
    }

    private void checkClosing() {
        if (runs) {
            MessageDisplayer.error(
                this, "MoveToDirectoryDialog.Error.CancelBeforeClose");
        } else {
            setVisible(false);
        }
    }

    private void checkErrors() {
        if (errors) {
            MessageDisplayer.error(this,
                                   "MoveToDirectoryDialog.Error.CheckLogfile");
        }
    }

    private void addXmpFiles() {
        List<File> xmpFiles = new ArrayList<File>();

        for (File sourceFile : sourceFiles) {
            File xmpFile = XmpMetadata.getSidecarFile(sourceFile);

            if (xmpFile != null) {
                xmpFiles.add(xmpFile);
            }
        }

        sourceFiles.addAll(xmpFiles);
    }

    private void reset() {
        runs   = false;
        cancel = false;
        errors = false;
        movedFiles.clear();
    }

    private void start() {
        reset();
        moveTask = new FileSystemMove(
            sourceFiles, targetDirectory,
            UserSettings.INSTANCE.getCopyMoveFilesOptions().equals(
                CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
        addListenerToMoveTask();

        Thread thread = new Thread(moveTask, getMoveThreadName());

        thread.start();
        runs = true;
    }

    private String getMoveThreadName() {
        return "JPhotoTagger: Moving files to directory "
                + targetDirectory.getAbsolutePath();
    }

    public void addFileSystemListener(FileSystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    private void addListenerToMoveTask() {
        moveTask.addFileSystemListener(this);
        moveTask.addProgressListener(this);

        for (FileSystemListener listener : ls.get()) {
            moveTask.addFileSystemListener(listener);
        }
    }

    private void cancel() {
        cancel = true;
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dlg =
            new DirectoryChooser(
                GUI.getAppFrame(), targetDirectory,
                UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(),
                           "MoveToDirectoriesDialog.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            List<File> files = dlg.getSelectedDirectories();

            if (files.size() > 0) {
                targetDirectory = files.get(0);

                if (targetDirectory.canWrite()) {
                    labelDirectoryName.setText(
                        targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    MessageDisplayer.error(
                        this, "MoveToDirectoryDialog.TargetDirNotWritable",
                        targetDirectory);
                }
            }
        } else {
            File dir = new File(labelDirectoryName.getText().trim());

            buttonStart.setEnabled(FileUtil.isWritableDirectory(dir));
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
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        this.sourceFiles = new ArrayList<File>(sourceFiles);
        addXmpFiles();
        Collections.sort(this.sourceFiles);
    }

    /**
     * Sets the target directory. If it exists, move will done after calling
     * {@link #setVisible(boolean)} with <code>true</code> as argument whitout
     * user interaction.
     *
     * @param directory  target directory
     */
    public void setTargetDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

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
                ls.add(this);
            } else {
                setTargetDirectory();
                ls.remove(this);
            }
        } else {
            targetDirectoryToSettings();
        }

        super.setVisible(visible);
    }

    private void setTargetDirectory() {
        targetDirectory = new File(
            UserSettings.INSTANCE.getSettings().getString(
                KEY_TARGET_DIRECTORY));

        if (targetDirectory.exists()) {
            labelDirectoryName.setText(targetDirectory.getAbsolutePath());
            setIconToLabelTargetDirectory();
            buttonStart.setEnabled(true);
        }
    }

    private void targetDirectoryToSettings() {
        UserSettings.INSTANCE.getSettings().set(
            targetDirectory.getAbsolutePath(), KEY_TARGET_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.cancel();
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
        buttonStart.setEnabled(false);
        buttonCancel.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        checkCancel(evt);
        pListenerSupport.notifyStarted(evt);
    }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
        progressBar.setValue(evt.getValue());

        @SuppressWarnings("unchecked") String filename =
            ((Pair<File, File>) evt.getInfo()).getFirst().getAbsolutePath();

        labelCurrentFilename.setText(filename);
        checkCancel(evt);
        pListenerSupport.notifyPerformed(evt);
    }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
        progressBar.setValue(evt.getValue());
        buttonCancel.setEnabled(true);
        buttonStart.setEnabled(true);
        runs     = false;
        moveTask = null;
        GUI.getThumbnailsPanel().remove(movedFiles);
        removeMovedFiles();
        pListenerSupport.notifyEnded(evt);
        checkErrors();
        setVisible(false);
    }
        });
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
    public void fileMoved(File source, File target) {
        movedFiles.add(source);
    }

    @Override
    public void fileCopied(File source, File target) {

        // ignore
    }

    @Override
    public void fileDeleted(File file) {

        // ignore
    }

    @Override
    public void fileRenamed(File oldFile, File newFile) {

        // ignore
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

        labelInfo = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        labelDirectoryName = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.labelInfo.text")); // NOI18N

        buttonChooseDirectory.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelInfoCurrentFilename.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        buttonCancel.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.buttonCancelCopy.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonStart.setText(JptBundle.INSTANCE.getString("MoveToDirectoryDialog.buttonStartCopy.text")); // NOI18N
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)))
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
                    .addComponent(buttonCancel)
                    .addComponent(labelInfoIsThread))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelCurrentFilename, labelInfo});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        start();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_buttonCancelActionPerformed

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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
