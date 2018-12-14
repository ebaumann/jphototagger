package org.jphototagger.program.misc;

import javax.swing.JTree;

/**
 * @author Elmar Baumann
 */
public class MiscXmpMetadataPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public MiscXmpMetadataPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tree.setRowHeight(0);
    }

    public JTree getTree() {
        return tree;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = org.jphototagger.resources.UiFactory.scrollPane();
        tree = new javax.swing.JTree();
        tree.setTransferHandler(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeTransferHandler());
        tree.setShowsRootHandles(true);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setName("scrollPane"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setCellRenderer(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeCellRenderer());
        tree.setDragEnabled(true);
        tree.setName("tree"); // NOI18N
        tree.setRootVisible(false);
        scrollPane.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
