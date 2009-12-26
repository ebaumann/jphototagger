/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.event.listener.impl.ListenerProvider;
import de.elmar_baumann.jpt.event.UserSettingsChangeEvent;
import de.elmar_baumann.jpt.model.ListModelActionsAfterDbInsertion;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.jpt.view.dialogs.ActionsDialog;
import de.elmar_baumann.jpt.view.dialogs.ProgramSelectDialog;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererActions;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-07
 */
public class SettingsActionsPanel extends javax.swing.JPanel implements
        ListSelectionListener, Persistence {

    private final ListModelActionsAfterDbInsertion modelActionsAfterDbInsertion;
    private final ListenerProvider listenerProvider = ListenerProvider.INSTANCE;

    public SettingsActionsPanel() {
        modelActionsAfterDbInsertion = new ListModelActionsAfterDbInsertion();
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listActionsAfterDatabaseInsertion.getSelectionModel().
                addListSelectionListener(this);
    }

    private void setButtonsEnabled() {
        int selIndex = listActionsAfterDatabaseInsertion.getSelectedIndex();
        boolean actionSelected = selIndex >= 0;
        buttonActionsAfterDatabaseInsertionRemove.setEnabled(actionSelected);
        buttonActionsAfterDatabaseInsertionMoveUp.setEnabled(selIndex > 0);
        buttonActionsAfterDatabaseInsertionMoveDown.setEnabled(
                selIndex < modelActionsAfterDbInsertion.getSize() - 1 &&
                selIndex >= 0);
    }

    private void handleActionsAfterDatabaseInsertionAdd() {
        ProgramSelectDialog dlg = new ProgramSelectDialog(null, true);
        dlg.setVisible(true);
        Program action = dlg.getSelectedProgram();
        if (dlg.accepted() && !modelActionsAfterDbInsertion.contains(action)) {
            modelActionsAfterDbInsertion.add(action);
            setButtonsEnabled();
        }
    }

    private void handleActionsAfterDatabaseInsertionMoveDown() {
        modelActionsAfterDbInsertion.moveDown(listActionsAfterDatabaseInsertion.
                getSelectedIndex());
        setButtonsEnabled();
    }

    private void handleActionsAfterDatabaseInsertionMoveUp() {
        modelActionsAfterDbInsertion.moveUp(listActionsAfterDatabaseInsertion.
                getSelectedIndex());
        setButtonsEnabled();
    }

    private void handleActionsAfterDatabaseInsertionRemove() {
        Program action = (Program) modelActionsAfterDbInsertion.get(
                listActionsAfterDatabaseInsertion.getSelectedIndex());
        if (confirmRemoveActionAfterDatabaseInsertion(action.getAlias())) {
            modelActionsAfterDbInsertion.remove(action);
            setButtonsEnabled();
        }
    }

    private void handleActionsAfterDatabaseInsertionExecuteAlways() {
        UserSettingsChangeEvent evt =
                new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS,
                this);
        evt.setExecuteActionsAfterImageChangeInDbAlways(
                radioButtonActionsAfterDatabaseInsertionExecuteAlways.isSelected());
        notifyChangeListener(evt);
    }

    private void handleActionsAfterDatabaseInsertionExecuteIfXmpExists() {
        UserSettingsChangeEvent evt =
                new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP,
                this);
        evt.setExecuteActionsAfterImageChangeInDbIfImageHasXmp(
                radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.
                isSelected());
        notifyChangeListener(evt);
    }

    @Override
    public void readProperties() {
        UserSettings settings = UserSettings.INSTANCE;
        radioButtonActionsAfterDatabaseInsertionExecuteAlways.setSelected(
                settings.isExecuteActionsAfterImageChangeInDbAlways());
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.setSelected(
                settings.isExecuteActionsAfterImageChangeInDbIfImageHasXmp());
    }

    @Override
    public void writeProperties() {
        // Nothing to write
    }

    private synchronized void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    private void handleActionsAfterDatabaseInsertionEdit() {
        ActionsDialog dlg = ActionsDialog.INSTANCE;
        if (dlg.isVisible()) {
            dlg.toFront();
        } else {
            dlg.setVisible(true);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setButtonsEnabled();
        }
    }

    private boolean confirmRemoveActionAfterDatabaseInsertion(String actionName) {
        return MessageDisplayer.confirmYesNo(
                this,
                "SettingsActionsPanel.Confirm.RemoveActionAfterDatabaseInsertion",
                actionName);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupActionsAfterDatabaseInsertion = new javax.swing.ButtonGroup();
        panelActionsAfterDatabaseInsertion = new javax.swing.JPanel();
        scrollPaneActionsAfterDatabaseInsertion = new javax.swing.JScrollPane();
        listActionsAfterDatabaseInsertion = new javax.swing.JList();
        radioButtonActionsAfterDatabaseInsertionExecuteAlways = new javax.swing.JRadioButton();
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp = new javax.swing.JRadioButton();
        buttonActionsAfterDatabaseInsertionMoveUp = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionMoveDown = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionEdit = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionRemove = new javax.swing.JButton();
        buttonActionsAfterDatabaseInsertionAdd = new javax.swing.JButton();

        panelActionsAfterDatabaseInsertion.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("SettingsActionsPanel.panelActionsAfterDatabaseInsertion.border.title")));

        listActionsAfterDatabaseInsertion.setModel(modelActionsAfterDbInsertion);
        listActionsAfterDatabaseInsertion.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listActionsAfterDatabaseInsertion.setCellRenderer(new ListCellRendererActions());
        scrollPaneActionsAfterDatabaseInsertion.setViewportView(listActionsAfterDatabaseInsertion);

        buttonGroupActionsAfterDatabaseInsertion.add(radioButtonActionsAfterDatabaseInsertionExecuteAlways);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle");
        radioButtonActionsAfterDatabaseInsertionExecuteAlways.setText(bundle.getString("SettingsActionsPanel.radioButtonActionsAfterDatabaseInsertionExecuteAlways.text"));
        radioButtonActionsAfterDatabaseInsertionExecuteAlways.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed(evt);
            }
        });

        buttonGroupActionsAfterDatabaseInsertion.add(radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp);
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.setText(bundle.getString("SettingsActionsPanel.radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.text"));
        radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed(evt);
            }
        });

        buttonActionsAfterDatabaseInsertionMoveUp.setText(Bundle.getString("SettingsActionsPanel.buttonActionsAfterDatabaseInsertionMoveUp.text"));
        buttonActionsAfterDatabaseInsertionMoveUp.setEnabled(false);
        buttonActionsAfterDatabaseInsertionMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionMoveUpActionPerformed(evt);
            }
        });

        buttonActionsAfterDatabaseInsertionMoveDown.setText(Bundle.getString("SettingsActionsPanel.buttonActionsAfterDatabaseInsertionMoveDown.text"));
        buttonActionsAfterDatabaseInsertionMoveDown.setEnabled(false);
        buttonActionsAfterDatabaseInsertionMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionMoveDownActionPerformed(evt);
            }
        });

        buttonActionsAfterDatabaseInsertionEdit.setText(Bundle.getString("SettingsActionsPanel.buttonActionsAfterDatabaseInsertionEdit.text"));
        buttonActionsAfterDatabaseInsertionEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionEditActionPerformed(evt);
            }
        });

        buttonActionsAfterDatabaseInsertionRemove.setText(Bundle.getString("SettingsActionsPanel.buttonActionsAfterDatabaseInsertionRemove.text"));
        buttonActionsAfterDatabaseInsertionRemove.setEnabled(false);
        buttonActionsAfterDatabaseInsertionRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionRemoveActionPerformed(evt);
            }
        });

        buttonActionsAfterDatabaseInsertionAdd.setText(Bundle.getString("SettingsActionsPanel.buttonActionsAfterDatabaseInsertionAdd.text"));
        buttonActionsAfterDatabaseInsertionAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionsAfterDatabaseInsertionAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelActionsAfterDatabaseInsertionLayout = new javax.swing.GroupLayout(panelActionsAfterDatabaseInsertion);
        panelActionsAfterDatabaseInsertion.setLayout(panelActionsAfterDatabaseInsertionLayout);
        panelActionsAfterDatabaseInsertionLayout.setHorizontalGroup(
            panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelActionsAfterDatabaseInsertionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelActionsAfterDatabaseInsertionLayout.createSequentialGroup()
                        .addComponent(buttonActionsAfterDatabaseInsertionEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonActionsAfterDatabaseInsertionRemove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonActionsAfterDatabaseInsertionAdd))
                    .addComponent(radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp)
                    .addComponent(radioButtonActionsAfterDatabaseInsertionExecuteAlways)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelActionsAfterDatabaseInsertionLayout.createSequentialGroup()
                        .addComponent(scrollPaneActionsAfterDatabaseInsertion, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonActionsAfterDatabaseInsertionMoveUp)
                            .addComponent(buttonActionsAfterDatabaseInsertionMoveDown))))
                .addContainerGap())
        );

        panelActionsAfterDatabaseInsertionLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonActionsAfterDatabaseInsertionMoveDown, buttonActionsAfterDatabaseInsertionMoveUp});

        panelActionsAfterDatabaseInsertionLayout.setVerticalGroup(
            panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelActionsAfterDatabaseInsertionLayout.createSequentialGroup()
                .addGroup(panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelActionsAfterDatabaseInsertionLayout.createSequentialGroup()
                        .addComponent(buttonActionsAfterDatabaseInsertionMoveUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonActionsAfterDatabaseInsertionMoveDown))
                    .addComponent(scrollPaneActionsAfterDatabaseInsertion, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonActionsAfterDatabaseInsertionExecuteAlways)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelActionsAfterDatabaseInsertionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonActionsAfterDatabaseInsertionAdd)
                    .addComponent(buttonActionsAfterDatabaseInsertionRemove)
                    .addComponent(buttonActionsAfterDatabaseInsertionEdit))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelActionsAfterDatabaseInsertion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelActionsAfterDatabaseInsertion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonActionsAfterDatabaseInsertionEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionEditActionPerformed
        handleActionsAfterDatabaseInsertionEdit();
}//GEN-LAST:event_buttonActionsAfterDatabaseInsertionEditActionPerformed

    private void buttonActionsAfterDatabaseInsertionMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionMoveUpActionPerformed
        handleActionsAfterDatabaseInsertionMoveUp();
}//GEN-LAST:event_buttonActionsAfterDatabaseInsertionMoveUpActionPerformed

    private void buttonActionsAfterDatabaseInsertionMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionMoveDownActionPerformed
        handleActionsAfterDatabaseInsertionMoveDown();
}//GEN-LAST:event_buttonActionsAfterDatabaseInsertionMoveDownActionPerformed

    private void buttonActionsAfterDatabaseInsertionRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionRemoveActionPerformed
        handleActionsAfterDatabaseInsertionRemove();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionRemoveActionPerformed

    private void buttonActionsAfterDatabaseInsertionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionsAfterDatabaseInsertionAddActionPerformed
        handleActionsAfterDatabaseInsertionAdd();
    }//GEN-LAST:event_buttonActionsAfterDatabaseInsertionAddActionPerformed

    private void radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed
        handleActionsAfterDatabaseInsertionExecuteAlways();
    }//GEN-LAST:event_radioButtonActionsAfterDatabaseInsertionExecuteAlwaysActionPerformed

    private void radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed
        handleActionsAfterDatabaseInsertionExecuteIfXmpExists();
}//GEN-LAST:event_radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmpActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonActionsAfterDatabaseInsertionAdd;
    private javax.swing.JButton buttonActionsAfterDatabaseInsertionEdit;
    private javax.swing.JButton buttonActionsAfterDatabaseInsertionMoveDown;
    private javax.swing.JButton buttonActionsAfterDatabaseInsertionMoveUp;
    private javax.swing.JButton buttonActionsAfterDatabaseInsertionRemove;
    private javax.swing.ButtonGroup buttonGroupActionsAfterDatabaseInsertion;
    private javax.swing.JList listActionsAfterDatabaseInsertion;
    private javax.swing.JPanel panelActionsAfterDatabaseInsertion;
    private javax.swing.JRadioButton radioButtonActionsAfterDatabaseInsertionExecuteAlways;
    private javax.swing.JRadioButton radioButtonActionsAfterDatabaseInsertionExecuteIfImageHasXmp;
    private javax.swing.JScrollPane scrollPaneActionsAfterDatabaseInsertion;
    // End of variables declaration//GEN-END:variables
}
