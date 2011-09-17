package org.jphototagger.program.view.panels;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SortOrder;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.swingx.ListTextFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.actions.SearchInJxListAction;
import org.jphototagger.program.controller.actions.SearchInJxTreeAction;
import org.jphototagger.program.datatransfer.DragListItemsTransferHandler;
import org.jphototagger.program.model.WaitListModel;
import org.jphototagger.program.model.WaitTreeModel;
import org.jphototagger.program.view.renderer.KeywordHighlightPredicate;
import org.jphototagger.program.view.renderer.KeywordsListCellRenderer;

/**
 *
 * @author Elmar Baumann
 */
public class KeywordsPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 5968799511284000903L;
    private ListTextFilter listTextFilter;
    private String keyTree = "KeywordsPanel.Tree.SelectedNode";
    private String keyCard = "KeywordsPanel.Card";

    public KeywordsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void decorateList() {
        listTextFilter = new ListTextFilter(list);
        listTextFilter.filterOnDocumentChanges(textFieldListFilter.getDocument());
        list.setAutoCreateRowSorter(true);
        list.setSortOrder(SortOrder.ASCENDING);
        list.addHighlighter(KeywordHighlightPredicate.getHighlighter());
    }

    public JTree getTree() {
        return tree;
    }

    public JXList getList() {
        return list;
    }

    public void setListModel(ListModel model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        list.setModel(model);
        decorateList();
    }

    public void setKeyCard(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        keyCard = key;
    }

    public void setKeyTree(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        keyTree = key;
    }

    /**
     * Reads the persistent properties, currently the selected tree node.
     */
    public void readProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.applyTreeSettings(keyTree, tree);
        readCardProperties();
    }

    private void readCardProperties() {
        String name = "Tree";
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        if (storage.containsKey(keyCard)) {
            String s = storage.getString(keyCard);

            if (s.equals("Tree") || s.equals("List")) {
                name = s;
            }
        }

        displayCard(name);
    }

    private void displayCard(String name) {
        CardLayout cl = (CardLayout) (getLayout());
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        cl.show(this, name);
        storage.setString(keyCard, name);
    }

    /**
     * Writes the persistent properties, currently the selected tree node.
     */
    public void writeProperties() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setTree(keyTree, tree);
    }

    /**
     * Expands or collapses all tree nodes and synchronizes the toggle button
     * for expanding and collapsing.
     *
     * @param expand true if expand, false if collapse
     */
    public void expandAll(boolean expand) {
        boolean buttonPressed = buttonToggleExpandAllNodes.isSelected();

        if (buttonPressed != expand) {
            buttonToggleExpandAllNodes.doClick();
        }
    }

    /**
     * Returns wether all nodes expanded.
     *
     * @return true if all nodes expanded
     */
    public boolean isExpandedAll() {
        return buttonToggleExpandAllNodes.isSelected();
    }

    private void handleButtonToggleExpandAllNodesActionPerformed() {
        boolean selected = buttonToggleExpandAllNodes.isSelected();

        TreeUtil.expandAll(tree, selected);
        buttonToggleExpandAllNodes.setText(selected
                                           ? Bundle.getString(KeywordsPanel.class, "KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                                           : Bundle.getString(KeywordsPanel.class, "KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
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

        panelTree = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new JXTree();
        tree.setShowsRootHandles(true);
        panelButtons = new javax.swing.JPanel();
        buttonToggleExpandAllNodes = new javax.swing.JToggleButton();
        buttonSearchInTree = new javax.swing.JButton();
        buttonAsList = new javax.swing.JButton();
        panelList = new javax.swing.JPanel();
        panelListFilter = new javax.swing.JPanel();
        labelListFilter = new javax.swing.JLabel();
        textFieldListFilter = new javax.swing.JTextField();
        scrollPaneList = new javax.swing.JScrollPane();
        list = new JXList();
        list.setTransferHandler(new DragListItemsTransferHandler(org.jphototagger.program.datatransfer.Flavor.KEYWORDS_LIST));
        buttonSearchInList = new javax.swing.JButton();
        buttonAsTree = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.CardLayout());

        panelTree.setName("panelTree"); // NOI18N
        panelTree.setLayout(new java.awt.GridBagLayout());

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N

        tree.setModel(WaitTreeModel.INSTANCE);
        tree.setCellRenderer(new org.jphototagger.program.view.renderer.KeywordsTreeCellRenderer());
        tree.setDragEnabled(true);
        tree.setName("tree"); // NOI18N
        scrollPaneTree.setViewportView(tree);
        tree.setTransferHandler(new org.jphototagger.program.datatransfer.KeywordsTreeTransferHandler());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTree.add(scrollPaneTree, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        buttonToggleExpandAllNodes.setText(bundle.getString("KeywordsPanel.buttonToggleExpandAllNodes.text")); // NOI18N
        buttonToggleExpandAllNodes.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonToggleExpandAllNodes.setName("buttonToggleExpandAllNodes"); // NOI18N
        buttonToggleExpandAllNodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToggleExpandAllNodesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelButtons.add(buttonToggleExpandAllNodes, gridBagConstraints);

        buttonSearchInTree.setAction(new SearchInJxTreeAction((JXTree)tree));
        buttonSearchInTree.setText(bundle.getString("KeywordsPanel.buttonSearchInTree.text")); // NOI18N
        buttonSearchInTree.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonSearchInTree.setName("buttonSearchInTree"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelButtons.add(buttonSearchInTree, gridBagConstraints);

        buttonAsList.setText(bundle.getString("KeywordsPanel.buttonAsList.text")); // NOI18N
        buttonAsList.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonAsList.setName("buttonAsList"); // NOI18N
        buttonAsList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        panelButtons.add(buttonAsList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panelTree.add(panelButtons, gridBagConstraints);

        add(panelTree, "Tree");

        panelList.setName("panelList"); // NOI18N
        panelList.setLayout(new java.awt.GridBagLayout());

        panelListFilter.setName("panelListFilter"); // NOI18N
        panelListFilter.setLayout(new java.awt.GridBagLayout());

        labelListFilter.setText(bundle.getString("KeywordsPanel.labelListFilter.text")); // NOI18N
        labelListFilter.setName("labelListFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelListFilter.add(labelListFilter, gridBagConstraints);

        textFieldListFilter.setName("textFieldListFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelListFilter.add(textFieldListFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panelList.add(panelListFilter, gridBagConstraints);

        scrollPaneList.setName("scrollPaneList"); // NOI18N

        list.setModel(WaitListModel.INSTANCE);
        list.setCellRenderer(new KeywordsListCellRenderer());
        list.setDragEnabled(true);
        list.setName("list"); // NOI18N
        scrollPaneList.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelList.add(scrollPaneList, gridBagConstraints);

        buttonSearchInList.setAction(new SearchInJxListAction(list));
        buttonSearchInList.setText(bundle.getString("KeywordsPanel.buttonSearchInList.text")); // NOI18N
        buttonSearchInList.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonSearchInList.setName("buttonSearchInList"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panelList.add(buttonSearchInList, gridBagConstraints);

        buttonAsTree.setText(bundle.getString("KeywordsPanel.buttonAsTree.text")); // NOI18N
        buttonAsTree.setMargin(new java.awt.Insets(1, 1, 1, 1));
        buttonAsTree.setName("buttonAsTree"); // NOI18N
        buttonAsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panelList.add(buttonAsTree, gridBagConstraints);

        add(panelList, "List");
    }//GEN-END:initComponents

    private void buttonToggleExpandAllNodesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToggleExpandAllNodesActionPerformed
        handleButtonToggleExpandAllNodesActionPerformed();
    }//GEN-LAST:event_buttonToggleExpandAllNodesActionPerformed

    private void buttonAsListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAsListActionPerformed
        displayCard("List");
    }//GEN-LAST:event_buttonAsListActionPerformed

    private void buttonAsTreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAsTreeActionPerformed
        displayCard("Tree");
    }//GEN-LAST:event_buttonAsTreeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAsList;
    private javax.swing.JButton buttonAsTree;
    private javax.swing.JButton buttonSearchInList;
    private javax.swing.JButton buttonSearchInTree;
    private javax.swing.JToggleButton buttonToggleExpandAllNodes;
    private javax.swing.JLabel labelListFilter;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelList;
    private javax.swing.JPanel panelListFilter;
    private javax.swing.JPanel panelTree;
    private javax.swing.JScrollPane scrollPaneList;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JTextField textFieldListFilter;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
