/*
 * @(#)KeywordsPanel.java    Created on 2009-07-10
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.view.panels;

import org.jphototagger.program.datatransfer.TransferHandlerDragListItems;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.renderer.ListCellRendererKeywords;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.componentutil.TreeUtil;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

/**
 * A tree for keywords.
 *
 * @author  Elmar Baumann
 */
public class KeywordsPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = 5968799511284000903L;
    private String            keyTree          =
        "KeywordsPanel.Tree.SelectedNode";
    private String            keyCard          = "KeywordsPanel.Card";

    public KeywordsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        MnemonicUtil.setMnemonics((Container) this);
    }

    public JTree getTree() {
        return tree;
    }

    public JList getList() {
        return list;
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
        UserSettings.INSTANCE.getSettings().applySettings(tree, keyTree);
        readCardProperties();
    }

    private void readCardProperties() {
        String name = "Tree";

        if (UserSettings.INSTANCE.getProperties().containsKey(keyCard)) {
            String s = UserSettings.INSTANCE.getSettings().getString(keyCard);

            if (s.equals("Tree") || s.equals("List")) {
                name = s;
            }
        }

        displayCard(name);
    }

    private void displayCard(String name) {
        CardLayout cl = (CardLayout) (getLayout());

        cl.show(this, name);
        UserSettings.INSTANCE.getSettings().set(name, keyCard);
        UserSettings.INSTANCE.writeToFile();
    }

    /**
     * Writes the persistent properties, currently the selected tree node.
     */
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tree, keyTree);
        UserSettings.INSTANCE.writeToFile();
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
                                           ? JptBundle.INSTANCE.getString(
                                           "KeywordsPanel.ButtonToggleExpandAllNodes.Selected")
                                           : JptBundle.INSTANCE.getString(
                                           "KeywordsPanel.ButtonToggleExpandAllNodes.DeSelected"));
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelTree = new javax.swing.JPanel();
        scrollPaneTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        panelButtons = new javax.swing.JPanel();
        buttonAsList = new javax.swing.JButton();
        buttonToggleExpandAllNodes = new javax.swing.JToggleButton();
        panelList = new javax.swing.JPanel();
        scrollPaneList = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        list.setTransferHandler(new TransferHandlerDragListItems(org.jphototagger.program.datatransfer.Flavor.KEYWORDS_LIST));
        buttonAsTree = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        panelTree.setLayout(new java.awt.GridBagLayout());

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setCellRenderer(new org.jphototagger.program.view.renderer.TreeCellRendererKeywords());
        tree.setDragEnabled(true);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        scrollPaneTree.setViewportView(tree);
        tree.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerKeywordsTree());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelTree.add(scrollPaneTree, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonAsList.setText(JptBundle.INSTANCE.getString("KeywordsPanel.buttonAsList.text")); // NOI18N
        buttonAsList.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonAsList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsListActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panelButtons.add(buttonAsList, gridBagConstraints);

        buttonToggleExpandAllNodes.setText(JptBundle.INSTANCE.getString("KeywordsPanel.buttonToggleExpandAllNodes.text")); // NOI18N
        buttonToggleExpandAllNodes.setMargin(new java.awt.Insets(2, 2, 2, 2));
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panelTree.add(panelButtons, gridBagConstraints);

        add(panelTree, "Tree");

        panelList.setLayout(new java.awt.GridBagLayout());

        list.setCellRenderer(new ListCellRendererKeywords());
        list.setDragEnabled(true);
        scrollPaneList.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelList.add(scrollPaneList, gridBagConstraints);

        buttonAsTree.setText(JptBundle.INSTANCE.getString("KeywordsPanel.buttonAsTree.text")); // NOI18N
        buttonAsTree.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonAsTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAsTreeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panelList.add(buttonAsTree, gridBagConstraints);

        add(panelList, "List");
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JToggleButton buttonToggleExpandAllNodes;
    private javax.swing.JList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelList;
    private javax.swing.JPanel panelTree;
    private javax.swing.JScrollPane scrollPaneList;
    private javax.swing.JScrollPane scrollPaneTree;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
