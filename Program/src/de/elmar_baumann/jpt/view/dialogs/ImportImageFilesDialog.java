/*
 * @(#)ImportImageFilesDialog.java    2010-01-23
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

package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.view.panels.ImagePreviewPanel;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.StringUtil;

import java.awt.Container;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 *
 *
 * @author  Elmar Baumann
 */
public class ImportImageFilesDialog extends Dialog {
    private static final long   serialVersionUID = -8291157139781240235L;
    private static final String KEY_LAST_SRC_DIR =
        "ImportImageFiles.LastSrcDir";
    private static final String KEY_LAST_TARGET_DIR =
        "ImportImageFiles.LastTargetDir";
    private static final String KEY_DEL_SRC_AFTER_COPY =
        "ImportImageFiles.DelSrcAfterCopy";
    private final FileSystemView fileSystemView =
        FileSystemView.getFileSystemView();
    private File sourceDir = new File(
                                 UserSettings.INSTANCE.getSettings().getString(
                                     KEY_LAST_SRC_DIR));
    private File targetDir = new File(
                                 UserSettings.INSTANCE.getSettings().getString(
                                     KEY_LAST_TARGET_DIR));
    private final List<File> sourceFiles = new ArrayList<File>();
    private boolean          filesChoosed;
    private boolean          accepted;
    private boolean          deleteSrcFilesAfterCopying;
    private boolean          listenToCheckBox = true;

    public ImportImageFilesDialog() {
        super(GUI.INSTANCE.getAppFrame(), true,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        init();
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString("Help.Url.ImportImageFiles"));
    }

    private void init() {
        if (FileUtil.existsDirectory(sourceDir)) {
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
        checkBoxDeleteAfterCopy.setSelected(
            UserSettings.INSTANCE.getSettings().getBoolean(
                KEY_DEL_SRC_AFTER_COPY));
        deleteSrcFilesAfterCopying = checkBoxDeleteAfterCopy.isSelected();
        listenToCheckBox           = true;
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
        sourceDir = dir;
        init();
    }

    /**
     * Call prior <code>setVisible()</code>.
     *
     * @param dir
     */
    public void setTargetDir(File dir) {
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
            sourceDir    = dir;
            filesChoosed = false;
            sourceFiles.clear();
            toSettings(KEY_LAST_SRC_DIR, dir);
            resetLabelChoosedFiles();
            setDirLabel(labelSourceDir, dir);
        }

        setEnabledOkButton();
    }

    private void chooseSourceFiles() {
        JFileChooser      fileChooser = new JFileChooser(sourceDir);
        ImagePreviewPanel imgPanel    = new ImagePreviewPanel();

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
        UserSettings.INSTANCE.getProperties().setProperty(key,
                dir.getAbsolutePath());
        UserSettings.INSTANCE.writeToFile();
    }

    private File chooseDir(File startDir) {
        DirectoryChooser dlg =
            new DirectoryChooser(
                GUI.INSTANCE.getAppFrame(), startDir,
                UserSettings.INSTANCE.getDirChooserOptionShowHiddenDirs());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), null);
        dlg.addWindowListener(new SizeAndLocationController());
        dlg.setVisible(true);

        return dlg.accepted()
               ? dlg.getSelectedDirectories().get(0)
               : null;
    }

    private void setDirLabel(JLabel label, File dir) {
        label.setIcon(fileSystemView.getSystemIcon(dir));
        label.setText(dir.getAbsolutePath());
    }

    private void setFileLabel(File file, boolean multipleFiles) {
        labelChoosedFiles.setIcon(fileSystemView.getSystemIcon(file));
        labelChoosedFiles.setText(StringUtil.getPrefixDotted(file.getName(),
                20) + (multipleFiles
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
            return !sourceFiles.isEmpty()
                   && FileUtil.existsDirectory(targetDir) && dirsDifferent();
        } else {
            return existsBothDirs() && dirsDifferent();
        }
    }

    private boolean dirsDifferent() {
        return !sourceDir.equals(targetDir);
    }

    private boolean existsBothDirs() {
        return FileUtil.existsDirectory(sourceDir)
               && FileUtil.existsDirectory(targetDir);
    }

    private boolean checkDirsDifferent(File src, File tgt) {
        if (src.equals(tgt)) {
            MessageDisplayer.error(this,
                                   "ImportImageFilesDialog.Error.DirsEquals");

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
            if (!MessageDisplayer.confirmYesNo(
                    this, "ImportImageFilesDialog.Confirm.DeleteAfterCopy")) {
                listenToCheckBox = false;
                selected         = false;
                checkBoxDeleteAfterCopy.setSelected(false);
                listenToCheckBox = true;
            }
        }

        deleteSrcFilesAfterCopying = selected;
        UserSettings.INSTANCE.getSettings().set(deleteSrcFilesAfterCopying,
                KEY_DEL_SRC_AFTER_COPY);
        UserSettings.INSTANCE.writeToFile();
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
        labelPromptSourceDir    = new javax.swing.JLabel();
        labelSourceDir          = new javax.swing.JLabel();
        buttonChooseSourceDir   = new javax.swing.JButton();
        labelPromptChooseFiles  = new javax.swing.JLabel();
        labelChoosedFiles       = new javax.swing.JLabel();
        buttonChooseFiles       = new javax.swing.JButton();
        labelPromptTargetDir    = new javax.swing.JLabel();
        labelTargetDir          = new javax.swing.JLabel();
        checkBoxDeleteAfterCopy = new javax.swing.JCheckBox();
        buttonChooseTargetDir   = new javax.swing.JButton();
        buttonCancel            = new javax.swing.JButton();
        buttonOk                = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        setTitle(bundle.getString("ImportImageFilesDialog.title"));    // NOI18N
        labelPromptSourceDir.setText(
            bundle.getString(
                "ImportImageFilesDialog.labelPromptSourceDir.text"));    // NOI18N
        buttonChooseSourceDir.setText(
            bundle.getString(
                "ImportImageFilesDialog.buttonChooseSourceDir.text"));    // NOI18N
        buttonChooseSourceDir.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseSourceDirActionPerformed(evt);
            }
        });
        labelPromptChooseFiles.setText(
            JptBundle.INSTANCE.getString(
                "ImportImageFilesDialog.labelPromptChooseFiles.text"));    // NOI18N
        buttonChooseFiles.setText(
            JptBundle.INSTANCE.getString(
                "ImportImageFilesDialog.buttonChooseFiles.text"));    // NOI18N
        buttonChooseFiles.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseFilesActionPerformed(evt);
            }
        });
        labelPromptTargetDir.setText(
            bundle.getString(
                "ImportImageFilesDialog.labelPromptTargetDir.text"));    // NOI18N
        checkBoxDeleteAfterCopy.setText(
            JptBundle.INSTANCE.getString(
                "ImportImageFilesDialog.checkBoxDeleteAfterCopy.text"));    // NOI18N
        checkBoxDeleteAfterCopy.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDeleteAfterCopyActionPerformed(evt);
            }
        });
        buttonChooseTargetDir.setText(
            bundle.getString(
                "ImportImageFilesDialog.buttonChooseTargetDir.text"));    // NOI18N
        buttonChooseTargetDir.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseTargetDirActionPerformed(evt);
            }
        });
        buttonCancel.setText(
            bundle.getString("ImportImageFilesDialog.buttonCancel.text"));    // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        buttonOk.setText(
            bundle.getString("ImportImageFilesDialog.buttonOk.text"));    // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGap(97, 97, 97).addComponent(
                    labelSourceDir, javax.swing.GroupLayout.DEFAULT_SIZE, 234,
                    Short.MAX_VALUE).addContainerGap(
                        211, Short.MAX_VALUE)).addGroup(
                            layout.createSequentialGroup().addGroup(
                                layout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                    layout.createSequentialGroup().addContainerGap().addGroup(
                                        layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                labelPromptTargetDir).addGroup(
                                                    layout.createSequentialGroup().addComponent(
                                                        labelPromptChooseFiles).addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                labelChoosedFiles,
                                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        161,
                                                                        Short.MAX_VALUE)).addComponent(
                                                                            labelPromptSourceDir).addComponent(
                                                                                checkBoxDeleteAfterCopy))).addGroup(
                                                                                    layout.createSequentialGroup().addGap(
                                                                                        97,
                                                                                        97,
                                                                                        97).addComponent(
                                                                                            labelTargetDir,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                    300,
                                                                                                    Short.MAX_VALUE))).addPreferredGap(
                                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                                                                            layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                                                                                    buttonChooseFiles,
                                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE).addComponent(
                                                                                                                                    buttonChooseSourceDir,
                                                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE).addComponent(
                                                                                                                                                    buttonChooseTargetDir)).addContainerGap()).addGroup(
                                                                                                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                                            layout.createSequentialGroup().addContainerGap(
                                                                                                                                                                306,
                                                                                                                                                                Short.MAX_VALUE).addComponent(
                                                                                                                                                                    buttonCancel).addPreferredGap(
                                                                                                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(buttonOk).addContainerGap()));
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                        new java.awt.Component[] { buttonChooseFiles,
                buttonChooseSourceDir, buttonChooseTargetDir });
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                        new java.awt.Component[] { buttonCancel,
                buttonOk });
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                        buttonChooseSourceDir).addComponent(
                        labelSourceDir).addComponent(
                        labelPromptSourceDir)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                            layout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                buttonChooseFiles).addComponent(
                                labelChoosedFiles).addComponent(
                                labelPromptChooseFiles)).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                    layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                                        buttonChooseTargetDir).addComponent(
                                        labelTargetDir).addComponent(
                                        labelPromptTargetDir)).addGap(
                                            8, 8, 8).addComponent(
                                                checkBoxDeleteAfterCopy).addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                                                        layout.createParallelGroup(
                                                            javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                                buttonOk).addComponent(
                                                                    buttonCancel)).addContainerGap(
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                            Short.MAX_VALUE)));
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { labelPromptSourceDir,
                labelPromptTargetDir, labelSourceDir, labelTargetDir });
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { buttonChooseFiles,
                buttonChooseSourceDir, buttonChooseTargetDir });
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { labelChoosedFiles,
                labelPromptChooseFiles });
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setAccepted(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        setAccepted(true);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonChooseSourceDirActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseSourceDirActionPerformed
        chooseSourceDir();
    }//GEN-LAST:event_buttonChooseSourceDirActionPerformed

    private void buttonChooseTargetDirActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseTargetDirActionPerformed
        chooseTargetDir();
    }//GEN-LAST:event_buttonChooseTargetDirActionPerformed

    private void buttonChooseFilesActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseFilesActionPerformed
        chooseSourceFiles();
    }//GEN-LAST:event_buttonChooseFilesActionPerformed

    private void checkBoxDeleteAfterCopyActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDeleteAfterCopyActionPerformed
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
    private javax.swing.JButton   buttonCancel;
    private javax.swing.JButton   buttonChooseFiles;
    private javax.swing.JButton   buttonChooseSourceDir;
    private javax.swing.JButton   buttonChooseTargetDir;
    private javax.swing.JButton   buttonOk;
    private javax.swing.JCheckBox checkBoxDeleteAfterCopy;
    private javax.swing.JLabel    labelChoosedFiles;
    private javax.swing.JLabel    labelPromptChooseFiles;
    private javax.swing.JLabel    labelPromptSourceDir;
    private javax.swing.JLabel    labelPromptTargetDir;
    private javax.swing.JLabel    labelSourceDir;
    private javax.swing.JLabel    labelTargetDir;

    // End of variables declaration//GEN-END:variables
}
