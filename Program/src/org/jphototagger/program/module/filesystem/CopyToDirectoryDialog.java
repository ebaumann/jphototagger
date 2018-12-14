package org.jphototagger.program.module.filesystem;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.CopyMoveFilesOptions;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.SourceTargetFile;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class CopyToDirectoryDialog extends Dialog implements ProgressListener {

    private static final String KEY_LAST_DIRECTORY = "org.jphototagger.program.view.dialogs.CopyToDirectoryDialog.LastDirectory";
    private static final String KEY_COPY_XMP = "CopyToDirectoryDialog.CopyXmp";
    private static final long serialVersionUID = 1L;
    private final transient ProgressListenerSupport pListenerSupport = new ProgressListenerSupport();
    private transient FilesystemCopy copyTask;
    private boolean copy;
    private boolean writeProperties = true;
    private Collection<File> sourceFiles;
    private File targetDirectory = new File("");
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

    public CopyToDirectoryDialog() {
        super(GUI.getAppFrame(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.HelpPage"));
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

    private void checkClosing() {
        if (copy) {
            String message = Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.Error.CancelBeforeClose");
            MessageDisplayer.error(this, message);
        } else {
            setVisible(false);
        }
    }

    private void checkError(List<String> errorFiles) {
        if (errorFiles.size() > 0) {
            String message = Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.Error.CopyErrorsOccured");
            MessageDisplayer.error(this, message);
        }
    }

    private void start(boolean addXmp, CopyMoveFilesOptions options) {
        copyTask = new FilesystemCopy(getFiles(addXmp), options);
        copyTask.setCopyListenerShallUpdateRepository(true);
        copyTask.addProgressListener(this);
        Thread thread = new Thread(copyTask, "JPhotoTagger: Copying files to directories");
        thread.start();
    }

    private CopyMoveFilesOptions getCopyOptions() {
        return radioButtonForceOverwrite.isSelected()
               ? CopyMoveFilesOptions.FORCE_OVERWRITE
               : radioButtonRenameIfTargetFileExists.isSelected()
                 ? CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS
                 : CopyMoveFilesOptions.CONFIRM_OVERWRITE;
    }

    private List<SourceTargetFile> getFiles(boolean addXmp) {
        List<SourceTargetFile> sourceTargetFiles = new ArrayList<>();
        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDirectory + File.separator + sourceFile.getName());
            // XMP first to avoid dynamically creating sidecar files before copied
            // when the embedded option is true and the image has IPTC or emb. XMP
            if (addXmp) {
                addXmp(sourceFile, sourceTargetFiles);
            }
            sourceTargetFiles.add(new SourceTargetFile(sourceFile, targetFile));
        }
        return sourceTargetFiles;
    }

    private void addXmp(File imageFile, List<SourceTargetFile> sourceTargetFiles) {
        File sidecarFile = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(imageFile);
        if (sidecarFile != null) {
            File sourceSidecarFile = sidecarFile;
            File targetSidecarFile = new File(targetDirectory + File.separator + sourceSidecarFile.getName());
            sourceTargetFiles.add(new SourceTargetFile(sourceSidecarFile, targetSidecarFile));
        }
    }

    private void cancel() {
        copyTask.cancel();
        setVisible(false);
    }

    private void chooseTargetDirectory() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), targetDirectory, hideRootFiles, getDirChooserOptionShowHiddenDirs());
        dlg.setPreferencesKey("CopyToDirectoryDialog.DirChooser");
        dlg.setVisible(true);
        toFront();
        if (dlg.isAccepted()) {
            List<File> files = dlg.getSelectedDirectories();
            if (files.size() > 0) {
                targetDirectory = files.get(0);
                if (targetDirectory.canWrite()) {
                    labelTargetDirectory.setText(targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    String message = Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.TargetDirNotWritable", targetDirectory);
                    MessageDisplayer.error(this, message);
                }
            }
        } else {
            File dir = new File(labelTargetDirectory.getText().trim());
            buttonStart.setEnabled(FileUtil.isWritableDirectory(dir));
        }
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setIconToLabelTargetDirectory() {
        File dir = new File(labelTargetDirectory.getText());
        if (dir.isDirectory()) {
            labelTargetDirectory.setIcon(FileSystemView.getFileSystemView().getSystemIcon(dir));
        }
    }

    public void setSourceFiles(Collection<File> sourceFiles) {
        if (sourceFiles == null) {
            throw new NullPointerException("sourceFiles == null");
        }
        this.sourceFiles = new ArrayList<>(sourceFiles);
    }

    public void setTargetDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }
        if (directory.isDirectory() && directory.exists()) {
            targetDirectory = directory;
        }
    }

    public void copy(boolean addXmp, CopyMoveFilesOptions options) {
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
        String message = Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.Error.TargetDirectoryDoesNotExist", targetDirectory.getAbsolutePath());
        MessageDisplayer.error(this, message);
    }

    private void errorMessageMissingSourceFiles() {
        String message = Bundle.getString(CopyToDirectoryDialog.class, "CopyToDirectoryDialog.Error.MissingSourceFiles");
        MessageDisplayer.error(this, message);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.applyToggleButtonSettings(KEY_COPY_XMP, checkBoxCopyXmp);
        File directory = new File(prefs.getString(KEY_LAST_DIRECTORY));
        if (directory.isDirectory()) {
            targetDirectory = directory;
        }
    }

    private void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setString(KEY_LAST_DIRECTORY, targetDirectory.getAbsolutePath());
        prefs.setToggleButton(KEY_COPY_XMP, checkBoxCopyXmp);
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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
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
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
                Object info = evt.getInfo();
                if (info instanceof SourceTargetFile) {
                    SourceTargetFile files = (SourceTargetFile) info;
                    labelCurrentFilename.setText(files.getSourceFile().getAbsolutePath());
                }
                pListenerSupport.notifyPerformed(evt);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
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

    private void setOptionsToRadioButtons(CopyMoveFilesOptions options) {
        radioButtonForceOverwrite.setSelected(options.equals(CopyMoveFilesOptions.FORCE_OVERWRITE));
        radioButtonRenameIfTargetFileExists.setSelected(options.equals(CopyMoveFilesOptions.RENAME_SOURCE_FILE_IF_TARGET_FILE_EXISTS));
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupFileExists = new javax.swing.ButtonGroup();
        panelContent = new javax.swing.JPanel();
        labelInfo = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        labelTargetDirectory = new javax.swing.JLabel();
        radioButtonForceOverwrite = new javax.swing.JRadioButton();
        radioButtonRenameIfTargetFileExists = new javax.swing.JRadioButton();
        checkBoxCopyXmp = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        panelCurrentFile = new javax.swing.JPanel();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();
        panelStartCancelButton = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "CopyToDirectoryDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelContent.add(labelInfo, gridBagConstraints);

        buttonChooseDirectory.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.setName("buttonChooseDirectory"); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(buttonChooseDirectory, gridBagConstraints);

        labelTargetDirectory.setText(" "); // NOI18N
        labelTargetDirectory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelTargetDirectory.setName("labelTargetDirectory"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(labelTargetDirectory, gridBagConstraints);

        buttonGroupFileExists.add(radioButtonForceOverwrite);
        radioButtonForceOverwrite.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.radioButtonForceOverwrite.text")); // NOI18N
        radioButtonForceOverwrite.setName("radioButtonForceOverwrite"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(radioButtonForceOverwrite, gridBagConstraints);

        buttonGroupFileExists.add(radioButtonRenameIfTargetFileExists);
        radioButtonRenameIfTargetFileExists.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.radioButtonRenameIfTargetFileExists.text")); // NOI18N
        radioButtonRenameIfTargetFileExists.setName("radioButtonRenameIfTargetFileExists"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(radioButtonRenameIfTargetFileExists, gridBagConstraints);

        checkBoxCopyXmp.setSelected(true);
        checkBoxCopyXmp.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.checkBoxCopyXmp.text")); // NOI18N
        checkBoxCopyXmp.setName("checkBoxCopyXmp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(checkBoxCopyXmp, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelContent.add(progressBar, gridBagConstraints);

        panelCurrentFile.setName("panelCurrentFile"); // NOI18N
        panelCurrentFile.setLayout(new java.awt.GridBagLayout());

        labelInfoCurrentFilename.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N
        labelInfoCurrentFilename.setName("labelInfoCurrentFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelCurrentFile.add(labelInfoCurrentFilename, gridBagConstraints);

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFilename.setText(" "); // NOI18N
        labelCurrentFilename.setName("labelCurrentFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelCurrentFile.add(labelCurrentFilename, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelCurrentFile, gridBagConstraints);

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.labelInfoIsThread.text")); // NOI18N
        labelInfoIsThread.setName("labelInfoIsThread"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelContent.add(labelInfoIsThread, gridBagConstraints);

        panelStartCancelButton.setName("panelStartCancelButton"); // NOI18N
        panelStartCancelButton.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelStartCancelButton.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonStart.setText(Bundle.getString(getClass(), "CopyToDirectoryDialog.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelStartCancelButton.add(buttonStart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(panelStartCancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

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
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelCurrentFile;
    private javax.swing.JPanel panelStartCancelButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioButtonForceOverwrite;
    private javax.swing.JRadioButton radioButtonRenameIfTargetFileExists;
    // End of variables declaration//GEN-END:variables
}
