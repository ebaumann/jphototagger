package de.elmar_baumann.imagemetadataviewer.view.dialogs;

import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.tasks.ImageMetadataToDatabase;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.io.DirectoryInfo;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 * Dialog zum Scannen eines Verzeichnisses nach Bildern und Einfügen
 * von Thumbnails in die Datenbank.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class UpdateMetaDataOfDirectoriesDialog extends javax.swing.JDialog implements
    ProgressListener {

    private final String keyLastDirectory = "de.elmar_baumann.imagemetadataviewer.view.ScanDirectoriesDialog.lastSelectedDirectory"; // NOI18N
    private final String keyForce = "de.elmar_baumann.imagemetadataviewer.view.ScanDirectoriesDialog.force"; // NOI18N
    private final String keyNoThumbnails = "de.elmar_baumann.imagemetadataviewer.view.ScanDirectoriesDialog.noThumbnails"; // NOI18N
    private final String keySubdirectories = "de.elmar_baumann.imagemetadataviewer.view.ScanDirectoriesDialog.subdirectories"; // NOI18N
    private final String title = Bundle.getString("UpdateMetaDataOfDirectoriesDialog.Title");
    private final String currentFilenameInfotextPrefix = Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InformationMessage.UpdateCurrentFile");
    private List<String> selectedImagesFilenames = new ArrayList<String>();
    private ImageMetadataToDatabase activeScanner;
    private File lastSelectedDirectory = new File(""); // NOI18N
    private DefaultListModel modelSelectedDirectoryList = new DefaultListModel();
    private int countSelectedFiles = 0;
    private static UpdateMetaDataOfDirectoriesDialog instance = new UpdateMetaDataOfDirectoriesDialog();

    private UpdateMetaDataOfDirectoriesDialog() {
        super((Frame) null, false);
        initComponents();
        postInitComponents();
    }

    public static UpdateMetaDataOfDirectoriesDialog getInstance() {
        return instance;
    }

    private void postInitComponents() {
        setIconImages(AppSettings.getAppIcons());
        PersistentAppSizes.getSizeAndLocation(this);
        readPersistent();
    }

    private void addFilecountToTitle() {
        MessageFormat message = new MessageFormat(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InfoMessage.Title.FileCount"));
        Object[] params = {selectedImagesFilenames.size()};
        setTitle(title + message.format(params));
    }

    private void chooseDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(null, UserSettings.getInstance().isAcceptHiddenDirectories());
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
                setDefaultTitle();
                buttonScan.setEnabled(listSelectedDirectories.getModel().getSize() > 0);
            }
        }
    }

    private void createScanner() {
        activeScanner =
            new ImageMetadataToDatabase(selectedImagesFilenames,
            UserSettings.getInstance().getMaxThumbnailLength());
        activeScanner.setCreateThumbnails(!checkBoxNoThumbnails.isSelected());
        activeScanner.setForceUpdate(checkBoxForce.isSelected());
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
        MessageFormat message = new MessageFormat(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InfoMessage.ThreadPriority"));
        Object[] params = {thread.getPriority()};
        Logger.getLogger(UpdateMetaDataOfDirectoriesDialog.class.getName()).log(Level.FINE, message.format(params));
    }

    private void readPersistent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.getCheckBox(checkBoxForce, keyForce);
        settings.getCheckBox(checkBoxIncludeSubdirectories, keySubdirectories);
        settings.getCheckBox(checkBoxNoThumbnails, keyNoThumbnails);
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
            buttonScan.setEnabled(!modelSelectedDirectoryList.isEmpty());
            setLastDirectory();
            setFileCountInfo();
        }
    }

    private void empty() {
        selectedImagesFilenames.clear();
        modelSelectedDirectoryList.removeAllElements();
        listSelectedDirectories.setEnabled(true);
        countSelectedFiles = 0;
        labelCountSelectedFiles.setText(" "); // NOI18N
    }

    private void scanDirectories() {
        setImageFilenames();
        setGuiBeforeScan();
        createScanner();
        activeScanner.addProgressListener(this);
        Thread thread = new Thread(activeScanner);
        setThreadPriority(thread);
        thread.start();
    }

    private void cancelScan() {
        stopScanner();
        zeroProgressBar();
        setGuiAfterScan();
    }

    private void setDefaultTitle() {
        setTitle(title);
    }

    private void setFileCountInfo() {
        MessageFormat message = new MessageFormat(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InfoMessage.SelectedFileCount"));
        Object[] params = {countSelectedFiles};
        labelCountSelectedFiles.setText(message.format(params));
    }

    private void setGuiAfterScan() {
        setButtonStatus(false);
        setCheckboxStatus(false);
        setDefaultTitle();
        labelCurrentFilename.setText(" "); // NOI18N
        listSelectedDirectories.setEnabled(true);
    }

    private void setGuiBeforeScan() {
        setButtonStatus(true);
        setCheckboxStatus(true);
        addFilecountToTitle();
        setProgressBarForScan();
        listSelectedDirectories.setEnabled(false);
    }

    private void setImageFilenames() {
        selectedImagesFilenames =
            FileUtil.getAbsolutePathnames(getAllImageFiles());
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
        progressBarScan.setMinimum(0);
        progressBarScan.setMaximum(selectedImagesFilenames.size());
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
        progressBarScan.setValue(0);
    }

    private synchronized void stopScanner() {
        if (activeScanner != null) {
            activeScanner.removeProgressListener(this);
            activeScanner.stop();
            activeScanner = null;
        }
    }

    private void endDialog() {
        stopScanner();
        writePersistent();
        PersistentSettings.getInstance().setString(
            lastSelectedDirectory.getAbsolutePath(), keyLastDirectory);
        setVisible(false);
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

    private void setButtonStatus(boolean willCreateThumbnails) {
        buttonChooseDirectory.setEnabled(!willCreateThumbnails);
        buttonScan.setEnabled(!willCreateThumbnails);
        buttonCancelScan.setEnabled(willCreateThumbnails);
    }

    private void setCheckboxStatus(boolean willCreateThumbnails) {
        checkBoxForce.setEnabled(!willCreateThumbnails);
        checkBoxIncludeSubdirectories.setEnabled(!willCreateThumbnails);
        checkBoxNoThumbnails.setEnabled(!willCreateThumbnails);
    }

    private void writePersistent() {
        PersistentSettings settings = PersistentSettings.getInstance();
        settings.setCheckBox(checkBoxForce, keyForce);
        settings.setCheckBox(checkBoxIncludeSubdirectories, keySubdirectories);
        settings.setCheckBox(checkBoxNoThumbnails, keyNoThumbnails);
        PersistentAppSizes.setSizeAndLocation(this);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBarScan.setValue(evt.getValue());
        setProgressPerformedInfo(evt);
    }

    private void setProgressPerformedInfo(ProgressEvent evt) {
        long remainingMilliSeconds = evt.getMilliSecondsRemaining();
        long remainingMinutes = remainingMilliSeconds / 60000;
        boolean isTime = remainingMinutes > 0;
        MessageFormat message = new MessageFormat(
            isTime
            ? Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InfoMessage.ProgressWithTime")
            : Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InfoMessage.ProgressWithoutTime"));
        Object[] params = {currentFilenameInfotextPrefix, evt.getInfo(), remainingMinutes};
        labelCurrentFilename.setText(message.format(params));
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        Logger.getLogger(UpdateMetaDataOfDirectoriesDialog.class.getName()).log(Level.FINE, Bundle.getString("UpdateMetaDataOfDirectoriesDialog.InformationMessage.UdateCompleted"));
        listSelectedDirectories.setEnabled(true);
        setButtonStatus(false);
        setCheckboxStatus(false);
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
        progressBarScan = new javax.swing.JProgressBar();
        labelCurrentFilename = new javax.swing.JLabel();
        labelHeadingTreeChosenDirectories = new javax.swing.JLabel();
        scrollPaneSelectedDirectories = new javax.swing.JScrollPane();
        listSelectedDirectories = new javax.swing.JList();
        buttonChooseDirectory = new javax.swing.JButton();
        buttonScan = new javax.swing.JButton();
        buttonCancelScan = new javax.swing.JButton();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        labelCountSelectedFiles = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();
        checkBoxNoThumbnails = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(title);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfotext.setFont(new java.awt.Font("Dialog", 0, 12));
        labelInfotext.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.labelInfotext.text")); // NOI18N

        progressBarScan.setFont(new java.awt.Font("Dialog", 0, 12));
        progressBarScan.setFocusable(false);
        progressBarScan.setStringPainted(true);

        labelCurrentFilename.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));

        labelHeadingTreeChosenDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        labelHeadingTreeChosenDirectories.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.labelHeadingTreeChosenDirectories.text")); // NOI18N

        listSelectedDirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        listSelectedDirectories.setModel(modelSelectedDirectoryList);
        listSelectedDirectories.setCellRenderer(new de.elmar_baumann.imagemetadataviewer.view.renderer.ListCellRendererDirectories());
        listSelectedDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listSelectedDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneSelectedDirectories.setViewportView(listSelectedDirectories);

        buttonChooseDirectory.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonChooseDirectory.setMnemonic('v');
        buttonChooseDirectory.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonScan.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonScan.setMnemonic('m');
        buttonScan.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.buttonScan.text")); // NOI18N
        buttonScan.setEnabled(false);
        buttonScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonScanActionPerformed(evt);
            }
        });

        buttonCancelScan.setFont(new java.awt.Font("Dialog", 0, 12));
        buttonCancelScan.setMnemonic('b');
        buttonCancelScan.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.buttonCancelScan.text")); // NOI18N
        buttonCancelScan.setEnabled(false);
        buttonCancelScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelScanActionPerformed(evt);
            }
        });

        checkBoxIncludeSubdirectories.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxIncludeSubdirectories.setSelected(true);
        checkBoxIncludeSubdirectories.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.checkBoxIncludeSubdirectories.text")); // NOI18N

        labelCountSelectedFiles.setFont(new java.awt.Font("Dialog", 0, 10));
        labelCountSelectedFiles.setForeground(new java.awt.Color(0, 153, 0));
        labelCountSelectedFiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        labelCountSelectedFiles.setPreferredSize(new java.awt.Dimension(4, 20));

        checkBoxForce.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxForce.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.checkBoxForce.text")); // NOI18N

        checkBoxNoThumbnails.setFont(new java.awt.Font("Dialog", 0, 12));
        checkBoxNoThumbnails.setText(Bundle.getString("UpdateMetaDataOfDirectoriesDialog.checkBoxNoThumbnails.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxNoThumbnails)
                    .addComponent(checkBoxForce)
                    .addComponent(checkBoxIncludeSubdirectories)
                    .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addComponent(progressBarScan, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addComponent(labelInfotext, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addComponent(labelHeadingTreeChosenDirectories)
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseDirectory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonCancelScan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonScan)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfotext, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBarScan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCurrentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelHeadingTreeChosenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxForce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxNoThumbnails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonScan)
                    .addComponent(buttonCancelScan)
                    .addComponent(buttonChooseDirectory))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonChooseDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoryActionPerformed
    chooseDirectories();
}//GEN-LAST:event_buttonChooseDirectoryActionPerformed

private void buttonScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonScanActionPerformed
    scanDirectories();
}//GEN-LAST:event_buttonScanActionPerformed

private void buttonCancelScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelScanActionPerformed
    cancelScan();
}//GEN-LAST:event_buttonCancelScanActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    endDialog();
}//GEN-LAST:event_formWindowClosing

private void listSelectedDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listSelectedDirectoriesKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        removeSelectedDirectories();
    }
}//GEN-LAST:event_listSelectedDirectoriesKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    UpdateMetaDataOfDirectoriesDialog dialog = new UpdateMetaDataOfDirectoriesDialog();
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }});
                    dialog.setVisible(true);
                }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelScan;
    private javax.swing.JButton buttonChooseDirectory;
    private javax.swing.JButton buttonScan;
    private javax.swing.JCheckBox checkBoxForce;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JCheckBox checkBoxNoThumbnails;
    private javax.swing.JLabel labelCountSelectedFiles;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelHeadingTreeChosenDirectories;
    private javax.swing.JLabel labelInfotext;
    private javax.swing.JList listSelectedDirectories;
    private javax.swing.JProgressBar progressBarScan;
    private javax.swing.JScrollPane scrollPaneSelectedDirectories;
    // End of variables declaration//GEN-END:variables

}
