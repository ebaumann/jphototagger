package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.SettingsHints;
import java.util.EnumSet;

/**
 * Modaler Dialog zur Wartung der Thumbnaildatenbank.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public final class DatabaseMaintainanceDialog extends Dialog {

    public static final DatabaseMaintainanceDialog INSTANCE =
            new DatabaseMaintainanceDialog();

    private DatabaseMaintainanceDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
        }
        panelMaintainance.getsVisible(visible);
        panelCount.listenToDatabaseChanges(visible);
        super.setVisible(visible);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().getComponent(this,
                new SettingsHints(EnumSet.of(
                SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().setComponent(this,
                new SettingsHints(EnumSet.of(
                SettingsHints.Option.SET_TABBED_PANE_CONTENT)));
        UserSettings.INSTANCE.writeToFile();
    }

    private void postInitComponents() {
        setIconImages(AppLookAndFeel.getAppIcons());
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
        registerKeyStrokes();
    }

    private void close() {
        if (panelMaintainance.canClose()) {
            writeProperties();
            setVisible(false);
        } else {
            MessageDisplayer.error(this,
                    "DatabaseMaintainanceDialog.Error.WaitBeforeClose"); // NOI18N
        }
    }

    @Override
    protected void help() {
        help(Bundle.getString("Help.Url.DatabaseMaintainanceDialog")); // NOI18N
    }

    @Override
    protected void escape() {
        close();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelCount = new de.elmar_baumann.imv.view.panels.DatabaseInfoCountPanel();
        panelMaintainance = new de.elmar_baumann.imv.view.panels.DatabaseMaintainancePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString("DatabaseMaintainanceDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.addTab(Bundle.getString("DatabaseMaintainanceDialog.panelCount.TabConstraints.tabTitle"), panelCount); // NOI18N
        tabbedPane.addTab(Bundle.getString("DatabaseMaintainanceDialog.panelMaintainance.TabConstraints.tabTitle"), panelMaintainance); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    close();
}//GEN-LAST:event_formWindowClosing

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
    private de.elmar_baumann.imv.view.panels.DatabaseInfoCountPanel panelCount;
    private de.elmar_baumann.imv.view.panels.DatabaseMaintainancePanel panelMaintainance;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
