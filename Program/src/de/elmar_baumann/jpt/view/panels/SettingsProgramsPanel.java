/*
 * JPhotoTagger tags and finds images fast.
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
import de.elmar_baumann.jpt.model.ListModelPrograms;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.dialogs.ProgramPropertiesDialog;
import de.elmar_baumann.jpt.view.renderer.ListCellRendererPrograms;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.image.util.IconUtil;

import java.awt.Container;
import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JFileChooser;

/**
 *
 * @author  Elmar Baumann
 * @version 2008-11-02
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
        String filename = UserSettings.INSTANCE.getDefaultImageOpenApp();

        labelDefaultProgramFile.setText(filename);

        File file = new File(filename);

        if (file.exists()) {
            labelDefaultProgramFile.setIcon(IconUtil.getSystemIcon(file));
        }
    }

    @Override
    public void writeProperties() {}

    private void setDefaultProgram() {
        File file = chooseFile(labelDefaultProgramFile.getText());

        if ((file != null) && file.exists()) {
            labelDefaultProgramFile.setText(file.getAbsolutePath());
            UserSettings.INSTANCE.setDefaultImageOpenApp(
                new File(labelDefaultProgramFile.getText()));
        }
    }

    private File chooseFile(String startDirectory) {
        JFileChooser fileChooser = new JFileChooser(new File(startDirectory));

        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.isDirectory()) {
                return file;
            }
        }

        return null;
    }

    private void addProgram() {
        ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);

        dialog.setVisible(true);

        if (dialog.accepted()) {
            model.add(dialog.getProgram());
        }
    }

    private void updateProgram() {
        if (listPrograms.getSelectedIndex() >= 0) {
            ProgramPropertiesDialog dialog = new ProgramPropertiesDialog(false);

            dialog.setProgram((Program) listPrograms.getSelectedValue());
            dialog.setVisible(true);

            if (dialog.accepted()) {
                model.update(dialog.getProgram());
            }
        }
    }

    private void removeProgram() {
        int index = listPrograms.getSelectedIndex();

        if ((index >= 0) && askRemove(model.getElementAt(index).toString())) {
            model.delete((Program) model.get(index));
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

        buttonEditProgram.setEnabled(programSelected);
        buttonRemoveProgram.setEnabled(programSelected);
    }

    private boolean isProgramSelected() {
        return listPrograms.getSelectedIndex() >= 0;
    }

    private void handleListOtherProgramsMouseClicked(MouseEvent evt) {
        if (evt.getClickCount() >= 2) {
            updateProgram();
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
        labelChooseDefaultProgram  = new javax.swing.JLabel();
        buttonChooseDefaultProgram = new javax.swing.JButton();
        labelDefaultProgramFile    = new javax.swing.JLabel();
        labelPrograms              = new javax.swing.JLabel();
        scrollPanePrograms         = new javax.swing.JScrollPane();
        listPrograms               = new javax.swing.JList();
        buttonRemoveProgram        = new javax.swing.JButton();
        buttonAddProgram           = new javax.swing.JButton();
        buttonEditProgram          = new javax.swing.JButton();
        labelChooseDefaultProgram.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.labelChooseDefaultProgram.text"));    // NOI18N
        buttonChooseDefaultProgram.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonChooseDefaultProgram.text"));    // NOI18N
        buttonChooseDefaultProgram.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDefaultProgramActionPerformed(evt);
            }
        });
        labelDefaultProgramFile.setForeground(new java.awt.Color(0, 0, 255));
        labelDefaultProgramFile.setBorder(
            javax.swing.BorderFactory.createEtchedBorder());
        labelPrograms.setLabelFor(listPrograms);
        labelPrograms.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.labelPrograms.text"));    // NOI18N
        listPrograms.setModel(model);
        listPrograms.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(new ListCellRendererPrograms());
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        listPrograms.addListSelectionListener(
            new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listProgramsValueChanged(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);
        buttonRemoveProgram.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonRemoveProgram.text"));    // NOI18N
        buttonRemoveProgram.setToolTipText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonRemoveProgram.toolTipText"));    // NOI18N
        buttonRemoveProgram.setEnabled(false);
        buttonRemoveProgram.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveProgramActionPerformed(evt);
            }
        });
        buttonAddProgram.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonAddProgram.text"));    // NOI18N
        buttonAddProgram.setToolTipText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonAddProgram.toolTipText"));    // NOI18N
        buttonAddProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddProgramActionPerformed(evt);
            }
        });
        buttonEditProgram.setText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonEditProgram.text"));    // NOI18N
        buttonEditProgram.setToolTipText(
            JptBundle.INSTANCE.getString(
                "SettingsProgramsPanel.buttonEditProgram.toolTipText"));    // NOI18N
        buttonEditProgram.setEnabled(false);
        buttonEditProgram.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditProgramActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addGap(10, 10, 10).addComponent(
                    labelChooseDefaultProgram, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE).addContainerGap()).addGroup(
                        layout.createSequentialGroup().addGap(
                            10, 10, 10).addComponent(
                            labelPrograms).addContainerGap(
                            178, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(
                                javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(
                                    layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
                                        layout.createSequentialGroup().addComponent(
                                            labelDefaultProgramFile, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                    buttonChooseDefaultProgram)).addComponent(
                                                        scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE).addGroup(
                                                            layout.createSequentialGroup().addComponent(
                                                                buttonRemoveProgram).addPreferredGap(
                                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                        buttonAddProgram).addPreferredGap(
                                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                                buttonEditProgram))).addGap(
                                                                                    12, 12, 12)));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    labelChooseDefaultProgram).addGap(7, 7, 7).addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                        layout.createSequentialGroup().addComponent(
                            labelDefaultProgramFile,
                            javax.swing.GroupLayout.PREFERRED_SIZE, 22,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                                labelPrograms)).addComponent(
                                    buttonChooseDefaultProgram)).addGap(
                                        5, 5, 5).addComponent(
                                        scrollPanePrograms,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        139, Short.MAX_VALUE).addGap(
                                            11, 11, 11).addGroup(
                                                layout.createParallelGroup(
                                                    javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                        buttonEditProgram).addComponent(
                                                            buttonRemoveProgram).addComponent(
                                                                buttonAddProgram)).addContainerGap()));
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonChooseDefaultProgramActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonChooseDefaultProgramActionPerformed
        setDefaultProgram();
    }    // GEN-LAST:event_buttonChooseDefaultProgramActionPerformed

    private void listProgramsValueChanged(
            javax.swing.event.ListSelectionEvent evt) {    // GEN-FIRST:event_listProgramsValueChanged
        setEnabled();
    }    // GEN-LAST:event_listProgramsValueChanged

    private void buttonRemoveProgramActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonRemoveProgramActionPerformed
        removeProgram();
    }    // GEN-LAST:event_buttonRemoveProgramActionPerformed

    private void buttonAddProgramActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonAddProgramActionPerformed
        addProgram();
    }    // GEN-LAST:event_buttonAddProgramActionPerformed

    private void buttonEditProgramActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonEditProgramActionPerformed
        updateProgram();
    }    // GEN-LAST:event_buttonEditProgramActionPerformed

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {    // GEN-FIRST:event_listProgramsMouseClicked
        handleListOtherProgramsMouseClicked(evt);
    }    // GEN-LAST:event_listProgramsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton     buttonAddProgram;
    private javax.swing.JButton     buttonChooseDefaultProgram;
    private javax.swing.JButton     buttonEditProgram;
    private javax.swing.JButton     buttonRemoveProgram;
    private javax.swing.JLabel      labelChooseDefaultProgram;
    private javax.swing.JLabel      labelDefaultProgramFile;
    private javax.swing.JLabel      labelPrograms;
    private javax.swing.JList       listPrograms;
    private javax.swing.JScrollPane scrollPanePrograms;

    // End of variables declaration//GEN-END:variables
}
