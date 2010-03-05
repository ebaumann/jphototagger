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

package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.componentutil.TabbedPaneUtil;
import de.elmar_baumann.lib.dialog.Dialog;

/**
 * Modaler Dialog zur Wartung der Thumbnaildatenbank.
 *
 * @author Elmar Baumann
 */
public final class DatabaseMaintainanceDialog extends Dialog {
    public static final DatabaseMaintainanceDialog INSTANCE =
        new DatabaseMaintainanceDialog();
    private static final long serialVersionUID = -6775385212305459197L;

    private DatabaseMaintainanceDialog() {
        super(GUI.INSTANCE.getAppFrame(), false,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
        TabbedPaneUtil.setMnemonics(tabbedPane);
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(
            JptBundle.INSTANCE.getString(
                "Help.Url.DatabaseMaintainanceDialog"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            UserSettings.INSTANCE.getSettings().applySettings(this,
                    UserSettings.SET_TABBED_PANE_SETTINGS);
        } else {
            UserSettings.INSTANCE.getSettings().set(this,
                    UserSettings.SET_TABBED_PANE_SETTINGS);
        }

        panelMaintainance.getsVisible(visible);
        panelCount.listenToDatabaseChanges(visible);
        super.setVisible(visible);
    }

    private void close() {
        if (panelMaintainance.canClose()) {
            UserSettings.INSTANCE.getSettings().set(this,
                    UserSettings.SET_TABBED_PANE_SETTINGS);
            setVisible(false);
        } else {
            MessageDisplayer.error(
                this, "DatabaseMaintainanceDialog.Error.WaitBeforeClose");
        }
    }

    @Override
    protected void escape() {
        close();
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
        panelCount =
            new de.elmar_baumann.jpt.view.panels.DatabaseInfoCountPanel();
        panelMaintainance =
            new de.elmar_baumann.jpt.view.panels.DatabaseMaintainancePanel();
        panelDatabaseUpdate =
            new de.elmar_baumann.jpt.view.panels.DatabaseUpdatePanel();
        setDefaultCloseOperation(
            javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(
            JptBundle.INSTANCE.getString("DatabaseMaintainanceDialog.title"));    // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainanceDialog.panelCount.TabConstraints.tabTitle"), panelCount);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "DatabaseMaintainanceDialog.panelMaintainance.TabConstraints.tabTitle"), panelMaintainance);    // NOI18N

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        tabbedPane.addTab(
            bundle.getString(
                "DatabaseMaintainanceDialog.panelDatabaseUpdate.TabConstraints.tabTitle"), panelDatabaseUpdate);    // NOI18N

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 564,
                    Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    tabbedPane).addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {    // GEN-FIRST:event_formWindowClosing
        close();
    }    // GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DatabaseMaintainanceDialog dialog =
                    new DatabaseMaintainanceDialog();

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
    private de.elmar_baumann.jpt.view.panels.DatabaseInfoCountPanel panelCount;
    private de.elmar_baumann.jpt.view.panels.DatabaseUpdatePanel    panelDatabaseUpdate;
    private de.elmar_baumann.jpt.view.panels.DatabaseMaintainancePanel panelMaintainance;
    private javax.swing.JTabbedPane tabbedPane;

    // End of variables declaration//GEN-END:variables
}
