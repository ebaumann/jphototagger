/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.image.util.IconUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Resources;
import de.elmar_baumann.lib.util.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Folder to choose one or multiple directories. Can create new directories,
 * delete and rename existing.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class DirectoryChooser extends Dialog {

    private static final long                          serialVersionUID = -4494883459031977084L;
    private final        File                          startDirectory;
    private final        Set<Option>                   directoryFilter;
    private              boolean                       accepted;
    private final        TreeModelAllSystemDirectories model;

    public enum Option {

        /** show hidden directories */
        ACCEPT_HIDDEN_DIRECTORIES,
        /** hide hidden directories */
        REJECT_HIDDEN_DIRECTORIES,
        /** multiple directories can be selected */
        MULTI_SELECTION,
        /** only one directory can be selected */
        SINGLE_SELECTION
    }

    /**
     * Creates an instance.
     *
     * @param parent          Elternframe
     * @param startDirectory  start directory, will be selected or {@code new File("")}
     * @param options         options
     */
    public DirectoryChooser(java.awt.Frame parent, File startDirectory,
            Set<Option> options) {
        super(parent, true);
        this.startDirectory = startDirectory;
        this.directoryFilter = options;
        initComponents();
        model = new TreeModelAllSystemDirectories(
                treeDirectories, getTreeModelFilter());
        treeDirectories.setModel(model);
        postInitComponents();
    }

    private void postInitComponents() {
        setIcons();
        registerKeyStrokes();
    }

    private void setIcons() {
        if (Resources.INSTANCE.hasFrameIconImages()) {
            setIconImages(IconUtil.getIconImages(
                    Resources.INSTANCE.getFramesIconImagesPaths()));
        }
    }

    private void setSelectionMode() {
        treeDirectories.getSelectionModel().setSelectionMode(
                directoryFilter.contains(Option.MULTI_SELECTION)
                ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
                : TreeSelectionModel.SINGLE_TREE_SELECTION);
        setTitle();
        setUsageText();
    }

    private void setUsageText() {
        labelUsage.setText(
                directoryFilter.contains(Option.MULTI_SELECTION)
                ? Bundle.getString("DirectoryChooser.LabelUsage.MultipleSelection")
                : Bundle.getString("DirectoryChooser.LabelUsage.SingleSelection"));
    }

    private void setTitle() {
        setTitle(
                directoryFilter.contains(Option.MULTI_SELECTION)
                ? Bundle.getString("DirectoryChooser.Title.MultipleSelection")
                : Bundle.getString("DirectoryChooser.Title.SingleSelection"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
            setSelectionMode();
            selectStartDirectory();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        Properties properties = Resources.INSTANCE.getProperties();
        if (properties != null) {
            Settings settings = new Settings(properties);
            settings.getSizeAndLocation(this);
        }
    }

    private void writeProperties() {
        Properties properties = Resources.INSTANCE.getProperties();
        if (properties != null) {
            Settings settings = new Settings(properties);
            settings.setSizeAndLocation(this);
        }
    }

    /**
     * Liefert, ob der Benutzer (mindestens) ein Verzeichnis auswählte.
     *
     * @return true, wenn ein Verzeichnis ausgewählt wurde und der Dialog
     *         nicht abgebrochen wurde
     */
    public boolean accepted() {
        return accepted;
    }

    /**
     * Liefert alle selektierten Verzeichnisse.
     *
     * @return Verzeichnisse
     */
    public List<File> getSelectedDirectories() {
        List<File> files = new ArrayList<File>();
        TreePath[] paths = treeDirectories.getSelectionPaths();
        if (paths != null) {
            for (int index = 0; index < paths.length; index++) {
                Object[] path = paths[index].getPath();
                int filecount = path.length;
                if (path != null && filecount >= 1) {
                    Object userObject =
                            ((DefaultMutableTreeNode) path[filecount - 1]).
                            getUserObject();
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
            model.expandToFile(startDirectory, true);
        }
    }

    private Set<DirectoryFilter.Option> getTreeModelFilter() {
        return EnumSet.of(directoryFilter.contains(
                Option.ACCEPT_HIDDEN_DIRECTORIES)
                          ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                          : DirectoryFilter.Option.REJECT_HIDDEN_FILES);
    }

    private void cancel() {
        accepted = false;
        writeProperties();
        dispose();
    }

    private void checkOk() {
        if (treeDirectories.getSelectionCount() > 0) {
            DefaultMutableTreeNode selNode =
                    (DefaultMutableTreeNode) treeDirectories.getSelectionPath().
                    getLastPathComponent();
            Object userObject = selNode.getUserObject();
            if (userObject instanceof File) {
                accepted = true;
                writeProperties();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        Bundle.getString("DirectoryChooser.Error.NoDirectoryChosen"),
                        Bundle.getString("DirectoryChooser.Error.NoDirectoryChosen.Title"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refresh() {
        model.update();
    }

    private void addDirectory() {
        if (!checkSelected()) return;
        TreePath[] selPaths = treeDirectories.getSelectionPaths();
        model.createNewDirectory(
                TreeFileSystemDirectories.getNodeOfLastPathComponent(
                selPaths[0]));
    }

    private void renameDirectory() {
        if (!checkSelected()) return;
        TreePath[] selPaths = treeDirectories.getSelectionPaths();
        for (TreePath treePath : selPaths) {
            DefaultMutableTreeNode node = TreeFileSystemDirectories.
                    getNodeOfLastPathComponent(treePath);
            File dir = node == null
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
        if (!checkSelected() || !confirmDelete()) return;
        TreePath[] selPaths = treeDirectories.getSelectionPaths();
        for (TreePath treePath : selPaths) {
            DefaultMutableTreeNode node = TreeFileSystemDirectories.
                    getNodeOfLastPathComponent(treePath);
            File dir = node == null
                       ? null
                       : TreeFileSystemDirectories.getFile(node);
            if (dir != null) {
                if (TreeFileSystemDirectories.delete(dir)) {
                    TreeFileSystemDirectories.removeFromTreeModel(model, node);
                }
            }
        }
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this,
                Bundle.getString("DirectoryChooser.Confirm.Delete"),
                Bundle.getString("DirectoryChooser.Confirm.Delete.Title"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private boolean checkSelected() {
        if (treeDirectories.getSelectionCount() <= 0) {
            JOptionPane.showMessageDialog(this,
                    Bundle.getString("DirectoryChooser.Error.NothingSelected"),
                    Bundle.getString("DirectoryChooser.Error.NothingSelected.Title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
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
        scrollPaneTreeDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        labelUsage = new javax.swing.JLabel();
        buttonCancel = new javax.swing.JButton();
        buttonChoose = new javax.swing.JButton();
        buttonRefresh = new javax.swing.JButton();

        menuItemAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icons/icon_folder_add.png")));
        menuItemAdd.setText(Bundle.getString("DirectoryChooser.menuItemAdd.text"));
        menuItemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAddActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemAdd);

        menuItemRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icons/icon_folder_rename.png")));
        menuItemRename.setText(Bundle.getString("DirectoryChooser.menuItemRename.text"));
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemRename);

        menuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icons/icon_folder_delete.png")));
        menuItemDelete.setText(Bundle.getString("DirectoryChooser.menuItemDelete.text"));
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/properties/Bundle");
        setTitle(bundle.getString("DirectoryChooser.title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeDirectories.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeDirectories.setToolTipText(Bundle.getString("DirectoryChooser.Tree directory chooser.toolTipText"));
        treeDirectories.setCellRenderer(new de.elmar_baumann.lib.renderer.TreeCellRendererAllSystemDirectories());
        treeDirectories.setComponentPopupMenu(popupMenu);
        treeDirectories.setName("Tree directory chooser");
        scrollPaneTreeDirectories.setViewportView(treeDirectories);

        labelUsage.setText(bundle.getString("DirectoryChooser.labelUsage.text"));

        buttonCancel.setMnemonic('b');
        buttonCancel.setText(bundle.getString("DirectoryChooser.buttonCancel.text"));
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        buttonChoose.setMnemonic('a');
        buttonChoose.setText(bundle.getString("DirectoryChooser.buttonChoose.text"));
        buttonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseActionPerformed(evt);
            }
        });

        buttonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/lib/resource/icons/icon_refresh.png")));
        buttonRefresh.setToolTipText(Bundle.getString("DirectoryChooser.buttonRefresh.toolTipText"));
        buttonRefresh.setPreferredSize(new java.awt.Dimension(25, 25));
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(labelUsage, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                        .addComponent(buttonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChoose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUsage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonChoose, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonRefresh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonCancel, buttonChoose, buttonRefresh});

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void buttonChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseActionPerformed
    checkOk();
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DirectoryChooser dialog = new DirectoryChooser(
                        new javax.swing.JFrame(), new File(""),
                        new HashSet<DirectoryChooser.Option>());
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
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChoose;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JLabel labelUsage;
    private javax.swing.JMenuItem menuItemAdd;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemRename;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JScrollPane scrollPaneTreeDirectories;
    private javax.swing.JTree treeDirectories;
    // End of variables declaration//GEN-END:variables
}
