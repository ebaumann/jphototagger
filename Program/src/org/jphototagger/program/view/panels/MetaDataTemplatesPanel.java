/*
 * @(#)MetaDataTemplatesPanel.java    Created on 2010-01-05
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

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;
import javax.swing.JList;

/**
 *
 *
 * @author  Elmar Baumann
 */
public class MetaDataTemplatesPanel extends javax.swing.JPanel
        implements ListSelectionListener, ThumbnailsPanelListener {
    private static final long serialVersionUID = 1760177225644789506L;

    public MetaDataTemplatesPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        list.addListSelectionListener(this);
        // Can be null if created via GUI editor (Matisse)
        if (GUI.INSTANCE.getAppPanel() != null) {
            GUI.INSTANCE.getAppPanel().getPanelThumbnails()
                    .addThumbnailsPanelListener(this);
        }
        MnemonicUtil.setMnemonics((Container) this);
    }

    public JList getList() {
        return list;
    }

    public JButton getButtonAdd() {
        return buttonAdd;
    }

    public JButton getButtonDelete() {
        return buttonDelete;
    }

    public JButton getButtonEdit() {
        return buttonEdit;
    }

    public JButton getButtonRename() {
        return buttonRename;
    }

    public JButton getButtonAddToSelImages() {
        return buttonAddToSelImages;
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            boolean selected = list.getSelectedIndex() >= 0;

            buttonDelete.setEnabled(selected);
            buttonEdit.setEnabled(selected);
            buttonRename.setEnabled(selected);
            buttonAddToSelImages
                .setEnabled(selected
                            && (GUI.INSTANCE.getAppPanel().getPanelThumbnails()
                                .getSelectionCount() > 0));
        }
    }

    @Override
    public void thumbnailsSelectionChanged() {
        buttonAddToSelImages
            .setEnabled((list.getSelectedIndex() >= 0)
                        && (GUI.INSTANCE.getAppPanel().getPanelThumbnails()
                            .getSelectionCount() > 0));
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
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

        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        panelButtons = new javax.swing.JPanel();
        buttonAddToSelImages = new javax.swing.JButton();
        panelModifyButtons = new javax.swing.JPanel();
        buttonRename = new javax.swing.JButton();
        buttonAdd = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new org.jphototagger.program.view.renderer.ListCellRendererMetadataTemplates());
        list.setDragEnabled(true);
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        buttonAddToSelImages.setText(bundle.getString("MetaDataTemplatesPanel.buttonAddToSelImages.text")); // NOI18N
        buttonAddToSelImages.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panelButtons.add(buttonAddToSelImages, gridBagConstraints);

        panelModifyButtons.setLayout(new java.awt.GridLayout(2, 0));

        buttonRename.setText(bundle.getString("MetaDataTemplatesPanel.buttonRename.text")); // NOI18N
        buttonRename.setEnabled(false);
        panelModifyButtons.add(buttonRename);

        buttonAdd.setText(bundle.getString("MetaDataTemplatesPanel.buttonAdd.text")); // NOI18N
        panelModifyButtons.add(buttonAdd);

        buttonEdit.setText(bundle.getString("MetaDataTemplatesPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setEnabled(false);
        panelModifyButtons.add(buttonEdit);

        buttonDelete.setText(bundle.getString("MetaDataTemplatesPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setEnabled(false);
        panelModifyButtons.add(buttonDelete);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelButtons.add(panelModifyButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonAddToSelImages;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonRename;
    private javax.swing.JList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelModifyButtons;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
