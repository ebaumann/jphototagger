package org.jphototagger.program.view.panels;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.Lookup;

import org.jphototagger.api.plugin.Plugin;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.HelpBrowser;
import org.jphototagger.plugin.AbstractFileProcessorPlugin;
import org.jphototagger.program.factory.FileProcessorPluginManager;
import org.jphototagger.program.factory.PluginManager;
import org.jphototagger.program.types.Persistence;

/**
 * Dynamically adds panels of plugins ({@link AbstractFileProcessorPlugin#getSettingsComponent()}).
 *
 * @author Elmar Baumann
 */
public class SettingsPluginsPanel extends javax.swing.JPanel implements ChangeListener, Persistence {

    private static class HelpContentsPathFirstPageName {
        private final String helpContentsPath;
        private final String firstHelpPageName;

        private HelpContentsPathFirstPageName(String helpContentsPath, String firstHelpPageName) {
            this.helpContentsPath = helpContentsPath;
            this.firstHelpPageName = firstHelpPageName;
        }
    }

    private static final long serialVersionUID = 6790634142245254676L;
    private static final String KEY_TABBED_PANE = "SettingsPluginsPanel.TabbedPane";
    private final Map<Component, HelpContentsPathFirstPageName> helpContentsPathOfTab = new HashMap<Component, HelpContentsPathFirstPageName>();

    public SettingsPluginsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addPlugins(FileProcessorPluginManager.INSTANCE);
        panelExcludeCheckboxes.add(new JPanel(), getGbcAfterLastCheckBox());    // ensures checkboxes vertically top and not centered
        MnemonicUtil.setMnemonics((Container) this);
        setEnabledHelpButton();
        tabbedPane.addChangeListener(this);
    }

    private <T extends Plugin> void addPlugins(PluginManager<T> pluginManager) {
        for (Plugin plugin : pluginManager.getEnabledPlugins()) {
            addPluginSettingsComponent(plugin);
        }

        for (T plugin : pluginManager.getAllPlugins()) {
            addPluginEnableCheckBox(pluginManager, plugin);
        }
    }

    private void addPluginSettingsComponent(Plugin plugin) {
        Component component = plugin.getSettingsComponent();

        if (component != null) {
            String helpContentsPath = plugin.getHelpContentsPath();
            String firstHelpPageName = plugin.getFirstHelpPageName();

            helpContentsPathOfTab.put(component, new HelpContentsPathFirstPageName(helpContentsPath, firstHelpPageName));
            tabbedPane.add(plugin.getDisplayName(), component);
        }
    }

    private <T extends Plugin> void addPluginEnableCheckBox(PluginManager<T> pluginManager, T plugin) {
        ActionExcludePlugin<T> actionExcludePlugin = new ActionExcludePlugin<T>(pluginManager, plugin);
        JCheckBox checkBox = new JCheckBox(actionExcludePlugin);

        checkBox.setSelected(pluginManager.isEnabled(plugin));
        panelExcludeCheckboxes.add(checkBox, getGbcCheckBox());
    }

    private GridBagConstraints getGbcCheckBox() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = GridBagConstraints.REMAINDER;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;

        return gbc;
    }

    private GridBagConstraints getGbcAfterLastCheckBox() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = GridBagConstraints.REMAINDER;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        return gbc;
    }

    @Override
    public void readProperties() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.applyTabbedPaneSettings(KEY_TABBED_PANE, tabbedPane, null);
    }

    @Override
    public void writeProperties() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setTabbedPane(KEY_TABBED_PANE, tabbedPane, null);
    }

    private static class ActionExcludePlugin<T extends Plugin> extends AbstractAction {
        private static final long serialVersionUID = -7156530079287891717L;
        private transient final T plugin;
        private transient final PluginManager<T> pluginManager;

        ActionExcludePlugin(PluginManager<T> pluginManager, T plugin) {
            this.pluginManager = pluginManager;
            this.plugin = plugin;
            putValue(Action.NAME, plugin.getDisplayName());
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            JCheckBox cb = (JCheckBox) evt.getSource();

            pluginManager.setEnabled(plugin, cb.isSelected());
        }
    }

    private void showHelp() {
        String helpContentsPath = helpContentsPathOfTab.get(tabbedPane.getSelectedComponent()).helpContentsPath;
        String firstPageUrl = helpContentsPathOfTab.get(tabbedPane.getSelectedComponent()).firstHelpPageName;

        if (helpContentsPath != null) {
            HelpBrowser help = HelpBrowser.INSTANCE;

            help.setContentsUrl(helpContentsPath);

            if (firstPageUrl != null) {
                help.setDisplayUrl(firstPageUrl);
            }

            ComponentUtil.show(help);
        }
    }

    private void setEnabledHelpButton() {
        buttonHelpPlugin.setEnabled(helpContentsPathOfTab.get(tabbedPane.getSelectedComponent()) != null);
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        setEnabledHelpButton();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        buttonGroupActionsAfterDatabaseInsertion = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        panelExclude = new javax.swing.JPanel();
        labelInfoExclude = new javax.swing.JLabel();
        scrollPaneExclude = new javax.swing.JScrollPane();
        panelExcludeCheckboxes = new javax.swing.JPanel();
        buttonHelpPlugin = new javax.swing.JButton();

        setName("Form"); // NOI18N

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setName("tabbedPane"); // NOI18N

        panelExclude.setName("panelExclude"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        labelInfoExclude.setText(bundle.getString("SettingsPluginsPanel.labelInfoExclude.text")); // NOI18N
        labelInfoExclude.setName("labelInfoExclude"); // NOI18N

        scrollPaneExclude.setAlignmentX(0.0F);
        scrollPaneExclude.setAlignmentY(0.0F);
        scrollPaneExclude.setName("scrollPaneExclude"); // NOI18N

        panelExcludeCheckboxes.setName("panelExcludeCheckboxes"); // NOI18N
        panelExcludeCheckboxes.setLayout(new java.awt.GridBagLayout());
        scrollPaneExclude.setViewportView(panelExcludeCheckboxes);

        javax.swing.GroupLayout panelExcludeLayout = new javax.swing.GroupLayout(panelExclude);
        panelExclude.setLayout(panelExcludeLayout);
        panelExcludeLayout.setHorizontalGroup(
            panelExcludeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExcludeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExcludeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPaneExclude, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                    .addComponent(labelInfoExclude))
                .addContainerGap())
        );
        panelExcludeLayout.setVerticalGroup(
            panelExcludeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExcludeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoExclude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneExclude, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(bundle.getString("SettingsPluginsPanel.panelExclude.TabConstraints.tabTitle"), panelExclude); // NOI18N

        buttonHelpPlugin.setText(bundle.getString("SettingsPluginsPanel.buttonHelpPlugin.text")); // NOI18N
        buttonHelpPlugin.setName("buttonHelpPlugin"); // NOI18N
        buttonHelpPlugin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpPluginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(420, Short.MAX_VALUE)
                .addComponent(buttonHelpPlugin)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonHelpPlugin)
                .addContainerGap())
        );
    }//GEN-END:initComponents

    private void buttonHelpPluginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHelpPluginActionPerformed
        showHelp();
    }//GEN-LAST:event_buttonHelpPluginActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupActionsAfterDatabaseInsertion;
    private javax.swing.JButton buttonHelpPlugin;
    private javax.swing.JLabel labelInfoExclude;
    private javax.swing.JPanel panelExclude;
    private javax.swing.JPanel panelExcludeCheckboxes;
    private javax.swing.JScrollPane scrollPaneExclude;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
