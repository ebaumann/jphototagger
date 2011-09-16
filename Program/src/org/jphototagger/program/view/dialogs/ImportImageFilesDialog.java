package org.jphototagger.program.view.dialogs;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Storage;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.dialog.DirectoryChooser.Option;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ImagePreviewPanel;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ImportImageFilesDialog extends Dialog {
    private static final long serialVersionUID = -8291157139781240235L;
    private static final String KEY_LAST_SRC_DIR = "ImportImageFiles.LastSrcDir";
    private static final String KEY_LAST_TARGET_DIR = "ImportImageFiles.LastTargetDir";
    private static final String KEY_DEL_SRC_AFTER_COPY = "ImportImageFiles.DelSrcAfterCopy";
    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private final Storage storage = Lookup.getDefault().lookup(Storage.class);
    private File sourceDir = new File(storage.getString(KEY_LAST_SRC_DIR));
    private File targetDir = new File(storage.getString(KEY_LAST_TARGET_DIR));
    private final List<File> sourceFiles = new ArrayList<File>();
    private boolean filesChoosed;
    private boolean accepted;
    private boolean deleteSrcFilesAfterCopying;
    private boolean listenToCheckBox = true;

    public ImportImageFilesDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        setHelpPage();
        init();
    }

    private void setHelpPage() {
        // Has to be localized!
        setHelpContentsUrl("/org/jphototagger/program/resource/doc/de/contents.xml");
        setHelpPageUrl("import_images.html");
    }

    private void init() {
        if (sourceDir.isDirectory()) {
            setDirLabel(labelSourceDir, sourceDir);
        }

        if (dirsValid()) {
            setDirLabel(labelTargetDir, targetDir);
            buttonOk.setEnabled(true);
        }

        initDeleteSrcFilesAfterCopying();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initDeleteSrcFilesAfterCopying() {
        listenToCheckBox = false;
        checkBoxDeleteAfterCopy.setSelected(storage.getBoolean(KEY_DEL_SRC_AFTER_COPY));
        deleteSrcFilesAfterCopying = checkBoxDeleteAfterCopy.isSelected();
        listenToCheckBox = true;
    }

    public boolean isDeleteSourceFilesAfterCopying() {
        return deleteSrcFilesAfterCopying;
    }

    /**
     * Call prior <code>setVisible()</code>.
     *
     * @param dir
     */
    public void setSourceDir(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        sourceDir = dir;
        init();
    }

    /**
     * Call prior <code>setVisible()</code>.
     *
     * @param dir
     */
    public void setTargetDir(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        targetDir = dir;
        init();
    }

    /**
     * Returns the source directory if the user choosed a source directory
     * rather than separate files.
     *
     * @return source directory
     * @see    #filesChoosed()
     */
    public File getSourceDir() {
        return sourceDir;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
        setVisible(false);
    }

    private void chooseSourceDir() {
        File dir = chooseDir(sourceDir);

        if (dir == null) {
            return;
        }

        if (!targetDir.exists() || checkDirsDifferent(dir, targetDir)) {
            sourceDir = dir;
            filesChoosed = false;
            sourceFiles.clear();
            toSettings(KEY_LAST_SRC_DIR, dir);
            resetLabelChoosedFiles();
            setDirLabel(labelSourceDir, dir);
        }

        setEnabledOkButton();
    }

    private void chooseSourceFiles() {
        JFileChooser fileChooser = new JFileChooser(sourceDir);
        ImagePreviewPanel imgPanel = new ImagePreviewPanel();

        fileChooser.setAccessory(imgPanel);
        fileChooser.addPropertyChangeListener(imgPanel);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(imgPanel.getFileFilter());

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sourceFiles.clear();

            File[] selFiles = fileChooser.getSelectedFiles();

            if ((selFiles == null) || (selFiles.length < 1)) {
                return;
            }

            sourceFiles.addAll(Arrays.asList(selFiles));
            sourceDir = selFiles[0].getParentFile();
            toSettings(KEY_LAST_SRC_DIR, sourceDir);
            filesChoosed = true;
            resetLabelSourceDir();
            setFileLabel(selFiles[0], selFiles.length > 1);
        }

        setEnabledOkButton();
    }

    /**
     * Returns the choosen files.
     * <p>
     * <em>Verify, that {@link #filesChoosed()} returns true!</em>
     *
     * @return choosen files
     */
    public List<File> getSourceFiles() {
        return new ArrayList<File>(sourceFiles);
    }

    /**
     * Returns, whether the user choosed files rather than a source directory.
     * <p>
     * In this case, {@link #getSourceFiles()} return the choosen files.
     *
     * @return true if files choosen
     * @see    #getSourceDir()
     */
    public boolean filesChoosed() {
        return filesChoosed;
    }

    private void chooseTargetDir() {
        File dir = chooseDir(targetDir);

        if (dir == null) {
            return;
        }

        if (!sourceDir.exists() || checkDirsDifferent(sourceDir, dir)) {
            targetDir = dir;
            toSettings(KEY_LAST_TARGET_DIR, dir);
            setDirLabel(labelTargetDir, dir);
        }

        setEnabledOkButton();
    }

    private void toSettings(String key, File dir) {
        storage.setString(key, dir.getAbsolutePath());
    }

    private File chooseDir(File startDir) {
        Option showHiddenDirs = getDirChooserOptionShowHiddenDirs();
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), startDir, showHiddenDirs);

        dlg.setStorageKey("ImportImageFilesDialog.DirChooser");
        dlg.setVisible(true);

        return dlg.isAccepted()
               ? dlg.getSelectedDirectories().get(0)
               : null;
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        return storage.containsKey(Storage.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Storage.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void setDirLabel(JLabel label, File dir) {
        label.setIcon(fileSystemView.getSystemIcon(dir));
        label.setText(dir.getAbsolutePath());
    }

    private void setFileLabel(File file, boolean multipleFiles) {
        labelChoosedFiles.setIcon(fileSystemView.getSystemIcon(file));
        labelChoosedFiles.setText(StringUtil.getPrefixDotted(file.getName(), 20) +
                (multipleFiles
                       ? ", ..."
                       : ""));
    }

    private void resetLabelChoosedFiles() {
        labelChoosedFiles.setIcon(null);
        labelChoosedFiles.setText("");
    }

    private void resetLabelSourceDir() {
        labelSourceDir.setText("");
        labelSourceDir.setIcon(null);
    }

    private void setEnabledOkButton() {
        buttonOk.setEnabled(dirsValid());
    }

    private boolean dirsValid() {
        if (filesChoosed) {
            return !sourceFiles.isEmpty() && targetDir.isDirectory() && dirsDifferent();
        } else {
            return existsBothDirs() && dirsDifferent();
        }
    }

    private boolean dirsDifferent() {
        return !sourceDir.equals(targetDir);
    }

    private boolean existsBothDirs() {
        return sourceDir.isDirectory() && targetDir.isDirectory();
    }

    private boolean checkDirsDifferent(File src, File tgt) {
        if (src.equals(tgt)) {
            String message = Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.Error.DirsEquals");
            MessageDisplayer.error(this, message);

            return false;
        }

        return true;
    }

    private void handleCheckBoxDeleteAfterCopyPerformed() {
        if (!listenToCheckBox) {
            return;
        }

        boolean selected = checkBoxDeleteAfterCopy.isSelected();

        if (selected) {
            String message = Bundle.getString(ImportImageFilesDialog.class, "ImportImageFilesDialog.Confirm.DeleteAfterCopy");

            if (!MessageDisplayer.confirmYesNo(this, message)) {
                listenToCheckBox = false;
                selected = false;
                checkBoxDeleteAfterCopy.setSelected(false);
                listenToCheckBox = true;
            }
        }

        deleteSrcFilesAfterCopying = selected;
        storage.setBoolean(KEY_DEL_SRC_AFTER_COPY, deleteSrcFilesAfterCopying);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelPromptSourceDir = new javax.swing.JLabel();
        labelSourceDir = new javax.swing.JLabel();
        buttonChooseSourceDir = new javax.swing.JButton();
        labelPromptChooseFiles = new javax.swing.JLabel();
        labelChoosedFiles = new javax.swing.JLabel();
        buttonChooseFiles = new javax.swing.JButton();
        labelPromptTargetDir = new javax.swing.JLabel();
        labelTargetDir = new javax.swing.JLabel();
        checkBoxDeleteAfterCopy = new javax.swing.JCheckBox();
        buttonChooseTargetDir = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("ImportImageFilesDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        labelPromptSourceDir.setText(bundle.getString("ImportImageFilesDialog.labelPromptSourceDir.text")); // NOI18N
        labelPromptSourceDir.setName("labelPromptSourceDir"); // NOI18N

        labelSourceDir.setName("labelSourceDir"); // NOI18N

        buttonChooseSourceDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseSourceDir.text")); // NOI18N
        buttonChooseSourceDir.setName("buttonChooseSourceDir"); // NOI18N
        buttonChooseSourceDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseSourceDirActionPerformed(evt);
            }
        });

        labelPromptChooseFiles.setText(bundle.getString("ImportImageFilesDialog.labelPromptChooseFiles.text")); // NOI18N
        labelPromptChooseFiles.setName("labelPromptChooseFiles"); // NOI18N

        labelChoosedFiles.setName("labelChoosedFiles"); // NOI18N

        buttonChooseFiles.setText(bundle.getString("ImportImageFilesDialog.buttonChooseFiles.text")); // NOI18N
        buttonChooseFiles.setName("buttonChooseFiles"); // NOI18N
        buttonChooseFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFilesActionPerformed(evt);
            }
        });

        labelPromptTargetDir.setText(bundle.getString("ImportImageFilesDialog.labelPromptTargetDir.text")); // NOI18N
        labelPromptTargetDir.setName("labelPromptTargetDir"); // NOI18N

        labelTargetDir.setName("labelTargetDir"); // NOI18N

        checkBoxDeleteAfterCopy.setText(bundle.getString("ImportImageFilesDialog.checkBoxDeleteAfterCopy.text")); // NOI18N
        checkBoxDeleteAfterCopy.setName("checkBoxDeleteAfterCopy"); // NOI18N
        checkBoxDeleteAfterCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteAfterCopyActionPerformed(evt);
            }
        });

        buttonChooseTargetDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseTargetDir.text")); // NOI18N
        buttonChooseTargetDir.setName("buttonChooseTargetDir"); // NOI18N
        buttonChooseTargetDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseTargetDirActionPerformed(evt);
            }
        });

        buttonCancel.setText(bundle.getString("ImportImageFilesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setText(bundle.getString("ImportImageFilesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(labelSourceDir, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap(261, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelPromptTargetDir)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelPromptChooseFiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelChoosedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                            .addComponent(labelPromptSourceDir)
                            .addComponent(checkBoxDeleteAfterCopy)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(labelTargetDir, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonChooseFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonChooseSourceDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonChooseTargetDir))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(405, Short.MAX_VALUE)
                .addComponent(buttonCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOk)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonChooseFiles, buttonChooseSourceDir, buttonChooseTargetDir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonCancel, buttonOk});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(buttonChooseSourceDir)
                    .addComponent(labelSourceDir)
                    .addComponent(labelPromptSourceDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(buttonChooseFiles)
                    .addComponent(labelChoosedFiles)
                    .addComponent(labelPromptChooseFiles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(buttonChooseTargetDir)
                    .addComponent(labelTargetDir)
                    .addComponent(labelPromptTargetDir))
                .addGap(8, 8, 8)
                .addComponent(checkBoxDeleteAfterCopy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOk)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelPromptSourceDir, labelPromptTargetDir, labelSourceDir, labelTargetDir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonChooseFiles, buttonChooseSourceDir, buttonChooseTargetDir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelChoosedFiles, labelPromptChooseFiles});

        pack();
    }//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setAccepted(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        setAccepted(true);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonChooseSourceDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseSourceDirActionPerformed
        chooseSourceDir();
    }//GEN-LAST:event_buttonChooseSourceDirActionPerformed

    private void buttonChooseTargetDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseTargetDirActionPerformed
        chooseTargetDir();
    }//GEN-LAST:event_buttonChooseTargetDirActionPerformed

    private void buttonChooseFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseFilesActionPerformed
        chooseSourceFiles();
    }//GEN-LAST:event_buttonChooseFilesActionPerformed

    private void checkBoxDeleteAfterCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteAfterCopyActionPerformed
        handleCheckBoxDeleteAfterCopyPerformed();
    }//GEN-LAST:event_checkBoxDeleteAfterCopyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ImportImageFilesDialog dialog = new ImportImageFilesDialog();

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
    private javax.swing.JButton buttonChooseFiles;
    private javax.swing.JButton buttonChooseSourceDir;
    private javax.swing.JButton buttonChooseTargetDir;
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxDeleteAfterCopy;
    private javax.swing.JLabel labelChoosedFiles;
    private javax.swing.JLabel labelPromptChooseFiles;
    private javax.swing.JLabel labelPromptSourceDir;
    private javax.swing.JLabel labelPromptTargetDir;
    private javax.swing.JLabel labelSourceDir;
    private javax.swing.JLabel labelTargetDir;
    // End of variables declaration//GEN-END:variables
}
