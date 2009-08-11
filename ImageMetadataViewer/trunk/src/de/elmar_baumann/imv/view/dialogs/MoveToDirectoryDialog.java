package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.event.FileSystemEvent;
import de.elmar_baumann.imv.event.listener.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.io.FileSystemMove;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-20
 */
public final class MoveToDirectoryDialog extends Dialog
        implements ProgressListener, FileSystemActionListener {

    private static final String KEY_TARGET_DIRECTORY =
            "de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog.TargetDirectory"; // NOI18N
    private final List<File> movedFiles = new ArrayList<File>();
    private final List<ProgressListener> progressListeners =
            new ArrayList<ProgressListener>();
    private FileSystemMove moveTask;
    private boolean runs = false;
    private boolean stop = false;
    private boolean errors = false;
    private List<File> sourceFiles;
    private File targetDirectory = new File(""); // NOI18N
    private boolean moveIfVisible = false;

    public MoveToDirectoryDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        setIconImages(AppIcons.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
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
        if (runs) {
            MessageDisplayer.error(this,
                    "MoveToDirectoryDialog.Error.AbortBeforeClose"); // NOI18N
        } else {
            setVisible(false);
        }
    }

    private void checkErrors() {
        if (errors) {
            MessageDisplayer.error(this,
                    "MoveToDirectoryDialog.Error.CheckLogfile"); // NOI18N
        }
    }

    private void addXmpFiles() {
        List<File> xmpFiles = new ArrayList<File>();
        for (File sourceFile : sourceFiles) {
            String xmpFilename =
                    XmpMetadata.getSidecarFilenameOfImageFileIfExists(
                    sourceFile.getAbsolutePath());
            if (xmpFilename != null) {
                xmpFiles.add(new File(xmpFilename));
            }
        }
        sourceFiles.addAll(xmpFiles);
    }

    private void reset() {
        runs = false;
        stop = false;
        errors = false;
        movedFiles.clear();
    }

    private synchronized void start() {
        reset();
        moveTask = new FileSystemMove(sourceFiles, targetDirectory);
        addListenerToMoveTask();
        Thread thread = new Thread(moveTask);
        thread.setName("Moving files to directory " + targetDirectory. // NOI18N
                getAbsolutePath() + " @ " + getClass().getName()); // NOI18N
        thread.start();
        runs = true;
    }

    private synchronized void addListenerToMoveTask() {
        moveTask.addActionListener(this);
        moveTask.addProgressListener(this);
        List<FileSystemActionListener> listeners = ListenerProvider.INSTANCE.
                getFileSystemActionListener();
        for (FileSystemActionListener listener : listeners) {
            moveTask.addActionListener(listener);
        }
    }

    private void stop() {
        stop = true;
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
                    labelDirectoryName.setText(targetDirectory.getAbsolutePath());
                    setIconToLabelTargetDirectory();
                    buttonStart.setEnabled(true);
                } else {
                    MessageDisplayer.error(this,
                            "MoveToDirectoryDialog.TargetDirNotWritable", // NOI18N
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
            readProperties();
            if (moveIfVisible) {
                start();
            } else {
                setTargetDirectory();
            }
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void setTargetDirectory() {
        targetDirectory =
                new File(UserSettings.INSTANCE.getSettings().getString(
                KEY_TARGET_DIRECTORY));
        if (targetDirectory.exists()) {
            labelDirectoryName.setText(targetDirectory.getAbsolutePath());
            setIconToLabelTargetDirectory();
            buttonStart.setEnabled(true);
        }
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setString(targetDirectory.
                getAbsolutePath(), KEY_TARGET_DIRECTORY);
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
        notifyProgressListenerStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        String filename = ((Pair<File, File>) evt.getInfo()).getFirst().
                getAbsolutePath();
        labelCurrentFilename.setText(filename);
        checkStopEvent(evt);
        notifyProgressListenerPerformed(evt);
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
        notifyProgressListenerEnded(evt);
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
    protected void help() {
        help(Bundle.getString("Help.Url.MoveToDirectoryDialog")); // NOI18N
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    @Override
    public void actionPerformed(FileSystemEvent action, File src, File target) {
        movedFiles.add(src);
    }

    @Override
    public void actionFailed(FileSystemEvent action, FileSystemError error,
            File src, File target) {
        AppLog.logWarning(MoveToDirectoryDialog.class, Bundle.getString(
                "MoveToDirectoryDialog.Error.Logfile", src, target, // NOI18N
                error.getLocalizedMessage()));
        errors = true;
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

        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("MoveToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle"); // NOI18N
        labelInfoCurrentFilename.setText(bundle.getString("MoveToDirectoryDialog.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));

        labelInfoIsThread.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoIsThread.setText(Bundle.getString("MoveToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("MoveToDirectoryDialog.buttonCancelCopy.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setMnemonic('s');
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(buttonChooseDirectory))
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)))
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
