package org.jphototagger.program.plugins;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import org.jphototagger.api.plugin.Plugin;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.factory.FileProcessorPluginManager;
import org.jphototagger.program.factory.PluginManager;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Dynamically adds panels of plugins ({@code AbstractFileProcessorPlugin#getSettingsComponent()}).
 *
 * @author Elmar Baumann
 */
public class PluginsSettingsPanel extends PanelExt implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private static final String KEY_TABBED_PANE = "SettingsPluginsPanel.TabbedPane";

    public PluginsSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addPlugins(FileProcessorPluginManager.INSTANCE);
        panelExcludeCheckboxes.add(UiFactory.panel(), getGbcAfterLastCheckBox());    // ensures checkboxes vertically top and not centered
        MnemonicUtil.setMnemonics((Container) this);
    }

    private <T extends Plugin> void addPlugins(PluginManager<T> pluginManager) {
        for (Plugin plugin : pluginManager.getEnabledPlugins()) {
            if (plugin.isAvailable()) {
                addPluginSettingsComponent(plugin);
            }
        }

        for (T plugin : pluginManager.getAllPlugins()) {
            if (plugin.isAvailable()) {
                addPluginEnableCheckBox(pluginManager, plugin);
            }
        }
    }

    private void addPluginSettingsComponent(Plugin plugin) {
        Component component = plugin.getSettingsComponent();

        if (component != null) {
            tabbedPane.add(plugin.getDisplayName(), component);
        }
    }

    private <T extends Plugin> void addPluginEnableCheckBox(PluginManager<T> pluginManager, T plugin) {
        ActionExcludePlugin<T> actionExcludePlugin = new ActionExcludePlugin<>(pluginManager, plugin);
        JCheckBox checkBox = UiFactory.checkBox(actionExcludePlugin);

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
    public void restore() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.applyTabbedPaneSettings(KEY_TABBED_PANE, tabbedPane, null);
    }

    @Override
    public void persist() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setTabbedPane(KEY_TABBED_PANE, tabbedPane, null);
    }

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(PluginsSettingsPanel.class, "PluginsSettingsPanel.HelpPage");
    }

private static class ActionExcludePlugin<T extends Plugin> extends AbstractAction {
        private static final long serialVersionUID = 1L;
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

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        tabbedPane = UiFactory.tabbedPane();
        panelExclude = UiFactory.panel();
        labelInfoExclude = UiFactory.label();
        scrollPaneExclude = UiFactory.scrollPane();
        panelExcludeCheckboxes = UiFactory.panel();

        
        setLayout(new java.awt.GridBagLayout());

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setName("tabbedPane"); // NOI18N

        panelExclude.setName("panelExclude"); // NOI18N
        panelExclude.setLayout(new java.awt.GridBagLayout());

        labelInfoExclude.setText(Bundle.getString(getClass(), "PluginsSettingsPanel.labelInfoExclude.text")); // NOI18N
        labelInfoExclude.setName("labelInfoExclude"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelExclude.add(labelInfoExclude, gridBagConstraints);

        scrollPaneExclude.setAlignmentX(0.0F);
        scrollPaneExclude.setAlignmentY(0.0F);
        scrollPaneExclude.setName("scrollPaneExclude"); // NOI18N

        panelExcludeCheckboxes.setName("panelExcludeCheckboxes"); // NOI18N
        panelExcludeCheckboxes.setLayout(new java.awt.GridBagLayout());
        scrollPaneExclude.setViewportView(panelExcludeCheckboxes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelExclude.add(scrollPaneExclude, gridBagConstraints);

        tabbedPane.addTab(Bundle.getString(getClass(), "PluginsSettingsPanel.panelExclude.TabConstraints.tabTitle"), panelExclude); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }

    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel labelInfoExclude;
    private javax.swing.JPanel panelExclude;
    private javax.swing.JPanel panelExcludeCheckboxes;
    private javax.swing.JScrollPane scrollPaneExclude;
    private javax.swing.JTabbedPane tabbedPane;
}
