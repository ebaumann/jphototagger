package org.jphototagger.program.module.thumbnails;

import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.settings.SettingsDialog;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class ThumbnailDimensionsSettingsDialog extends DialogExt {

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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        panelSettingsThumbnailDimensions = new org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel();
        panelButtons = UiFactory.panel();
        buttonClose = UiFactory.button();
        buttonFurtherSettings = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ThumbnailDimensionsSettingsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
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

        buttonClose.setText(Bundle.getString(getClass(), "ThumbnailDimensionsSettingsDialog.buttonClose.text")); // NOI18N
        buttonClose.setName("buttonClose"); // NOI18N
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        panelButtons.add(buttonClose, new java.awt.GridBagConstraints());

        buttonFurtherSettings.setText(Bundle.getString(getClass(), "ThumbnailDimensionsSettingsDialog.buttonFurtherSettings.text")); // NOI18N
        buttonFurtherSettings.setName("buttonFurtherSettings"); // NOI18N
        buttonFurtherSettings.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFurtherSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonFurtherSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void buttonFurtherSettingsActionPerformed(java.awt.event.ActionEvent evt) {
        displayFurtherSettings();
    }

    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonFurtherSettings;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel panelSettingsThumbnailDimensions;
}
