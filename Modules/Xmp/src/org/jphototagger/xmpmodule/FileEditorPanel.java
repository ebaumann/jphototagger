package org.jphototagger.xmpmodule;

import java.awt.Container;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
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
import org.openide.util.Lookup;

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
    private List<File> selectedFiles = new ArrayList<>();
    private List<File> selectedDirectories = new ArrayList<>();
    private File prevSelectedDirectory = new File("");
    private FileEditor fileEditor = new FileEditor();
    private FileFilter fileChooserFileFilter = createFileChooserFilter();
    private java.io.FileFilter dirChooserFileFilter = new RegexFileFilter(".*", ";");
    private String title = "";
    private volatile boolean selectDirs;
    private volatile boolean cancel;
    private volatile boolean isRunning;

    public FileEditorPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
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

        org.jphototagger.resources.UiFactory.configure(this);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private List<File> getFilesOfDirectories(List<File> selectedDirectories) {
        List<File> selFiles = new ArrayList<>();
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
        List<File> allDirs = new ArrayList<>();
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

        dlg.setPreferencesKey("FileEditorPanel.DirChooser");
        dlg.setVisible(true);
        ComponentUtil.parentWindowToFront(this);

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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prevSelectedDirectory = new File(prefs.getString(KEY_DIRECTORY_NAME));
        prefs.applyComponentSettings(this, null);
        checkBoxIncludeSubdirectories.setSelected(prefs.getBoolean(KEY_INCLUDE_SUBDIRS));
        checkBoxReplaceExistingFiles.setSelected(prefs.getBoolean(KEY_REPLACE_EXISTING_FILES));
    }

    public void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setComponent(this, null);
        prefs.setString(KEY_DIRECTORY_NAME, prevSelectedDirectory.getAbsolutePath());
        prefs.setToggleButton(KEY_INCLUDE_SUBDIRS, checkBoxIncludeSubdirectories);
        prefs.setToggleButton(KEY_REPLACE_EXISTING_FILES, checkBoxReplaceExistingFiles);
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
        listFiles.setModel(new javax.swing.AbstractListModel<File>() {

            private static final long serialVersionUID = 1L;
            private final List<File> files = fileList;

            @Override
            public int getSize() {
                return files.size();
            }

            @Override
            public File getElementAt(int i) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = org.jphototagger.resources.UiFactory.panel();
        labelDescription = org.jphototagger.resources.UiFactory.label();
        panelOptions = org.jphototagger.resources.UiFactory.panel();
        checkBoxIncludeSubdirectories = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxReplaceExistingFiles = org.jphototagger.resources.UiFactory.checkBox();
        labelInfoFiles = org.jphototagger.resources.UiFactory.label();
        scrollPaneListFiles = org.jphototagger.resources.UiFactory.scrollPane();
        listFiles = org.jphototagger.resources.UiFactory.jxList();
        progressBar = org.jphototagger.resources.UiFactory.progressBar();
        labelPromptCurrentFile = org.jphototagger.resources.UiFactory.label();
        labelCurrentFile = org.jphototagger.resources.UiFactory.label();
        panelButtons = org.jphototagger.resources.UiFactory.panel();
        buttonSelectFiles = org.jphototagger.resources.UiFactory.button();
        buttonCancel = org.jphototagger.resources.UiFactory.button();
        buttonStart = org.jphototagger.resources.UiFactory.button();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelDescription.setText("Beschreibung"); // NOI18N
        labelDescription.setName("labelDescription"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelDescription, gridBagConstraints);

        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "FileEditorPanel.panelOptions.border.title"))); // NOI18N
        panelOptions.setName("panelOptions"); // NOI18N
        panelOptions.setLayout(new java.awt.GridBagLayout());

        checkBoxIncludeSubdirectories.setText(Bundle.getString(getClass(), "FileEditorPanel.checkBoxIncludeSubdirectories.text")); // NOI18N
        checkBoxIncludeSubdirectories.setName("checkBoxIncludeSubdirectories"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelOptions.add(checkBoxIncludeSubdirectories, gridBagConstraints);

        checkBoxReplaceExistingFiles.setText(Bundle.getString(getClass(), "FileEditorPanel.checkBoxReplaceExistingFiles.text")); // NOI18N
        checkBoxReplaceExistingFiles.setName("checkBoxReplaceExistingFiles"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelOptions.add(checkBoxReplaceExistingFiles, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelOptions, gridBagConstraints);

        labelInfoFiles.setText(Bundle.getString(getClass(), "FileEditorPanel.labelInfoFiles.text")); // NOI18N
        labelInfoFiles.setName("labelInfoFiles"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(labelInfoFiles, gridBagConstraints);

        scrollPaneListFiles.setName("scrollPaneListFiles"); // NOI18N
        scrollPaneListFiles.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(400, 200));

        listFiles.setCellRenderer(new org.jphototagger.lib.swing.FileSystemListCellRenderer(true));
        listFiles.setEnabled(false);
        listFiles.setName("listFiles"); // NOI18N
        scrollPaneListFiles.setViewportView(listFiles);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(scrollPaneListFiles, gridBagConstraints);

        progressBar.setName("progressBar"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(progressBar, gridBagConstraints);

        labelPromptCurrentFile.setText(Bundle.getString(getClass(), "FileEditorPanel.labelPromptCurrentFile.text")); // NOI18N
        labelPromptCurrentFile.setName("labelPromptCurrentFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(labelPromptCurrentFile, gridBagConstraints);

        labelCurrentFile.setForeground(new java.awt.Color(0, 0, 255));
        labelCurrentFile.setName("labelCurrentFile"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelContent.add(labelCurrentFile, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonSelectFiles.setText(Bundle.getString(getClass(), "FileEditorPanel.buttonSelectFiles.text")); // NOI18N
        buttonSelectFiles.setName("buttonSelectFiles"); // NOI18N
        buttonSelectFiles.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectFilesActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSelectFiles, new java.awt.GridBagConstraints());

        buttonCancel.setText(Bundle.getString(getClass(), "FileEditorPanel.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonCancel, gridBagConstraints);

        buttonStart.setText(Bundle.getString(getClass(), "FileEditorPanel.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonStart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        add(panelContent, gridBagConstraints);
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
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPaneListFiles;
    // End of variables declaration//GEN-END:variables
}
