package org.jphototagger.program.view.dialogs;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Storage;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.program.resource.GUI;

/**
 * Holds a {@link org.jphototagger.program.view.panels.RenameFilenamesInDbPanel}.
 *
 * @author Elmar Baumann
 */
public class RenameFilenamesInDbDialog extends Dialog {
    private static final long serialVersionUID = 4052809300034354623L;

    public RenameFilenamesInDbDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();

        Storage storage = Lookup.getDefault().lookup(Storage.class);
        String key = RenameFilenamesInDbDialog.class.getName();

        storage.applySize(key, this);
        storage.applyLocation(key, this);
    }

    private void setHelpPage() {
        // Has to be localized!
        setHelpContentsUrl("/org/jphototagger/program/resource/doc/de/contents.xml");
        setHelpPageUrl("rename_images_in_database.html");
    }

    private void checkClosing() {
        if (!panelDbFilenameReplace.runs()) {
            setVisible(false);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelDbFilenameReplace.readProperties();
        } else {
            panelDbFilenameReplace.writeProperties();
        }

        super.setVisible(visible);
    }

    @Override
    protected void escape() {
        checkClosing();
    }

    private void readProperties() {}

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        panelDbFilenameReplace = new org.jphototagger.program.view.panels.RenameFilenamesInDbPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("RenameFilenamesInDbDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelDbFilenameReplace.setName("panelDbFilenameReplace"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDbFilenameReplace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDbFilenameReplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        checkClosing();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                RenameFilenamesInDbDialog dialog =
                        new RenameFilenamesInDbDialog();

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
    private org.jphototagger.program.view.panels.RenameFilenamesInDbPanel panelDbFilenameReplace;
    // End of variables declaration//GEN-END:variables
}
