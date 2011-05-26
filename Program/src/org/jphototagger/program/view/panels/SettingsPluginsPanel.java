package org.jphototagger.program.view.panels;

import org.jphototagger.program.factory.FileProcessorPluginManager;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.HelpBrowser;
import org.jphototagger.lib.generics.Pair;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import org.jphototagger.program.factory.MainWindowComponentPluginManager;
import org.jphototagger.program.factory.PluginManager;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.services.plugin.Plugin;

/**
 * Dynamically adds panels of plugins ({@link AbstractFileProcessorPlugin#getSettingsComponent()}).
 *
 * @author Elmar Baumann
 */
public class SettingsPluginsPanel extends javax.swing.JPanel implements ChangeListener, Persistence {
    private static final long serialVersionUID = 6790634142245254676L;
    private static final String KEY_TABBED_PANE = "SettingsPluginsPanel.TabbedPane";
    private final Map<Component, Pair<String, String>> helpContentsPathOfTab = new HashMap<Component, Pair<String, String>>();

    public SettingsPluginsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        addPlugins(FileProcessorPluginManager.INSTANCE);
        addPlugins(MainWindowComponentPluginManager.INSTANCE);
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

            helpContentsPathOfTab.put(component, new Pair<String, String>(helpContentsPath, firstHelpPageName));
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
        UserSettings.INSTANCE.getSettings().applySettings(KEY_TABBED_PANE, tabbedPane, null);
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(KEY_TABBED_PANE, tabbedPane, null);
        UserSettings.INSTANCE.writeToFile();
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
        String helpContentsPath = helpContentsPathOfTab.get(tabbedPane.getSelectedComponent()).getFirst();
        String firstPageUrl = helpContentsPathOfTab.get(tabbedPane.getSelectedComponent()).getSecond();

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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        labelInfoExclude.setText(JptBundle.INSTANCE.getString("SettingsPluginsPanel.labelInfoExclude.text")); // NOI18N
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
                    .addComponent(scrollPaneExclude, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                    .addComponent(labelInfoExclude))
                .addContainerGap())
        );
        panelExcludeLayout.setVerticalGroup(
            panelExcludeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExcludeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoExclude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneExclude, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(JptBundle.INSTANCE.getString("SettingsPluginsPanel.panelExclude.TabConstraints.tabTitle"), panelExclude); // NOI18N

        buttonHelpPlugin.setText(JptBundle.INSTANCE.getString("SettingsPluginsPanel.buttonHelpPlugin.text")); // NOI18N
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
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
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
    }// </editor-fold>//GEN-END:initComponents

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
