package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent;
import org.jphototagger.domain.metadata.event.UpdateMetadataCheckEvent.Type;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.comparator.FileSort;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.DirectoryChooser;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.io.filefilter.DirectoryFilter.Option;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.program.app.AppPreferencesKeys;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.io.ImageFileDirectory;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class UpdateMetadataOfDirectoriesPanel extends JPanel implements ProgressListener {
    private static final String KEY_LAST_DIRECTORY = "org.jphototagger.program.view.ScanDirectoriesDialog.lastSelectedDirectory";
    private static final String KEY_FORCE = "org.jphototagger.program.view.ScanDirectoriesDialog.force";
    private static final String KEY_SUBDIRECTORIES = "org.jphototagger.program.view.ScanDirectoriesDialog.subdirectories";
    private static final long serialVersionUID = -8953645248403117494L;
    private final DefaultListModel listModelDirectories = new DefaultListModel();
    private File lastDirectory = new File("");
    private static final transient Logger LOGGER = Logger.getLogger(UpdateMetadataOfDirectoriesPanel.class.getName());
    private transient InsertImageFilesIntoDatabase imageFileInserter;
    private transient volatile boolean cancelChooseDirectories;
    private final transient CancelChooseRequest cancelChooseRequest = new CancelChooseRequest();

    public UpdateMetadataOfDirectoriesPanel() {
        initComponents();
        readProperties();
        MnemonicUtil.setMnemonics((Container) this);
        AnnotationProcessor.process(this);
    }

    public void willDispose() {
        interruptImageFileInsterter();
        writeProperties();
    }

    private void handleListKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedDirectories();
        }
    }

    private void removeSelectedDirectories() {
        final int selectedIndex = list.getSelectedIndex();

        if (selectedIndex >= 0) {
            for (Object selectedValue : list.getSelectedValues()) {
                listModelDirectories.removeElement(selectedValue);
            }

            buttonStart.setEnabled(!listModelDirectories.isEmpty());
            labelFilecount.setText(Integer.toString(getFileCount()));
            ListUtil.selectNearestIndex(list, selectedIndex);
            list.requestFocusInWindow();
        }
    }

    private int getFileCount() {
        int count = 0;

        for (Object element : listModelDirectories.toArray()) {
            count += ((ImageFileDirectory) element).getImageFileCount();
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
        list.setEnabled(false);
    }

    private void setProgressBarPreStartUpdate(int filecount) {
        progressBar.setValue(0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(filecount);
    }

    private List<File> getSelectedImageFiles() {
        List<File> imageFiles = new ArrayList<File>();

        for (Object element : listModelDirectories.toArray()) {
            imageFiles.addAll(((ImageFileDirectory) element).getImageFiles());
        }

        return imageFiles;
    }

    private void createImageFileInserter(List<File> selectedImageFiles) {
        InsertIntoRepository[] insertIntoDatabase = getWhatToInsertIntoDatabase();

        imageFileInserter = new InsertImageFilesIntoDatabase(selectedImageFiles, insertIntoDatabase);
        imageFileInserter.addProgressListener(this);
    }

    private InsertIntoRepository[] getWhatToInsertIntoDatabase() {
        return checkBoxForce.isSelected()
               ? new InsertIntoRepository[] { InsertIntoRepository.EXIF, InsertIntoRepository.THUMBNAIL, InsertIntoRepository.XMP }
               : new InsertIntoRepository[] { InsertIntoRepository.OUT_OF_DATE };
    }

    private void cancelUpdate() {
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
        buttonCancel.setEnabled(isUpdate);
    }

    private void setEnabledCheckboxes(boolean isUpdate) {
        checkBoxForce.setEnabled(!isUpdate);
        checkBoxIncludeSubdirectories.setEnabled(!isUpdate);
    }

    private void setEnabledMenuItems() {
        boolean itemIsSelected = list.getSelectedIndex() >= 0;

        menuItemDelete.setEnabled(itemIsSelected && list.isEnabled());
    }

    private void readProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage != null) {
            storage.applyToggleButtonSettings(KEY_FORCE, checkBoxForce);
            storage.applyToggleButtonSettings(KEY_SUBDIRECTORIES, checkBoxIncludeSubdirectories);
            readLastDirectoryFromProperties();
        }
    }

    private void readLastDirectoryFromProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);
        String lastDirectoryName = storage.getString(KEY_LAST_DIRECTORY);

        if (!lastDirectoryName.isEmpty()) {
            File directory = new File(lastDirectoryName);

            if (directory.exists() && directory.isDirectory()) {
                lastDirectory = directory;
            }
        }
    }

    private void writeProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setToggleButton(KEY_FORCE, checkBoxForce);
        storage.setToggleButton(KEY_SUBDIRECTORIES, checkBoxIncludeSubdirectories);
        storage.setString(KEY_LAST_DIRECTORY, lastDirectory.getAbsolutePath());
    }

    /**
     * Called from the current updater.
     *
     * @param evt event containing the current filename
     */
    @EventSubscriber(eventClass = UpdateMetadataCheckEvent.class)
    public void checkForUpdate(UpdateMetadataCheckEvent evt) {
        if (evt.getType().equals(Type.CHECKING_FILE)) {
            File file = evt.getImageFile();

            if (file != null) {
                setFileLabel(file);
            }
        } else if (evt.getType().equals(Type.CHECK_FINISHED)) {
            imageFileInserter = null;
            updateFinished();
        }
    }

    private void setFileLabel(final File file) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                labelCurrentFilename.setText(file.getAbsolutePath());
            }
        });
    }

    private void updateFinished() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                setEnabledButtons(false);
                setEnabledCheckboxes(false);
                labelCurrentFilename.setText("-");
                list.setEnabled(true);
            }
        });
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setMinimum(evt.getMinimum());
                progressBar.setMaximum(evt.getMaximum());
                progressBar.setValue(evt.getValue());
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setValue(evt.getValue());
            }
        });
    }

    private void chooseDirectories() {
        List<File> hideRootFiles = SelectRootFilesPanel.readPersistentRootFiles(AppPreferencesKeys.KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES);
        DirectoryChooser dlg = new DirectoryChooser(GUI.getAppFrame(), lastDirectory, hideRootFiles, getDirChooserOptionShowHiddenDirs());

        buttonChooseDirectories.setEnabled(false);
        dlg.setStorageKey("UpdateMetadataOfDirectoriesPanel.DirChooser");
        dlg.setVisible(true);

        if (dlg.isAccepted()) {
            List<File> selectedDirs = dlg.getSelectedDirectories();

            lastDirectory = selectedDirs.get(0);
            progressBar.setIndeterminate(true);
            progressBar.setString(Bundle.getString(UpdateMetadataOfDirectoriesPanel.class, "UpdateMetadataOfDirectoriesPanel.Info.ScanningDirs"));
            cancelChooseDirectories = false;
            cancelChooseRequest.cancel = false;
            buttonCancelChooseDirectories.setEnabled(true);
            new AddNotContainedDirectories(selectedDirs).start();
        } else {
            buttonChooseDirectories.setEnabled(true);
        }
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

    private class AddNotContainedDirectories extends Thread {
        private final List<File> directories;

        AddNotContainedDirectories(List<File> directories) {
            super("JPhotoTagger: Adding directories for updating metadata");
            this.directories = new ArrayList<File>(directories);
        }

        @Override
        public void run() {
            final List<File> newDirectories = getDirectoriesNotInListModelFrom(directories);

            LOGGER.log(Level.INFO, "Adding previously added directories {0} to {1}", new Object[]{newDirectories, directories});
            CollectionUtil.addNotContainedElements(directories, newDirectories);

            if (checkBoxIncludeSubdirectories.isSelected()) {
                LOGGER.log(Level.INFO, "Adding recursively all not previously added subdirectories of {0}", newDirectories);
                addSubdirectories(newDirectories);
            }

            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                    @Override
                    public void run() {
                        addDirectories(newDirectories);
                        labelFilecount.setText(Integer.toString(getFileCount()));
                        buttonStart.setEnabled(!listModelDirectories.isEmpty());
                        buttonChooseDirectories.setEnabled(true);
                        buttonCancelChooseDirectories.setEnabled(false);
                        progressBar.setIndeterminate(false);
                        progressBar.setString(null);
                    }
                });
            }
    }

    private List<File> getDirectoriesNotInListModelFrom(List<File> directories) {
        List<File> newDirectories = new ArrayList<File>();

        LOGGER.log(Level.INFO, "Searching directories not previously added from {0}", directories);
        for (File directory : directories) {
            if (cancelChooseDirectories) {
                return newDirectories;
            }

            if (!listModelDirectories.contains(directory)) {
                LOGGER.log(Level.INFO, "Found not previously added directory {0}", directory);
                newDirectories.add(directory);
            }
        }

        return newDirectories;
    }

    private void addDirectories(List<File> directories) {
        LOGGER.log(Level.INFO, "Adding directories {0}", directories);
        Collections.sort(directories, FileSort.PATHS_ASCENDING.getComparator());

        for (File directory : directories) {
            if (cancelChooseDirectories) {
                return;
            }

            LOGGER.log(Level.INFO, "Searching image files in directory {0}", directory);
            ImageFileDirectory imageFileDir = new ImageFileDirectory(directory);

            if (imageFileDir.hasImageFiles() &&!listModelDirectories.contains(imageFileDir)) {
                listModelDirectories.addElement(imageFileDir);
            }
        }
    }

    private void addSubdirectories(List<File> directories) {
        List<File> subdirectories = new ArrayList<File>();
        Option showHiddenFiles = getDirFilterOptionShowHiddenFiles();

        for (File dir : directories) {
            if (cancelChooseDirectories) {
                return;
            }

            LOGGER.log(Level.INFO, "Searching recursively subdirectories of {0}", dir);
            subdirectories.addAll(FileUtil.getSubDirectoriesRecursive(dir, cancelChooseRequest, showHiddenFiles));
        }

        LOGGER.log(Level.INFO, "Adding from {0} not previously added directories to {1}", new Object[]{subdirectories, directories});
        CollectionUtil.addNotContainedElements(subdirectories, directories);
    }

    private DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private static class CancelChooseRequest implements CancelRequest {

        boolean cancel;

        @Override
        public boolean isCancel() {
            return cancel;
        }
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

        popupMenu = new javax.swing.JPopupMenu();
        menuItemDelete = new javax.swing.JMenuItem();
        labelHeadingListDirectories = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        labelInfoFilecount = new javax.swing.JLabel();
        labelFilecount = new javax.swing.JLabel();
        checkBoxForce = new javax.swing.JCheckBox();
        checkBoxIncludeSubdirectories = new javax.swing.JCheckBox();
        panelCurrentFile = new javax.swing.JPanel();
        labelInfoCurrentFilename = new javax.swing.JLabel();
        labelCurrentFilename = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        panelButtons = new javax.swing.JPanel();
        buttonCancelChooseDirectories = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonChooseDirectories = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N
        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupMenuPopupMenuWillBecomeVisible(evt);
            }
        });

        menuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        menuItemDelete.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelHeadingListDirectories.setLabelFor(list);
        labelHeadingListDirectories.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.labelHeadingListDirectories.text")); // NOI18N
        labelHeadingListDirectories.setName("labelHeadingListDirectories"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelHeadingListDirectories, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(listModelDirectories);
        list.setCellRenderer(new org.jphototagger.program.view.renderer.DirectoriesListCellRenderer());
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                listKeyReleased(evt);
            }
        });
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(scrollPane, gridBagConstraints);

        labelInfoFilecount.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoFilecount.text")); // NOI18N
        labelInfoFilecount.setName("labelInfoFilecount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(labelInfoFilecount, gridBagConstraints);

        labelFilecount.setForeground(new java.awt.Color(0, 153, 0));
        labelFilecount.setText("0"); // NOI18N
        labelFilecount.setName("labelFilecount"); // NOI18N
        labelFilecount.setPreferredSize(new java.awt.Dimension(4, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(labelFilecount, gridBagConstraints);

        checkBoxForce.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxForce.text")); // NOI18N
        checkBoxForce.setName("checkBoxForce"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(checkBoxForce, gridBagConstraints);

        checkBoxIncludeSubdirectories.setSelected(true);
        checkBoxIncludeSubdirectories.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.checkBoxIncludeSubdirectories.text")); // NOI18N
        checkBoxIncludeSubdirectories.setName("checkBoxIncludeSubdirectories"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(checkBoxIncludeSubdirectories, gridBagConstraints);

        panelCurrentFile.setName("panelCurrentFile"); // NOI18N
        panelCurrentFile.setLayout(new java.awt.GridBagLayout());

        labelInfoCurrentFilename.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.labelInfoCurrentFilename.text")); // NOI18N
        labelInfoCurrentFilename.setName("labelInfoCurrentFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelCurrentFile.add(labelInfoCurrentFilename, gridBagConstraints);

        labelCurrentFilename.setForeground(new java.awt.Color(51, 51, 255));
        labelCurrentFilename.setName("labelCurrentFilename"); // NOI18N
        labelCurrentFilename.setPreferredSize(new java.awt.Dimension(4, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelCurrentFile.add(labelCurrentFilename, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(panelCurrentFile, gridBagConstraints);

        progressBar.setFocusable(false);
        progressBar.setName("progressBar"); // NOI18N
        progressBar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(progressBar, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        buttonCancelChooseDirectories.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonCancelChooseDirectories.text")); // NOI18N
        buttonCancelChooseDirectories.setEnabled(false);
        buttonCancelChooseDirectories.setName("buttonCancelChooseDirectories"); // NOI18N
        buttonCancelChooseDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelChooseDirectoriesActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancelChooseDirectories);

        buttonCancel.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonCancel.text")); // NOI18N
        buttonCancel.setEnabled(false);
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel);

        buttonChooseDirectories.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonChooseDirectories.text")); // NOI18N
        buttonChooseDirectories.setName("buttonChooseDirectories"); // NOI18N
        buttonChooseDirectories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDirectoriesActionPerformed(evt);
            }
        });
        panelButtons.add(buttonChooseDirectories);

        buttonStart.setText(bundle.getString("UpdateMetadataOfDirectoriesPanel.buttonStart.text")); // NOI18N
        buttonStart.setEnabled(false);
        buttonStart.setName("buttonStart"); // NOI18N
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });
        panelButtons.add(buttonStart);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }//GEN-END:initComponents

    private void listKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyReleased
        handleListKeyReleased(evt);
    }//GEN-LAST:event_listKeyReleased

    private void buttonChooseDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDirectoriesActionPerformed
        chooseDirectories();
    }//GEN-LAST:event_buttonChooseDirectoriesActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        startUpdate();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancelUpdate();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionPerformed
        removeSelectedDirectories();
    }//GEN-LAST:event_menuItemDeleteActionPerformed

    private void popupMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupMenuPopupMenuWillBecomeVisible
        setEnabledMenuItems();
    }//GEN-LAST:event_popupMenuPopupMenuWillBecomeVisible

    private void buttonCancelChooseDirectoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelChooseDirectoriesActionPerformed
        cancelChooseDirectories = true;
        cancelChooseRequest.cancel = true;
    }//GEN-LAST:event_buttonCancelChooseDirectoriesActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonCancelChooseDirectories;
    private javax.swing.JButton buttonChooseDirectories;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkBoxForce;
    private javax.swing.JCheckBox checkBoxIncludeSubdirectories;
    private javax.swing.JLabel labelCurrentFilename;
    private javax.swing.JLabel labelFilecount;
    private javax.swing.JLabel labelHeadingListDirectories;
    private javax.swing.JLabel labelInfoCurrentFilename;
    private javax.swing.JLabel labelInfoFilecount;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCurrentFile;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
