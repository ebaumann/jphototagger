package org.jphototagger.fileeventhooks;

import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public class SettingsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    public SettingsPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = org.jphototagger.resources.UiFactory.tabbedPane();
        panelJPhotoTaggerActionsSettings = new org.jphototagger.fileeventhooks.JPhotoTaggerActionsSettingsPanel();
        userScriptsSettingsPanel1 = new org.jphototagger.fileeventhooks.UserScriptsSettingsPanel();

        setLayout(new java.awt.GridBagLayout());

        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsPanel.panelJPhotoTaggerActionsSettings.TabConstraints.tabTitle"), panelJPhotoTaggerActionsSettings); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsPanel.userScriptsSettingsPanel1.TabConstraints.tabTitle"), userScriptsSettingsPanel1); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.fileeventhooks.JPhotoTaggerActionsSettingsPanel panelJPhotoTaggerActionsSettings;
    private javax.swing.JTabbedPane tabbedPane;
    private org.jphototagger.fileeventhooks.UserScriptsSettingsPanel userScriptsSettingsPanel1;
    // End of variables declaration//GEN-END:variables
}
