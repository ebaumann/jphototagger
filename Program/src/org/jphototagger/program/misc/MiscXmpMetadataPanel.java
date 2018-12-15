package org.jphototagger.program.misc;

import javax.swing.JTree;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class MiscXmpMetadataPanel extends PanelExt {

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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = UiFactory.scrollPane();
        tree = UiFactory.tree();
        tree.setTransferHandler(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeTransferHandler());
        tree.setShowsRootHandles(true);

        setLayout(new java.awt.GridBagLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setCellRenderer(new org.jphototagger.program.module.miscmetadata.MiscMetadataTreeCellRenderer());
        tree.setDragEnabled(true);
        tree.setRootVisible(false);
        scrollPane.setViewportView(tree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }

    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTree tree;
}
