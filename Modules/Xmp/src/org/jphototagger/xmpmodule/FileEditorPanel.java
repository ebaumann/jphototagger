package org.jphototagger.xmpmodule;

import java.awt.Container;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.io.filefilter.FileChooserFilter;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.DirectoryChooser.Option;
import org.jphototagger.lib.swing.SelectRootFilesPanel;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Panel to select files for a {@code org.jphototagger.program.types.FileEditor}.
 * Starts a thread, let the editor edit all selected files and displays the
 * progress.
 *
 * @author Elmar Baumann
 */
public final class FileEditorPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    private static final String KEY_DIRECTORY_NAME = "org.jphototagger.program.view.FileEditorDialog.panels.Directory";
    private static final String KEY_INCLUDE_SUBDIRS = "FileEditorPanel.IncludeSubdirs";
    private static final String KEY_REPLACE_EXISTING_FILES = "FileEditorPanel.ReplaceExistingFiles";
    private List<File> selectedFiles = new ArrayList<File>();
    private List<File> selectedDirectories = new ArrayList<File>();
    private File prevSelectedDirectory = new File("");
    private FileEditor fileEditor = new FileEditor();
    private FileFilter fileChooserFileFilter = createFileChooserFilter();
    private java.io.FileFilter dirChooserFileFilter = new RegexFileFilter(".*", ";");
    private String title = "";
    private volatile boolean selectDirs;
    private volatile boolean cancel;
    private volatile boolean isRunning;

    public FileEditorPanel() {
        initComponents();
        setModeInfo();
        MnemonicUtil.setMnemonics((Container) this);
    }

    /**
     * Constructor.
     *
     * @param fileEditor  editor
     * @param selectDirs  true, if the use shall select directories and false,
     *                    if the user shall select files
     */
    public FileEditorPanel(FileEditor fileEditor, boolean selectDirs) {
        if (fileEditor == null) {
            throw new NullPointerException("fileEditor == null");
        }

        this.fileEditor = fileEditor;
        this.selectDirs = selectDirs;
        initComponents();
        postInitComponents();
    }

    private FileFilter createFileChooserFilter() {
        AppFileFilterProvider provider = Lookup.getDefault().lookup(AppFileFilterProvider.class);
        java.io.FileFilter filter = provider.getAcceptedImageFilesFileFilter();
        String description = Bundle.getString(FileEditorPanel.class, "FileEditorPanel.FileChooserFileFilter.Description");
        return new FileChooserFilter(filter, description);
    }

    private void postInitComponents() {
        setModeInfo();
    }

    private void setModeInfo() {
        setFileButtonText();
    }

    public void setSelectDirs(boolean select) {
        selectDirs = select;
        setModeInfo();
    }

    public void setFileChooserFilter(FileFilter fileChooserFileFilter) {
        if (fileChooserFileFilter == null) {
            throw new NullPointerException("fileChooserFileFilter == null");
        }

        this.fileChooserFileFilter = fileChooserFileFilter;
    }

    public boolean isSelectDirs() {
        return selectDirs;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new NullPointerException("description == null");
        }

        labelDescription.setText(asHtml(description));
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("title == null");
        }

        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    private String asHtml(String description) {
        return "<html><p>" + description + "</p></html>";
    }

    public void setFileChooserFileFilter(FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        fileChooserFileFilter = filter;
    }

    public void setDirChooserFileFilter(java.io.FileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        dirChooserFileFilter = filter;
    }

    public void setEditor(FileEditor fileEditor) {
        if (fileEditor == null) {
            throw new NullPointerException("fileEditor == null");
        }

        if (!isRunning) {
            this.fileEditor = fileEditor;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private Option[] getDirChooserOptions() {
        return new Option[] {
            getDirChooserOptionShowHiddenDirs(),
            DirectoryChooser.Option.MULTI_SELECTION };
    }

    private DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                : DirectoryChooser.Option.NO_OPTION;
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
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

        for (File dir : dirs) {
            allDirs.add(dir);

            if (includeSubDirs) {
                allDirs.addAll(FileUtil.getSubDirectoriesRecursive(dir, null, getDirFilterOptionShowHiddenFiles()));
            }
        }

        return allDirs;
    }

    private DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private void handleSelectFilesActionPerformed() {
        Cursor cursor = getCursor();

        buttonSelectFiles.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (selectDirs) {
            selectDirectories();
        } else {
            selectFiles();
        }

        boolean hasFiles = !selectedFiles.isEmpty();

        setCursor(cursor);
        buttonSelectFiles.setEnabled(true);
        buttonStart.setEnabled(hasFiles);
    }

    private void handleStartActionPerformed() {
        Thread thread = new Thread(new EditThread(),
                "JPhotoTagger: File editor " + title);

        thread.start();
    }

    private void selectDirectories() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(DomainPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
        DirectoryChooser dlg = new DirectoryChooser(ComponentUtil.findFrameWithIcon(), prevSelectedDirectory, hideRootFiles, getDirChooserOptions());

        dlg.setStorageKey("FileEditorPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            selectedDirectories = dlg.getSelectedDirectories();
            selectedFiles = getFilesOfDirectories(selectedDirectories);
            setFilesToList(selectedFiles);
            prevSelectedDirectory = dlg.getSelectedDirectories().get(0);
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
            setFilesToList(selectedFiles);
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
                                  ? Bundle.getString(FileEditorPanel.class, "FileEditorPanel.ButtonFiles.DirectoriesText")
                                  : Bundle.getString(FileEditorPanel.class, "FileEditorPanel.ButtonFiles.FilesText"));
    }

    public void readProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        prevSelectedDirectory = new File(storage.getString(KEY_DIRECTORY_NAME));
        storage.applyComponentSettings(this, null);
        checkBoxIncludeSubdirectories.setSelected(storage.getBoolean(KEY_INCLUDE_SUBDIRS));
        checkBoxReplaceExistingFiles.setSelected(storage.getBoolean(KEY_REPLACE_EXISTING_FILES));
    }

    public void writeProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setComponent(this, null);
        storage.setString(KEY_DIRECTORY_NAME, prevSelectedDirectory.getAbsolutePath());
        storage.setToggleButton(KEY_INCLUDE_SUBDIRS, checkBoxIncludeSubdirectories);
        storage.setToggleButton(KEY_REPLACE_EXISTING_FILES, checkBoxReplaceExistingFiles);
    }

    private class EditThread implements Runnable {
        @Override
        public void run() {
            setIsRunning(true);
            fileEditor.setConfirmOverwrite(!checkBoxReplaceExistingFiles.isSelected());

            int filesCount = selectedFiles.size();

            initProgressBar(filesCount);

            for (int i = 0; (i < filesCount) &&!cancel; i++) {
                File file = selectedFiles.get(i);

                fileEditor.edit(file);
                labelCurrentFile.setText(file.getAbsolutePath());
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
            buttonCancel.setEnabled(runs);
            buttonSelectFiles.setEnabled(!runs);
            checkBoxIncludeSubdirectories.setEnabled(!runs);
            checkBoxReplaceExistingFiles.setEnabled(!runs);
            labelCurrentFile.setText("");
            isRunning = runs;
            cancel    = false;
        }
    }


    private void setFilesToList(final List<File> fileList) {
        listFiles.setModel(new javax.swing.AbstractListModel() {
            private static final long serialVersionUID = 1L;
            private final List<File> files = fileList;

            @Override
            public int getSize() {
                return files.size();
            }

            @Override
            public Object getElementAt(int i) {
                return files.get(i);
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        labelDescription = new javax.swing.JLabel();
        panelOptions = new javax.swing.JPanel();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        checkBoxReplaceExistingFiles = new javax.swing.JCheckBox();
        labelInfoFiles = new javax.swing.JLabel();
        scrollPaneListFiles = new javax.swing.JScrollPane();
        listFiles = new org.jdesktop.swingx.JXList();
        progressBar = new javax.swing.JProgressBar();
        labelPromptCurrentFile = new javax.swing.JLabel();
        labelCurrentFile = new javax.swing.JLabel();
        buttonSelectFiles = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        setName("Form"); // NOI18N

        labelDescription.setText("Beschreibung"); // NOI18N
        labelDescription.setName("labelDescription"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/xmpmodule/Bundle"); // NOI18N
        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FileEditorPanel.panelOptions.border.title"))); // NOI18N
        panelOptions.setName("panelOptions"); // NOI18N

        checkBoxIncludeSubdirectories.setText(bundle.getString("FileEditorPanel.checkBoxIncludeSubdirectories.text")); // NOI18N
        checkBoxIncludeSubdirectories.setName("checkBoxIncludeSubdirectories"); // NOI18N

        checkBoxReplaceExistingFiles.setText(bundle.getString("FileEditorPanel.checkBoxReplaceExistingFiles.text")); // NOI18N
        checkBoxReplaceExistingFiles.setName("checkBoxReplaceExistingFiles"); // NOI18N

        javax.swing.GroupLayout panelOptionsLayout = new javax.swing.GroupLayout(panelOptions);
        panelOptions.setLayout(panelOptionsLayout);
        panelOptionsLayout.setHorizontalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOptionsLayout.createSequentialGroup()
                .addGroup(panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxReplaceExistingFiles)
                    .addComponent(checkBoxIncludeSubdirectories))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        panelOptionsLayout.setVerticalGroup(
            panelOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOptionsLayout.createSequentialGroup()
                .addComponent(checkBoxIncludeSubdirectories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkBoxReplaceExistingFiles)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        labelInfoFiles.setText(bundle.getString("FileEditorPanel.labelInfoFiles.text")); // NOI18N
        labelInfoFiles.setName("labelInfoFiles"); // NOI18N

        scrollPaneListFiles.setName("scrollPaneListFiles"); // NOI18N

        listFiles.setCellRenderer(new org.jphototagger.lib.swing.FileSystemListCellRenderer(true));
        listFiles.setEnabled(false);
        listFiles.setName("listFiles"); // NOI18N
        scrollPaneListFiles.setViewportView(listFiles);

        progressBar.setName("progressBar"); // NOI18N

        labelPromptCurrentFile.setText(bundle.getString("FileEditorPanel.labelPromptCurrentFile.text")); // NOI18N
        labelPromptCurrentFile.setName("labelPromptCurrentFile"); // NOI18N

        labelCurrentFile.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFile.setName("labelCurrentFile"); // NOI18N

        buttonSelectFiles.setText(bundle.getString("FileEditorPanel.buttonSelectFiles.text")); // NOI18N
        buttonSelectFiles.setName("buttonSelectFiles"); // NOI18N
        buttonSelectFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectFilesActionPerformed(evt);
            }
        });

        buttonCancel.setText(bundle.getString("FileEditorPanel.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonStart.setText(bundle.getString("FileEditorPanel.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
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
                    .addComponent(scrollPaneListFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(panelOptions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(labelDescription, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                    .addComponent(labelInfoFiles)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(labelPromptCurrentFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonSelectFiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonStart))
                            .addComponent(labelCurrentFile, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelPromptCurrentFile)
                    .addComponent(labelCurrentFile, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonSelectFiles)
                    .addComponent(buttonStart))
                .addContainerGap())
        );
    }//GEN-END:initComponents

    private void buttonSelectFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectFilesActionPerformed
        handleSelectFilesActionPerformed();
    }//GEN-LAST:event_buttonSelectFilesActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        handleStartActionPerformed();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel = true;
    }//GEN-LAST:event_buttonCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonSelectFiles;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JCheckBox checkBoxReplaceExistingFiles;
    private javax.swing.JLabel labelCurrentFile;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelInfoFiles;
    private javax.swing.JLabel labelPromptCurrentFile;
    private org.jdesktop.swingx.JXList listFiles;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneListFiles;
    // End of variables declaration//GEN-END:variables
}
