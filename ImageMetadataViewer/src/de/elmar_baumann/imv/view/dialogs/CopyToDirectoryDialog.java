package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.CopyFiles;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.template.Pair;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class CopyToDirectoryDialog extends Dialog
    implements ProgressListener {

    private static final String keyLastDirectory = "de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog.LastDirectory"; // NOI18N
    private CopyFiles copyTask;
    private boolean copy = false;
    private List<File> sourceFiles;
    private String lastDirectory = ""; // NOI18N

    /** Creates new form CopyToDirectoryDialog */
    public CopyToDirectoryDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        setIconImages(AppSettings.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    private void checkClosing() {
        if (copy) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.AbortBeforeClose"),
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.AbortBeforeClose.Title"),
                JOptionPane.INFORMATION_MESSAGE,
                AppSettings.getMediumAppIcon());
        } else {
            setVisible(false);
        }
    }

    private void checkError(List<String> errorFiles) {
        if (errorFiles.size() > 0) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.CopyErrorsOccured"),
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.CopyErrorsOccured.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
        }
    }

    private void startCopy() {
        copyTask = new CopyFiles();
        copyTask.setFiles(getFiles());
        copyTask.addProgressListener(this);
        copyTask.setForceOverwrite(checkBoxForceOverwrite.isSelected());
        Thread thread = new Thread(copyTask);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
    }

    private List<Pair<File, File>> getFiles() {
        String directory = labelDirectoryName.getText().trim();
        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();
        for (File sourceFile : sourceFiles) {
            File targetFile = new File(directory + File.separator + sourceFile.getName());
            filePairs.add(new Pair<File, File>(sourceFile, targetFile));
        }
        return filePairs;
    }

    private void cancelCopy() {
        copyTask.stop();
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        dialog.setStartDirectory(new File(lastDirectory));
        dialog.setMultiSelection(false);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            if (files.size() > 0) {
                String directoryName = files.get(0).getAbsolutePath();
                labelDirectoryName.setText(directoryName);
                lastDirectory = directoryName;
                buttonStartCopy.setEnabled(true);
            }
        } else {
            String directoryName = labelDirectoryName.getText().trim();
            if (directoryName.isEmpty() || !FileUtil.existsDirectory(directoryName)) {
                buttonStartCopy.setEnabled(false);
            }
        }
    }

    /**
     * Setzt die zu kopierenden Quelldateien.
     * 
     * @param sourceFiles  Quelldateien
     */
    public void setSourceFiles(List<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentAppSizes.getSizeAndLocation(this);
            lastDirectory = PersistentSettings.getInstance().getString(keyLastDirectory);
            if (FileUtil.existsDirectory(lastDirectory)) {
                labelDirectoryName.setText(lastDirectory);
                buttonStartCopy.setEnabled(true);
            }
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setString(lastDirectory, keyLastDirectory);
        }
        super.setVisible(visible);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        copy = true;
        buttonStartCopy.setEnabled(false);
        buttonCancelCopy.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        String filename = ((Pair<File, File>) evt.getInfo()).getFirst().getAbsolutePath();
        labelCurrentFilename.setText(filename);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        List<String> errorFiles = (List<String>) evt.getInfo();
        checkError(errorFiles);
        buttonCancelCopy.setEnabled(false);
        buttonStartCopy.setEnabled(true);
        copy = false;
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.CopyToDirectoryDialog"));
    }

    @Override
    protected void escape() {
        checkClosing();
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
        labelDirectoryName = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        checkBoxForceOverwrite = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        buttonStartCopy = new javax.swing.JButton();
        buttonCancelCopy = new javax.swing.JButton();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("CopyToDirectoryDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfo.setText(Bundle.getString("CopyToDirectoryDialog.labelInfo.text")); // NOI18N

        labelDirectoryName.setFont(new java.awt.Font("Dialog", 0, 11));
        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("CopyToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        checkBoxForceOverwrite.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxForceOverwrite.setMnemonic('x');
        checkBoxForceOverwrite.setText(Bundle.getString("CopyToDirectoryDialog.checkBoxForceOverwrite.text")); // NOI18N

        buttonStartCopy.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonStartCopy.setMnemonic('s');
        buttonStartCopy.setText(Bundle.getString("CopyToDirectoryDialog.buttonStartCopy.text")); // NOI18N
        buttonStartCopy.setEnabled(false);
        buttonStartCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartCopyActionPerformed(evt);
            }
        });

        buttonCancelCopy.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonCancelCopy.setMnemonic('o');
        buttonCancelCopy.setText(Bundle.getString("CopyToDirectoryDialog.buttonCancelCopy.text")); // NOI18N
        buttonCancelCopy.setEnabled(false);
        buttonCancelCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelCopyActionPerformed(evt);
            }
        });

        labelCurrentFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFilename.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));

        labelInfoIsThread.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoIsThread.setText(Bundle.getString("CopyToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfo)
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBoxForceOverwrite)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                        .addComponent(buttonCancelCopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStartCopy)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelDirectoryName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkBoxForceOverwrite)
                    .addComponent(buttonChooseDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCurrentFilename)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfoIsThread)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonStartCopy)
                        .addComponent(buttonCancelCopy)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelCurrentFilename, labelInfo});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonStartCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartCopyActionPerformed
    startCopy();
}//GEN-LAST:event_buttonStartCopyActionPerformed

private void buttonCancelCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelCopyActionPerformed
    cancelCopy();
}//GEN-LAST:event_buttonCancelCopyActionPerformed

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
    private javax.swing.JButton buttonCancelCopy;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStartCopy;
    private javax.swing.JCheckBox checkBoxForceOverwrite;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelDirectoryName;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
