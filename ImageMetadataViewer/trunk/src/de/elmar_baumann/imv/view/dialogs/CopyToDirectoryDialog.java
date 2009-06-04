package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.CopyFiles;
import de.elmar_baumann.imv.tasks.CopyFiles.Options;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class CopyToDirectoryDialog extends Dialog
    implements ProgressListener {

    private static final String keyLastDirectory = "de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog.LastDirectory"; // NOI18N
    private static final String keyCopyXmp = "CopyToDirectoryDialog.CopyXmp"; // NOI18N
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private CopyFiles copyTask;
    private boolean copy = false;
    private List<File> sourceFiles;
    private String lastDirectory = ""; // NOI18N
    private boolean copyIfVisible = false;

    /** Creates new form CopyToDirectoryDialog */
    public CopyToDirectoryDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        setIconImages(AppIcons.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    private synchronized void notifyProgressListenerStarted(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    private synchronized void notifyProgressListenerPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    private synchronized void notifyProgressListenerEnded(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    private void checkClosing() {
        if (copy) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.AbortBeforeClose"),
                Bundle.getString("CopyToDirectoryDialog.ErrorMessage.AbortBeforeClose.Title"),
                JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void start() {
        copyTask = new CopyFiles(getFiles(), getCopyOptions());
        copyTask.addProgressListener(this);
        Thread thread = new Thread(copyTask);
        thread.setPriority(UserSettings.INSTANCE.getThreadPriority());
        thread.setName("CopyToDirectoryDialog#start"); // NOI18N
        thread.start();
    }

    private Options getCopyOptions() {
        return checkBoxForceOverwrite.isSelected()
            ? CopyFiles.Options.FORCE_OVERWRITE
            : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    private List<Pair<File, File>> getFiles() {
        String targetDirectory = labelTargetDirectory.getText().trim();
        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();
        for (File sourceFile : sourceFiles) {
            File targetFile = new File(targetDirectory + File.separator + sourceFile.getName());
            filePairs.add(new Pair<File, File>(sourceFile, targetFile));
            addXmp(sourceFile, filePairs);
        }
        return filePairs;
    }

    private void addXmp(File sourceFile, List<Pair<File, File>> filePairs) {
        if (checkBoxCopyXmp.isSelected()) {
            String targetDirectory = labelTargetDirectory.getText().trim();
            String sidecarFilename =
                XmpMetadata.getSidecarFilename(sourceFile.getAbsolutePath());
            if (sidecarFilename != null) {
                File sourceSidecarFile = new File(sidecarFilename);
                File targetSidecarFile = new File(targetDirectory + File.separator + sourceSidecarFile.getName());
                filePairs.add(new Pair<File, File>(sourceSidecarFile, targetSidecarFile));
            }
        }
    }

    private void stop() {
        copyTask.stop();
        setVisible(false);
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, new File(lastDirectory), UserSettings.INSTANCE.getDefaultDirectoryChooserOptions());
        ViewUtil.setDirectoryTreeModel(dialog);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            if (files.size() > 0) {
                String directoryName = files.get(0).getAbsolutePath();
                labelTargetDirectory.setText(directoryName);
                lastDirectory = directoryName;
                buttonStart.setEnabled(true);
            }
        } else {
            String directoryName = labelTargetDirectory.getText().trim();
            if (directoryName.isEmpty() || !FileUtil.existsDirectory(directoryName)) {
                buttonStart.setEnabled(false);
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

    /**
     * Sets the target directory. If it exists copying is done after calling
     * {@link #setVisible(boolean)}  with <code>true</code> as argument whitout
     * user interaction.
     * 
     * @param directory  target directory
     */
    public void setTargetDirectory(File directory) {
        if (directory.exists()) {
            labelTargetDirectory.setText(directory.getAbsolutePath());
            buttonChooseDirectory.setEnabled(false);
            buttonStart.setEnabled(false);
            checkBoxCopyXmp.setSelected(true);
            checkBoxForceOverwrite.setSelected(false);
            copyIfVisible = true;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
            if (copyIfVisible) {
                start();
            } else {
                setLastDirectory();
            }
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().getCheckBox(checkBoxCopyXmp, keyCopyXmp);
        String dir = UserSettings.INSTANCE.getSettings().getString(keyLastDirectory);
        if (FileUtil.existsDirectory(dir)) {
            lastDirectory = dir;
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setString(lastDirectory, keyLastDirectory);
        UserSettings.INSTANCE.getSettings().setCheckBox(checkBoxCopyXmp, keyCopyXmp);
    }

    private void setLastDirectory() {
        if (FileUtil.existsDirectory(lastDirectory)) {
            labelTargetDirectory.setText(lastDirectory);
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
        String filename = ((Pair<File, File>) evt.getInfo()).getFirst().getAbsolutePath();
        labelCurrentFilename.setText(filename);
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
        buttonChooseDirectory = new javax.swing.JButton();
        labelTargetDirectory = new javax.swing.JLabel();
        checkBoxForceOverwrite = new javax.swing.JCheckBox();
        checkBoxCopyXmp = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
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

        checkBoxForceOverwrite.setMnemonic('x');
        checkBoxForceOverwrite.setText(Bundle.getString("CopyToDirectoryDialog.checkBoxForceOverwrite.text")); // NOI18N

        checkBoxCopyXmp.setMnemonic('x');
        checkBoxCopyXmp.setSelected(true);
        checkBoxCopyXmp.setText(Bundle.getString("CopyToDirectoryDialog.checkBoxCopyXmp.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFilename.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelTargetDirectory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                    .addComponent(checkBoxCopyXmp, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxForceOverwrite, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory)))
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
                .addComponent(checkBoxForceOverwrite)
                .addGap(2, 2, 2)
                .addComponent(checkBoxCopyXmp)
                .addGap(4, 4, 4)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCurrentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoIsThread)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonStart, buttonStop, progressBar});

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
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxCopyXmp;
    private javax.swing.JCheckBox checkBoxForceOverwrite;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JLabel labelTargetDirectory;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables
}
