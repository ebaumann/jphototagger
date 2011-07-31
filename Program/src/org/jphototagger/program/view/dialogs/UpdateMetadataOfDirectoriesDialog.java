package org.jphototagger.program.view.dialogs;

import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;

/**
 * Dialog zum Scannen eines Verzeichnisses nach Bildern und Einfügen
 * von Thumbnails in die Datenbank.
 *
 * @author Elmar Baumann
 */
public final class UpdateMetadataOfDirectoriesDialog extends Dialog {
    public static final UpdateMetadataOfDirectoriesDialog INSTANCE = new UpdateMetadataOfDirectoriesDialog();
    private static final long serialVersionUID = -3660709942403455416L;

    private UpdateMetadataOfDirectoriesDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpPages();
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.UpdateMetadataOfDirectories"));
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

    private void initComponents() {//GEN-BEGIN:initComponents

        panel = new org.jphototagger.program.view.panels.UpdateMetadataOfDirectoriesPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("UpdateMetadataOfDirectoriesDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panel.setName("panel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 487, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

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
    private org.jphototagger.program.view.panels.UpdateMetadataOfDirectoriesPanel panel;
    // End of variables declaration//GEN-END:variables
}
