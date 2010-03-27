/*
 * @(#)ImageCollectionsDialog.java    Created on 2008-09-08
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

package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.renderer.ListCellRendererImageCollections;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.ListModel;

/**
 * Dialog zum Anzeigen und Auswählen der Namen von Bildsammlungen.
 *
 * @author  Elmar Baumann
 */
public final class ImageCollectionsDialog extends Dialog {
    private static final long serialVersionUID = 1314098937293915298L;
    private boolean           ok               = false;

    public ImageCollectionsDialog() {
        super(GUI.INSTANCE.getAppFrame(), true,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString("Help.Url.ImageCollectionsDialog"));
    }

    public boolean isCollectionSelected() {
        return ok && (listImageCollectionNames.getSelectedValue() != null);
    }

    public String getSelectedCollectionName() {
        Object value = listImageCollectionNames.getSelectedValue();

        return ((value == null) ||!ok)
               ? null
               : value.toString();
    }

    private void checkDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int       index =
                listImageCollectionNames.locationToIndex(e.getPoint());
            ListModel model = listImageCollectionNames.getModel();
            Object    item  = model.getElementAt(index);

            if (item != null) {
                ok = true;
                setVisible(true);
            }
        }
    }

    private void handleButtonOkClicked() {
        ok = true;
        setVisible(false);
    }

    @Override
    protected void escape() {
        setVisible(false);
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
        labelSelectImageCollection     = new javax.swing.JLabel();
        scrollPaneImageCollectionNames = new javax.swing.JScrollPane();
        listImageCollectionNames       = new javax.swing.JList();
        buttonOk                       = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("ImageCollectionsDialog.title"));    // NOI18N
        labelSelectImageCollection.setLabelFor(listImageCollectionNames);
        labelSelectImageCollection.setText(
            JptBundle.INSTANCE.getString(
                "ImageCollectionsDialog.labelSelectImageCollection.text"));    // NOI18N
        listImageCollectionNames.setModel(
            ModelFactory.INSTANCE.getModel(ListModelImageCollections.class));
        listImageCollectionNames.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listImageCollectionNames.setCellRenderer(
            new ListCellRendererImageCollections());
        listImageCollectionNames.addMouseListener(
            new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listImageCollectionNamesMouseClicked(evt);
            }
        });
        scrollPaneImageCollectionNames.setViewportView(
            listImageCollectionNames);
        buttonOk.setText(
            JptBundle.INSTANCE.getString(
                "ImageCollectionsDialog.buttonOk.text"));    // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addGroup(layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment
                        .LEADING)
                            .addComponent(scrollPaneImageCollectionNames, javax
                                .swing.GroupLayout.DEFAULT_SIZE, 240, Short
                                .MAX_VALUE)
                                    .addComponent(labelSelectImageCollection, javax
                                        .swing.GroupLayout
                                        .DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                            .addComponent(buttonOk, javax.swing
                                                .GroupLayout.Alignment
                                                    .TRAILING))
                                                        .addContainerGap()));
        layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(labelSelectImageCollection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement
                    .RELATED)
                        .addComponent(scrollPaneImageCollectionNames,
                                      javax.swing.GroupLayout.DEFAULT_SIZE,
                                      160, Short.MAX_VALUE)
                                          .addPreferredGap(javax.swing
                                              .LayoutStyle.ComponentPlacement
                                                  .RELATED)
                                                      .addComponent(buttonOk)
                                                          .addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        handleButtonOkClicked();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void listImageCollectionNamesMouseClicked(
            java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listImageCollectionNamesMouseClicked
        checkDoubleClick(evt);
    }//GEN-LAST:event_listImageCollectionNamesMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageCollectionsDialog dialog = new ImageCollectionsDialog();

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
    private javax.swing.JButton     buttonOk;
    private javax.swing.JLabel      labelSelectImageCollection;
    private javax.swing.JList       listImageCollectionNames;
    private javax.swing.JScrollPane scrollPaneImageCollectionNames;

    // End of variables declaration//GEN-END:variables
}
