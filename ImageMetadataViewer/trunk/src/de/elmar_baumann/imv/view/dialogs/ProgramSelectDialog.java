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
package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.model.ListModelPrograms;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.renderer.ListCellRendererActions;
import de.elmar_baumann.imv.view.renderer.ListCellRendererPrograms;
import de.elmar_baumann.lib.dialog.Dialog;
import java.awt.event.MouseEvent;

/**
 * Dialog to select an {@link de.elmar_baumann.imv.data.Program}.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-07
 */
public class ProgramSelectDialog extends Dialog {

    private final ListModelPrograms model;
    private final boolean action;
    private boolean accepted;

    /**
     * Contructor.
     *
     * @param parent  parent frame
     * @param action  true, if the program acts as action
     */
    public ProgramSelectDialog(java.awt.Frame parent, boolean action) {
        super(parent, true);
        this.action = action;
        model = new ListModelPrograms(action);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIconImages(AppLookAndFeel.getAppIcons());
        registerKeyStrokes();
    }

    /**
     * Returns whether the dialog was closed throug the button with the meaning:
     * Select this program.
     *
     * @return  true if a program was selected
     */
    public boolean accepted() {
        return accepted;
    }

    /**
     * Returns the selected program.
     *
     * @return program or null if no program was selected. You can determine
     *         whether an program was selected through {@link #accepted()}.
     */
    public Program getSelectedProgram() {
        Program program = null;
        int selIndex = listPrograms.getSelectedIndex();
        if (selIndex >= 0) {
            program = (Program) model.get(selIndex);
        }
        return program;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
            accepted = false;
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.writeToFile();
    }

    private void handleMousClicked(MouseEvent e) {
        int selIndex = listPrograms.getSelectedIndex();
        boolean isSelected = selIndex >= 0;
        if ( e.getClickCount() >= 2 && isSelected) {
            handleButtonSelectAction();
        }
        buttonSelect.setEnabled(isSelected);
    }

    private void handleButtonSelectAction() {
        accepted = true;
        setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPanePrograms = new javax.swing.JScrollPane();
        listPrograms = new javax.swing.JList();
        buttonSelect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(action ? Bundle.getString("ProgramSelectDialog.Title.Actions") : Bundle.getString("ProgramSelectDialog.Title.Programs"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        scrollPanePrograms.setFocusable(false);

        listPrograms.setModel(model);
        listPrograms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPrograms.setCellRenderer(action ? new ListCellRendererActions() : new ListCellRendererPrograms());
        listPrograms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listProgramsMouseClicked(evt);
            }
        });
        scrollPanePrograms.setViewportView(listPrograms);

        buttonSelect.setText(Bundle.getString("ProgramSelectDialog.buttonSelect.text")); // NOI18N
        buttonSelect.setEnabled(false);
        buttonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonSelect)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPanePrograms, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSelect)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void listProgramsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listProgramsMouseClicked
        handleMousClicked(evt);
}//GEN-LAST:event_listProgramsMouseClicked

    private void buttonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectActionPerformed
        handleButtonSelectAction();
    }//GEN-LAST:event_buttonSelectActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ProgramSelectDialog dialog = new ProgramSelectDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSelect;
    private javax.swing.JList listPrograms;
    private javax.swing.JScrollPane scrollPanePrograms;
    // End of variables declaration//GEN-END:variables
}
