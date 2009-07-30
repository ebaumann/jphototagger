package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.io.DirectoryInfo;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JProgressBar;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class UpdateMetadataOfDirectoriesPanel extends javax.swing.JPanel
        implements ActionListener {

    private static final String KEY_LAST_DIRECTORY =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.lastSelectedDirectory"; // NOI18N
    private static final String KEY_FORCE =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.force"; // NOI18N
    private static final String KEY_SUBDIRECTORIES =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.subdirectories"; // NOI18N
    private final DefaultListModel modelSelectedDirectoryList =
            new DefaultListModel();
    private List<File> selectedFiles = new ArrayList<File>();
    private InsertImageFilesIntoDatabase imageFileInserter;
    private File lastSelectedDirectory = new File(""); // NOI18N
    private int countSelectedFiles = 0;
    private ProgressBarProvider progressBarProvider;

    /** Creates new form UpdateMetadataOfDirectoriesPanel */
    public UpdateMetadataOfDirectoriesPanel() {
        initComponents();
        progressBarProvider = new ProgressBarProvider(progressBar);
        readProperties();
    }

    public void willDispose() {
        interruptImageFileInsterter();
        writeProperties();
        UserSettings.INSTANCE.getSettings().setString(
                lastSelectedDirectory.getAbsolutePath(), KEY_LAST_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    private void chooseDirectories() {
        final DirectoryChooser dialog = new DirectoryChooser(null,
                lastSelectedDirectory, getDirectoryChooserOptions());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            progressBar.setIndeterminate(true);
            List<File> selDirs = dialog.getSelectedDirectories();
            lastSelectedDirectory = selDirs.get(0);
            AddDirectories addDirectories = new AddDirectories(selDirs);
            addDirectories.start(); // unsets the progressbar indeterminate state
        }
    }

    private void createImageFileInserter() {
        imageFileInserter = new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(selectedFiles),
                getWhatToInsertIntoDatabase(),
                progressBarProvider);
        imageFileInserter.addActionListener(this);
    }

    private Set<DirectoryChooser.Option> getDirectoryChooserOptions() {
        return EnumSet.of(DirectoryChooser.Option.MULTI_SELECTION,
                UserSettings.INSTANCE.isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.ACCEPT_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.REJECT_HIDDEN_DIRECTORIES);
    }

    private EnumSet<InsertImageFilesIntoDatabase.Insert> getWhatToInsertIntoDatabase() {
        return checkBoxForce.isSelected()
               ? EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.EXIF,
                InsertImageFilesIntoDatabase.Insert.THUMBNAIL,
                InsertImageFilesIntoDatabase.Insert.XMP)
               : EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE);
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

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getCheckBox(checkBoxForce, KEY_FORCE);
        UserSettings.INSTANCE.getSettings().getCheckBox(
                checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        readCurrentDirectoryFromProperties();
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
            setFileCountInfo();
        }
    }

    private void empty() {
        selectedFiles.clear();
        modelSelectedDirectoryList.removeAllElements();
        listSelectedDirectories.setEnabled(true);
        countSelectedFiles = 0;
        labelCountSelectedFiles.setText("0"); // NOI18N
    }

    private void startUpdate() {
        setImageFilenames();
        createImageFileInserter();
        imageFileInserter.start();
        updateStarted();
    }

    private void cancelUpdate() {
        interruptImageFileInsterter();
        updateFinished();
    }

    private synchronized void interruptImageFileInsterter() {
        if (imageFileInserter != null) {
            imageFileInserter.cancel();
            imageFileInserter = null;
        }
    }

    private void updateFinished() {
        setEnabledButtons(false);
        setEnabledCheckboxes(false);
        labelCurrentFilename.setText("-"); // NOI18N
        listSelectedDirectories.setEnabled(true);
    }

    private void updateStarted() {
        setEnabledButtons(true);
        setEnabledCheckboxes(true);
        setProgressBarForUpdate();
        listSelectedDirectories.setEnabled(false);
    }

    private void setProgressBarForUpdate() {
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(selectedFiles.size());
    }

    private void setFileCountInfo() {
        labelCountSelectedFiles.setText(Integer.toString(countSelectedFiles));
    }

    private void setImageFilenames() {
        selectedFiles = getAllImageFiles();
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
        List<File> subdirectories = FileUtil.getSubdirectoriesRecursive(
                directory,
                UserSettings.INSTANCE.getDefaultDirectoryFilterOptions());
        for (File dir : subdirectories) {
            DirectoryInfo directoryInfo = new DirectoryInfo(dir);
            if (directoryInfo.hasImageFiles()) {
                modelSelectedDirectoryList.addElement(directoryInfo);
                countSelectedFiles += directoryInfo.getImageFileCount();
            }
        }
    }

    private void readCurrentDirectoryFromProperties() {
        String currentDirectoryname =
                UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIRECTORY);
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

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setCheckBox(checkBoxForce, KEY_FORCE);
        UserSettings.INSTANCE.getSettings().setCheckBox(
                checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == imageFileInserter && imageFileInserter != null) {
            String filename = imageFileInserter.getCurrentFilename();
            assert filename != null : "Filename is null!";
            if (filename != null) {
                labelCurrentFilename.setText(filename);
            }
        }
    }

    private class ProgressBarProvider extends MutualExcludedResource {

        ProgressBarProvider(JProgressBar progressBar) {
            setResource(progressBar);
        }
    }

    private class AddDirectories extends Thread {

        private final List<File> directories;

        public AddDirectories(List<File> directories) {
            this.directories = new ArrayList<File>(directories);
            setName("Searching new image files @ " + getClass().getName());
        }

        @Override
        public void run() {
            progressBar.setString(Bundle.getString(
                    "UpdateMetadataOfDirectoriesPanel.Info.AddDirectories"));
            List<File> newDirectories = getNotAlreadyChoosenDirectoriesFrom(
                    directories);
            if (newDirectories.size() > 0) {
                empty();
                addDirectories(newDirectories);
                setFileCountInfo();
            }
            progressBar.setString("");
            progressBar.setIndeterminate(false);
            buttonStart.setEnabled(
                    listSelectedDirectories.getModel().getSize() > 0);
        }
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
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        labelHeadingTreeChosenDirectories = new javax.swing.JLabel();
        scrollPaneSelectedDirectories = new javax.swing.JScrollPane();
        listSelectedDirectories = new javax.swing.JList();
        labelInfoCountSelectedFiles = new javax.swing.JLabel();
        labelCountSelectedFiles = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonChooseDirectory = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        labelInfotext.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfotext.text")); // NOI18N
        labelInfotext.setPreferredSize(new java.awt.Dimension(637, 28));

        progressBar.setFocusable(false);
        progressBar.setStringPainted(true);

        labelInfoCurrentFilename.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));

        labelHeadingTreeChosenDirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelHeadingTreeChosenDirectories.text")); // NOI18N

        listSelectedDirectories.setModel(modelSelectedDirectoryList);
        listSelectedDirectories.setCellRenderer(new de.elmar_baumann.imv.view.renderer.ListCellRendererDirectories());
        listSelectedDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listSelectedDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneSelectedDirectories.setViewportView(listSelectedDirectories);

        labelInfoCountSelectedFiles.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoCountSelectedFiles.text")); // NOI18N

        labelCountSelectedFiles.setForeground(new java.awt.Color(0, 153, 0));
        labelCountSelectedFiles.setText("0");
        labelCountSelectedFiles.setPreferredSize(new java.awt.Dimension(4, 20));

        checkBoxForce.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxForce.text")); // NOI18N

        checkBoxIncludeSubdirectories.setSelected(true);
        checkBoxIncludeSubdirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxIncludeSubdirectories.text")); // NOI18N

        buttonChooseDirectory.setMnemonic('v');
        buttonChooseDirectory.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonChooseDirectory.text")); // NOI18N
        buttonChooseDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoryActionPerformed(evt);
            }
        });

        buttonStop.setMnemonic('b');
        buttonStop.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonStop.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setMnemonic('m');
        buttonStart.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(checkBoxIncludeSubdirectories)
                    .addComponent(checkBoxForce)
                    .addComponent(labelInfotext, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseDirectory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoCountSelectedFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelHeadingTreeChosenDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCurrentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelInfoCurrentFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelHeadingTreeChosenDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneSelectedDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCountSelectedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelInfoCountSelectedFiles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    startUpdate();
}//GEN-LAST:event_buttonStartActionPerformed

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    cancelUpdate();
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
    private javax.swing.JLabel labelInfoCountSelectedFiles;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfotext;
    private javax.swing.JList listSelectedDirectories;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneSelectedDirectories;
    // End of variables declaration//GEN-END:variables
}
