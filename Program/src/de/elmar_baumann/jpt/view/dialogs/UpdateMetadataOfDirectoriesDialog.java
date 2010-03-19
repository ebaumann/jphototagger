/*
 * @(#)UpdateMetadataOfDirectoriesDialog.java    Created on 2008-10-05
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

package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.dialog.Dialog;

/**
 * Dialog zum Scannen eines Verzeichnisses nach Bildern und Einfügen
 * von Thumbnails in die Datenbank.
 *
 * @author  Elmar Baumann
 */
public final class UpdateMetadataOfDirectoriesDialog extends Dialog {
    public static final UpdateMetadataOfDirectoriesDialog INSTANCE =
        new UpdateMetadataOfDirectoriesDialog();
    private static final long serialVersionUID = -3660709942403455416L;

    private UpdateMetadataOfDirectoriesDialog() {
        super(GUI.INSTANCE.getAppFrame(), false,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString(
                "Help.Url.UpdateMetadataOfDirectories"));
    }

    private void endDialog() {
        UserSettings.INSTANCE.writeToFile();
        panel.willDispose();
        setVisible(false);
    }

    @Override
    protected void escape() {
        endDialog();
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
        panel =
            new de.elmar_baumann.jpt.view.panels
                .UpdateMetadataOfDirectoriesPanel();
        setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(
            JptBundle.INSTANCE.getString(
                "UpdateMetadataOfDirectoriesDialog.title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panel, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                panel, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        endDialog();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                UpdateMetadataOfDirectoriesDialog dialog =
                    new UpdateMetadataOfDirectoriesDialog();

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
    private de.elmar_baumann.jpt.view.panels.UpdateMetadataOfDirectoriesPanel panel;

    // End of variables declaration//GEN-END:variables
}
