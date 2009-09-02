package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.event.FileSystemEvent;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.FileSystemActionListener;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.helper.CopyFiles;
import de.elmar_baumann.imv.helper.CopyFiles.Options;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class CopyToDirectoryDialog
        extends Dialog
        implements ProgressListener {

    private static final String KEY_LAST_DIRECTORY =
            "de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog.LastDirectory"; // NOI18N
    private static final String KEY_COPY_XMP = "CopyToDirectoryDialog.CopyXmp"; // NOI18N
    private final Set<ProgressListener> progressListeners =
            Collections.synchronizedSet(new HashSet<ProgressListener>());
    private final Set<FileSystemActionListener> fileSystemActionListeners =
            Collections.synchronizedSet(new HashSet<FileSystemActionListener>());
    private CopyFiles copyTask;
    private boolean copy = false;
    private boolean writeProperties = true;
    private Collection<File> sourceFiles;
    private File targetDirectory = new File(""); // NOI18N

    public CopyToDirectoryDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        setIconImages(AppIcons.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
        registerKeyStrokes();
    }

    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    public void addFileSystemActionListener(FileSystemActionListener listener) {
        fileSystemActionListeners.add(listener);
    }

    public void removeFileSystemActionListener(FileSystemActionListener listener) {
        fileSystemActionListeners.remove(listener);
    }

    public void notifyFileSystemActionListenersCopied(File src, File target) {
        synchronized (fileSystemActionListeners) {
            for (FileSystemActionListener listener : fileSystemActionListeners) {
                listener.actionPerformed(FileSystemEvent.COPY, src, target);
            }
        }
    }

    private void notifyProgressListenerStarted(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressStarted(evt);
            }
        }
    }

    private void notifyProgressListenerPerformed(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressPerformed(evt);
            }
        }
    }

    private void notifyProgressListenerEnded(ProgressEvent evt) {
        synchronized (progressListeners) {
            for (ProgressListener listener : progressListeners) {
                listener.progressEnded(evt);
            }
        }
    }

    private void checkClosing() {
        if (copy) {
            MessageDisplayer.error(this,
                    "CopyToDirectoryDialog.Error.AbortBeforeClose"); // NOI18N
        } else {
            setVisible(false);
        }
    }

    private void checkError(List<String> errorFiles) {
        if (errorFiles.size() > 0) {
            MessageDisplayer.error(this,
                    "CopyToDirectoryDialog.Error.CopyErrorsOccured"); // NOI18N
        }
    }

    private void start(boolean addXmp, Options options) {
        copyTask = new CopyFiles(getFiles(addXmp), options);
        copyTask.addProgressListener(this);
        Thread thread = new Thread(copyTask);
        thread.setName("Copying files to directories" + " @ " + // NOI18N
                getClass().getName());
        thread.start();
    }

    private Options getCopyOptions() {
        return radioButtonForceOverwrite.isSelected()
               ? CopyFiles.Options.FORCE_OVERWRITE
               : radioButtonRenameIfTargetFileExists.isSelected()
                 ? CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS
                 : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    private List<Pair<File, File>> getFiles(boolean addXmp) {
        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();
        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDirectory + File.separator +
                    sourceFile.getName());
            filePairs.add(new Pair<File, File>(sourceFile, targetFile));
            if (addXmp) {
                addXmp(sourceFile, filePairs);
            }
        }
        return filePairs;
    }

    private void addXmp(File sourceFile, List<Pair<File, File>> filePairs) {
        String sidecarFilename =
                XmpMetadata.getSidecarFilenameOfImageFileIfExists(sourceFile.
                getAbsolutePath());
        if (sidecarFilename != null) {
            File sourceSidecarFile = new File(sidecarFilename);
            File targetSidecarFile = new File(targetDirectory +
                    File.separator + sourceSidecarFile.getName());
            filePairs.add(new Pair<File, File>(sourceSidecarFile,
                    targetSidecarFile));
        }
    }

    private void stop() {
        copyTask.stop();
        setVisible(false);
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, targetDirectory,
                UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            if (files.size() > 0) {
                targetDirectory = files.get(0);
                if (targetDirectory.canWrite()) {
                    labelTargetDirectory.setText(
                            targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    MessageDisplayer.error(this,
                            "CopyToDirectoryDialog.TargetDirNotWritable", // NOI18N
                            targetDirectory);
                }
            }
        } else {
            File dir = new File(labelTargetDirectory.getText().trim());
            buttonStart.setEnabled(
                    FileUtil.existsDirectory(dir) && dir.canWrite());
        }
    }

    private void setIconToLabelTargetDirectory() {
        File dir = new File(labelTargetDirectory.getText());
        if (dir.isDirectory()) {
            labelTargetDirectory.setIcon(
                    FileSystemView.getFileSystemView().getSystemIcon(dir));
        }
    }

    /**
     * Setzt die zu kopierenden Quelldateien.
     * 
     * @param sourceFiles  Quelldateien
     */
    public void setSourceFiles(Collection<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    /**
     * Sets the target directory if exists.
     * 
     * @param directory target directory
     */
    public void setTargetDirectory(File directory) {
        if (directory.isDirectory() && directory.exists()) {
            targetDirectory = directory;
        }
    }

    /**
     * Makes this dialog visible and copies the files set with
     * {@link #setSourceFiles(java.util.List)} if not empty into the directory
     * set with {@link #setTargetDirectory(java.io.File)} if exists.
     *
     * @param addXmp  true if copy XMP sidecar files too
     * @param options copy options
     */
    public void copy(boolean addXmp, Options options) {
        if (targetDirectory.exists() && sourceFiles.size() > 0) {
            labelTargetDirectory.setText(targetDirectory.getAbsolutePath());
            setOptionsToRadioButtons(options);
            setIconToLabelTargetDirectory();
            buttonChooseDirectory.setEnabled(false);
            buttonStart.setEnabled(false);
            checkBoxCopyXmp.setSelected(true);
            radioButtonForceOverwrite.setSelected(false);
            radioButtonRenameIfTargetFileExists.setSelected(true);
            writeProperties = false;
            super.setVisible(true);
            ComponentUtil.centerScreen(this);
            start(addXmp, options);
        } else {
            if (!targetDirectory.exists()) {
                errorMessageTargetDirectoryDoesNotExist();
            } else if (sourceFiles.size() <= 0) {
                errorMessageMissingSourceFiles();
            }
        }
    }

    private void errorMessageTargetDirectoryDoesNotExist() {
        MessageDisplayer.error(this,
                "CopyToDirectoryDialog.Error.TargetDirectoryDoesNotExist", // NOI18N
                targetDirectory.getAbsolutePath());
    }

    private void errorMessageMissingSourceFiles() {
        MessageDisplayer.error(this,
                "CopyToDirectoryDialog.Error.MissingSourceFiles"); // NOI18N
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
            initDirectory();
        } else {
            if (writeProperties) {
                writeProperties();
            }
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().getCheckBox(checkBoxCopyXmp,
                KEY_COPY_XMP);
        String dir = UserSettings.INSTANCE.getSettings().getString(
                KEY_LAST_DIRECTORY);
        File directory = new File(dir);
        if (FileUtil.existsDirectory(directory)) {
            targetDirectory = directory;
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setString(
                targetDirectory.getAbsolutePath(), KEY_LAST_DIRECTORY);
        UserSettings.INSTANCE.getSettings().setCheckBox(
                checkBoxCopyXmp, KEY_COPY_XMP);
        UserSettings.INSTANCE.writeToFile();
    }

    private void initDirectory() {
        if (targetDirectory.exists()) {
            labelTargetDirectory.setText(targetDirectory.getAbsolutePath());
            setIconToLabelTargetDirectory();
            buttonStart.setEnabled(true);
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        copy = true;
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        notifyProgressListenerStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        Pair<File, File> files = (Pair<File, File>) evt.getInfo();
        labelCurrentFilename.setText(files.getFirst().getAbsolutePath());
        notifyFileSystemActionListenersCopied(
                files.getFirst(), files.getSecond());
        notifyProgressListenerPerformed(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        List<String> errorFiles = (List<String>) evt.getInfo();
        checkError(errorFiles);
        buttonStop.setEnabled(false);
        buttonStart.setEnabled(true);
        copy = false;
        notifyProgressListenerEnded(evt);
        setVisible(false);
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.CopyToDirectoryDialog")); // NOI18N
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    private void setOptionsToRadioButtons(Options options) {
        radioButtonForceOverwrite.setSelected(
                options.equals(CopyFiles.Options.FORCE_OVERWRITE));
        radioButtonRenameIfTargetFileExists.setSelected(
                options.equals(
                CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupFileExists = new javax.swing.ButtonGroup();
        labelInfo = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        labelTargetDirectory = new javax.swing.JLabel();
        radioButtonForceOverwrite = new javax.swing.JRadioButton();
        radioButtonRenameIfTargetFileExists = new javax.swing.JRadioButton();
        checkBoxCopyXmp = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("CopyToDirectoryDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(Bundle.getString("CopyToDirectoryDialog.labelInfo.text")); // NOI18N

        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("CopyToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelTargetDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroupFileExists.add(radioButtonForceOverwrite);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle"); // NOI18N
        radioButtonForceOverwrite.setText(bundle.getString("CopyToDirectoryDialog.radioButtonForceOverwrite.text")); // NOI18N

        buttonGroupFileExists.add(radioButtonRenameIfTargetFileExists);
        radioButtonRenameIfTargetFileExists.setText(bundle.getString("CopyToDirectoryDialog.radioButtonRenameIfTargetFileExists.text")); // NOI18N

        checkBoxCopyXmp.setMnemonic('x');
        checkBoxCopyXmp.setSelected(true);
        checkBoxCopyXmp.setText(Bundle.getString("CopyToDirectoryDialog.checkBoxCopyXmp.text")); // NOI18N

        labelInfoCurrentFilename.setText(bundle.getString("CopyToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(Bundle.getString("CopyToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("CopyToDirectoryDialog.buttonStop.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setMnemonic('s');
        buttonStart.setText(Bundle.getString("CopyToDirectoryDialog.buttonStart.text")); // NOI18N
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
                    .addComponent(checkBoxCopyXmp)
                    .addComponent(radioButtonRenameIfTargetFileExists)
                    .addComponent(radioButtonForceOverwrite)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelTargetDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfo)
                    .addComponent(buttonChooseDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTargetDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonForceOverwrite)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonRenameIfTargetFileExists)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxCopyXmp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelInfoCurrentFilename)
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoIsThread)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop))
                .addGap(6, 6, 6))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonStart, buttonStop, progressBar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
    start(checkBoxCopyXmp.isSelected(), getCopyOptions());
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
                CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
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
    private javax.swing.ButtonGroup buttonGroupFileExists;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxCopyXmp;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JLabel labelTargetDirectory;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioButtonForceOverwrite;
    private javax.swing.JRadioButton radioButtonRenameIfTargetFileExists;
    // End of variables declaration//GEN-END:variables
}
