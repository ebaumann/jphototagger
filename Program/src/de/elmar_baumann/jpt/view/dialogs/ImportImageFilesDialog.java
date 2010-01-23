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

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-23
 */
public class ImportImageFilesDialog extends Dialog {

    private static final long           serialVersionUID    = -8291157139781240235L;
    private static final String         KEY_LAST_SRC_DIR    = "ImportImageFiles.LastSrcDir";
    private static final String         KEY_LAST_TARGET_DIR = "ImportImageFiles.LastTargetDir";
    private final        FileSystemView fileSystemView      = FileSystemView.getFileSystemView();
    private              File           sourceDir           = new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_SRC_DIR));
    private              File           targetDir           = new File(UserSettings.INSTANCE.getSettings().getString(KEY_LAST_TARGET_DIR));
    private              boolean        accepted;

    public ImportImageFilesDialog() {
        super(GUI.INSTANCE.getAppFrame(), true, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        init();
    }

    private void init() {
        if (FileUtil.existsDirectory(sourceDir)) {
            setLabel(labelSourceDir, sourceDir);
        }
        if (dirsValid()) {
            setLabel(labelTargetDir, targetDir);
            buttonOk.setEnabled(true);
        }
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

    private void chooseSrcDir() {
        File dir = chooseDir(sourceDir);
        
        if (dir == null) return;
        if (!targetDir.exists() || checkDirsDifferent(dir, targetDir)) {
            sourceDir = dir;
            toSettings(KEY_LAST_SRC_DIR, dir);
            setLabel(labelSourceDir, dir);
        }
        setEnabledOkButton();
    }

    private void chooseTargetDir() {
        File dir = chooseDir(targetDir);

        if (dir == null) return;
        if (!sourceDir.exists() || checkDirsDifferent(sourceDir, dir)) {
            targetDir = dir;
            toSettings(KEY_LAST_TARGET_DIR, dir);
            setLabel(labelTargetDir, dir);
        }
        setEnabledOkButton();
    }

    private void toSettings(String key, File dir) {
        UserSettings.INSTANCE.getProperties().setProperty(key, dir.getAbsolutePath());
        UserSettings.INSTANCE.writeToFile();
    }

    private File chooseDir(File startDir) {
        DirectoryChooser dlg = new DirectoryChooser(GUI.INSTANCE.getAppFrame(), startDir, UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());

        dlg.setSettings(UserSettings.INSTANCE.getSettings(), null);
        dlg.setVisible(true);

        return dlg.accepted() ? dlg.getSelectedDirectories().get(0) : null;
    }

    private void setLabel(JLabel label, File dir) {
        label.setIcon(fileSystemView.getSystemIcon(dir));
        label.setText(dir.getAbsolutePath());
    }
    
    private void setEnabledOkButton() {
        buttonOk.setEnabled(dirsValid());
    }
    
    private boolean dirsValid() {
        return existsBothDirs() && dirsDifferent();
    }
    
    private boolean dirsDifferent() {
        return !sourceDir.equals(targetDir);
    }

    private boolean existsBothDirs() {
        return FileUtil.existsDirectory(sourceDir) && FileUtil.existsDirectory(targetDir);
    }

    private boolean checkDirsDifferent(File src, File tgt) {
        if (src.equals(tgt)) {
            MessageDisplayer.error(this, "ImportImageFilesDialog.Error.DirsEquals");
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelPromptSourceDir = new javax.swing.JLabel();
        labelSourceDir = new javax.swing.JLabel();
        labelPromptTargetDir = new javax.swing.JLabel();
        labelTargetDir = new javax.swing.JLabel();
        buttonChooseSourceDir = new javax.swing.JButton();
        buttonChooseTargetDir = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        setTitle(bundle.getString("ImportImageFilesDialog.title")); // NOI18N

        labelPromptSourceDir.setText(bundle.getString("ImportImageFilesDialog.labelPromptSourceDir.text")); // NOI18N

        labelPromptTargetDir.setText(bundle.getString("ImportImageFilesDialog.labelPromptTargetDir.text")); // NOI18N

        buttonChooseSourceDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseSourceDir.text")); // NOI18N
        buttonChooseSourceDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseSourceDirActionPerformed(evt);
            }
        });

        buttonChooseTargetDir.setText(bundle.getString("ImportImageFilesDialog.buttonChooseTargetDir.text")); // NOI18N
        buttonChooseTargetDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseTargetDirActionPerformed(evt);
            }
        });

        buttonCancel.setText(bundle.getString("ImportImageFilesDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonOk.setText(bundle.getString("ImportImageFilesDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelPromptTargetDir)
                                .addGap(177, 177, 177))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(labelTargetDir, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelPromptSourceDir)
                                .addGap(177, 177, 177))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addComponent(labelSourceDir, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(buttonChooseSourceDir)
                            .addComponent(buttonChooseTargetDir)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonOk)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonChooseSourceDir, buttonChooseTargetDir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonCancel, buttonOk});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(labelPromptSourceDir)
                    .addComponent(labelSourceDir)
                    .addComponent(buttonChooseSourceDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(buttonChooseTargetDir)
                    .addComponent(labelTargetDir)
                    .addComponent(labelPromptTargetDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonOk)
                    .addComponent(buttonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelPromptSourceDir, labelPromptTargetDir, labelSourceDir, labelTargetDir});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setAccepted(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        setAccepted(true);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonChooseSourceDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseSourceDirActionPerformed
        chooseSrcDir();
    }//GEN-LAST:event_buttonChooseSourceDirActionPerformed

    private void buttonChooseTargetDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseTargetDirActionPerformed
        chooseTargetDir();
    }//GEN-LAST:event_buttonChooseTargetDirActionPerformed

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
    private javax.swing.JButton buttonChooseSourceDir;
    private javax.swing.JButton buttonChooseTargetDir;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelPromptSourceDir;
    private javax.swing.JLabel labelPromptTargetDir;
    private javax.swing.JLabel labelSourceDir;
    private javax.swing.JLabel labelTargetDir;
    // End of variables declaration//GEN-END:variables

}
