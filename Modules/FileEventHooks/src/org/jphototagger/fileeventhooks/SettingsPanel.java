package org.jphototagger.fileeventhooks;

import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public class SettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;

    public SettingsPanel() {
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = UiFactory.tabbedPane();
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
    }

    private org.jphototagger.fileeventhooks.JPhotoTaggerActionsSettingsPanel panelJPhotoTaggerActionsSettings;
    private javax.swing.JTabbedPane tabbedPane;
    private org.jphototagger.fileeventhooks.UserScriptsSettingsPanel userScriptsSettingsPanel1;
}
