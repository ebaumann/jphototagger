package org.jphototagger.lib.dialog;

import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.lib.resource.JslBundle;

import java.awt.Container;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Folder to choose one or multiple directories. Can create new directories,
 * delete and rename existing.
 *
 * @author Elmar Baumann
 */
public final class DirectoryChooser extends Dialog implements TreeSelectionListener, PopupMenuListener {
    private static final long serialVersionUID = -4494883459031977084L;
    private final File startDirectory;
    private final List<Option> directoryFilter;
    private boolean accepted;
    private final TreeModelAllSystemDirectories model;

    public enum Option {

        /** Show hidden directories */
        DISPLAY_HIDDEN_DIRECTORIES,

        /** Multiple directories can be selected (Default: Single selection) */
        MULTI_SELECTION, NO_OPTION
    }

    /**
     * Creates an instance.
     *
     * @param parent          Elternframe
     * @param startDirectory  start directory, will be selected or {@code new File("")}
     * @param options         options
     */
    public DirectoryChooser(java.awt.Frame parent, File startDirectory, Option... options) {
        this(parent, startDirectory, Collections.<File>emptyList(), options);
    }

    public DirectoryChooser(java.awt.Frame parent, File startDirectory,
            Collection<? extends File> excludeRootDirectories, Option... options) {
        super(parent, true);

        if (startDirectory == null) {
            throw new NullPointerException("startDirectory == null");
        }

        if (excludeRootDirectories == null) {
            throw new NullPointerException("excludeRootDirectories == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        this.startDirectory  = startDirectory;
        this.directoryFilter = Arrays.asList(options);
        initComponents();
        this.model = new TreeModelAllSystemDirectories(tree, excludeRootDirectories, getIsShowHiddenDirsFilter());
        postInitComponents();
    }

    private void postInitComponents() {
        tree.setModel(model);
        tree.addTreeSelectionListener(this);
        popupMenu.addPopupMenuListener(this);
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setSelectionMode() {
        tree.getSelectionModel().setSelectionMode(
            directoryFilter.contains(Option.MULTI_SELECTION)
            ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
            : TreeSelectionModel.SINGLE_TREE_SELECTION);
        setTitle();
        setUsageText();
    }

    private void setUsageText() {
        labelUsage.setText(directoryFilter.contains(Option.MULTI_SELECTION)
                           ? JslBundle.INSTANCE.getString("DirectoryChooser.LabelUsage.MultipleSelection")
                           : JslBundle.INSTANCE.getString("DirectoryChooser.LabelUsage.SingleSelection"));
    }

    private void setTitle() {
        setTitle(directoryFilter.contains(Option.MULTI_SELECTION)
                 ? JslBundle.INSTANCE.getString("DirectoryChooser.Title.MultipleSelection")
                 : JslBundle.INSTANCE.getString("DirectoryChooser.Title.SingleSelection"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            applySizeAndLocation();
            setSelectionMode();
            selectStartDirectory();
        }

        super.setVisible(visible);
    }

    /**
     * Liefert, ob der Benutzer (mindestens) ein Verzeichnis auswählte.
     *
     * @return true, wenn ein Verzeichnis ausgewählt wurde und der Dialog
     *         nicht abgebrochen wurde
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
        List<File> files = new ArrayList<File>();
        TreePath[] paths = tree.getSelectionPaths();

        if (paths != null) {
            for (int index = 0; index < paths.length; index++) {
                Object[] path = paths[index].getPath();
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
            EventQueueUtil.invokeLater(new Runnable() {

                @Override
                public void run() {
                    model.expandToFile(startDirectory, true);
                }
            });
        }
    }

    private DirectoryFilter.Option getIsShowHiddenDirsFilter() {
        return directoryFilter.contains(Option.DISPLAY_HIDDEN_DIRECTORIES)
               ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
               : DirectoryFilter.Option.NO_OPTION;
    }

    private void cancel() {
        accepted = false;
        super.setVisible(false);
    }

    private void ok() {
        if (tree.getSelectionCount() > 0) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            Object userObject = selNode.getUserObject();

            if (userObject instanceof File) {
                accepted = true;
                super.setVisible(false);
            } else {
                JOptionPane
                    .showMessageDialog(
                    this,
                    JslBundle.INSTANCE.getString("DirectoryChooser.Error.NoDirectoryChosen"),
                    JslBundle.INSTANCE.getString("DirectoryChooser.Error.NoDirectoryChosen.Title"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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
        boolean allDirOptions = allDirActionsPossible();

        menuItemAdd.setEnabled(!isWorkspace(node));
        menuItemDelete.setEnabled(allDirOptions);
        menuItemRename.setEnabled(allDirOptions);
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
        boolean allDirActions = allDirActionsPossible();
        boolean isWorkspace = isWorkspace(node);

        buttonAdd.setEnabled(!isWorkspace);
        buttonChoose.setEnabled(!isWorkspace);
        buttonDelete.setEnabled(allDirActions);
        buttonRename.setEnabled(allDirActions);
    }

    private boolean allDirActionsPossible() {
        Object node = tree.getLastSelectedPathComponent();

        return (node != null) &&!isWorkspace(node) &&!isRootFile(node);
    }

    private boolean isWorkspace(Object o) {
        return o == tree.getModel().getRoot();
    }

    private boolean isRootFile(Object o) {
        if (o instanceof DefaultMutableTreeNode) {
            return ((DefaultMutableTreeNode) o).getParent()
                   == tree.getModel().getRoot();
        }

        return false;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        menuItemAdd = new javax.swing.JMenuItem();
        menuItemRename = new javax.swing.JMenuItem();
        menuItemDelete = new javax.swing.JMenuItem();
        scrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        labelUsage = new javax.swing.JLabel();
        buttonRefresh = new javax.swing.JButton();
        buttonAdd = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        buttonRename = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        buttonChoose = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemAdd.setText(JslBundle.INSTANCE.getString("DirectoryChooser.menuItemAdd.text")); // NOI18N
        menuItemAdd.setName("menuItemAdd"); // NOI18N
        menuItemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAdd);

        menuItemRename.setText(JslBundle.INSTANCE.getString("DirectoryChooser.menuItemRename.text")); // NOI18N
        menuItemRename.setName("menuItemRename"); // NOI18N
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemRename);

        menuItemDelete.setText(JslBundle.INSTANCE.getString("DirectoryChooser.menuItemDelete.text")); // NOI18N
        menuItemDelete.setName("menuItemDelete"); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JslBundle.INSTANCE.getString("DirectoryChooser.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        scrollPane.setName("scrollPane"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setCellRenderer(new org.jphototagger.lib.renderer.TreeCellRendererAllSystemDirectories());
        tree.setComponentPopupMenu(popupMenu);
        tree.setName("Tree directory chooser"); // NOI18N
        scrollPane.setViewportView(tree);

        labelUsage.setText(JslBundle.INSTANCE.getString("DirectoryChooser.labelUsage.text")); // NOI18N
        labelUsage.setName("labelUsage"); // NOI18N

        buttonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/lib/resource/icons/icon_refresh.png"))); // NOI18N
        buttonRefresh.setToolTipText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonRefresh.toolTipText")); // NOI18N
        buttonRefresh.setName("buttonRefresh"); // NOI18N
        buttonRefresh.setPreferredSize(new java.awt.Dimension(25, 25));
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });

        buttonAdd.setText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonAdd.text")); // NOI18N
        buttonAdd.setEnabled(false);
        buttonAdd.setName("buttonAdd"); // NOI18N
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });

        buttonDelete.setText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.setName("buttonDelete"); // NOI18N
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonRename.setText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonRename.text")); // NOI18N
        buttonRename.setEnabled(false);
        buttonRename.setName("buttonRename"); // NOI18N
        buttonRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameActionPerformed(evt);
            }
        });

        buttonCancel.setMnemonic('b');
        buttonCancel.setText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonChoose.setMnemonic('a');
        buttonChoose.setText(JslBundle.INSTANCE.getString("DirectoryChooser.buttonChoose.text")); // NOI18N
        buttonChoose.setName("buttonChoose"); // NOI18N
        buttonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addComponent(labelUsage, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(buttonRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonAdd)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonDelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonRename))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(buttonCancel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonChoose))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAdd, buttonCancel, buttonChoose, buttonDelete, buttonRename});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUsage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonAdd)
                        .addComponent(buttonDelete)
                        .addComponent(buttonRename))
                    .addComponent(buttonRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChoose)
                    .addComponent(buttonCancel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonAdd, buttonCancel, buttonChoose, buttonDelete, buttonRefresh, buttonRename});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseActionPerformed
        ok();
    }//GEN-LAST:event_buttonChooseActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancel();
    }//GEN-LAST:event_formWindowClosing

    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        refresh();
    }//GEN-LAST:event_buttonRefreshActionPerformed

    private void menuItemAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAddActionPerformed
        addDirectory();
    }//GEN-LAST:event_menuItemAddActionPerformed

    private void menuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRenameActionPerformed
        renameDirectory();
    }//GEN-LAST:event_menuItemRenameActionPerformed

    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionPerformed
        deleteDirectory();
    }//GEN-LAST:event_menuItemDeleteActionPerformed

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
        addDirectory();
    }//GEN-LAST:event_buttonAddActionPerformed

    private void buttonRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameActionPerformed
        renameDirectory();
    }//GEN-LAST:event_buttonRenameActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        deleteDirectory();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DirectoryChooser dialog =
                    new DirectoryChooser(new javax.swing.JFrame(),
                                         new File(""));

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
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
