/*
 * @(#)SettingsProgramsPanel.java    Created on 2008-11-02
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

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms.Type;
import org.jphototagger.program.model.ListModelPrograms;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.view.dialogs.ProgramPropertiesDialog;
import org.jphototagger.program.view.renderer.ListCellRendererPrograms;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.database.DatabasePrograms;



/**
 *
 * @author  Elmar Baumann
 */
public final class SettingsProgramsPanel extends javax.swing.JPanel
        implements Persistence {
    private static final long       serialVersionUID = 6156362511361451187L;
    private final ListModelPrograms model            =
        new ListModelPrograms(Type.PROGRAM);

    public SettingsProgramsPanel() {
        initComponents();
        MnemonicUtil.setMnemonics((Container) this);
        setEnabled();
    }

    @Override
    public void readProperties() {
    }

    @Override
    public void writeProperties() {
    }

    private void addProgram() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);

        dialog.setVisible(true);

        if (dialog.accepted()) {
            DatabasePrograms.INSTANCE.insert(dialog.getProgram());
        }
    }

    private void updateProgram() {
        if (listPrograms.getSelectedIndex() >= 0) {
            ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);

            dialog.setProgram((Program) listPrograms.getSelectedValue());
            dialog.setVisible(true);

            if (dialog.accepted()) {
                DatabasePrograms.INSTANCE.update(dialog.getProgram());
            }
        }
    }

    private void removeProgram() {
        int index = listPrograms.getSelectedIndex();

        if ((index >= 0) && askRemove(model.getElementAt(index).toString())) {
            DatabasePrograms.INSTANCE.delete((Program) model.get(index));
            setEnabled();
        }
    }

    private boolean askRemove(String otherImageOpenApp) {
        return MessageDisplayer.confirmYesNo(this,
                "SettingsProgramsPanel.Confirm.RemoveImageOpenApp",
                otherImageOpenApp);
    }

    private void setEnabled() {
        boolean programSelected = isProgramSelected();
        int     selIndex        = listPrograms.getSelectedIndex();
        int     size            = listPrograms.getModel().getSize();

        buttonEditProgram.setEnabled(programSelected);
        buttonRemoveProgram.setEnabled(programSelected);
        buttonMoveProgramDown.setEnabled(programSelected && selIndex < size - 1);
        buttonMoveProgramUp.setEnabled(programSelected && selIndex > 0);
    }

    private boolean isProgramSelected() {
        return listPrograms.getSelectedIndex() >= 0;
    }

    private void handleListOtherProgramsMouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            updateProgram();
        }
    }

    private void moveProgramDown() {
        int     size            = model.getSize();
        int     selIndex        = listPrograms.getSelectedIndex();
        int     downIndex       = selIndex + 1;
        boolean programSelected = isProgramSelected();

        assert programSelected;
        assert downIndex < size : downIndex;

        if (programSelected && downIndex < size) {
            ListUtil.swapModelElements(model, downIndex, selIndex);
            reorderPrograms();
            listPrograms.setSelectedIndex(downIndex);
        }
    }

    private void moveProgramUp() {
        int     selIndex        = listPrograms.getSelectedIndex();
        int     upIndex         = selIndex - 1;
        boolean programSelected = isProgramSelected();

        assert programSelected;
        assert upIndex >= 0 : upIndex;

        if (programSelected && upIndex >= 0) {
            ListUtil.swapModelElements(model, upIndex, selIndex);
            reorderPrograms();
            listPrograms.setSelectedIndex(upIndex);
        }
    }

    private void reorderPrograms() {
        int size = model.getSize();
        List<Program> programs = new ArrayList<Program>(size);

        for (int sequenceNo = 0; sequenceNo < size; sequenceNo++) {
            Object o = model.get(sequenceNo);
            assert o instanceof Program;
            Program program = (Program) o;
            program.setSequenceNumber(sequenceNo);
            programs.add(program);
        }

        for (Program program : programs) {
            DatabasePrograms.INSTANCE.update(program);
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

        labelChooseDefaultProgram = new javax.swing.JLabel();
        labelPrograms = new javax.swing.JLabel();
        scrollPanePrograms = new javax.swing.JScrollPane();
        listPrograms = new javax.swing.JList();
        buttonRemoveProgram = new javax.swing.JButton();
        buttonMoveProgramUp = new javax.swing.JButton();
        buttonMoveProgramDown = new javax.swing.JButton();
        buttonAddProgram = new javax.swing.JButton();
        buttonEditProgram = new javax.swing.JButton();

        labelChooseDefaultProgram.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.labelChooseDefaultProgram.text")); // NOI18N

        labelPrograms.setLabelFor(listPrograms);
        labelPrograms.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.labelPrograms.text")); // NOI18N

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new ListCellRendererPrograms());
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listProgramsValueChanged(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        buttonRemoveProgram.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonRemoveProgram.text")); // NOI18N
        buttonRemoveProgram.setToolTipText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonRemoveProgram.toolTipText")); // NOI18N
        buttonRemoveProgram.setEnabled(false);
        buttonRemoveProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveProgramActionPerformed(evt);
            }
        });

        buttonMoveProgramUp.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonMoveProgramUp.text")); // NOI18N
        buttonMoveProgramUp.setEnabled(false);
        buttonMoveProgramUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramUpActionPerformed(evt);
            }
        });

        buttonMoveProgramDown.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonMoveProgramDown.text")); // NOI18N
        buttonMoveProgramDown.setEnabled(false);
        buttonMoveProgramDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMoveProgramDownActionPerformed(evt);
            }
        });

        buttonAddProgram.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonAddProgram.text")); // NOI18N
        buttonAddProgram.setToolTipText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonAddProgram.toolTipText")); // NOI18N
        buttonAddProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddProgramActionPerformed(evt);
            }
        });

        buttonEditProgram.setText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonEditProgram.text")); // NOI18N
        buttonEditProgram.setToolTipText(JptBundle.INSTANCE.getString("SettingsProgramsPanel.buttonEditProgram.toolTipText")); // NOI18N
        buttonEditProgram.setEnabled(false);
        buttonEditProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditProgramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addComponent(labelChooseDefaultProgram)
                    .addComponent(labelPrograms)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonMoveProgramUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonMoveProgramDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRemoveProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonAddProgram)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonEditProgram)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAddProgram, buttonEditProgram, buttonMoveProgramDown, buttonMoveProgramUp, buttonRemoveProgram});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPrograms)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelChooseDefaultProgram)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEditProgram)
                    .addComponent(buttonAddProgram)
                    .addComponent(buttonRemoveProgram)
                    .addComponent(buttonMoveProgramDown)
                    .addComponent(buttonMoveProgramUp))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonAddProgram, buttonEditProgram, buttonMoveProgramDown, buttonMoveProgramUp, buttonRemoveProgram});

    }// </editor-fold>//GEN-END:initComponents

    private void listProgramsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listProgramsValueChanged
        setEnabled();
    }//GEN-LAST:event_listProgramsValueChanged

    private void buttonRemoveProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveProgramActionPerformed
        removeProgram();
    }//GEN-LAST:event_buttonRemoveProgramActionPerformed

    private void buttonAddProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddProgramActionPerformed
        addProgram();
    }//GEN-LAST:event_buttonAddProgramActionPerformed

    private void buttonEditProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditProgramActionPerformed
        updateProgram();
    }//GEN-LAST:event_buttonEditProgramActionPerformed

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listProgramsMouseClicked
        handleListOtherProgramsMouseClicked(evt);
    }//GEN-LAST:event_listProgramsMouseClicked

    private void buttonMoveProgramDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveProgramDownActionPerformed
        moveProgramDown();
    }//GEN-LAST:event_buttonMoveProgramDownActionPerformed

    private void buttonMoveProgramUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMoveProgramUpActionPerformed
        moveProgramUp();
    }//GEN-LAST:event_buttonMoveProgramUpActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddProgram;
    private javax.swing.JButton buttonEditProgram;
    private javax.swing.JButton buttonMoveProgramDown;
    private javax.swing.JButton buttonMoveProgramUp;
    private javax.swing.JButton buttonRemoveProgram;
    private javax.swing.JLabel labelChooseDefaultProgram;
    private javax.swing.JLabel labelPrograms;
    private javax.swing.JList listPrograms;
    private javax.swing.JScrollPane scrollPanePrograms;
    // End of variables declaration//GEN-END:variables
}
