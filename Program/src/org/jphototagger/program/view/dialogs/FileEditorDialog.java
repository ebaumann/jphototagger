package org.jphototagger.program.view.dialogs;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.FileEditorPanel;
import org.jphototagger.lib.dialog.Dialog;

/**
 * Dialog with a {@link org.jphototagger.program.view.panels.FileEditorPanel}.
 * Closing is disabled as long as the file editor runs.
 *
 * @author Elmar Baumann
 */
public class FileEditorDialog extends Dialog {
    private static final long serialVersionUID = -3235645652277682178L;

    public FileEditorDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
    }

    public FileEditorPanel getFileEditorPanel() {
        return panelFileEditor;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setTitle(panelFileEditor.getTitle());
            panelFileEditor.readProperties();
        } else {
            panelFileEditor.writeProperties();
            hideIfNotRunning();
        }

        super.setVisible(visible);
    }

    private void hideIfNotRunning() {
        if (panelFileEditor.isRunning()) {
            MessageDisplayer.error(this, "FileEditorDialog.Error.Running");
        } else {
            super.setVisible(false);
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

        panelFileEditor = new org.jphototagger.program.view.panels.FileEditorPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelFileEditor.setName("panelFileEditor"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFileEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFileEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                FileEditorDialog dialog = new FileEditorDialog();

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
    private org.jphototagger.program.view.panels.FileEditorPanel panelFileEditor;
    // End of variables declaration//GEN-END:variables
}
