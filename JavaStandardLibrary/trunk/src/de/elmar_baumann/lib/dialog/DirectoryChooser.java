package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.model.TreeModelDirectories;
import de.elmar_baumann.lib.persistence.PersistentComponentSizes;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Verzeichnisauswahldialog.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class DirectoryChooser extends Dialog {

    private boolean accepted = false;
    private boolean multiSelection = true;
    private boolean acceptHidden = true;
    private File startDirectory = new File(""); // NOI18N
    private TreeModel model;

    /**
     * Erzeugt einen modalen Verzeichnisauswahldialog.
     * 
     * @param parent        Elternframe
     * @param acceptHidden  true, if show hidden directories. Default: true
     */
    public DirectoryChooser(java.awt.Frame parent, boolean acceptHidden) {
        super(parent, true);
        this.acceptHidden = acceptHidden;
        initComponents();
        postInitComponents();
    }

    /**
     * Sets a model. Usually this is an instance of 
     * {@link de.elmar_baumann.lib.model.TreeModelDirectories} used by
     * other trees to save time.
     * 
     * @param model  model. Default: new instance of
     *               {@link de.elmar_baumann.lib.model.TreeModelDirectories}
     */
    public void setModel(TreeModel model) {
        this.model = model;
    }

    private void postInitComponents() {
        setIcons();
        registerKeyStrokes();
    }

    private void setIcons() {
        if (Settings.INSTANCE.hasIconImages()) {
            setIconImages(IconUtil.getIconImages(
                Settings.INSTANCE.getIconImagesPaths()));
        }
    }

    private void setSelectionMode() {
        treeDirectories.getSelectionModel().setSelectionMode(
            multiSelection
            ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
            : TreeSelectionModel.SINGLE_TREE_SELECTION);
        setTitle();
        setUsageText();
    }

    private void setUsageText() {
        labelUsage.setText(
            multiSelection
            ? Bundle.getString("DirectoryChooser.LabelUsage.MultipleSelection")
            : Bundle.getString("DirectoryChooser.LabelUsage.SingleSelection"));
    }

    private void setTitle() {
        setTitle(
            multiSelection
            ? Bundle.getString("DirectoryChooser.Title.MultipleSelection")
            : Bundle.getString("DirectoryChooser.Title.SingleSelection"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            PersistentComponentSizes.getSizeAndLocation(this);
            setSelectionMode();
            selectStartDirectory();
        } else {
            PersistentComponentSizes.setSizeAndLocation(this);
        }
        super.setVisible(visible);
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
     * Setzt, ob mehr als ein Verzeichnis ausgewählt werden kann.
     * 
     * @param enable true, wenn mehr als ein Verzeichnis ausgewählt werden
     *    kann (Default). false, wenn nur ein einziges Verzeichnis ausgewählt
     *    werden kann.
     */
    public void setMultiSelection(boolean enable) {
        multiSelection = enable;
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
                    files.add(new File(((File) path[filecount - 1]).getAbsolutePath()));
                }
            }
        }
        return files;
    }

    /**
     * Setzt das Startverzeichnis.
     * 
     * @param startDirectory  Name des Startverzeichnisses
     */
    public void setStartDirectory(File startDirectory) {
        this.startDirectory = startDirectory;
    }

    private void selectStartDirectory() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (model == null) {
                    model = new TreeModelDirectories(acceptHidden);
                }
                treeDirectories.setModel(model);
                if (!startDirectory.getName().isEmpty()) {
                    TreePath path = TreeUtil.getTreePath(
                        startDirectory, treeDirectories.getModel());
                    if (path != null) {
                        TreeUtil.expandPathCascade(treeDirectories, path);
                        treeDirectories.setSelectionPath(path);
                        treeDirectories.scrollPathToVisible(path);
                    }
                }
            }
        }).start();
    }

    private void cancel() {
        accepted = false;
        PersistentComponentSizes.setSizeAndLocation(this);
        dispose();
    }

    private void checkOk() {
        if (treeDirectories.getSelectionCount() > 0 &&
            treeDirectories.getSelectionPath().getLastPathComponent() instanceof File) {
            accepted = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                Bundle.getString("DirectoryChooser.ErrorMessage.NoDirectoryChosen"),
                Bundle.getString("DirectoryChooser.ErrorMessage.NoDirectoryChosen.Title"),
                JOptionPane.ERROR_MESSAGE);
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

        scrollPaneTreeDirectories = new javax.swing.JScrollPane();
        treeDirectories = new javax.swing.JTree();
        buttonChoose = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        labelUsage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/lib/resource/Bundle"); // NOI18N
        setTitle(bundle.getString("DirectoryChooser.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        treeDirectories.setCellRenderer(new de.elmar_baumann.lib.renderer.TreeCellRendererDirectories());
        treeDirectories.setModel(null);
        scrollPaneTreeDirectories.setViewportView(treeDirectories);

        buttonChoose.setMnemonic('a');
        buttonChoose.setText(bundle.getString("DirectoryChooser.buttonChoose.text")); // NOI18N
        buttonChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseActionPerformed(evt);
            }
        });

        buttonCancel.setMnemonic('b');
        buttonCancel.setText(bundle.getString("DirectoryChooser.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        labelUsage.setFont(new java.awt.Font("Dialog", 0, 12));
        labelUsage.setText(bundle.getString("DirectoryChooser.labelUsage.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addComponent(labelUsage, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonChoose)
                    .addComponent(buttonCancel))
                .addContainerGap())
        );

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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DirectoryChooser dialog = new DirectoryChooser(new javax.swing.JFrame(), false);
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
    private javax.swing.JLabel labelUsage;
    private javax.swing.JScrollPane scrollPaneTreeDirectories;
    private javax.swing.JTree treeDirectories;
    // End of variables declaration//GEN-END:variables
}
