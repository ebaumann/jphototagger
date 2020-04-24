package org.jphototagger.lib.swing;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Folder to choose one or multiple directories. Can create new directories, delete and rename existing.
 *
 * @author Elmar Baumann
 */
public final class DirectoryChooser extends DialogExt implements TreeSelectionListener, PopupMenuListener {

    private static final long serialVersionUID = 1L;
    private final File startDirectory;
    private final List<Option> options;
    private final AllSystemDirectoriesTreeModel model;
    private boolean accepted;
    private boolean titleSet;

    public enum Option {

        /**
         * Show hidden directories
         */
        DISPLAY_HIDDEN_DIRECTORIES,
        /**
         * Multiple directories can be selected (Default: Single selection)
         */
        MULTI_SELECTION, NO_OPTION
    }

    /**
     * @param owner
     * @param startDirectory start directory, will be selected or {@code new File("")}
     * @param options options
     */
    public DirectoryChooser(java.awt.Frame owner, File startDirectory, Option... options) {
        this(owner, startDirectory, Collections.<File>emptyList(), options);
    }

    /**
     * @param owner
     * @param startDirectory start directory, will be selected or {@code new File("")}
     * @param options options
     */
    public DirectoryChooser(JDialog owner, File startDirectory, Option... options) {
        this(owner, startDirectory, Collections.<File>emptyList(), options);
    }

    public DirectoryChooser(java.awt.Frame parent, File startDirectory, Collection<? extends File> excludeRootDirectories, Option... options) {
        super(parent, true);
        this.startDirectory = Objects.requireNonNull(startDirectory, "startDirectory == null");
        this.options = Arrays.asList(Objects.requireNonNull(options, "options == null"));
        initComponents();
        this.model = new AllSystemDirectoriesTreeModel(tree, Objects.requireNonNull(excludeRootDirectories, "excludeRootDirectories == null"), getIsShowHiddenDirsFilter());
        postInitComponents();
    }

    public DirectoryChooser(JDialog owner, File startDirectory, Collection<? extends File> excludeRootDirectories, Option... options) {
        super(owner, true);
        this.startDirectory = Objects.requireNonNull(startDirectory, "startDirectory == null");
        this.options = Arrays.asList(Objects.requireNonNull(options, "options == null"));
        initComponents();
        this.model = new AllSystemDirectoriesTreeModel(tree, Objects.requireNonNull(excludeRootDirectories, "excludeRootDirectories == null"), getIsShowHiddenDirsFilter());
        postInitComponents();
    }

    private void postInitComponents() {
        tree.setModel(model);
        tree.addTreeSelectionListener(this);
        tree.setRowHeight(0);
        popupMenu.addPopupMenuListener(this);
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setSelectionMode() {
        tree.getSelectionModel().setSelectionMode(
                options.contains(Option.MULTI_SELECTION)
                ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
                : TreeSelectionModel.SINGLE_TREE_SELECTION);
        setDefaultTitle();
        setUsageText();
    }

    private void setUsageText() {
        labelUsage.setText(options.contains(Option.MULTI_SELECTION)
                ? Bundle.getString(DirectoryChooser.class, "DirectoryChooser.LabelUsage.MultipleSelection")
                : Bundle.getString(DirectoryChooser.class, "DirectoryChooser.LabelUsage.SingleSelection"));
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        titleSet = true;
    }

    private void setDefaultTitle() {
        if (titleSet) {
            return;
        }
        super.setTitle(options.contains(Option.MULTI_SELECTION)
                ? Bundle.getString(DirectoryChooser.class, "DirectoryChooser.Title.MultipleSelection")
                : Bundle.getString(DirectoryChooser.class, "DirectoryChooser.Title.SingleSelection"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            restoreSizeAndLocation();
            setSelectionMode();
            selectStartDirectory();
            if (lookupAutoScanDirectories()) {
                model.startAutoUpdate();
            }
        }
        super.setVisible(visible);
    }

    private static boolean lookupAutoScanDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(PreferencesKeys.KEY_AUTOSCAN_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_AUTOSCAN_DIRECTORIES)
                : false;
    }

    /**
     * Liefert, ob der Benutzer (mindestens) ein Verzeichnis auswählte.
     *
     * @return true, wenn ein Verzeichnis ausgewählt wurde und der DialogExt nicht abgebrochen wurde
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Liefert alle selektierten Verzeichnisse.
     *
     * @return Verzeichnisse
     */
    public List<File> getSelectedDirectories() {
        List<File> files = new ArrayList<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path1 : paths) {
                Object[] path = path1.getPath();
                int filecount = path.length;
                if ((path != null) && (filecount >= 1)) {
                    Object userObject = ((DefaultMutableTreeNode) path[filecount - 1]).getUserObject();
                    if (userObject instanceof File) {
                        files.add((File) userObject);
                    }
                }
            }
        }
        return files;
    }

    private void selectStartDirectory() {
        if (startDirectory.isDirectory()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    model.expandToFile(startDirectory, true);
                }
            });
        }
    }

    private DirectoryFilter.Option getIsShowHiddenDirsFilter() {
        return options.contains(Option.DISPLAY_HIDDEN_DIRECTORIES)
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private void cancel() {
        model.stopAutoUpdate();
        accepted = false;
        super.setVisible(false);
    }

    private void ok() {
        if (tree.getSelectionCount() > 0) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            Object userObject = selNode.getUserObject();
            if (userObject instanceof File) {
                accepted = true;
                model.stopAutoUpdate();
                super.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        Bundle.getString(DirectoryChooser.class, "DirectoryChooser.Error.NoDirectoryChosen"),
                        Bundle.getString(DirectoryChooser.class, "DirectoryChooser.Error.NoDirectoryChosen.Title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void escape() {
        model.stopAutoUpdate();
        super.escape();
    }

    private void refresh() {
        model.update();
    }

    private void addDirectory() {
        TreePath[] selPaths = tree.getSelectionPaths();
        model.createDirectoryIn(
                TreeFileSystemDirectories.getNodeOfLastPathComponent(selPaths[0]));
    }

    private void renameDirectory() {
        TreePath[] selPaths = tree.getSelectionPaths();
        for (TreePath treePath : selPaths) {
            DefaultMutableTreeNode node = TreeFileSystemDirectories.getNodeOfLastPathComponent(treePath);
            File dir = (node == null)
                    ? null
                    : TreeFileSystemDirectories.getFile(node);
            if (dir != null) {
                File newDir = TreeFileSystemDirectories.rename(dir);
                if (newDir != null) {
                    node.setUserObject(newDir);
                    TreeFileSystemDirectories.updateInTreeModel(model, node);
                    model.update();
                }
            }
        }
    }

    private void deleteDirectory() {
        TreePath[] selPaths = tree.getSelectionPaths();
        for (TreePath treePath : selPaths) {
            DefaultMutableTreeNode node = TreeFileSystemDirectories.getNodeOfLastPathComponent(treePath);
            File dir = (node == null)
                    ? null
                    : TreeFileSystemDirectories.getFile(node);
            if (dir != null) {
                if (TreeFileSystemDirectories.delete(dir)) {
                    TreeFileSystemDirectories.removeFromTreeModel(model, node);
                }
            }
        }
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        Object node = tree.getLastSelectedPathComponent();
        boolean allDirOptionsPossible = allDirActionsPossible();
        menuItemAdd.setEnabled(!isWorkspace(node));
        menuItemDelete.setEnabled(allDirOptionsPossible && isDeleteDirectoriesEnabled());
        menuItemRename.setEnabled(allDirOptionsPossible);
    }

    private boolean isDeleteDirectoriesEnabled() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs != null && prefs.containsKey(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                : true;
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {
        // ignore
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        Object node = tree.getLastSelectedPathComponent();
        boolean allDirActionsPossible = allDirActionsPossible();
        boolean isWorkspace = isWorkspace(node);
        buttonAdd.setEnabled(!isWorkspace);
        buttonChoose.setEnabled(!isWorkspace);
        buttonDelete.setEnabled(allDirActionsPossible && isDeleteDirectoriesEnabled());
        buttonRename.setEnabled(allDirActionsPossible);
    }

    private boolean allDirActionsPossible() {
        Object node = tree.getLastSelectedPathComponent();
        return (node != null) && !isWorkspace(node) && !isRootFile(node);
    }

    private boolean isWorkspace(Object o) {
        return o == tree.getModel().getRoot();
    }

    private boolean isRootFile(Object o) {
        if (o instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
            TreeNode parentNode = node.getParent();
            Object rootNode = tree.getModel().getRoot();
            return parentNode == rootNode;
        }
        return false;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenu = UiFactory.popupMenu();
        menuItemAdd = UiFactory.menuItem();
        menuItemRename = UiFactory.menuItem();
        menuItemDelete = UiFactory.menuItem();
        panelContents = UiFactory.panel();
        scrollPane = UiFactory.scrollPane();
        tree = UiFactory.tree();
        labelUsage = UiFactory.label();
        panelActionButtons = UiFactory.panel();
        buttonRefresh = UiFactory.button();
        buttonAdd = UiFactory.button();
        buttonDelete = UiFactory.button();
        buttonRename = UiFactory.button();
        panelDialogButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonChoose = UiFactory.button();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemAdd.setText(Bundle.getString(getClass(), "DirectoryChooser.menuItemAdd.text")); // NOI18N
        menuItemAdd.setName("menuItemAdd"); // NOI18N
        menuItemAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAdd);

        menuItemRename.setText(Bundle.getString(getClass(), "DirectoryChooser.menuItemRename.text")); // NOI18N
        menuItemRename.setName("menuItemRename"); // NOI18N
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemRename);

        menuItemDelete.setText(Bundle.getString(getClass(), "DirectoryChooser.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "DirectoryChooser.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContents.setName("panelContents"); // NOI18N
        panelContents.setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N
        scrollPane.setPreferredSize(UiFactory.dimension(400, 400));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setCellRenderer(new org.jphototagger.lib.swing.AllSystemDirectoriesTreeCellRenderer());
        tree.setComponentPopupMenu(popupMenu);
        tree.setName("Tree directory chooser"); // NOI18N
        scrollPane.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelContents.add(scrollPane, gridBagConstraints);

        labelUsage.setText(Bundle.getString(getClass(), "DirectoryChooser.labelUsage.text")); // NOI18N
        labelUsage.setName("labelUsage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContents.add(labelUsage, gridBagConstraints);

        panelActionButtons.setName("panelActionButtons"); // NOI18N
        panelActionButtons.setLayout(new java.awt.GridBagLayout());

        buttonRefresh.setIcon(org.jphototagger.resources.Icons.getIcon("icon_refresh.png"));
        buttonRefresh.setToolTipText(Bundle.getString(getClass(), "DirectoryChooser.buttonRefresh.toolTipText")); // NOI18N
        buttonRefresh.setName("buttonRefresh"); // NOI18N
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        panelActionButtons.add(buttonRefresh, gridBagConstraints);

        buttonAdd.setText(Bundle.getString(getClass(), "DirectoryChooser.buttonAdd.text")); // NOI18N
        buttonAdd.setEnabled(false);
        buttonAdd.setName("buttonAdd"); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelActionButtons.add(buttonAdd, gridBagConstraints);

        buttonDelete.setText(Bundle.getString(getClass(), "DirectoryChooser.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelActionButtons.add(buttonDelete, gridBagConstraints);

        buttonRename.setText(Bundle.getString(getClass(), "DirectoryChooser.buttonRename.text")); // NOI18N
        buttonRename.setEnabled(false);
        buttonRename.setName("buttonRename"); // NOI18N
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelActionButtons.add(buttonRename, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContents.add(panelActionButtons, gridBagConstraints);

        panelDialogButtons.setName("panelDialogButtons"); // NOI18N
        panelDialogButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setMnemonic('b');
        buttonCancel.setText(Bundle.getString(getClass(), "DirectoryChooser.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelDialogButtons.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonChoose.setMnemonic('a');
        buttonChoose.setText(Bundle.getString(getClass(), "DirectoryChooser.buttonChoose.text")); // NOI18N
        buttonChoose.setName("buttonChoose"); // NOI18N
        buttonChoose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelDialogButtons.add(buttonChoose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 0, 0, 0);
        panelContents.add(panelDialogButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContents, gridBagConstraints);

        pack();
    }

    private void buttonChooseActionPerformed(java.awt.event.ActionEvent evt) {
        ok();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        cancel();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        cancel();
    }

    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        refresh();
    }

    private void menuItemAddActionPerformed(java.awt.event.ActionEvent evt) {
        addDirectory();
    }

    private void menuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {
        renameDirectory();
    }

    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        deleteDirectory();
    }

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {
        addDirectory();
    }

    private void buttonRenameActionPerformed(java.awt.event.ActionEvent evt) {
        renameDirectory();
    }

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        deleteDirectory();
    }

    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChoose;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JButton buttonRename;
    private javax.swing.JLabel labelUsage;
    private javax.swing.JMenuItem menuItemAdd;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemRename;
    private javax.swing.JPanel panelActionButtons;
    private javax.swing.JPanel panelContents;
    private javax.swing.JPanel panelDialogButtons;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree tree;
}
