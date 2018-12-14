package org.jphototagger.findduplicates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.event.FileDeletedEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.FileSystemViewListCellRenderer;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemProperties;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class FindDuplicatesDialog extends Dialog {

    private static final String KEY_SOURCE_DIRS = "FindDuplicatesDialog.SourceDirectories";
    private static final String KEY_SOURCE_DIRS_RECURSIVE = "FindDuplicatesDialog.SourceDirectoriesRecursive";
    private static final String KEY_COMPARE_ONLY_EQUAL_FILENAMES = "FindDuplicatesDialog.CompareOnlyEqualFilenames";
    private static final String KEY_COMPARE_ONLY_EQUAL_DATES = "FindDuplicatesDialog.CompareOnlyEqualDates";
    private static final String KEY_DIR_CHOOSER_START_DIRECTORY = "FindDuplicatesDialog.DirChooserStartDir";
    private static final Logger LOGGER = Logger.getLogger(FindDuplicatesDialog.class.getName());
    private static final long serialVersionUID = 1L;
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final SourceDirectoriesListModel sourceDirectoriesListModel = new SourceDirectoriesListModel();
    private final FileDuplicatesPanel panelFileDuplicates = new FileDuplicatesPanel();

    FindDuplicatesDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        buttonRemoveSourceDirectories.setAction(new RemoveSelectedSourceDirsAction());
        buttonSearch.setAction(new SearchAction());
        buttonDeleteSelectedFiles.setAction(new DeleteSelectedFilesAction());
        addFileDuplicatesPanel();
        MnemonicUtil.setMnemonics(this);
        AnnotationProcessor.process(this);
    }

    private void addFileDuplicatesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        panel.add(panelFileDuplicates, gbc);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        panel.add(new JPanel(), gbc); // Fill panel
        scrollPaneResult.setViewportView(panel);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (isVisible()) {
                toFront();
                return;
            }
            restore();
        } else {
            persist();
        }
        super.setVisible(visible);
    }

    private void persist() {
        if (prefs != null) {
            prefs.setBoolean(KEY_SOURCE_DIRS_RECURSIVE, checkBoxSourceDirsRecursive.isSelected());
            prefs.setBoolean(KEY_COMPARE_ONLY_EQUAL_DATES, checkBoxCompareOnlyEqualDates.isSelected());
            prefs.setBoolean(KEY_COMPARE_ONLY_EQUAL_FILENAMES, checkBoxCompareOnlyEqualFilenames.isSelected());
            prefs.setStringCollection(KEY_SOURCE_DIRS, FileUtil.getAbsolutePathnames(sourceDirectoriesListModel.getDirs()));
        }
    }

    private void restore() {
        if (prefs != null) {
            checkBoxSourceDirsRecursive.setSelected(prefs.containsKey(KEY_SOURCE_DIRS_RECURSIVE)
                    ? prefs.getBoolean(KEY_SOURCE_DIRS_RECURSIVE)
                    : true);
            checkBoxCompareOnlyEqualFilenames.setSelected(prefs.containsKey(KEY_COMPARE_ONLY_EQUAL_FILENAMES)
                    ? prefs.getBoolean(KEY_COMPARE_ONLY_EQUAL_FILENAMES)
                    : false);
            checkBoxCompareOnlyEqualDates.setSelected(prefs.containsKey(KEY_COMPARE_ONLY_EQUAL_DATES)
                    ? prefs.getBoolean(KEY_COMPARE_ONLY_EQUAL_DATES)
                    : false);
            sourceDirectoriesListModel.setDirs(FileUtil.getStringsAsFiles(prefs.getStringCollection(KEY_SOURCE_DIRS)));
        }
    }

    @Override
    protected void escape() {
        persist();
        super.escape();
    }

    private static class SourceDirectoriesListModel implements ListModel<File> {

        private final DefaultListModel<File> delegate = new DefaultListModel<>();

        @Override
        public int getSize() {
            return delegate.getSize();
        }

        @Override
        public File getElementAt(int index) {
            return delegate.getElementAt(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            delegate.addListDataListener(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            delegate.removeListDataListener(l);
        }

        private void remove(File dir) {
            delegate.removeElement(dir);
        }

        private void add(File dir) {
            if (dir.isDirectory() && !delegate.contains(dir)) {
                delegate.addElement(dir);
                ListUtil.sort(delegate, DIR_SORT_COMPARATOR);
            }
        }

        private void add(Collection<? extends File> dirs) {
            for (File dir : dirs) {
                add(dir);
            }
        }

        private List<File> getDirs() {
            return ListUtil.getElements(delegate);
        }

        private void setDirs(List<File> dirs) {
            delegate.clear();
            for (File dir : dirs) {
                add(dir);
            }
        }
    }

    private static final Comparator<File> DIR_SORT_COMPARATOR = new Comparator<File>() {

        private final Comparator<String> delegate = String.CASE_INSENSITIVE_ORDER;

        @Override
        public int compare(File o1, File o2) {
            String pathname1 = o1.getAbsolutePath();
            String pathname2 = o2.getAbsolutePath();
            return delegate.compare(pathname1, pathname2);
        }
    };

    private void addSourceDirectories() {
        DirectoryChooser dirChooser = new DirectoryChooser(ComponentUtil.findFrameWithIcon(), getStartDirectory(), DirectoryChooser.Option.MULTI_SELECTION);
        dirChooser.setVisible(true);
        if (dirChooser.isAccepted()) {
            List<File> selDirs = dirChooser.getSelectedDirectories();
            sourceDirectoriesListModel.add(selDirs);
            if (!selDirs.isEmpty()) {
                prefs.setString(KEY_DIR_CHOOSER_START_DIRECTORY, selDirs.get(0).getAbsolutePath());
            }
        }
    }

    private File getStartDirectory() {
        String userHomeName = SystemProperties.getUserHome();
        File dir = new File(prefs.containsKey(KEY_DIR_CHOOSER_START_DIRECTORY)
                ? prefs.getString(KEY_DIR_CHOOSER_START_DIRECTORY)
                : "");
        return dir.isDirectory()
                ? dir
                : userHomeName != null && new File(userHomeName).isDirectory()
                ? new File(userHomeName)
                : new File("");
    }

    private class RemoveSelectedSourceDirsAction extends AbstractAction implements ListSelectionListener, KeyListener {

        private static final long serialVersionUID = 1L;

        private RemoveSelectedSourceDirsAction() {
            init();
        }

        private void init() {
            putValue(Action.NAME, Bundle.getString(FindDuplicatesDialog.class, "FindDuplicatesDialog.RemoveSelectedSourceDirsAction.Name"));
            listSourceDirectories.addListSelectionListener(this);
            listSourceDirectories.addKeyListener(this);
            setEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (File selDir : listSourceDirectories.getSelectedValuesList()) {
                sourceDirectoriesListModel.remove(selDir);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setEnabled();
            }
        }

        private void setEnabled() {
            boolean dirSelected = listSourceDirectories.getSelectedIndex() >= 0;
            setEnabled(dirSelected);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // ignore
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                actionPerformed(null);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // ignore
        }
    }

    private class SearchAction extends AbstractAction implements FileDuplicatesListener, ListDataListener {

        private static final long serialVersionUID = 1L;
        private boolean listen = true;
        private final String START_SEARCH_NAME = Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesDialog.SearchAction.Name.StartSearch");
        private final String CANCEL_SEARCH_NAME = Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesDialog.SearchAction.Name.CancelSearch");
        private FileDuplicatesFinder duplicatesFinder;

        private SearchAction() {
            init();
        }

        private void init() {
            setName(false);
            setEnabled();
            sourceDirectoriesListModel.addListDataListener(this);
        }

        private void setName(boolean started) {
            putValue(Action.NAME, started
                    ? CANCEL_SEARCH_NAME
                    : START_SEARCH_NAME);
            MnemonicUtil.setMnemonics(buttonSearch);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!listen) {
                return;
        }
            if (buttonSearch.isSelected()) {
                startSearch();
            } else {
                stopSearch();
            }
        }

        private void startSearch() {
            progressBarSearch.setIndeterminate(true);
            duplicatesFinder = new FileDuplicatesFinder(sourceDirectoriesListModel.getDirs(), checkBoxSourceDirsRecursive.isSelected());
            duplicatesFinder.setCompareOnlyEqualDates(checkBoxCompareOnlyEqualDates.isSelected());
            duplicatesFinder.setCompareOnlyEqualFilenames(checkBoxCompareOnlyEqualFilenames.isSelected());
            duplicatesFinder.addFileDuplicateListener(this);
            panelFileDuplicates.clear();
            buttonDeleteSelectedFiles.setEnabled(false);
            Thread searchThread = new Thread(duplicatesFinder);
            searchThread.setName("JPhotoTagger: Searching duplicates");
            searchThread.start();
        }

        private void stopSearch() {
            if (duplicatesFinder != null) {
                duplicatesFinder.stop();
            }
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            setEnabled();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            setEnabled();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            setEnabled();
        }

        private void setEnabled() {
            boolean srcDirsExisting = sourceDirectoriesListModel.getSize() > 0;
            setEnabled(srcDirsExisting);
        }

        @Override
        public void setMessage(String message) {
            progressBarSearch.setString(message);
        }

        @Override
        public void duplicatesFound(final Collection<? extends File> duplicates) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {
                @Override
                public void run() {
                    panelFileDuplicates.addDuplicates(duplicates);
                    panelFileDuplicates.revalidate();
                }
            });
        }

        @Override
        public void searchStarted() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    setName(true);
                }
            });
        }

        @Override
        public void searchFinished(boolean wasCancelled) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    listen = false;
                    buttonSearch.setSelected(false);
                    listen = true;
                    setName(false);
                    progressBarSearch.setString("");
                    progressBarSearch.setIndeterminate(false);
                }
            });
        }
    }

    private final class DeleteSelectedFilesAction extends AbstractAction implements PropertyChangeListener {

        private static final long serialVersionUID = 1L;
        private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);

        private DeleteSelectedFilesAction() {
            super(Bundle.getString(FindDuplicatesDialog.class, "FindDuplicatesDialog.DeleteSelectedFilesAction.Name"));
            init();
        }

        private void init() {
            panelFileDuplicates.addPropertyChangeListener(FileDuplicatesPanel.PROPERTY_FILE_SELECTED, this);
            setEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = Bundle.getString(FindDuplicatesDialog.class, "FindDuplicatesDialog.DeleteSelectedFilesAction.ConfirmDelete");
            if (MessageDisplayer.confirmYesNo(FindDuplicatesDialog.this, message)) {
                for (File file : panelFileDuplicates.getSelectedFiles()) {
                    if (file.delete()) {
                        EventBus.publish(new FileDeletedEvent(this, file));
                        File xmp = xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(file);
                        if (xmp != null) {
                            if (xmp.delete()) {
                                EventBus.publish(new FileDeletedEvent(this, xmp));
                            } else {
                                LOGGER.log(Level.WARNING, "Sidecar file ''{0}'' couldn''t be deleted", xmp);
                            }
                        }
                        panelFileDuplicates.removeFile(file);
                    } else {
                        LOGGER.log(Level.WARNING, "File ''{0}'' couldn''t be deleted", file);
                    }
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            setEnabled();
        }

        private void setEnabled() {
            setEnabled(panelFileDuplicates.isFileSelected());
        }
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        labelInfo = new javax.swing.JLabel();
        panelSourceDirectories = new javax.swing.JPanel();
        scrollPaneSourceDirectories = new javax.swing.JScrollPane();
        listSourceDirectories = new javax.swing.JList<>();
        panelSourceDirectoriesActions = new javax.swing.JPanel();
        checkBoxSourceDirsRecursive = org.jphototagger.resources.UiFactory.checkBox();
        buttonAddSourceDirectory = new javax.swing.JButton();
        buttonRemoveSourceDirectories = new javax.swing.JButton();
        panelOptions = new javax.swing.JPanel();
        checkBoxCompareOnlyEqualFilenames = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxCompareOnlyEqualDates = org.jphototagger.resources.UiFactory.checkBox();
        panelSearchActions = new javax.swing.JPanel();
        progressBarSearch = new javax.swing.JProgressBar();
        buttonSearch = new javax.swing.JToggleButton();
        panelResult = new javax.swing.JPanel();
        scrollPaneResult = new javax.swing.JScrollPane();
        panelResultActions = new javax.swing.JPanel();
        buttonDeleteSelectedFiles = new javax.swing.JButton();

        setTitle(Bundle.getString(getClass(), "FindDuplicatesDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setFont(new java.awt.Font("Tahoma", 1, UiFactory.scale(12))); // NOI18N
        labelInfo.setText(Bundle.getString(getClass(), "FindDuplicatesDialog.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelContent.add(labelInfo, gridBagConstraints);

        panelSourceDirectories.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "FindDuplicatesDialog.panelSourceDirectories.border.title"))); // NOI18N
        panelSourceDirectories.setLayout(new java.awt.GridBagLayout());

        listSourceDirectories.setModel(sourceDirectoriesListModel);
        listSourceDirectories.setCellRenderer(new FileSystemViewListCellRenderer(false));
        scrollPaneSourceDirectories.setViewportView(listSourceDirectories);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelSourceDirectories.add(scrollPaneSourceDirectories, gridBagConstraints);

        panelSourceDirectoriesActions.setLayout(new java.awt.GridBagLayout());

        checkBoxSourceDirsRecursive.setText(Bundle.getString(getClass(), "FindDuplicatesDialog.checkBoxSourceDirsRecursive.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(checkBoxSourceDirsRecursive, gridBagConstraints);

        buttonAddSourceDirectory.setText(Bundle.getString(getClass(), "FindDuplicatesDialog.buttonAddSourceDirectory.text")); // NOI18N
        buttonAddSourceDirectory.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddSourceDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(buttonAddSourceDirectory, gridBagConstraints);

        buttonRemoveSourceDirectories.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(buttonRemoveSourceDirectories, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelSourceDirectories.add(panelSourceDirectoriesActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelSourceDirectories, gridBagConstraints);

        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "FindDuplicatesDialog.panelOptions.border.title"))); // NOI18N
        panelOptions.setLayout(new java.awt.GridBagLayout());

        checkBoxCompareOnlyEqualFilenames.setText(Bundle.getString(getClass(), "FindDuplicatesDialog.checkBoxCompareOnlyEqualFilenames.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelOptions.add(checkBoxCompareOnlyEqualFilenames, gridBagConstraints);

        checkBoxCompareOnlyEqualDates.setText(Bundle.getString(getClass(), "FindDuplicatesDialog.checkBoxCompareOnlyEqualDates.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelOptions.add(checkBoxCompareOnlyEqualDates, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelOptions, gridBagConstraints);

        panelSearchActions.setLayout(new java.awt.GridBagLayout());

        progressBarSearch.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSearchActions.add(progressBarSearch, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelSearchActions.add(buttonSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelSearchActions, gridBagConstraints);

        panelResult.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "FindDuplicatesDialog.panelResult.border.title"))); // NOI18N
        panelResult.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelResult.add(scrollPaneResult, gridBagConstraints);

        panelResultActions.setLayout(new java.awt.GridBagLayout());

        buttonDeleteSelectedFiles.setEnabled(false);
        panelResultActions.add(buttonDeleteSelectedFiles, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelResult.add(panelResultActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        panelContent.add(panelResult, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddSourceDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddSourceDirectoryActionPerformed
        addSourceDirectories();
    }//GEN-LAST:event_buttonAddSourceDirectoryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddSourceDirectory;
    private javax.swing.JButton buttonDeleteSelectedFiles;
    private javax.swing.JButton buttonRemoveSourceDirectories;
    private javax.swing.JToggleButton buttonSearch;
    private javax.swing.JCheckBox checkBoxCompareOnlyEqualDates;
    private javax.swing.JCheckBox checkBoxCompareOnlyEqualFilenames;
    private javax.swing.JCheckBox checkBoxSourceDirsRecursive;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JList<File> listSourceDirectories;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JPanel panelResult;
    private javax.swing.JPanel panelResultActions;
    private javax.swing.JPanel panelSearchActions;
    private javax.swing.JPanel panelSourceDirectories;
    private javax.swing.JPanel panelSourceDirectoriesActions;
    private javax.swing.JProgressBar progressBarSearch;
    private javax.swing.JScrollPane scrollPaneResult;
    private javax.swing.JScrollPane scrollPaneSourceDirectories;
    // End of variables declaration//GEN-END:variables

}
