package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.listener.FileSystemListener;
import org.jphototagger.program.event.listener.impl.FileSystemListenerSupport;
import org.jphototagger.program.event.listener.impl.ProgressListenerSupport;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.helper.CopyFiles;
import org.jphototagger.program.helper.CopyFiles.Options;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Settings;

import java.awt.Container;
import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.filechooser.FileSystemView;
import org.jphototagger.program.view.panels.SelectRootFilesPanel;

/**
 *
 * @author Elmar Baumann
 */
public final class CopyToDirectoryDialog extends Dialog implements ProgressListener {
    private static final String KEY_LAST_DIRECTORY = "org.jphototagger.program.view.dialogs.CopyToDirectoryDialog.LastDirectory";
    private static final String KEY_COPY_XMP = "CopyToDirectoryDialog.CopyXmp";
    private static final long serialVersionUID = 2401347394410721552L;
    private final transient ProgressListenerSupport pListenerSupport = new ProgressListenerSupport();
    private final transient FileSystemListenerSupport fsListenerSupport = new FileSystemListenerSupport();
    private transient CopyFiles copyTask;
    private boolean copy;
    private boolean writeProperties = true;
    private Collection<File> sourceFiles;
    private File targetDirectory = new File("");

    public CopyToDirectoryDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.CopyToDirectoryDialog"));
    }

    public void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pListenerSupport.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        pListenerSupport.remove(listener);
    }

    public void addFileSystemActionListener(FileSystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        fsListenerSupport.add(listener);
    }

    public void removeFileSystemActionListener(FileSystemListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        fsListenerSupport.remove(listener);
    }

    private void notifyFileSystemActionListenersCopied(File src, File target) {
        fsListenerSupport.notifyCopied(src, target);
    }

    private void checkClosing() {
        if (copy) {
            MessageDisplayer.error(this, "CopyToDirectoryDialog.Error.CancelBeforeClose");
        } else {
            setVisible(false);
        }
    }

    private void checkError(List<String> errorFiles) {
        if (errorFiles.size() > 0) {
            MessageDisplayer.error(this, "CopyToDirectoryDialog.Error.CopyErrorsOccured");
        }
    }

    private void start(boolean addXmp, Options options) {
        copyTask = new CopyFiles(getFiles(addXmp), options);
        copyTask.addProgressListener(this);

        Thread thread = new Thread(copyTask, "JPhotoTagger: Copying files to directories");

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
            File targetFile = new File(targetDirectory + File.separator + sourceFile.getName());

            // XMP first to avoid dynamically creating sidecar files before copied
            // when the embedded option is true and the image has IPTC or emb. XMP
            if (addXmp) {
                addXmp(sourceFile, filePairs);
            }

            filePairs.add(new Pair<File, File>(sourceFile, targetFile));
        }

        return filePairs;
    }

    private void addXmp(File imageFile, List<Pair<File, File>> filePairs) {
        File sidecarFile = XmpMetadata.getSidecarFile(imageFile);

        if (sidecarFile != null) {
            File sourceSidecarFile = sidecarFile;
            File targetSidecarFile = new File(targetDirectory + File.separator + sourceSidecarFile.getName());

            filePairs.add(new Pair<File, File>(sourceSidecarFile, targetSidecarFile));
        }
    }

    private void cancel() {
        copyTask.cancel();
        setVisible(false);
    }

    private void chooseTargetDirectory() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(UserSettings.KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), targetDirectory, hideRootFiles, UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), "CopyToDirectoryDialog.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            List<File> files = dlg.getSelectedDirectories();

            if (files.size() > 0) {
                targetDirectory = files.get(0);

                if (targetDirectory.canWrite()) {
                    labelTargetDirectory.setText(targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    MessageDisplayer.error(this, "CopyToDirectoryDialog.TargetDirNotWritable", targetDirectory);
                }
            }
        } else {
            File dir = new File(labelTargetDirectory.getText().trim());

            buttonStart.setEnabled(FileUtil.isWritableDirectory(dir));
        }
    }

    private void setIconToLabelTargetDirectory() {
        File dir = new File(labelTargetDirectory.getText());

        if (dir.isDirectory()) {
            labelTargetDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
        }
    }

    /**
     * Setzt die zu kopierenden Quelldateien.
     *
     * @param sourceFiles  Quelldateien
     */
    public void setSourceFiles(Collection<File> sourceFiles) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }

        this.sourceFiles = new ArrayList<File>(sourceFiles);
    }

    /**
     * Sets the target directory if exists.
     *
     * @param directory target directory
     */
    public void setTargetDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory() && directory.exists()) {
            targetDirectory = directory;
        }
    }

    /**
     * Makes this dialog visible and copies the files set with
     * {@link #setSourceFiles(Collection)} if not empty into the directory
     * set with {@link #setTargetDirectory(java.io.File)} if exists.
     *
     * @param addXmp  true if copy XMP sidecar files too
     * @param options copy options
     */
    public void copy(boolean addXmp, Options options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        if (targetDirectory.exists() && (sourceFiles.size() > 0)) {
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
        MessageDisplayer.error(this, "CopyToDirectoryDialog.Error.TargetDirectoryDoesNotExist", targetDirectory.getAbsolutePath());
    }

    private void errorMessageMissingSourceFiles() {
        MessageDisplayer.error(this, "CopyToDirectoryDialog.Error.MissingSourceFiles");
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
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.applySettings(checkBoxCopyXmp, KEY_COPY_XMP);

        File directory = new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIRECTORY));

        if (directory.isDirectory()) {
            targetDirectory = directory;
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(targetDirectory.getAbsolutePath(), KEY_LAST_DIRECTORY);
        settings.set(checkBoxCopyXmp, KEY_COPY_XMP);
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
    public void progressStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                copy = true;
                buttonStart.setEnabled(false);
                buttonCancel.setEnabled(true);
                progressBar.setMinimum(evt.getMinimum());
                progressBar.setMaximum(evt.getMaximum());
                progressBar.setValue(evt.getValue());
                pListenerSupport.notifyStarted(evt);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());

                Pair<File, File> files = (Pair<File, File>) evt.getInfo();

                labelCurrentFilename.setText(files.getFirst().getAbsolutePath());
                notifyFileSystemActionListenersCopied(files.getFirst(), files.getSecond());
                pListenerSupport.notifyPerformed(evt);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());

                List<String> errorFiles = (List<String>) evt.getInfo();

                checkError(errorFiles);
                buttonCancel.setEnabled(false);
                buttonStart.setEnabled(true);
                copy = false;
                pListenerSupport.notifyEnded(evt);
                setVisible(false);
            }
        });
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    private void setOptionsToRadioButtons(Options options) {
        radioButtonForceOverwrite.setSelected(options.equals(CopyFiles.Options.FORCE_OVERWRITE));
        radioButtonRenameIfTargetFileExists.setSelected(options.equals(CopyFiles.Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS));
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
        buttonCancel = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N

        buttonChooseDirectory.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelTargetDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelTargetDirectory.setName("labelTargetDirectory"); // NOI18N

        buttonGroupFileExists.add(radioButtonForceOverwrite);
        radioButtonForceOverwrite.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.radioButtonForceOverwrite.text")); // NOI18N
        radioButtonForceOverwrite.setName("radioButtonForceOverwrite"); // NOI18N

        buttonGroupFileExists.add(radioButtonRenameIfTargetFileExists);
        radioButtonRenameIfTargetFileExists.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.radioButtonRenameIfTargetFileExists.text")); // NOI18N
        radioButtonRenameIfTargetFileExists.setName("radioButtonRenameIfTargetFileExists"); // NOI18N

        checkBoxCopyXmp.setSelected(true);
        checkBoxCopyXmp.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.checkBoxCopyXmp.text")); // NOI18N
        checkBoxCopyXmp.setName("checkBoxCopyXmp"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        labelInfoCurrentFilename.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N
        labelInfoCurrentFilename.setName("labelInfoCurrentFilename"); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFilename.setName("labelCurrentFilename"); // NOI18N

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.labelInfoIsThread.text")); // NOI18N
        labelInfoIsThread.setName("labelInfoIsThread"); // NOI18N

        buttonCancel.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonStart.setText(JptBundle.INSTANCE.getString("CopyToDirectoryDialog.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxCopyXmp)
                    .addComponent(radioButtonRenameIfTargetFileExists)
                    .addComponent(radioButtonForceOverwrite)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelTargetDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
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
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonCancel, buttonStart, progressBar});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        start(checkBoxCopyXmp.isSelected(), getCopyOptions());
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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.ButtonGroup buttonGroupFileExists;
    private javax.swing.JButton buttonStart;
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
