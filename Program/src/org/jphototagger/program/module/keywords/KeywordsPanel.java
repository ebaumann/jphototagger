package org.jphototagger.program.module.keywords;

import java.awt.CardLayout;
import java.awt.Container;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SortOrder;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTree;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.swingx.ListTextFilter;
import org.jphototagger.lib.swingx.SearchInJxListAction;
import org.jphototagger.lib.swingx.SearchInJxTreeAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class KeywordsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private ListTextFilter listTextFilter;
    private String keyTree = "KeywordsPanel.Tree.SelectedNode";
    private String keyCard = "KeywordsPanel.Card";

    public KeywordsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setRowHeight(0);
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

    public void setListModel(ListModel<?> model) {
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.applyTreeSettings(keyTree, tree);
        readCardProperties();
    }

    private void readCardProperties() {
        String name = "Tree";
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(keyCard)) {
            String s = prefs.getString(keyCard);
            if (s.equals("Tree") || s.equals("List")) {
                name = s;
            }
        }
        displayCard(name);
    }

    private void displayCard(String name) {
        CardLayout cl = (CardLayout) (getLayout());
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        cl.show(this, name);
        prefs.setString(keyCard, name);
    }

    /**
     * Writes the persistent properties, currently the selected tree node.
     */
    public void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setTree(keyTree, tree);
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
        boolean expand = buttonToggleExpandAllNodes.isSelected();

        TreeUtil.expandAll(tree, expand);

        // Because root handle is invisible, all nodes would disappear when
        // collapsing it
        if (!expand && !tree.isRootVisible()) {
            tree.expandPath(new TreePath((TreeNode) tree.getModel().getRoot()));
        }

        buttonToggleExpandAllNodes.setText(expand
                                           ? Bundle.getString(KeywordsPanel.class, "KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                                           : Bundle.getString(KeywordsPanel.class, "KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTree = UiFactory.panel();
        scrollPaneTree = UiFactory.scrollPane();
        tree = UiFactory.jxTree();
        tree.setShowsRootHandles(true);
        panelButtons = UiFactory.panel();
        buttonToggleExpandAllNodes = UiFactory.toggleButton();
        buttonSearchInTree = UiFactory.button();
        buttonAsList = UiFactory.button();
        panelList = UiFactory.panel();
        panelListFilter = UiFactory.panel();
        labelListFilter = UiFactory.label();
        textFieldListFilter = UiFactory.textField();
        scrollPaneList = UiFactory.scrollPane();
        list = UiFactory.jxList();
        list.setTransferHandler(new org.jphototagger.program.datatransfer.DragListItemsTransferHandler(org.jphototagger.program.datatransfer.Flavor.KEYWORDS_LIST));
        buttonSearchInList = UiFactory.button();
        buttonAsTree = UiFactory.button();

        
        setLayout(new java.awt.CardLayout());

        panelTree.setName("panelTree"); // NOI18N
        panelTree.setLayout(new java.awt.GridBagLayout());

        scrollPaneTree.setName("scrollPaneTree"); // NOI18N

        tree.setModel(org.jphototagger.lib.swing.WaitTreeModel.INSTANCE);
        tree.setCellRenderer(new org.jphototagger.program.module.keywords.tree.KeywordsTreeCellRenderer());
        tree.setDragEnabled(true);
        tree.setName("tree"); // NOI18N
        scrollPaneTree.setViewportView(tree);
        tree.setTransferHandler(new org.jphototagger.program.module.keywords.tree.KeywordsTreeTransferHandler());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTree.add(scrollPaneTree, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonToggleExpandAllNodes.setText(Bundle.getString(getClass(), "KeywordsPanel.buttonToggleExpandAllNodes.text")); // NOI18N
        buttonToggleExpandAllNodes.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonToggleExpandAllNodes.setName("buttonToggleExpandAllNodes"); // NOI18N
        buttonToggleExpandAllNodes.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToggleExpandAllNodesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(2, 0, 2, 2);
        panelButtons.add(buttonToggleExpandAllNodes, gridBagConstraints);

        buttonSearchInTree.setAction(new SearchInJxTreeAction((JXTree)tree, true));
        buttonSearchInTree.setText(Bundle.getString(getClass(), "KeywordsPanel.buttonSearchInTree.text")); // NOI18N
        buttonSearchInTree.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInTree.setName("buttonSearchInTree"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = UiFactory.insets(2, 0, 2, 2);
        panelButtons.add(buttonSearchInTree, gridBagConstraints);

        buttonAsList.setText(Bundle.getString(getClass(), "KeywordsPanel.buttonAsList.text")); // NOI18N
        buttonAsList.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonAsList.setName("buttonAsList"); // NOI18N
        buttonAsList.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(2, 0, 2, 0);
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

        labelListFilter.setText(Bundle.getString(getClass(), "KeywordsPanel.labelListFilter.text")); // NOI18N
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
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListFilter.add(textFieldListFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelList.add(panelListFilter, gridBagConstraints);

        scrollPaneList.setName("scrollPaneList"); // NOI18N

        list.setModel(org.jphototagger.lib.swing.WaitListModel.INSTANCE);
        list.setCellRenderer(new org.jphototagger.program.module.keywords.list.KeywordsListCellRenderer());
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
        buttonSearchInList.setText(Bundle.getString(getClass(), "KeywordsPanel.buttonSearchInList.text")); // NOI18N
        buttonSearchInList.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonSearchInList.setName("buttonSearchInList"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 2);
        panelList.add(buttonSearchInList, gridBagConstraints);

        buttonAsTree.setText(Bundle.getString(getClass(), "KeywordsPanel.buttonAsTree.text")); // NOI18N
        buttonAsTree.setMargin(UiFactory.insets(1, 1, 1, 1));
        buttonAsTree.setName("buttonAsTree"); // NOI18N
        buttonAsTree.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(2, 2, 2, 2);
        panelList.add(buttonAsTree, gridBagConstraints);

        add(panelList, "List");
    }

    private void buttonToggleExpandAllNodesActionPerformed(java.awt.event.ActionEvent evt) {
        handleButtonToggleExpandAllNodesActionPerformed();
    }

    private void buttonAsListActionPerformed(java.awt.event.ActionEvent evt) {
        displayCard("List");
    }

    private void buttonAsTreeActionPerformed(java.awt.event.ActionEvent evt) {
        displayCard("Tree");
    }

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
}
