package org.jphototagger.program.module.thumbnails;

import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.SettingsDialog;

/**
 * @author Elmar Baumann
 */
public class SettingsThumbnailDimensionsDialog extends Dialog {

    private static final long serialVersionUID = 1L;

    public SettingsThumbnailDimensionsDialog() {
        super(GUI.getAppFrame(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelSettingsThumbnailDimensions.restore();
        } else {
            panelSettingsThumbnailDimensions.persist();
        }

        super.setVisible(visible);
    }

    private void displayFurtherSettings() {
        setVisible(false);

        SettingsDialog dlg = SettingsDialog.INSTANCE;

        dlg.selectTab(SettingsDialog.Tab.THUMBNAILS);

        if (dlg.isVisible()) {
            dlg.toFront();
        } else {
            dlg.setVisible(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        panelSettingsThumbnailDimensions = new org.jphototagger.program.module.thumbnails.SettingsThumbnailDimensionsPanel();
        buttonClose = new javax.swing.JButton();
        buttonFurtherSettings = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/thumbnails/Bundle"); // NOI18N
        setTitle(bundle.getString("SettingsThumbnailDimensionsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelSettingsThumbnailDimensions.setName("panelSettingsThumbnailDimensions"); // NOI18N

        buttonClose.setText(bundle.getString("SettingsThumbnailDimensionsDialog.buttonClose.text")); // NOI18N
        buttonClose.setName("buttonClose"); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        buttonFurtherSettings.setText(bundle.getString("SettingsThumbnailDimensionsDialog.buttonFurtherSettings.text")); // NOI18N
        buttonFurtherSettings.setName("buttonFurtherSettings"); // NOI18N
        buttonFurtherSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFurtherSettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(262, Short.MAX_VALUE)
                .addComponent(buttonFurtherSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonClose)
                .addContainerGap())
            .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonClose)
                    .addComponent(buttonFurtherSettings))
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void buttonFurtherSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFurtherSettingsActionPerformed
        displayFurtherSettings();
    }//GEN-LAST:event_buttonFurtherSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                SettingsThumbnailDimensionsDialog dialog = new SettingsThumbnailDimensionsDialog();
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
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonFurtherSettings;
    private org.jphototagger.program.module.thumbnails.SettingsThumbnailDimensionsPanel panelSettingsThumbnailDimensions;
    // End of variables declaration//GEN-END:variables
}