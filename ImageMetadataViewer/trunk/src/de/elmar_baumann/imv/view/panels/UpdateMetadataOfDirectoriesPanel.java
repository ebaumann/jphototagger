package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.io.DirectoryInfo;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imv.types.DatabaseUpdate;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class UpdateMetadataOfDirectoriesPanel extends javax.swing.JPanel
    implements ProgressListener {

    private static final String keyLastDirectory = "de.elmar_baumann.imv.view.ScanDirectoriesDialog.lastSelectedDirectory"; // NOI18N
    private static final String keyForce = "de.elmar_baumann.imv.view.ScanDirectoriesDialog.force"; // NOI18N
    private static final String keySubdirectories = "de.elmar_baumann.imv.view.ScanDirectoriesDialog.subdirectories"; // NOI18N
    private static final String currentFilenameInfotextPrefix = Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.UpdateCurrentFile");
    private final DefaultListModel modelSelectedDirectoryList = new DefaultListModel();
    private List<File> selectedFiles = new ArrayList<File>();
    private ImageMetadataToDatabase activeUpdater;
    private File lastSelectedDirectory = new File(""); // NOI18N
    private int countSelectedFiles = 0;

    /** Creates new form UpdateMetadataOfDirectoriesPanel */
    public UpdateMetadataOfDirectoriesPanel() {
        initComponents();
        readPersistent();
    }

    public void willDispose() {
        stop();
        writePersistent();
        PersistentSettings.getInstance().setString(
            lastSelectedDirectory.getAbsolutePath(), keyLastDirectory);
    }

    private void chooseDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
        ViewUtil.setDirectoryTreeModel(dialog);
        dialog.setStartDirectory(lastSelectedDirectory);
        dialog.setMultiSelection(true);
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> newDirectories = getNotAlreadyChoosenDirectoriesFrom(
                dialog.getSelectedDirectories());
            if (newDirectories.size() > 0) {
                empty();
                addDirectories(newDirectories);
                setFileCountInfo();
                setLastDirectory();
                buttonStart.setEnabled(listSelectedDirectories.getModel().getSize() > 0);
            }
        }
    }

    private void createScanner() {
        activeUpdater =
            new ImageMetadataToDatabase(
            FileUtil.getAsFilenames(selectedFiles), getDatabaseUpdateMethod());
    }

    private DatabaseUpdate getDatabaseUpdateMethod() {
        return checkBoxForce.isSelected()
            ? DatabaseUpdate.COMPLETE
            : DatabaseUpdate.IF_LAST_MODIFIED_CHANGED;
    }

    private List<File> getAllImageFiles() {
        List<File> imageFiles = new ArrayList<File>();
        Object[] elements = modelSelectedDirectoryList.toArray();
        if (elements != null) {
            for (int index = 0; index < elements.length; index++) {
                if (elements[index] instanceof DirectoryInfo) {
                    DirectoryInfo directoryInfo =
                        (DirectoryInfo) elements[index];
                    imageFiles.addAll(directoryInfo.getImageFiles());
                }
            }
        }
        return imageFiles;
    }

    private List<File> getNotAlreadyChoosenDirectoriesFrom(
        List<File> directories) {
        List<File> newDirectories = new ArrayList<File>();
        for (File directory : directories) {
            if (!modelSelectedDirectoryList.contains(directory)) {
                newDirectories.add(directory);
            }
        }
        return newDirectories;
    }

    private void messageThreadPriority(Thread thread) {
        MessageFormat message = new MessageFormat(Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.ThreadPriority"));
        Object[] params = {thread.getPriority()};
        Log.logFinest(UpdateMetadataOfDirectoriesPanel.class, message.format(params));
    }

    private void readPersistent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.getCheckBox(checkBoxForce, keyForce);
        settings.getCheckBox(checkBoxIncludeSubdirectories, keySubdirectories);
        readPersistentCurrentDirectory();
    }

    private void removeSelectedDirectories() {
        Object[] selectedObjects = listSelectedDirectories.getSelectedValues();
        if (selectedObjects != null) {
            for (int index = 0; index < selectedObjects.length; index++) {
                Object selectedObject = selectedObjects[index];
                if (selectedObject instanceof DirectoryInfo) {
                    DirectoryInfo directoryInfo = (DirectoryInfo) selectedObject;
                    modelSelectedDirectoryList.removeElement(directoryInfo);
                    countSelectedFiles -= directoryInfo.getImageFileCount();
                }
            }
            buttonStart.setEnabled(!modelSelectedDirectoryList.isEmpty());
            setLastDirectory();
            setFileCountInfo();
        }
    }

    private void empty() {
        selectedFiles.clear();
        modelSelectedDirectoryList.removeAllElements();
        listSelectedDirectories.setEnabled(true);
        countSelectedFiles = 0;
        labelCountSelectedFiles.setText(" "); // NOI18N
    }

    private void start() {
        setImageFilenames();
        beforeUpdate();
        createScanner();
        activeUpdater.addProgressListener(this);
        Thread thread = new Thread(activeUpdater);
        setThreadPriority(thread);
        thread.start();
    }

    private void cancel() {
        stop();
        zeroProgressBar();
        afterUpdate();
    }

    private void setFileCountInfo() {
        MessageFormat message = new MessageFormat(Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.SelectedFileCount"));
        Object[] params = {countSelectedFiles};
        labelCountSelectedFiles.setText(message.format(params));
    }

    private void afterUpdate() {
        setEnabledButtons(false);
        setEnabledCheckboxes(false);
        labelCurrentFilename.setText(" "); // NOI18N
        listSelectedDirectories.setEnabled(true);
    }

    private void beforeUpdate() {
        setEnabledButtons(true);
        setEnabledCheckboxes(true);
        setProgressBarForScan();
        listSelectedDirectories.setEnabled(false);
    }

    private void setImageFilenames() {
        selectedFiles = getAllImageFiles();
    }

    private void setLastDirectory() {
        if (!modelSelectedDirectoryList.isEmpty()) {
            Object object = modelSelectedDirectoryList.elementAt(
                modelSelectedDirectoryList.getSize() - 1);
            if (object instanceof DirectoryInfo) {
                DirectoryInfo directoryInfo = (DirectoryInfo) object;
                lastSelectedDirectory = directoryInfo.getDirectory();
            }
        }
    }

    private void setProgressBarForScan() {
        zeroProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(selectedFiles.size());
    }

    private void addDirectories(List<File> directories) {
        for (File directory : directories) {
            DirectoryInfo directoryInfo = new DirectoryInfo(directory);
            if (directoryInfo.hasImageFiles()) {
                modelSelectedDirectoryList.addElement(directoryInfo);
                countSelectedFiles += directoryInfo.getImageFileCount();
            }
            if (checkBoxIncludeSubdirectories.isSelected()) {
                addSubdirectories(directory);
            }
        }
    }

    private void addSubdirectories(File directory) {
        List<File> subdirectories = FileUtil.getAllSubDirectories(directory, UserSettings.getInstance().isAcceptHiddenDirectories());
        for (File dir : subdirectories) {
            DirectoryInfo directoryInfo = new DirectoryInfo(dir);
            if (directoryInfo.hasImageFiles()) {
                modelSelectedDirectoryList.addElement(directoryInfo);
                countSelectedFiles += directoryInfo.getImageFileCount();
            }
        }
    }

    private void setThreadPriority(Thread thread) {
        thread.setPriority(UserSettings.getInstance().getThreadPriority());
        messageThreadPriority(thread);
    }

    private void zeroProgressBar() {
        progressBar.setValue(0);
    }

    private synchronized void stop() {
        if (activeUpdater != null) {
            activeUpdater.removeProgressListener(this);
            activeUpdater.stop();
            activeUpdater = null;
        }
    }

    private void readPersistentCurrentDirectory() {
        String currentDirectoryname =
            PersistentSettings.getInstance().getString(keyLastDirectory);
        if (!currentDirectoryname.isEmpty()) {
            File directory = new File(currentDirectoryname);
            if (directory.exists() && directory.isDirectory()) {
                lastSelectedDirectory = directory;
            }

        }
    }

    private void setEnabledButtons(boolean willCreateThumbnails) {
        buttonChooseDirectory.setEnabled(!willCreateThumbnails);
        buttonStart.setEnabled(!willCreateThumbnails);
        buttonStop.setEnabled(willCreateThumbnails);
    }

    private void setEnabledCheckboxes(boolean willCreateThumbnails) {
        checkBoxForce.setEnabled(!willCreateThumbnails);
        checkBoxIncludeSubdirectories.setEnabled(!willCreateThumbnails);
    }

    private void writePersistent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.setCheckBox(checkBoxForce, keyForce);
        settings.setCheckBox(checkBoxIncludeSubdirectories, keySubdirectories);
        PersistentAppSizes.setSizeAndLocation(this);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        setProgressPerformedInfo(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
        Log.logFinest(UpdateMetadataOfDirectoriesPanel.class, Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.UdateCompleted"));
        listSelectedDirectories.setEnabled(true);
        setEnabledButtons(false);
        setEnabledCheckboxes(false);
    }

    private void setProgressPerformedInfo(ProgressEvent evt) {
        long remainingMilliSeconds = evt.getMilliSecondsRemaining();
        long remainingMinutes = remainingMilliSeconds / 60000;
        boolean isTime = remainingMinutes > 0;
        MessageFormat message = new MessageFormat(
            isTime
            ? Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.ProgressWithTime")
            : Bundle.getString("UpdateMetadataOfDirectoriesPanel.InformationMessage.ProgressWithoutTime"));
        Object[] params = {currentFilenameInfotextPrefix, evt.getInfo(), remainingMinutes};
        labelCurrentFilename.setText(message.format(params));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelInfotext = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        labelCurrentFilename = new javax.swing.JLabel();
        labelHeadingTreeChosenDirectories = new javax.swing.JLabel();
        scrollPaneSelectedDirectories = new javax.swing.JScrollPane();
        listSelectedDirectories = new javax.swing.JList();
        buttonChooseDirectory = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        labelCountSelectedFiles = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();

        labelInfotext.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfotext.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfotext.text")); // NOI18N

        progressBar.setFont(new java.awt.Font("Dialog", 0, 12));
        progressBar.setFocusable(false);
        progressBar.setStringPainted(true);

        labelCurrentFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));

        labelHeadingTreeChosenDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        labelHeadingTreeChosenDirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelHeadingTreeChosenDirectories.text")); // NOI18N

        listSelectedDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        listSelectedDirectories.setModel(modelSelectedDirectoryList);
        listSelectedDirectories.setCellRenderer(new de.elmar_baumann.imv.view.renderer.ListCellRendererDirectories());
        listSelectedDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listSelectedDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneSelectedDirectories.setViewportView(listSelectedDirectories);

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('v');
        buttonChooseDirectory.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonStart.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonStart.setMnemonic('m');
        buttonStart.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        buttonStop.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttonStop.setMnemonic('b');
        buttonStop.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonStop.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        checkBoxIncludeSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIncludeSubdirectories.setSelected(true);
        checkBoxIncludeSubdirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxIncludeSubdirectories.text")); // NOI18N

        labelCountSelectedFiles.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCountSelectedFiles.setForeground(new java.awt.Color(0, 153, 0));
        labelCountSelectedFiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelCountSelectedFiles.setPreferredSize(new java.awt.Dimension(4, 20));

        checkBoxForce.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxForce.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxForce.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIncludeSubdirectories)
                    .addComponent(checkBoxForce)
                    .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(labelInfotext, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(labelHeadingTreeChosenDirectories)
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseDirectory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfotext, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCurrentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelHeadingTreeChosenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxForce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop)
                    .addComponent(buttonChooseDirectory))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void listSelectedDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listSelectedDirectoriesKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        removeSelectedDirectories();
    }
}//GEN-LAST:event_listSelectedDirectoriesKeyReleased

private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
    chooseDirectories();
}//GEN-LAST:event_buttonChooseDirectoryActionPerformed

private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
    start();
}//GEN-LAST:event_buttonStartActionPerformed

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    cancel();
}//GEN-LAST:event_buttonStopActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxForce;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JLabel labelCountSelectedFiles;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelHeadingTreeChosenDirectories;
    private javax.swing.JLabel labelInfotext;
    private javax.swing.JList listSelectedDirectories;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneSelectedDirectories;
    // End of variables declaration//GEN-END:variables
}
