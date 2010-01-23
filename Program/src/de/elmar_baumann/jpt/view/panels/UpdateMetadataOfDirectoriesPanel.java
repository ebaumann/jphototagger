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
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.event.UpdateMetadataCheckEvent;
import de.elmar_baumann.jpt.event.UpdateMetadataCheckEvent.Type;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.UpdateMetadataCheckListener;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.io.DirectoryInfo;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.comparator.FileSort;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.FileUtil;
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
import javax.swing.JPanel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 */
public final class UpdateMetadataOfDirectoriesPanel
        extends    JPanel
        implements UpdateMetadataCheckListener,
                   ProgressListener {

    private static final String                       KEY_LAST_DIRECTORY   = "de.elmar_baumann.jpt.view.ScanDirectoriesDialog.lastSelectedDirectory";
    private static final String                       KEY_FORCE            = "de.elmar_baumann.jpt.view.ScanDirectoriesDialog.force";
    private static final String                       KEY_SUBDIRECTORIES   = "de.elmar_baumann.jpt.view.ScanDirectoriesDialog.subdirectories";
    private static final long                         serialVersionUID     = -8953645248403117494L;
    private final        DefaultListModel             listModelDirectories = new DefaultListModel();
    private              File                         lastDirectory        = new File("");
    private              InsertImageFilesIntoDatabase imageFileInserter;

    public UpdateMetadataOfDirectoriesPanel() {
        initComponents();
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
                getWhatToInsertIntoDatabase());

        imageFileInserter.addProgressListener(this);
        imageFileInserter.addUpdateMetadataCheckListener(this);
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
        settings.applySettings(checkBoxForce, KEY_FORCE);
        settings.applySettings(checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        readLastDirectoryFromProperties();
    }

    private void readLastDirectoryFromProperties() {
        String lastDirectoryName = UserSettings.INSTANCE.getSettings().getString(KEY_LAST_DIRECTORY);
        if (!lastDirectoryName.isEmpty()) {
            File directory = new File(lastDirectoryName);
            if (directory.exists() && directory.isDirectory()) {
                lastDirectory = directory;
            }
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();
        settings.set(checkBoxForce, KEY_FORCE);
        settings.set(checkBoxIncludeSubdirectories, KEY_SUBDIRECTORIES);
        settings.set(lastDirectory.getAbsolutePath(), KEY_LAST_DIRECTORY);
        UserSettings.INSTANCE.writeToFile();
    }

    /**
     * Called from the current updater.
     *
     * @param e event containing the current filename
     */
    @Override
    public void actionPerformed(UpdateMetadataCheckEvent e) {
        if (e.getType().equals(Type.CHECKING_FILE)) {
            String filename = e.getImageFilename();
            assert filename != null : "Filename is null!";
            if (filename != null) {
                labelCurrentFilename.setText(filename);
            }
        } else if (e.getType().equals(Type.CHECK_FINISHED)) {
            imageFileInserter.removeUpdateMetadataCheckListener(this);
            imageFileInserter = null;
            updateFinished();
        }
    }

    private void updateFinished() {
        setEnabledButtons(false);
        setEnabledCheckboxes(false);
        labelCurrentFilename.setText("-");
        listDirectories.setEnabled(true);
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
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBar.setValue(evt.getValue());
    }

    private void chooseDirectories() {
        final DirectoryChooser dialog = new DirectoryChooser(
                                            GUI.INSTANCE.getAppFrame(),
                                            lastDirectory,
                                            getDirectoryChooserOptions());
        dialog.addWindowListener(new SizeAndLocationController());
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
        Collections.sort(directories, FileSort.NAMES_ASCENDING.getComparator());
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

        labelHeadingListDirectories = new javax.swing.JLabel();
        scrollPaneListDirectories = new javax.swing.JScrollPane();
        listDirectories = new javax.swing.JList();
        labelInfoFilecount = new javax.swing.JLabel();
        labelFilecount = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonChooseDirectories = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        labelHeadingListDirectories.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelHeadingListDirectories.text")); // NOI18N

        listDirectories.setModel(listModelDirectories);
        listDirectories.setCellRenderer(new de.elmar_baumann.jpt.view.renderer.ListCellRendererDirectories());
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

        labelInfoCurrentFilename.setText(Bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoCurrentFilename.text")); // NOI18N

        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));

        progressBar.setFocusable(false);
        progressBar.setStringPainted(true);

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneListDirectories, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                    .addComponent(checkBoxIncludeSubdirectories)
                    .addComponent(checkBoxForce)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoFilecount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelFilecount, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonChooseDirectories)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelInfoCurrentFilename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelCurrentFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                    .addComponent(labelHeadingListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHeadingListDirectories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFilecount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelInfoFilecount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxForce)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelCurrentFilename, 0, 0, Short.MAX_VALUE)
                    .addComponent(labelInfoCurrentFilename))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JList listDirectories;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneListDirectories;
    // End of variables declaration//GEN-END:variables
}
