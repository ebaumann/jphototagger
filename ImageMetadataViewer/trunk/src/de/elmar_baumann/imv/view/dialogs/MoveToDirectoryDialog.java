package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.FileSystemAction;
import de.elmar_baumann.imv.event.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.io.FileSystemMove;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import de.elmar_baumann.lib.template.Pair;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/20
 */
public final class MoveToDirectoryDialog extends Dialog
    implements ProgressListener, FileSystemActionListener {

    private static final String keyTargetDirectory = "de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog.TargetDirectory"; // NOI18N
    private final List<File> movedFiles = new ArrayList<File>();
    private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
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
        setIconImages(AppSettings.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
        registerKeyStrokes();
    }

    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }
    
    private void notifyProgressListenerStarted(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressStarted(evt);
        }
    }

    private void notifyProgressListenerPerformed(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressPerformed(evt);
        }
    }

    private void notifyProgressListenerEnded(ProgressEvent evt) {
        for (ProgressListener listener : progressListeners) {
            listener.progressEnded(evt);
        }
    }

    private void checkClosing() {
        if (runs) {
            JOptionPane.showMessageDialog(
                null,
                Bundle.getString("MoveToDirectoryDialog.ErrorMessage.AbortBeforeClose"),
                Bundle.getString("MoveToDirectoryDialog.ErrorMessage.AbortBeforeClose.Title"),
                JOptionPane.INFORMATION_MESSAGE,
                AppSettings.getMediumAppIcon());
        } else {
            setVisible(false);
        }
    }

    private void checkErrors() {
        if (errors) {
            JOptionPane.showMessageDialog(
                this,
                Bundle.getString("MoveToDirectoryDialog.ErrorMessage.CheckLogfile"),
                Bundle.getString("MoveToDirectoryDialog.ErrorMessage.CheckLogfile.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppSettings.getMediumAppIcon());
        }
    }

    private void addXmpFiles() {
        List<File> xmpFiles = new ArrayList<File>();
        for (File sourceFile : sourceFiles) {
            String xmpFilename =
                XmpMetadata.getSidecarFilename(sourceFile.getAbsolutePath());
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

    synchronized private void start() {
        reset();
        moveTask = new FileSystemMove(sourceFiles, targetDirectory);
        addListenerToMoveTask();
        Thread thread = new Thread(moveTask);
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        thread.start();
        runs = true;
    }

    private void addListenerToMoveTask() {
        moveTask.addActionListener(this);
        moveTask.addProgressListener(this);
        List<FileSystemActionListener> listeners = ListenerProvider.getInstance().getFileSystemActionListener();
        for (FileSystemActionListener listener : listeners) {
            moveTask.addActionListener(listener);
        }
    }

    private void stop() {
        stop = true;
    }

    private void chooseTargetDirectory() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        ViewUtil.setDirectoryTreeModel(dialog);
        dialog.setStartDirectory(targetDirectory);
        dialog.setMultiSelection(false);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> files = dialog.getSelectedDirectories();
            if (files.size() > 0) {
                targetDirectory = files.get(0);
                labelDirectoryName.setText(targetDirectory.getAbsolutePath());
                buttonStart.setEnabled(true);
            }
        } else {
            String directoryName = labelDirectoryName.getText().trim();
            if (directoryName.isEmpty() || !FileUtil.existsDirectory(directoryName)) {
                buttonStart.setEnabled(false);
            }
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
            PersistentAppSizes.getSizeAndLocation(this);
            if (moveIfVisible) {
                start();
            } else {
                targetDirectory = new File(PersistentSettings.getInstance().getString(keyTargetDirectory));
                if (targetDirectory.exists()) {
                    labelDirectoryName.setText(targetDirectory.getAbsolutePath());
                    buttonStart.setEnabled(true);
                }
            }
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
            PersistentSettings.getInstance().setString(targetDirectory.getAbsolutePath(), keyTargetDirectory);
        }
        super.setVisible(visible);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
        evt.setStop(stop);
        notifyProgressListenerStarted(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        @SuppressWarnings("unchecked")
        String filename = ((Pair<File, File>) evt.getInfo()).getFirst().getAbsolutePath();
        labelCurrentFilename.setText(filename);
        evt.setStop(stop);
        notifyProgressListenerPerformed(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        buttonStop.setEnabled(true);
        buttonStart.setEnabled(true);
        runs = false;
        moveTask = null;
        Panels.getInstance().getAppPanel().getPanelThumbnails().remove(movedFiles);
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
        help(Bundle.getString("Help.Url.MoveToDirectoryDialog"));
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    @Override
    public void actionPerformed(FileSystemAction action, File src, File target) {
        movedFiles.add(src);
    }

    @Override
    public void actionFailed(FileSystemAction action, FileSystemError error, File src, File target) {
        MessageFormat msg = new MessageFormat(Bundle.getString("MoveToDirectoryDialog.ErrorMessage.Logfile"));
        Object[] params = {src, target, error.getLocalizedMessage()};
        String errorMsg = msg.format(params);
        Log.logWarning(MoveToDirectoryDialog.class, errorMsg);
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
        labelDirectoryName = new javax.swing.JLabel();
        buttonChooseDirectory = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        buttonStart = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        labelCurrentFilename = new javax.swing.JLabel();
        labelInfoIsThread = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("MoveToDirectoryDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfo.setText(Bundle.getString("MoveToDirectoryDialog.labelInfo.text")); // NOI18N

        labelDirectoryName.setFont(new java.awt.Font("Dialog", 0, 11));
        labelDirectoryName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('a');
        buttonChooseDirectory.setText(Bundle.getString("MoveToDirectoryDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonStart.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonStart.setMnemonic('s');
        buttonStart.setText(Bundle.getString("MoveToDirectoryDialog.buttonStartCopy.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        buttonStop.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("MoveToDirectoryDialog.buttonCancelCopy.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        labelCurrentFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCurrentFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFilename.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(225, 225, 225)));

        labelInfoIsThread.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfoIsThread.setText(Bundle.getString("MoveToDirectoryDialog.labelInfoIsThread.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfo)
                    .addComponent(labelDirectoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(buttonChooseDirectory, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoIsThread)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
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
                .addComponent(buttonChooseDirectory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCurrentFilename)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfoIsThread)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonStart)
                        .addComponent(buttonStop)))
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
    private javax.swing.JLabel labelInfoIsThread;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}
