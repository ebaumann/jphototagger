/*
 * @(#)EditMetadataActionsPanel.java    Created on 
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

import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;

import javax.swing.JComboBox;

/**
 * Panel for action sources (buttons) related to edit metadata.
 *
 * @author  Elmar Baumann
 */
public final class EditMetadataActionsPanel extends javax.swing.JPanel {
    private static final long serialVersionUID = -8123589850440517504L;

    public EditMetadataActionsPanel() {
        initComponents();
        setTemplateName();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public JComboBox getComboBoxMetadataTemplates() {
        return comboBoxMetadataTemplates;
    }

    private void setTemplateName() {
        Object selItem = comboBoxMetadataTemplates.getSelectedItem();

        labelTemplateName.setText((selItem == null)
                                  ? JptBundle.INSTANCE.getString(
                                  "EditMetadataActionsPanel.labelTemplateName.text")
                                  : selItem.toString());
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

        tabbedPane = new javax.swing.JTabbedPane();
        panelGroupMetadataEdit = new javax.swing.JPanel();
        labelMetadataInfoEditable = new javax.swing.JLabel();
        buttonEmptyMetadata = new javax.swing.JButton();
        buttonMetadataTemplateCreate = new javax.swing.JButton();
        buttonMetadataTemplateInsert = new javax.swing.JButton();
        labelTemplateName = new javax.swing.JLabel();
        labelPromptInfoCurrentTemplate = new javax.swing.JLabel();
        panelGroupMetadataTemplates = new javax.swing.JPanel();
        labelPromptCurrentTemplate = new javax.swing.JLabel();
        comboBoxMetadataTemplates = new javax.swing.JComboBox();
        buttonMetadataTemplateRename = new javax.swing.JButton();
        buttonMetadataTemplateUpdate = new javax.swing.JButton();
        buttonMetadataTemplateDelete = new javax.swing.JButton();
        buttonMetadataTemplateEdit = new javax.swing.JButton();
        buttonMetadataTemplateAdd = new javax.swing.JButton();

        labelMetadataInfoEditable.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.labelMetadataInfoEditable.text")); // NOI18N

        buttonEmptyMetadata.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonEmptyMetadata.text")); // NOI18N
        buttonEmptyMetadata.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonEmptyMetadata.toolTipText")); // NOI18N
        buttonEmptyMetadata.setEnabled(false);
        buttonEmptyMetadata.setMargin(new java.awt.Insets(2, 2, 2, 2));

        buttonMetadataTemplateCreate.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateCreate.text")); // NOI18N
        buttonMetadataTemplateCreate.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateCreate.toolTipText")); // NOI18N
        buttonMetadataTemplateCreate.setEnabled(false);
        buttonMetadataTemplateCreate.setMargin(new java.awt.Insets(2, 2, 2, 2));

        buttonMetadataTemplateInsert.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateInsert.text")); // NOI18N
        buttonMetadataTemplateInsert.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateInsert.toolTipText")); // NOI18N
        buttonMetadataTemplateInsert.setEnabled(false);
        buttonMetadataTemplateInsert.setMargin(new java.awt.Insets(2, 2, 2, 2));

        labelTemplateName.setForeground(new java.awt.Color(0, 0, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        labelTemplateName.setText(bundle.getString("EditMetadataActionsPanel.labelTemplateName.text")); // NOI18N

        labelPromptInfoCurrentTemplate.setText(bundle.getString("EditMetadataActionsPanel.labelPromptInfoCurrentTemplate.text")); // NOI18N

        javax.swing.GroupLayout panelGroupMetadataEditLayout = new javax.swing.GroupLayout(panelGroupMetadataEdit);
        panelGroupMetadataEdit.setLayout(panelGroupMetadataEditLayout);
        panelGroupMetadataEditLayout.setHorizontalGroup(
            panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                        .addComponent(buttonMetadataTemplateInsert)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEmptyMetadata))
                    .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                        .addComponent(labelPromptInfoCurrentTemplate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelTemplateName, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                    .addComponent(labelMetadataInfoEditable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(buttonMetadataTemplateCreate))
                .addContainerGap())
        );
        panelGroupMetadataEditLayout.setVerticalGroup(
            panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataEditLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(labelMetadataInfoEditable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPromptInfoCurrentTemplate)
                    .addComponent(labelTemplateName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetadataTemplateInsert)
                    .addComponent(buttonEmptyMetadata))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonMetadataTemplateCreate)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.panelGroupMetadataEdit.TabConstraints.tabTitle"), panelGroupMetadataEdit); // NOI18N

        labelPromptCurrentTemplate.setLabelFor(comboBoxMetadataTemplates);
        labelPromptCurrentTemplate.setText(bundle.getString("EditMetadataActionsPanel.labelPromptCurrentTemplate.text")); // NOI18N

        comboBoxMetadataTemplates.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.comboBoxMetadataTemplates.toolTipText")); // NOI18N
        comboBoxMetadataTemplates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxMetadataTemplatesActionPerformed(evt);
            }
        });
        comboBoxMetadataTemplates.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                comboBoxMetadataTemplatesPropertyChange(evt);
            }
        });

        buttonMetadataTemplateRename.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateRename.text")); // NOI18N
        buttonMetadataTemplateRename.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateRename.toolTipText")); // NOI18N
        buttonMetadataTemplateRename.setEnabled(false);
        buttonMetadataTemplateRename.setMargin(new java.awt.Insets(2, 2, 2, 2));

        buttonMetadataTemplateUpdate.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateUpdate.text")); // NOI18N
        buttonMetadataTemplateUpdate.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateUpdate.toolTipText")); // NOI18N
        buttonMetadataTemplateUpdate.setEnabled(false);
        buttonMetadataTemplateUpdate.setMargin(new java.awt.Insets(2, 2, 2, 2));

        buttonMetadataTemplateDelete.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateDelete.text")); // NOI18N
        buttonMetadataTemplateDelete.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateDelete.toolTipText")); // NOI18N
        buttonMetadataTemplateDelete.setEnabled(false);
        buttonMetadataTemplateDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));

        buttonMetadataTemplateEdit.setText(bundle.getString("EditMetadataActionsPanel.buttonMetadataTemplateEdit.text")); // NOI18N
        buttonMetadataTemplateEdit.setToolTipText(bundle.getString("EditMetadataActionsPanel.buttonMetadataTemplateEdit.toolTipText")); // NOI18N
        buttonMetadataTemplateEdit.setEnabled(false);

        buttonMetadataTemplateAdd.setText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateAdd.text")); // NOI18N
        buttonMetadataTemplateAdd.setToolTipText(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.buttonMetadataTemplateAdd.toolTipText")); // NOI18N
        buttonMetadataTemplateAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));

        javax.swing.GroupLayout panelGroupMetadataTemplatesLayout = new javax.swing.GroupLayout(panelGroupMetadataTemplates);
        panelGroupMetadataTemplates.setLayout(panelGroupMetadataTemplatesLayout);
        panelGroupMetadataTemplatesLayout.setHorizontalGroup(
            panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPromptCurrentTemplate)
                    .addComponent(comboBoxMetadataTemplates, javax.swing.GroupLayout.Alignment.TRAILING, 0, 224, Short.MAX_VALUE)
                    .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                        .addComponent(buttonMetadataTemplateRename)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMetadataTemplateUpdate))
                    .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                        .addComponent(buttonMetadataTemplateDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMetadataTemplateEdit))
                    .addComponent(buttonMetadataTemplateAdd))
                .addContainerGap())
        );

        panelGroupMetadataTemplatesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonMetadataTemplateAdd, buttonMetadataTemplateDelete, buttonMetadataTemplateEdit, buttonMetadataTemplateRename, buttonMetadataTemplateUpdate});

        panelGroupMetadataTemplatesLayout.setVerticalGroup(
            panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGroupMetadataTemplatesLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(labelPromptCurrentTemplate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxMetadataTemplates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetadataTemplateRename)
                    .addComponent(buttonMetadataTemplateUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGroupMetadataTemplatesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonMetadataTemplateDelete)
                    .addComponent(buttonMetadataTemplateEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonMetadataTemplateAdd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("EditMetadataActionsPanel.panelGroupMetadataTemplates.TabConstraints.tabTitle"), panelGroupMetadataTemplates); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboBoxMetadataTemplatesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_comboBoxMetadataTemplatesPropertyChange
        setTemplateName();
    }//GEN-LAST:event_comboBoxMetadataTemplatesPropertyChange

    private void comboBoxMetadataTemplatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxMetadataTemplatesActionPerformed
        setTemplateName();
    }//GEN-LAST:event_comboBoxMetadataTemplatesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton buttonEmptyMetadata;
    public javax.swing.JButton buttonMetadataTemplateAdd;
    public javax.swing.JButton buttonMetadataTemplateCreate;
    public javax.swing.JButton buttonMetadataTemplateDelete;
    public javax.swing.JButton buttonMetadataTemplateEdit;
    public javax.swing.JButton buttonMetadataTemplateInsert;
    public javax.swing.JButton buttonMetadataTemplateRename;
    public javax.swing.JButton buttonMetadataTemplateUpdate;
    public javax.swing.JComboBox comboBoxMetadataTemplates;
    public javax.swing.JLabel labelMetadataInfoEditable;
    public javax.swing.JLabel labelPromptCurrentTemplate;
    private javax.swing.JLabel labelPromptInfoCurrentTemplate;
    private javax.swing.JLabel labelTemplateName;
    private javax.swing.JPanel panelGroupMetadataEdit;
    public javax.swing.JPanel panelGroupMetadataTemplates;
    public javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
