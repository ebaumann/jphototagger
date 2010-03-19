/*
 * @(#)SettingsActionsPanel.java    Created on 2009-06-07
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

package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms.Type;
import de.elmar_baumann.jpt.model.ListModelActionsAfterDbInsertion;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.dialogs.ActionsDialog;
import de.elmar_baumann.jpt.view.dialogs.ProgramSelectDialog;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererActions;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;

import java.awt.Container;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  Elmar Baumann
 */
public class SettingsActionsPanel extends javax.swing.JPanel
        implements ListSelectionListener, Persistence {
    private static final long                      serialVersionUID =
        6440789488453905704L;
    private final ListModelActionsAfterDbInsertion modelActionsAfterDbInsertion;

    public SettingsActionsPanel() {
        modelActionsAfterDbInsertion = new ListModelActionsAfterDbInsertion();
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listActionsAfterDatabaseInsertion.getSelectionModel()
            .addListSelectionListener(this);
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setButtonsEnabled() {
        int     selIndex       =
            listActionsAfterDatabaseInsertion.getSelectedIndex();
        boolean actionSelected = selIndex >= 0;

        buttonActionsAfterDatabaseInsertionRemove.setEnabled(actionSelected);
        buttonActionsAfterDatabaseInsertionMoveUp.setEnabled(selIndex > 0);
        buttonActionsAfterDatabaseInsertionMoveDown.setEnabled((selIndex
                < modelActionsAfterDbInsertion.getSize() - 1) && (selIndex
                    >= 0));
    }

    private void handleActionsAfterDatabaseInsertionAdd() {
        ProgramSelectDialog dlg = new ProgramSelectDialog(Type.ACTION);

        dlg.setVisible(true);

        Program action = dlg.getSelectedProgram();

        if (dlg.accepted() &&!modelActionsAfterDbInsertion.contains(action)) {
            modelActionsAfterDbInsertion.insert(action);
            setButtonsEnabled();
        }
    }

    private void handleActionsAfterDatabaseInsertionMoveDown() {
        modelActionsAfterDbInsertion.moveDown(
            listActionsAfterDatabaseInsertion.getSelectedIndex());
        setButtonsEnabled();
    }

    private void handleActionsAfterDatabaseInsertionMoveUp() {
        modelActionsAfterDbInsertion.moveUp(
            listActionsAfterDatabaseInsertion.getSelectedIndex());
        setButtonsEnabled();
    }

    private void handleActionsAfterDatabaseInsertionRemove() {
        Program action =
            (Program) modelActionsAfterDbInsertion.get(
                listActionsAfterDatabaseInsertion.getSelectedIndex());

        if (confirmRemoveActionAfterDatabaseInsertion(action.getAlias())) {
            modelActionsAfterDbInsertion.delete(action);
            setButtonsEnabled();
        }
    }

    private void handleActionsAfterDatabaseInsertionExecuteAlways() {
        UserSettings.INSTANCE.setExecuteActionsAfterImageChangeInDbAlways(
            radioButtonActionsAfterDatabaseInsertionExecuteAlways.isSelected());
    }

    private void handleActionsAfterDatabaseInsertionExecuteIfXmpExists() {
        UserSettings.INSTANCE
            .setExecuteActionsAfterImageChangeInDbIfImageHasXmp(
                radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp
                    .isSelected());
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;

        radioButtonActionsAfterDatabaseInsertionExecuteAlways.setSelected(
            settings.isExecuteActionsAfterImageChangeInDbAlways());
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp
            .setSelected(settings
                .isExecuteActionsAfterImageChangeInDbIfImageHasXmp());
    }

    @Override
    public void writeProperties() {

        // Nothing to write
    }

    private void handleActionsAfterDatabaseInsertionEdit() {
        ComponentUtil.show(ActionsDialog.INSTANCE);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setButtonsEnabled();
        }
    }

    private boolean confirmRemoveActionAfterDatabaseInsertion(
            String actionName) {
        return MessageDisplayer.confirmYesNo(
            this,
            "SettingsActionsPanel.Confirm.RemoveActionAfterDatabaseInsertion",
            actionName);
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
        buttonGroupActionsAfterDatabaseInsertion =
            new javax.swing.ButtonGroup();
        panelActionsAfterDatabaseInsertion                    =
            new javax.swing.JPanel();
        scrollPaneActionsAfterDatabaseInsertion               =
            new javax.swing.JScrollPane();
        listActionsAfterDatabaseInsertion                     =
            new javax.swing.JList();
        buttonActionsAfterDatabaseInsertionMoveUp             =
            new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionMoveDown           =
            new javax.swing.JButton();
        radioButtonActionsAfterDatabaseInsertionExecuteAlways =
            new javax.swing.JRadioButton();
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp =
            new javax.swing.JRadioButton();
        buttonActionsAfterDatabaseInsertionEdit   = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionRemove = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionAdd    = new javax.swing.JButton();
        panelActionsAfterDatabaseInsertion.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                JptBundle.INSTANCE.getString(
                    "SettingsActionsPanel.panelActionsAfterDatabaseInsertion.border.title")));    // NOI18N
        listActionsAfterDatabaseInsertion.setModel(
            modelActionsAfterDbInsertion);
        listActionsAfterDatabaseInsertion.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listActionsAfterDatabaseInsertion.setCellRenderer(
            new ListCellRendererActions());
        scrollPaneActionsAfterDatabaseInsertion.setViewportView(
            listActionsAfterDatabaseInsertion);
        buttonActionsAfterDatabaseInsertionMoveUp.setText(
            JptBundle.INSTANCE.getString(
                "SettingsActionsPanel.buttonActionsAfterDatabaseInsertionMoveUp.text"));    // NOI18N
        buttonActionsAfterDatabaseInsertionMoveUp.setEnabled(false);
        buttonActionsAfterDatabaseInsertionMoveUp.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionMoveUpActionPerformed(evt);
            }
        });
        buttonActionsAfterDatabaseInsertionMoveDown.setText(
            JptBundle.INSTANCE.getString(
                "SettingsActionsPanel.buttonActionsAfterDatabaseInsertionMoveDown.text"));    // NOI18N
        buttonActionsAfterDatabaseInsertionMoveDown.setEnabled(false);
        buttonActionsAfterDatabaseInsertionMoveDown.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionMoveDownActionPerformed(evt);
            }
        });
        buttonGroupActionsAfterDatabaseInsertion.add(
            radioButtonActionsAfterDatabaseInsertionExecuteAlways);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        radioButtonActionsAfterDatabaseInsertionExecuteAlways.setText(
            bundle.getString(
                "SettingsActionsPanel.radioButtonActionsAfterDatabaseInsertionExecuteAlways.text"));    // NOI18N
        radioButtonActionsAfterDatabaseInsertionExecuteAlways.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed(
                    evt);
            }
        });
        buttonGroupActionsAfterDatabaseInsertion.add(
            radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp);
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.setText(
            bundle.getString(
                "SettingsActionsPanel.radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.text"));    // NOI18N
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp
            .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed(
                    evt);
            }
        });
        buttonActionsAfterDatabaseInsertionEdit.setText(
            JptBundle.INSTANCE.getString(
                "SettingsActionsPanel.buttonActionsAfterDatabaseInsertionEdit.text"));    // NOI18N
        buttonActionsAfterDatabaseInsertionEdit.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionEditActionPerformed(evt);
            }
        });
        buttonActionsAfterDatabaseInsertionRemove.setText(
            JptBundle.INSTANCE.getString(
                "SettingsActionsPanel.buttonActionsAfterDatabaseInsertionRemove.text"));    // NOI18N
        buttonActionsAfterDatabaseInsertionRemove.setEnabled(false);
        buttonActionsAfterDatabaseInsertionRemove.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionRemoveActionPerformed(evt);
            }
        });
        buttonActionsAfterDatabaseInsertionAdd.setText(
            JptBundle.INSTANCE.getString(
                "SettingsActionsPanel.buttonActionsAfterDatabaseInsertionAdd.text"));    // NOI18N
        buttonActionsAfterDatabaseInsertionAdd.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelActionsAfterDatabaseInsertionLayout =
            new javax.swing.GroupLayout(panelActionsAfterDatabaseInsertion);

        panelActionsAfterDatabaseInsertion.setLayout(
            panelActionsAfterDatabaseInsertionLayout);
        panelActionsAfterDatabaseInsertionLayout.setHorizontalGroup(
            panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelActionsAfterDatabaseInsertionLayout.createSequentialGroup().addContainerGap().addGroup(
                    panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp).addComponent(
                        radioButtonActionsAfterDatabaseInsertionExecuteAlways).addGroup(
                        panelActionsAfterDatabaseInsertionLayout.createSequentialGroup().addComponent(
                            scrollPaneActionsAfterDatabaseInsertion,
                            javax.swing.GroupLayout.DEFAULT_SIZE, 289,
                            Short.MAX_VALUE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
                                panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                                    panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                        buttonActionsAfterDatabaseInsertionRemove).addComponent(
                                        buttonActionsAfterDatabaseInsertionMoveUp).addComponent(
                                        buttonActionsAfterDatabaseInsertionMoveDown).addComponent(
                                        buttonActionsAfterDatabaseInsertionAdd)).addComponent(
                                            buttonActionsAfterDatabaseInsertionEdit)))).addContainerGap()));
        panelActionsAfterDatabaseInsertionLayout.linkSize(
            javax.swing.SwingConstants.HORIZONTAL,
            new java.awt.Component[] { buttonActionsAfterDatabaseInsertionAdd,
                                       buttonActionsAfterDatabaseInsertionEdit,
                                       buttonActionsAfterDatabaseInsertionMoveDown,
                                       buttonActionsAfterDatabaseInsertionMoveUp,
                                       buttonActionsAfterDatabaseInsertionRemove });
        panelActionsAfterDatabaseInsertionLayout.setVerticalGroup(
            panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panelActionsAfterDatabaseInsertionLayout.createSequentialGroup().addGap(
                    12, 12, 12).addGroup(
                    panelActionsAfterDatabaseInsertionLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        scrollPaneActionsAfterDatabaseInsertion,
                        javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 182,
                        Short.MAX_VALUE).addGroup(
                            panelActionsAfterDatabaseInsertionLayout.createSequentialGroup().addComponent(
                                buttonActionsAfterDatabaseInsertionMoveUp).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonActionsAfterDatabaseInsertionMoveDown).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonActionsAfterDatabaseInsertionAdd).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonActionsAfterDatabaseInsertionRemove).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                buttonActionsAfterDatabaseInsertionEdit))).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                    radioButtonActionsAfterDatabaseInsertionExecuteAlways).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                    radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    panelActionsAfterDatabaseInsertion,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    panelActionsAfterDatabaseInsertion,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addContainerGap()));
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonActionsAfterDatabaseInsertionEditActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionEditActionPerformed
        handleActionsAfterDatabaseInsertionEdit();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionEditActionPerformed

    private void buttonActionsAfterDatabaseInsertionMoveUpActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionMoveUpActionPerformed
        handleActionsAfterDatabaseInsertionMoveUp();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionMoveUpActionPerformed

    private void buttonActionsAfterDatabaseInsertionMoveDownActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionMoveDownActionPerformed
        handleActionsAfterDatabaseInsertionMoveDown();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionMoveDownActionPerformed

    private void buttonActionsAfterDatabaseInsertionRemoveActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionRemoveActionPerformed
        handleActionsAfterDatabaseInsertionRemove();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionRemoveActionPerformed

    private void buttonActionsAfterDatabaseInsertionAddActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionAddActionPerformed
        handleActionsAfterDatabaseInsertionAdd();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionAddActionPerformed

    private void radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed
        handleActionsAfterDatabaseInsertionExecuteAlways();
    }//GEN-LAST:event_radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed

    private void radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed
        handleActionsAfterDatabaseInsertionExecuteIfXmpExists();
    }//GEN-LAST:event_radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton      buttonActionsAfterDatabaseInsertionAdd;
    private javax.swing.JButton      buttonActionsAfterDatabaseInsertionEdit;
    private javax.swing.JButton      buttonActionsAfterDatabaseInsertionMoveDown;
    private javax.swing.JButton      buttonActionsAfterDatabaseInsertionMoveUp;
    private javax.swing.JButton      buttonActionsAfterDatabaseInsertionRemove;
    private javax.swing.ButtonGroup  buttonGroupActionsAfterDatabaseInsertion;
    private javax.swing.JList        listActionsAfterDatabaseInsertion;
    private javax.swing.JPanel       panelActionsAfterDatabaseInsertion;
    private javax.swing.JRadioButton radioButtonActionsAfterDatabaseInsertionExecuteAlways;
    private javax.swing.JRadioButton radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp;
    private javax.swing.JScrollPane scrollPaneActionsAfterDatabaseInsertion;

    // End of variables declaration//GEN-END:variables
}
