package org.jphototagger.program.module.thumbnails;

import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.SettingsDialog;

/**
 * @author Elmar Baumann
 */
public class ThumbnailDimensionsSettingsDialog extends Dialog {

    private static final long serialVersionUID = 1L;

    public ThumbnailDimensionsSettingsDialog() {
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        panelSettingsThumbnailDimensions = new org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel();
        panelButtons = new javax.swing.JPanel();
        buttonClose = new javax.swing.JButton();
        buttonFurtherSettings = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/thumbnails/Bundle"); // NOI18N
        setTitle(bundle.getString("ThumbnailDimensionsSettingsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelSettingsThumbnailDimensions.setName("panelSettingsThumbnailDimensions"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelContent.add(panelSettingsThumbnailDimensions, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonClose.setText(bundle.getString("ThumbnailDimensionsSettingsDialog.buttonClose.text")); // NOI18N
        buttonClose.setName("buttonClose"); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        panelButtons.add(buttonClose, new java.awt.GridBagConstraints());

        buttonFurtherSettings.setText(bundle.getString("ThumbnailDimensionsSettingsDialog.buttonFurtherSettings.text")); // NOI18N
        buttonFurtherSettings.setName("buttonFurtherSettings"); // NOI18N
        buttonFurtherSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFurtherSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonFurtherSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonFurtherSettings;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel panelSettingsThumbnailDimensions;
    // End of variables declaration//GEN-END:variables
}
