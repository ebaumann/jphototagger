package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent;
import de.elmar_baumann.imv.event.CheckForUpdateMetadataEvent.Type;
import de.elmar_baumann.imv.event.listener.CheckingForUpdateMetadataListener;
import de.elmar_baumann.imv.io.DirectoryInfo;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.lib.comparator.ComparatorFilesNames;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import de.elmar_baumann.lib.util.ArrayUtil;
import de.elmar_baumann.lib.util.Settings;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JProgressBar;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class UpdateMetadataOfDirectoriesPanel
        extends javax.swing.JPanel
        implements CheckingForUpdateMetadataListener {

    private static final String KEY_LAST_DIRECTORY =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.lastSelectedDirectory"; // NOI18N
    private static final String KEY_FORCE =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.force"; // NOI18N
    private static final String KEY_SUBDIRECTORIES =
            "de.elmar_baumann.imv.view.ScanDirectoriesDialog.subdirectories"; // NOI18N
    private final DefaultListModel listModelDirectories = new DefaultListModel();
    private InsertImageFilesIntoDatabase imageFileInserter;
    private File lastDirectory = new File(""); // NOI18N
    private ProgressBarProvider progressBarProvider;

    public UpdateMetadataOfDirectoriesPanel() {
        initComponents();
        progressBarProvider = new ProgressBarProvider(progressBar);
        readProperties();
    }

    public void willDispose() {
        interruptImageFileInsterter();
        writeProperties();
    }

    public void handleRemoveSelectedDirectories() {
        removeSelectedDirectories();
        buttonStart.setEnabled(!listModelDirectories.isEmpty());
        labelFilecount.setText(Integer.toString(getFileCount()));
    }

    private void removeSelectedDirectories() {
        for (Object selectedValue : listDirectories.getSelectedValues()) {
            listModelDirectories.removeElement(selectedValue);
        }
    }

    private int getFileCount() {
        int count = 0;
        for (Object element : listModelDirectories.toArray()) {
            count += ((DirectoryInfo) element).getImageFileCount();
        }
        return count;
    }

    private void startUpdate() {
        List<File> selectedImageFiles = getSelectedImageFiles();
        updateWillStart(selectedImageFiles.size());
        createImageFileInserter(selectedImageFiles);
        imageFileInserter.start();
    }

    private void updateWillStart(int filecount) {
        setEnabledButtons(true);
        setEnabledCheckboxes(true);
        setProgressBarPreStartUpdate(filecount);
        listDirectories.setEnabled(false);
    }

    private void setProgressBarPreStartUpdate(int filecount) {
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(filecount);
    }

    private List<File> getSelectedImageFiles() {
        List<File> imageFiles = new ArrayList<File>();
        for (Object element : listModelDirectories.toArray()) {
            imageFiles.addAll(((DirectoryInfo) element).getImageFiles());
        }
        return imageFiles;
    }

    private void createImageFileInserter(List<File> selectedImageFiles) {
        imageFileInserter = new InsertImageFilesIntoDatabase(
                FileUtil.getAsFilenames(selectedImageFiles),
                getWhatToInsertIntoDatabase(),
                progressBarProvider);
        imageFileInserter.addActionListener(this);
    }

    private EnumSet<InsertImageFilesIntoDatabase.Insert> getWhatToInsertIntoDatabase() {
        return checkBoxForce.isSelected()
                ? EnumSet.of(
                InsertImageFilesIntoDatabase.Insert.EXIF,
                InsertImageFilesIntoDatabase.Insert.THUMBNAIL,
                InsertImageFilesIntoDatabase.Insert.XMP)
                : EnumSet.of(InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE);
    }

    private void stopUpdate() {
        interruptImageFileInsterter();
    }

    private synchronized void interruptImageFileInsterter() {
        if (imageFileInserter != null) {
            imageFileInserter.cancel();
        }
    }

    private void setEnabledButtons(boolean isUpdate) {
        buttonChooseDirectories.setEnabled(!isUpdate);
        buttonStart.setEnabled(!isUpdate);
        buttonStop.setEnabled(isUpdate);
    }

    private void setEnabledCheckboxes(boolean isUpdate) {
        checkBoxForce.setEnabled(!isUpdate);
        checkBoxIncludeSubdirectories.setEnabled(!isUpdate);
    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.getCheckBox(checkBoxForce, KEY_FORCE);
        settings.getCheckBox(checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        readLastDirectoryFromProperties();
    }

    private void readLastDirectoryFromProperties() {
        String lastDirectoryName =
                UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIRECTORY);
        if (!lastDirectoryName.isEmpty()) {
            File directory = new File(lastDirectoryName);
            if (directory.exists() && directory.isDirectory()) {
                lastDirectory = directory;
            }
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.setCheckBox(checkBoxForce, KEY_FORCE);
        settings.setCheckBox(checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        settings.setSizeAndLocation(this);
        settings.setString(lastDirectory.getAbsolutePath(), KEY_LAST_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    /**
     * Called from the current updater.
     *
     * @param e event containing the current filename
     */
    @Override
    public void actionPerformed(CheckForUpdateMetadataEvent e) {
        if (e.getType().equals(Type.CHECKING_FILE)) {
            String filename = e.getImageFilename();
            assert filename != null : "Filename is null!"; // NOI18N
            if (filename != null) {
                labelCurrentFilename.setText(filename);
            }
        } else if (e.getType().equals(Type.CHECK_FINISHED)) {
            imageFileInserter.removeActionListener(this);
            imageFileInserter = null;
            updateFinished();
        }
    }

    private void updateFinished() {
        setEnabledButtons(false);
        setEnabledCheckboxes(false);
        labelCurrentFilename.setText("-"); // NOI18N
        listDirectories.setEnabled(true);
    }

    private class ProgressBarProvider
            extends MutualExcludedResource<JProgressBar> {

        ProgressBarProvider(JProgressBar progressBar) {
            setResource(progressBar);
        }
    }

    private void chooseDirectories() {
        final DirectoryChooser dialog = new DirectoryChooser(
                null, lastDirectory, getDirectoryChooserOptions());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            List<File> selDirs = dialog.getSelectedDirectories();
            lastDirectory = selDirs.get(0);
            addNotContainedDirectories(selDirs);
        }
    }

    private Set<DirectoryChooser.Option> getDirectoryChooserOptions() {
        return EnumSet.of(DirectoryChooser.Option.MULTI_SELECTION,
                UserSettings.INSTANCE.isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.ACCEPT_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.REJECT_HIDDEN_DIRECTORIES);
    }

    private void addNotContainedDirectories(List<File> directories) {
        List<File> newDirectories =
                getNotDirectoriesNotInListFrom(directories);
        ArrayUtil.addNotContainedElements(directories, newDirectories);
        addDirectories(newDirectories);
        labelFilecount.setText(Integer.toString(getFileCount()));
        buttonStart.setEnabled(listModelDirectories.getSize() > 0);
    }

    private List<File> getNotDirectoriesNotInListFrom(List<File> directories) {
        List<File> newDirectories = new ArrayList<File>();
        for (File directory : directories) {
            if (!listModelDirectories.contains(directory)) {
                newDirectories.add(directory);
            }
        }
        return newDirectories;
    }

    private void addDirectories(List<File> directories) {
        if (checkBoxIncludeSubdirectories.isSelected()) {
            addSubdirectories(directories);
        }
        Collections.sort(directories,
                ComparatorFilesNames.ASCENDING_IGNORE_CASE);
        for (File directory : directories) {
            DirectoryInfo directoryInfo = new DirectoryInfo(directory);
            if (directoryInfo.hasImageFiles() &&
                    !listModelDirectories.contains(directoryInfo)) {
                listModelDirectories.addElement(directoryInfo);
            }
        }
    }

    private void addSubdirectories(List<File> directories) {
        List<File> subdirectories = new ArrayList<File>();
        for (File dir : directories) {
            subdirectories.addAll(
                    FileUtil.getSubdirectoriesRecursive(
                    dir,
                    UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
        }
        ArrayUtil.addNotContainedElements(subdirectories, directories);
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
        labelHeadingListDirectories = new javax.swing.JLabel();
        scrollPaneListDirectories = new javax.swing.JScrollPane();
        listDirectories = new javax.swing.JList();
        labelInfoFilecount = new javax.swing.JLabel();
        labelFilecount = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        buttonChooseDirectories = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        labelInfotext.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfotext.text")); // NOI18N
        labelInfotext.setPreferredSize(new java.awt.Dimension(637, 28));

        progressBar.setFocusable(false);
        progressBar.setStringPainted(true);

        labelInfoCurrentFilename.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));

        labelHeadingListDirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelHeadingListDirectories.text")); // NOI18N

        listDirectories.setModel(listModelDirectories);
        listDirectories.setCellRenderer(new de.elmar_baumann.imv.view.renderer.ListCellRendererDirectories());
        listDirectories.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listDirectoriesKeyReleased(evt);
            }
        });
        scrollPaneListDirectories.setViewportView(listDirectories);

        labelInfoFilecount.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoFilecount.text")); // NOI18N

        labelFilecount.setForeground(new java.awt.Color(0, 153, 0));
        labelFilecount.setText("0");
        labelFilecount.setPreferredSize(new java.awt.Dimension(4, 20));

        checkBoxForce.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxForce.text")); // NOI18N

        checkBoxIncludeSubdirectories.setSelected(true);
        checkBoxIncludeSubdirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxIncludeSubdirectories.text")); // NOI18N

        buttonChooseDirectories.setMnemonic('v');
        buttonChooseDirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonChooseDirectories.text")); // NOI18N
        buttonChooseDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoriesActionPerformed(evt);
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
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(checkBoxIncludeSubdirectories)
                    .addComponent(checkBoxForce)
                    .addComponent(labelInfotext, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseDirectories)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoFilecount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelFilecount, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelHeadingListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(scrollPaneListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelCurrentFilename, 0, 0, Short.MAX_VALUE)
                    .addComponent(labelInfoCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addComponent(labelHeadingListDirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFilecount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelInfoFilecount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxForce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart)
                    .addComponent(buttonStop)
                    .addComponent(buttonChooseDirectories))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void listDirectoriesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listDirectoriesKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        handleRemoveSelectedDirectories();
    }
}//GEN-LAST:event_listDirectoriesKeyReleased

private void buttonChooseDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoriesActionPerformed
    chooseDirectories();
}//GEN-LAST:event_buttonChooseDirectoriesActionPerformed

private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
    startUpdate();
}//GEN-LAST:event_buttonStartActionPerformed

private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
    stopUpdate();
}//GEN-LAST:event_buttonStopActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseDirectories;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxForce;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelFilecount;
    private javax.swing.JLabel labelHeadingListDirectories;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfoFilecount;
    private javax.swing.JLabel labelInfotext;
    private javax.swing.JList listDirectories;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneListDirectories;
    // End of variables declaration//GEN-END:variables
}
