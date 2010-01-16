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
import de.elmar_baumann.jpt.app.AppFileFilters;
import de.elmar_baumann.jpt.controller.misc.SizeAndLocationController;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.FileEditor;
import de.elmar_baumann.jpt.view.dialogs.ShowFilesDialog;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.dialog.DirectoryChooser.Option;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import de.elmar_baumann.lib.util.SettingsHints;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Panel to select files for a {@link de.elmar_baumann.jpt.types.FileEditor}.
 * Starts a thread, let the editor edit all selected files and displays the
 * progress.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-05-22
 */
public final class FileEditorPanel extends javax.swing.JPanel {

    private static final String          KEY_DIRECTORY_NAME    = "de.elmar_baumann.jpt.view.FileEditorDialog.panels.Directory";
    private static final long            serialVersionUID      = 1672989914070513384L;
    private              List<File>      selectedFiles         = new ArrayList<File>();
    private              List<File>      selectedDirectories   = new ArrayList<File>();
    private              File            prevSelectedDirectory = new File("");
    private              FileEditor      fileEditor            = new FileEditor();
    private              FileFilter      fileChooserFileFilter = AppFileFilters.ACCEPTED_IMAGE_FILE_FORMATS.forFileChooser(Bundle.getString("FileEditorPanel.FileChooserFileFilter.Description"));
    private              RegexFileFilter dirChooserFileFilter  = new RegexFileFilter(".*", ";");
    private              String          title                 = "";
    private volatile     boolean         selectDirs;
    private volatile     boolean         stop;
    private volatile     boolean         isRunning;

    public FileEditorPanel() {
        initComponents();
        setModeInfo();
    }

    /**
     * Constructor.
     *
     * @param fileEditor  editor
     * @param selectDirs  true, if the use shall select directories and false,
     *                    if the user shall select files
     */
    public FileEditorPanel(FileEditor fileEditor, boolean selectDirs) {
        this.fileEditor = fileEditor;
        this.selectDirs = selectDirs;
        initComponents();
        setModeInfo();
    }

    private void setModeInfo() {
        setFileButtonText();
        setInfoText();
    }

    public void setSelectDirs(boolean select) {
        selectDirs = select;
        setModeInfo();
    }

    public void setFileChooserFilter(FileFilter fileChooserFileFilter) {
        this.fileChooserFileFilter = fileChooserFileFilter;
    }

    public boolean isSelectDirs() {
        return selectDirs;
    }

    public void setDescription(String description) {
        labelDescription.setText(asHtml(description));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private String asHtml(String description) {
        return "<html><p>" + description + "</p></html>";
    }

    public void setFileChooserFileFilter(FileFilter filter) {
        fileChooserFileFilter = filter;
    }

    public void setDirChooserFileFilter(RegexFileFilter filter) {
        dirChooserFileFilter = filter;
    }

    public void setEditor(FileEditor fileEditor) {
        if (!isRunning) {
            this.fileEditor = fileEditor;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private Set<Option> getDirChooserOptions() {
        Set<Option> options = UserSettings.INSTANCE.getDefaultDirectoryChooserOptions();
        options.add(DirectoryChooser.Option.MULTI_SELECTION);
        return options;
    }

    private List<File> getFilesOfDirectories(List<File> selectedDirectories) {
        List<File> selFiles = new ArrayList<File>();
        List<File> selDirs = includeSubdirectories(selectedDirectories);
        for (File dir : selDirs) {
            File[] foundFiles = dir.listFiles(dirChooserFileFilter);
            if (foundFiles != null) {
                selFiles.addAll(Arrays.asList(foundFiles));
            }
        }
        return selFiles;
    }

    private List<File> includeSubdirectories(List<File> dirs) {
        List<File> allDirs = new ArrayList<File>();
        boolean includeSubDirs = checkBoxIncludeSubdirectories.isSelected();
        Set<DirectoryFilter.Option> options = UserSettings.INSTANCE.getDefaultDirectoryFilterOptions();
        for (File dir : dirs) {
            allDirs.add(dir);
            if (includeSubDirs) {
                allDirs.addAll(FileUtil.getSubdirectoriesRecursive(dir, options));
            }
        }
        return allDirs;
    }

    private void handleInfoMouseClicked() {
        if (selectDirs) {
            ShowFilesDialog dialog = new ShowFilesDialog(null,
                    selectedDirectories);
            dialog.setVisible(true);
        }
    }

    private void handleSelectFilesActionPerformed() {
        if (selectDirs) {
            selectDirectories();
        } else {
            selectFiles();
        }
        boolean hasFiles = !selectedFiles.isEmpty();
        buttonStart.setEnabled(hasFiles);
        buttonShowFiles.setEnabled(hasFiles);
    }

    private void handleShowFilesActionPerformed() {
        ShowFilesDialog dialog = new ShowFilesDialog(null, selectedFiles);
        dialog.setVisible(true);
    }

    private void handleStartActionPerformed() {
        Thread thread = new Thread(new EditThread());
        thread.setName("File editor " + title + " @ " + getClass().getSimpleName());
        thread.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelDescription = new javax.swing.JLabel();
        panelOptions = new javax.swing.JPanel();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        checkBoxReplaceExistingFiles = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        labelFilename = new javax.swing.JLabel();
        labelInfo = new javax.swing.JLabel();
        buttonShowFiles = new javax.swing.JButton();
        buttonSelectFiles = new javax.swing.JButton();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("FileEditorPanel.panelOptions.border.title"))); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        labelDescription.setText(bundle.getString("FileEditorPanel.labelDescription.text")); // NOI18N

        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("FileEditorPanel.panelOptions.border.title"))); // NOI18N

        checkBoxIncludeSubdirectories.setMnemonic('u');
        checkBoxIncludeSubdirectories.setText(Bundle.getString("FileEditorPanel.checkBoxIncludeSubdirectories.text")); // NOI18N

        checkBoxReplaceExistingFiles.setMnemonic('x');
        checkBoxReplaceExistingFiles.setText(Bundle.getString("FileEditorPanel.checkBoxReplaceExistingFiles.text")); // NOI18N

        javax.swing.GroupLayout panelOptionsLayout = new javax.swing.GroupLayout(panelOptions);
        panelOptions.setLayout(panelOptionsLayout);
        panelOptionsLayout.setHorizontalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionsLayout.createSequentialGroup()
                .addGroup(panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxReplaceExistingFiles)
                    .addComponent(checkBoxIncludeSubdirectories))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        panelOptionsLayout.setVerticalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOptionsLayout.createSequentialGroup()
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxReplaceExistingFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelFilename.setForeground(new java.awt.Color(0, 0, 255));
        labelFilename.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelInfo.setText(Bundle.getString("FileEditorPanel.labelInfo.text")); // NOI18N
        labelInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelInfoMouseClicked(evt);
            }
        });

        buttonShowFiles.setText(Bundle.getString("FileEditorPanel.buttonShowFiles.text")); // NOI18N
        buttonShowFiles.setEnabled(false);
        buttonShowFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowFilesActionPerformed(evt);
            }
        });

        buttonSelectFiles.setMnemonic('e');
        buttonSelectFiles.setText(Bundle.getString("FileEditorPanel.buttonSelectFiles.text")); // NOI18N
        buttonSelectFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectFilesActionPerformed(evt);
            }
        });

        buttonStop.setMnemonic('o');
        buttonStop.setText(Bundle.getString("FileEditorPanel.buttonStop.text")); // NOI18N
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setMnemonic('s');
        buttonStart.setText(Bundle.getString("FileEditorPanel.buttonStart.text")); // NOI18N
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
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(labelDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(panelOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonShowFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonStart)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(labelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(panelOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStop)
                    .addComponent(buttonSelectFiles)
                    .addComponent(buttonStart)
                    .addComponent(buttonShowFiles))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSelectFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectFilesActionPerformed
        handleSelectFilesActionPerformed();
    }//GEN-LAST:event_buttonSelectFilesActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        handleStartActionPerformed();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
        stop = true;
    }//GEN-LAST:event_buttonStopActionPerformed

    private void buttonShowFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowFilesActionPerformed
        handleShowFilesActionPerformed();
    }//GEN-LAST:event_buttonShowFilesActionPerformed

    private void labelInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelInfoMouseClicked
        handleInfoMouseClicked();
    }//GEN-LAST:event_labelInfoMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSelectFiles;
    private javax.swing.JButton buttonShowFiles;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JCheckBox checkBoxReplaceExistingFiles;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

    private void selectDirectories() {
        DirectoryChooser dialog = new DirectoryChooser(
                                        GUI.INSTANCE.getAppFrame(),
                                        prevSelectedDirectory,
                                        getDirChooserOptions());
        dialog.addWindowListener(new SizeAndLocationController());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            selectedDirectories = dialog.getSelectedDirectories();
            selectedFiles = getFilesOfDirectories(selectedDirectories);
            prevSelectedDirectory = dialog.getSelectedDirectories().get(0);
            labelInfo.setText(Bundle.getString("FileEditorPanel.SelectDirectories.LabelInfo.Text"));
        }
    }

    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser(prevSelectedDirectory);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(fileChooserFileFilter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFiles.clear();
            File[] selFiles = fileChooser.getSelectedFiles();
            selectedFiles.addAll(Arrays.asList(selFiles));
            setDirectory(selFiles);
        }
    }

    private void setDirectory(File[] selFiles) {
        if (selFiles.length > 0) {
            prevSelectedDirectory = selFiles[0].getParentFile();
        }
    }

    private void setFileButtonText() {
        buttonSelectFiles.setText(selectDirs
                                  ? Bundle.getString("FileEditorDialog.ButtonFiles.DirectoriesText")
                                  : Bundle.getString("FileEditorDialog.ButtonFiles.FilesText"));
    }

    private void setInfoText() {
        labelInfo.setText(selectDirs
                          ? Bundle.getString("FileEditorPanel.LabelInfo.SelectDirs.Text")
                          : Bundle.getString("FileEditorPanel.LabelInfo.SelectFiles.Text"));
    }

    public void readProperties() {
        prevSelectedDirectory = new File(UserSettings.INSTANCE.getSettings().
                getString(KEY_DIRECTORY_NAME));
        UserSettings.INSTANCE.getSettings().getComponent(this,
                new SettingsHints(EnumSet.of(SettingsHints.Option.NONE)));
    }

    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().setString(prevSelectedDirectory.
                getAbsolutePath(), KEY_DIRECTORY_NAME);
        UserSettings.INSTANCE.getSettings().setComponent(this,
                new SettingsHints(EnumSet.of(SettingsHints.Option.NONE)));
        UserSettings.INSTANCE.writeToFile();
    }

    private class EditThread implements Runnable {

        @Override
        public void run() {
            setIsRunning(true);
            fileEditor.setConfirmOverwrite(!checkBoxReplaceExistingFiles.
                    isSelected());
            int filesCount = selectedFiles.size();
            initProgressBar(filesCount);
            for (int i = 0; i < filesCount && !stop; i++) {
                File file = selectedFiles.get(i);
                fileEditor.edit(file);
                labelFilename.setText(file.getAbsolutePath());
                progressBar.setValue(i + 1);
            }
            setIsRunning(false);
        }

        private void initProgressBar(int count) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(count);
            progressBar.setValue(0);
        }

        private void setIsRunning(boolean runs) {
            buttonStart.setEnabled(!runs);
            buttonShowFiles.setEnabled(!runs);
            buttonStop.setEnabled(runs);
            buttonSelectFiles.setEnabled(!runs);
            checkBoxIncludeSubdirectories.setEnabled(!runs);
            checkBoxReplaceExistingFiles.setEnabled(!runs);
            labelFilename.setText("");
            isRunning = runs;
            stop = false;
        }
    }
}
