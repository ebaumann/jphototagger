/*
 * @(#)ActionsPanel.java    Created on 
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
import de.elmar_baumann.jpt.database.DatabaseActionsAfterDbInsertion;
import de.elmar_baumann.jpt.database.DatabasePrograms.Type;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.event.listener.ProgramActionListener;
import de.elmar_baumann.jpt.event.ProgramEvent;
import de.elmar_baumann.jpt.model.ListModelPrograms;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.dialogs.ProgramPropertiesDialog;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererActions;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.event.MouseEvent;

import java.util.Set;

import javax.swing.JProgressBar;

/**
 *
 * @author  Elmar Baumann
 */
public final class ActionsPanel extends javax.swing.JPanel {
    private static final long                            serialVersionUID =
        8875330844851092391L;
    private final ListModelPrograms                      model            =
        new ListModelPrograms(Type.ACTION);
    private final ListenerSupport<ProgramActionListener> listenerSupport  =
        new ListenerSupport<ProgramActionListener>();
    private Object progressBarOwner;

    public ActionsPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public synchronized JProgressBar getProgressBar(Object owner) {
        if (progressBarOwner == null) {
            progressBarOwner = owner;

            return progressBar;
        }

        return null;
    }

    public synchronized boolean isProgressBarAvailable() {
        return progressBarOwner == null;
    }

    public synchronized void releaseProgressBar(Object owner) {
        if (progressBarOwner == owner) {
            progressBarOwner = null;
        }
    }

    public void setButtonsEnabled() {
        boolean selectedIndex = list.getSelectedIndex() >= 0;

        buttonDelete.setEnabled(selectedIndex);
        buttonEdit.setEnabled(selectedIndex);
        buttonExecute.setEnabled(selectedIndex);
    }

    private Program getSelectedProgram() {
        return (Program) list.getSelectedValue();
    }

    private void execute() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();

            notify(new ProgramEvent(ProgramEvent.Type.PROGRAM_EXECUTED,
                                    program));
        }
    }

    private void handleButtonNewActionPerformed() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(true);

        dialog.setVisible(true);

        if (dialog.accepted()) {
            Program program = dialog.getProgram();

            model.add(program);
        }

        setButtonsEnabled();
    }

    private void handleButtonEditActionPerformed() {
        if (list.getSelectedIndex() >= 0) {
            Program                 program = getSelectedProgram();
            ProgramPropertiesDialog dialog  = new ProgramPropertiesDialog(true);

            dialog.setProgram(program);
            dialog.setVisible(true);

            if (dialog.accepted()) {
                model.update(program);
            }
        }

        setButtonsEnabled();
    }

    private void handleListMouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (evt.getClickCount() == 1) {
                setButtonsEnabled();
            } else if (evt.getClickCount() >= 2) {
                execute();
            }
        }
    }

    private void handleButtonDeleteActionPerformed() {
        if (list.getSelectedIndex() >= 0) {
            Program program = getSelectedProgram();

            if (confirmDelete(program)) {
                model.delete(program);
            }

            setButtonsEnabled();
        }
    }

    private boolean confirmDelete(Program program) {
        String  programName                     = program.getAlias();
        boolean existsInActionsAfterDbInsertion =
            DatabaseActionsAfterDbInsertion.INSTANCE.exists(program);

        return existsInActionsAfterDbInsertion
               ? MessageDisplayer.confirmYesNo(this,
                "ActionsPanel.Confirm.Delete.ExistsInOtherDb", programName)
               : MessageDisplayer.confirmYesNo(this,
                "ActionsPanel.Confirm.Delete", programName);
    }

    public synchronized void addListener(ProgramActionListener l) {
        listenerSupport.add(l);
    }

    private synchronized void notify(ProgramEvent evt) {
        Set<ProgramActionListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (ProgramActionListener l : listeners) {
                l.actionPerformed(evt);
            }
        }
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
        progressBar = new javax.swing.JProgressBar();
        panelButtons = new javax.swing.JPanel();
        buttonDelete = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonNew = new javax.swing.JButton();
        buttonExecute = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setFocusable(false);

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ListCellRendererActions());
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseClicked(evt);
            }
        });
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(scrollPane, gridBagConstraints);

        progressBar.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.progressBar.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(progressBar, gridBagConstraints);

        buttonDelete.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonDelete.text")); // NOI18N
        buttonDelete.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonDelete.toolTipText")); // NOI18N
        buttonDelete.setEnabled(false);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });

        buttonEdit.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonEdit.text")); // NOI18N
        buttonEdit.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonEdit.toolTipText")); // NOI18N
        buttonEdit.setEnabled(false);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonNew.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonNew.text")); // NOI18N
        buttonNew.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonNew.toolTipText")); // NOI18N
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });

        buttonExecute.setText(JptBundle.INSTANCE.getString("ActionsPanel.buttonExecute.text")); // NOI18N
        buttonExecute.setToolTipText(JptBundle.INSTANCE.getString("ActionsPanel.buttonExecute.toolTipText")); // NOI18N
        buttonExecute.setEnabled(false);
        buttonExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExecuteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addComponent(buttonDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonExecute))
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(buttonExecute)
                .addComponent(buttonNew)
                .addComponent(buttonEdit)
                .addComponent(buttonDelete))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 3, 5);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        if (!evt.getValueIsAdjusting()) {
            setButtonsEnabled();
        }
    }//GEN-LAST:event_listValueChanged

    private void listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseClicked
        handleListMouseClicked(evt);
    }//GEN-LAST:event_listMouseClicked

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        handleButtonDeleteActionPerformed();
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        handleButtonEditActionPerformed();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
        handleButtonNewActionPerformed();
    }//GEN-LAST:event_buttonNewActionPerformed

    private void buttonExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExecuteActionPerformed
        execute();
    }//GEN-LAST:event_buttonExecuteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonExecute;
    private javax.swing.JButton buttonNew;
    private javax.swing.JList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
