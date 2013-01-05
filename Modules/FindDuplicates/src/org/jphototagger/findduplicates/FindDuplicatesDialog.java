package org.jphototagger.findduplicates;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.FileSystemViewListCellRenderer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemProperties;
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
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final SourceDirectoriesListModel sourceDirectoriesListModel = new SourceDirectoriesListModel();

    FindDuplicatesDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        buttonRemoveSourceDirectories.setAction(new RemoveSelectedSourceDirsAction());
        buttonSearch.setAction(new SearchAction());
        MnemonicUtil.setMnemonics(this);
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
                ListUtil.sort(delegate, dirSortComparator);
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

    private static final Comparator<File> dirSortComparator = new Comparator<File>() {

        private final Comparator<String> delegate = String.CASE_INSENSITIVE_ORDER;

        @Override
        public int compare(File o1, File o2) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            return delegate.compare(name1, name2);
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

        public RemoveSelectedSourceDirsAction() {
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

    private class SearchAction extends AbstractAction implements ListDataListener {

        private final String START_SEARCH_NAME = Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesDialog.SearchAction.Name.StartSearch");
        private final String CANCEL_SEARCH_NAME = Bundle.getString(FindDuplicatesAction.class, "FindDuplicatesDialog.SearchAction.Name.CancelSearch");

        public SearchAction() {
            init();
        }

        private void init() {
            setName();
            setEnabled();
            sourceDirectoriesListModel.addListDataListener(this);
        }

        private void setName() {
            putValue(Action.NAME, buttonSearch.isSelected()
                    ? CANCEL_SEARCH_NAME
                    : START_SEARCH_NAME);
        }

        private boolean isCancel() {
            return buttonSearch.isSelected();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
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
        checkBoxSourceDirsRecursive = new javax.swing.JCheckBox();
        buttonAddSourceDirectory = new javax.swing.JButton();
        buttonRemoveSourceDirectories = new javax.swing.JButton();
        panelOptions = new javax.swing.JPanel();
        checkBoxCompareOnlyEqualFilenames = new javax.swing.JCheckBox();
        checkBoxCompareOnlyEqualDates = new javax.swing.JCheckBox();
        panelSearchActions = new javax.swing.JPanel();
        labelCloseInfo = new javax.swing.JLabel();
        buttonSearch = new javax.swing.JToggleButton();
        panelResult = new javax.swing.JPanel();
        scrollPaneResult = new javax.swing.JScrollPane();
        panelResultActions = new javax.swing.JPanel();
        buttonDeleteSelectedFiles = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/findduplicates/Bundle"); // NOI18N
        setTitle(bundle.getString("FindDuplicatesDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labelInfo.setText(bundle.getString("FindDuplicatesDialog.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelContent.add(labelInfo, gridBagConstraints);

        panelSourceDirectories.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FindDuplicatesDialog.panelSourceDirectories.border.title"))); // NOI18N
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelSourceDirectories.add(scrollPaneSourceDirectories, gridBagConstraints);

        panelSourceDirectoriesActions.setLayout(new java.awt.GridBagLayout());

        checkBoxSourceDirsRecursive.setText(bundle.getString("FindDuplicatesDialog.checkBoxSourceDirsRecursive.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(checkBoxSourceDirsRecursive, gridBagConstraints);

        buttonAddSourceDirectory.setText(bundle.getString("FindDuplicatesDialog.buttonAddSourceDirectory.text")); // NOI18N
        buttonAddSourceDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddSourceDirectoryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(buttonAddSourceDirectory, gridBagConstraints);

        buttonRemoveSourceDirectories.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSourceDirectoriesActions.add(buttonRemoveSourceDirectories, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelSourceDirectories.add(panelSourceDirectoriesActions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelSourceDirectories, gridBagConstraints);

        panelOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FindDuplicatesDialog.panelOptions.border.title"))); // NOI18N
        panelOptions.setLayout(new java.awt.GridBagLayout());

        checkBoxCompareOnlyEqualFilenames.setText(bundle.getString("FindDuplicatesDialog.checkBoxCompareOnlyEqualFilenames.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelOptions.add(checkBoxCompareOnlyEqualFilenames, gridBagConstraints);

        checkBoxCompareOnlyEqualDates.setText(bundle.getString("FindDuplicatesDialog.checkBoxCompareOnlyEqualDates.text")); // NOI18N
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
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelOptions, gridBagConstraints);

        panelSearchActions.setLayout(new java.awt.GridBagLayout());

        labelCloseInfo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelCloseInfo.setText(bundle.getString("FindDuplicatesDialog.labelCloseInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelSearchActions.add(labelCloseInfo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelSearchActions.add(buttonSearch, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelContent.add(panelSearchActions, gridBagConstraints);

        panelResult.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FindDuplicatesDialog.panelResult.border.title"))); // NOI18N
        panelResult.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelResult.add(scrollPaneResult, gridBagConstraints);

        panelResultActions.setLayout(new java.awt.GridBagLayout());

        buttonDeleteSelectedFiles.setText(bundle.getString("FindDuplicatesDialog.buttonDeleteSelectedFiles.text")); // NOI18N
        buttonDeleteSelectedFiles.setEnabled(false);
        panelResultActions.add(buttonDeleteSelectedFiles, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
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
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddSourceDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddSourceDirectoryActionPerformed
        addSourceDirectories();
    }//GEN-LAST:event_buttonAddSourceDirectoryActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FindDuplicatesDialog dialog = new FindDuplicatesDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddSourceDirectory;
    private javax.swing.JButton buttonDeleteSelectedFiles;
    private javax.swing.JButton buttonRemoveSourceDirectories;
    private javax.swing.JToggleButton buttonSearch;
    private javax.swing.JCheckBox checkBoxCompareOnlyEqualDates;
    private javax.swing.JCheckBox checkBoxCompareOnlyEqualFilenames;
    private javax.swing.JCheckBox checkBoxSourceDirsRecursive;
    private javax.swing.JLabel labelCloseInfo;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JList<File> listSourceDirectories;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelOptions;
    private javax.swing.JPanel panelResult;
    private javax.swing.JPanel panelResultActions;
    private javax.swing.JPanel panelSearchActions;
    private javax.swing.JPanel panelSourceDirectories;
    private javax.swing.JPanel panelSourceDirectoriesActions;
    private javax.swing.JScrollPane scrollPaneResult;
    private javax.swing.JScrollPane scrollPaneSourceDirectories;
    // End of variables declaration//GEN-END:variables

}
