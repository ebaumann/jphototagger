package de.elmar_baumann.lib.dialog;

import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.persistence.PersistentAppSizes;
import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.resource.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Verzeichnisauswahldialog.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class DirectoryChooser extends javax.swing.JDialog {

    private boolean accepted = false;
    private boolean multiSelection = true;
    private File startDirectory = new File(""); // NOI18N

    /**
     * Erzeugt einen modalen Verzeichnisauswahldialog.
     * 
     * @param parent Elternframe
     */
    public DirectoryChooser(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setIcons();
    }

    private void setIcons() {
        Settings settings = Settings.getInstance();
        if (settings.hasIconImages()) {
            setIconImages(IconUtil.getIconImages(settings.getIconImagesPaths()));
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
            PersistentAppSizes.getSizeAndLocation(this);
            setSelectionMode();
            selectStartDirectory();
        } else {
            PersistentAppSizes.setSizeAndLocation(this);
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
        String delimiter = "|"; // NOI18N
        TreePath path = TreeUtil.getTreePath(
            treeDirectories.getModel(),
            treeDirectories.getModel().getRoot().toString() +
            delimiter + toPathString(startDirectory, delimiter),
            delimiter);
        if (path != null) {
            treeDirectories.setSelectionPath(path);
            treeDirectories.scrollPathToVisible(path);
        }
    }

    private String toPathString(File file, String delimiter) {
        Stack<File> path = FileUtil.getPathFromRoot(file);
        StringBuffer pathString = new StringBuffer();
        while (!path.isEmpty()) {
            File f = path.pop();
            String n = f.getName();
            String p = f.getPath();
            String name = n.isEmpty() ? p : n;
            while (name.endsWith("\\")) { // Windows-Laufwerksbuchstabe  // NOI18N
                name = name.substring(0, name.length() - 1);
            }
            pathString.append(name + (path.isEmpty() ? "" : delimiter)); // NOI18N
        }
        return pathString.toString();
    }

    private void cancel() {
        accepted = false;
        setVisible(false);
    }

    private void checkOk() {
        if (treeDirectories.getSelectionCount() > 0) {
            accepted = true;
            setVisible(false);
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
        treeDirectories.setModel(new de.elmar_baumann.lib.model.TreeModelDirectories());
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
                    .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(labelUsage, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
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
                .addComponent(scrollPaneTreeDirectories, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
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
                DirectoryChooser dialog = new DirectoryChooser(new javax.swing.JFrame());
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
