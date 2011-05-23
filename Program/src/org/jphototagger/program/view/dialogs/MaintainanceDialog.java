/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jphototagger.program.view.dialogs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.MaintainanceCachesPanel;

/**
 *
 *
 * @author Elmar Baumann
 */
public class MaintainanceDialog extends Dialog {
    private static final long serialVersionUID = 1L;

    public MaintainanceDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        tabbedPane = new JTabbedPane();
        panelMaintainanceCaches = new MaintainanceCachesPanel();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("MaintainanceDialog.title")); // NOI18N
        setIconImage(null);
        setName("MaintainanceDialog"); // NOI18N
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelMaintainanceCaches.setName("panelMaintainanceCaches"); // NOI18N
        tabbedPane.addTab(JptBundle.INSTANCE.getString("MaintainanceDialog.panelMaintainanceCaches.TabConstraints.tabTitle"), panelMaintainanceCaches); // NOI18N

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        super.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MaintainanceDialog dialog = new MaintainanceDialog();
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
    private MaintainanceCachesPanel panelMaintainanceCaches;
    private JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
