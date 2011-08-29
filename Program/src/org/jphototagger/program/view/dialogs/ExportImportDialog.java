package org.jphototagger.program.view.dialogs;

import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.ExportImportPanel;
import org.jphototagger.program.view.panels.ExportImportPanel.ExportImportListener;

/**
 *
 * @author Elmar Baumann
 */
public class ExportImportDialog extends Dialog implements ExportImportListener {
    private static final long serialVersionUID = 8937656035473070405L;
    private final ExportImportPanel.Context context;

    public ExportImportDialog(ExportImportPanel.Context context) {
        super(GUI.getAppFrame());

        setStorageKey("ExportImportDialog");

        if (context == null) {
            throw new NullPointerException("context == null");
        }

        this.context = context;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setTitle();
        setHelpPage();
        panelExportImport.setContext(context);
        panelExportImport.addListener(this);
    }

    private void setTitle() {
        setTitle(context.equals(ExportImportPanel.Context.EXPORT)
                 ? Bundle.getString(ExportImportDialog.class, "ExportImportDialog.Title.Export")
                 : Bundle.getString(ExportImportDialog.class, "ExportImportDialog.Title.Import"));
    }

    private void setHelpPage() {
        // Has to be localized!
        setHelpContentsUrl("/org/jphototagger/program/resource/doc/de/contents.xml");
        setHelpPageUrl(context.equals(ExportImportPanel.Context.EXPORT) ? "export_jpt.html" : "import_jpt.html");
    }

    @Override
    public void done() {
        setVisible(false);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        panelExportImport = new org.jphototagger.program.view.panels.ExportImportPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("ExportImportDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        panelExportImport.setName("panelExportImport"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelExportImport, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelExportImport, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ExportImportDialog dialog =
                        new ExportImportDialog(ExportImportPanel.Context.EXPORT);

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
    private org.jphototagger.program.view.panels.ExportImportPanel panelExportImport;
    // End of variables declaration//GEN-END:variables
}
